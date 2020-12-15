package ru.bechol.devpub.service;

import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import ru.bechol.devpub.event.DevpubAppEvent;
import ru.bechol.devpub.models.Post;
import ru.bechol.devpub.models.User;
import ru.bechol.devpub.models.Vote;
import ru.bechol.devpub.repository.UserRepository;
import ru.bechol.devpub.repository.VoteRepository;
import ru.bechol.devpub.request.ChangePasswordRequest;
import ru.bechol.devpub.request.RegisterRequest;
import ru.bechol.devpub.response.Response;
import ru.bechol.devpub.response.StatisticResponse;
import ru.bechol.devpub.response.UserData;
import ru.bechol.devpub.service.enums.ModerationStatus;
import ru.bechol.devpub.service.enums.SettingValue;
import ru.bechol.devpub.service.exception.CodeNotFoundException;
import ru.bechol.devpub.service.exception.UserNotFoundException;

import javax.management.relation.RoleNotFoundException;
import java.security.Principal;
import java.time.ZoneOffset;
import java.util.*;
import java.util.stream.Collectors;
import static ru.bechol.devpub.service.helper.ErrorMapHelper.createBindingErrorResponse;

/**
 * Класс UserService.
 * Реализация сервисного слоя для User.
 *
 * @author Oleg Bech
 * @version 1.0
 * @email oleg071984@gmail.com
 * @see ru.bechol.devpub.models.User
 * @see UserRepository
 */
@Service
public class UserService implements UserDetailsService {

    private final static String ROLE_USER = "ROLE_USER";
    private final static Map<String, Boolean> RESULT_TRUE_MAP = Map.of("result", true);
    private final static Map<String, Boolean> RESULT_FALSE_MAP = Map.of("result", false);
    @Value("${time-offset}")
    private String clientZoneOffsetId;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleService roleService;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private Messages messages;
    @Autowired
    private EmailService emailService;
    @Autowired
    private CaptchaCodesService captchaCodesService;
    @Autowired
    private PostService postService;
    @Autowired
    private VoteRepository voteRepository;
    @Autowired
    private GlobalSettingsService globalSettingsService;
    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    /**
     * Метод registrateNewUser.
     * Регистрация нового пользователя.
     *
     * @param registerRequest данные с формы, прошедшие валидацию.
     * @return ResponseEntity<?>.
     */
    public ResponseEntity<?> registrateNewUser(RegisterRequest registerRequest, BindingResult bindingResult)
            throws Exception {
        if(globalSettingsService.checkSetting("MULTIUSER_MODE", SettingValue.NO)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(messages.getMessage("multi-user.off"));
        }
        if (!captchaCodesService.captchaIsExist(registerRequest.getCaptcha(), registerRequest.getCaptcha_secret())) {
            bindingResult.addError(new FieldError(
                    "captcha", "captcha", messages.getMessage("cp.errors.captcha-code")));
        }
        if (bindingResult.hasErrors()) {
            return createBindingErrorResponse(bindingResult, HttpStatus.OK);
        }
        User user = new User();
        user.setEmail(registerRequest.getEmail());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setName(registerRequest.getName());
        user.setModerator(false);
        applicationEventPublisher.publishEvent(new DevpubAppEvent<>(
                this, this.setUserRole(user), DevpubAppEvent.EventType.SAVE_USER
        ));
        return ResponseEntity.ok(RESULT_TRUE_MAP);
    }

    /**
     * Метод setUserRole.
     * Присвоение роли ROLE_USER вновь зарегистрированному пользователь.
     *
     * @param user - пользователь.
     * @return пользователь с роллью user.
     */
    private User setUserRole(User user) throws RoleNotFoundException {
        user.setRoles(Collections.singletonList(roleService.findByName(ROLE_USER)));
        return user;
    }

