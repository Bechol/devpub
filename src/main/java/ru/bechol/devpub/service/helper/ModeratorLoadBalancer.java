package ru.bechol.devpub.service.helper;

import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.Component;
import ru.bechol.devpub.models.*;
import ru.bechol.devpub.service.*;
import ru.bechol.devpub.service.exception.ModeratorNotFoundException;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Класс ModeratorLoadBalancer.
 * Распределение новых постов между модераторами.
 *
 * @author Oleg Bech
 * @version 1.0
 * @email oleg071984@gmail.com
 * @see ru.bechol.devpub.service.IPostService
 */
@Component
public class ModeratorLoadBalancer {

	@Autowired
	@Qualifier("roleService")
	private IRoleService roleService;
	@Autowired
	private Messages messages;

	/**
	 * Метод appointModerator.
	 * Возвращает модератора с минимальным среди всех модераторов количеством модерируемых постов.
	 *
	 * @return - User модератор
	 * @throws Exception - если модератор не найден.
	 */
	public User appointModerator() throws Exception {
		Role roleModerator = roleService.findByName("ROLE_MODERATOR");
		List<User> availableModeratorList = roleModerator.getUsers().stream()
				.filter(User::isAccountNonExpired)
				.filter(User::isAccountNonLocked)
				.filter(User::isEnabled)
				.collect(Collectors.toList());
		return availableModeratorList.stream().min(Comparator.comparing(user -> user.getModeratedPosts().size()))
				.orElseThrow(() -> new ModeratorNotFoundException(
						messages.getMessage("warning.moderator.not-found"))
				);
	}
}