    /**
     * Метод findByEmail.
     * Поиск пользователя по email.
     *
     * @param email -  email пользователя для поиска.
     * @return Optional<User>.
     */
    public User findByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(() -> new UserNotFoundException(
                messages.getMessage("warning.user.not-found"), "email", email));
    }

    /**
     * Метод isUserNotExistByEmail.
     * Проверяет, существует что пользователь с данным email-ом не существует.
     *
     * @param email - почта пользователя.
     * @return true - если пользователь не существует.
     */
    public boolean isUserNotExistByEmail(String email) {
        return Strings.isNotEmpty(email) && userRepository.findByEmail(email).isEmpty();
    }

    /**
     * Метод loadUserByUsername
     * Поиск пользователя по email в базе.
     *
     * @param email - email введеный на форме авторизации и аутентификации.
     * @return UserDetails
     * @throws UsernameNotFoundException
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException(
                messages.getMessage("warning.user.not-found")));
    }

    /**
     * Метод checkAuthorization.
     * Метод возвращает информацию о текущем авторизованном пользователе, если он авторизован.
     * Проверяет, сохранён ли идентификатор текущей сессии в списке авторизованных.
     * Значение moderationCount содержит количество постов необходимых для проверки модераторами.
     * Считаются посты имеющие статус NEW и не проверерны модератором.
     * Если пользователь не модератор возращать 0 в moderationCount.
     *
     * @param authorizedUser авторизованный пользователь.
     * @return ResponseEntity.
     */
    public ResponseEntity<?> checkAuthorization(User authorizedUser) {
        if (authorizedUser == null) {
            return ResponseEntity.ok(RESULT_FALSE_MAP);
        }
        return ResponseEntity.ok().body(Response.builder().result(true)
                .user(UserData.builder()
                        .id(authorizedUser.getId())
                        .email(authorizedUser.getEmail())
                        .name(authorizedUser.getName())
                        .photo(authorizedUser.getPhotoLink())
                        .moderationCount(postService.findPostsByStatus(ModerationStatus.NEW))
                        .moderation(authorizedUser.isModerator())
                        .settings(authorizedUser.isModerator()).build()).build());
    }

    /**
     * Метод checkAndSendForgotPasswordMail.
     * Формирование и отправка письма с ссылкой для восстановления пароля.
     *
     * @param email         - email для отправки.
     * @param bindingResult - результат валидации данных, ввуденых пользователем
     */
    public Map<String, Boolean> checkAndSendForgotPasswordMail(String email, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return RESULT_FALSE_MAP;
        }
        User user = userRepository.findByEmail(email).orElse(null);
        if (user != null) {
            user.setForgotCode(UUID.randomUUID().toString());
            applicationEventPublisher.publishEvent(new DevpubAppEvent<>(
                    this, user, DevpubAppEvent.EventType.SAVE_USER
            ));
            emailService.send(user.getEmail(), messages.getMessage("rp.mail-subject"),
                    messages.getMessage(
                            "rp.message-text", user.getName(), createRestorePasswordLink(user.getForgotCode())
                    )
            );
            return RESULT_TRUE_MAP;
        }
        return RESULT_FALSE_MAP;
    }

    /**
     * Метод createRestorePasswordLink.
     * Формирование ссылки для восстановления пароля.
     *
     * @param forgotCode - токен для восстановления пароля.
     * @return ссылка для восстановления пароля.
     */
    private String createRestorePasswordLink(String forgotCode) {
        return new StringBuffer(ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/login/change-password/").toUriString()).append(forgotCode).toString();
    }

    /**
     * Метод changePassword.
     * Изменение парооля пользователя.
     *
     * @param changePasswordRequest тело запроса на изменение пароля.
     * @param bindingResult         результаты валидации данных.
     */
    public ResponseEntity<?> changePassword(ChangePasswordRequest changePasswordRequest,
                                            BindingResult bindingResult) {
        if (!captchaCodesService.captchaIsExist(changePasswordRequest.getCaptcha(),
                changePasswordRequest.getCaptcha_secret())) {
            bindingResult.addError(new FieldError(
                    "captcha", "captcha", messages.getMessage("cp.errors.captcha-code")));
        }
        User user = userRepository.findByForgotCode(changePasswordRequest.getCode()).orElse(null);
        if (user == null) {
            bindingResult.addError(new FieldError("code", "code",
                    messages.getMessage("cp.errors.forgot-code")));
        }
        if (bindingResult.hasErrors()) {
            return createBindingErrorResponse(bindingResult, HttpStatus.OK);
        }
        user.setPassword(passwordEncoder.encode(changePasswordRequest.getPassword()));
        user.setForgotCode(null);
        applicationEventPublisher.publishEvent(new DevpubAppEvent<>(
                this, user, DevpubAppEvent.EventType.SAVE_USER
        ));
        return ResponseEntity.ok().body(Map.of("result", true));
    }

    /**
     * Метод calculateMyStatistics.
     * Статистика по актвным постам авторизованного пользователя.
     *
     * @param principal - авторизованный пользователь.
     * @return StatisticResponse
     */
    public StatisticResponse calculateMyStatistics(Principal principal) {
        return this.createStatisticsResponse(this.findByEmail(principal.getName()));
    }

    /**
     * Метод calculateSiteStatistics.
     * Статистика по всем постам блога.
     *
     * @return ResponseEntity<?>
     */
    public ResponseEntity<?> calculateSiteStatistics(Principal principal) throws CodeNotFoundException {
        if (globalSettingsService.checkSetting("STATISTICS_IS_PUBLIC", SettingValue.NO)
        && !this.findByEmail(principal.getName()).isModerator()) {
            return ResponseEntity.status(401).build();
        }
        return ResponseEntity.ok(this.createStatisticsResponse(null));
    }

    /**
     * Метод createStatisticsResponse.
     * Создание отчета по статистике.
     *
     * @param activeUser - авторизованный пользователь.
     * @return StatisticResponse.
     */
    private StatisticResponse createStatisticsResponse(User activeUser) {
        List<Post> postList = activeUser != null ?
                activeUser.getPosts().stream().filter(Post::isActive).collect(Collectors.toList()) :
                postService.findAll();
        postList.sort(Comparator.comparing(Post::getTime));
        List<Vote> postsVotes = voteRepository.findByPostIn(postList);
        long postsCount = postList.size();
        long likesCount = postsVotes.stream().filter(vote -> vote.getValue() == 1).count();
        long dislikesCount = postsVotes.stream().filter(vote -> vote.getValue() == -1).count();
        int viewsCount = postList.stream().map(Post::getViewCount).reduce(Integer::sum).orElse(0);
        return StatisticResponse.builder()
                .postsCount(postsCount)
                .likesCount(likesCount)
                .dislikesCount(dislikesCount)
                .viewsCount(viewsCount)
                .firstPublication(!postList.isEmpty() ? postList.get(0).getTime()
                        .toInstant(ZoneOffset.of(clientZoneOffsetId)).getEpochSecond() : 0)
                .build();
    }
}
