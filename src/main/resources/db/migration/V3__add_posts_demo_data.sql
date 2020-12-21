insert into posts (is_active, moderation_status, moderator_id, moderated_by, user_id, time, title, post_text, view_count)
values (true, 'ACCEPTED', 1, 1, 1, '2020-12-10 13:00:01',
'Вступление из книги Vue.js в действии',
'Клиентская веб-разработка стала на удивление сложной. Если вы никогда не имели дела с современными фреймворками для JavaScript, 
то можете потратить целую неделю на создание своей первой программы, которая всего лишь выводит приветствие! Это звучит нелепо,
и здесь я с вами соглашусь. Проблема в том, что большинство фреймворков требуют навыков работы с терминалом, углубленного знания
JavaScript, владения такими инструментами, как NPM (Node Package Manager - диспетчер пакетов Node ), Babel, Webpack, - и часто это еще не полный список.
Однако к Vue все это не относится. Мы называем эту библиотеку прогрессивной, поскольку она масштабируется в обе стороны. 
Если приложение простое, используйте Vue по аналогии cjQuery, вставляя элемент script. Но с ростом требований
и приобретением вами новых навыков работа с Vue станет все более разносторонней и продуктивной.
У Vue есть еще одна отличительная черта. Этот проект разрабатывается не только программистами, но и дизайнерами, преподавателями 
и специалистами других гуманитарных профессий. Благодаря этому наши документация, руководства и инструменты
для разработки одни из лучших в мире. Ощущения от использования Vue имеют для нас такое же значение, как производительность, надежность и гибкость.
Эрику удалось сделать эту книгу ориентированной на обычных людей. Прежде всего она необыкновенно наглядная. Множество подробных иллюстраций и снимков
экрана с примечаниями неразрывно связывают приведенные здесь примеры с реальным процессом разработки.
В итоге, чтобы закрепить свои знания, вам придется научиться работать с браузером и инструментами для разработки Vue и, что важнее,
овладеть методами отладки при возникновении проблем. Для читателей, у которых нет существенного опыта клиентской разработки, написания
программ нajavaScript или даже просто программирования, Эрик тщательно излагает фундаментальные концепции и объясняет принцип работы и назначение
Vue. Добавьте к этому методологию описания новых возможностей в контекстереальных проектов - и вы получите идеальную книгу для
относительно неопытных разработчиков, которые начинают знакомство с современными клиентскими фреймворками с Vue и хотят расширить
свои знания в этой области.',
102);

insert into tags(name) values ('Vue');
insert into tags(name) values ('FrontEnd');

insert into tag2post (post_id, tag_id) values (1, 1);
insert into tag2post (post_id, tag_id) values (1, 2);

insert into post_votes (user_id, post_id, time, value) values (1, 1, '2020-12-10 13:00:02', 1);
insert into post_votes (user_id, post_id, time, value) values (2, 1, '2020-12-10 13:21:02', 1);
insert into post_votes (user_id, post_id, time, value) values (3, 1, '2020-12-10 14:20:14', -1);

insert into post_comments (parent_id, post_id, user_id, comment_time, comment_text)
values (null, 1, 1, '2020-12-12 13:20:01', 'Отличная книга. Прочитал на одном дыхании!');
insert into post_comments (parent_id, post_id, user_id, comment_time, comment_text)
values (1, 1, 2, '2020-12-12 13:20:01', 'Очень полезный материал=)');
insert into post_comments (parent_id, post_id, user_id, comment_time, comment_text)
values (null, 1, 3, '2020-12-12 13:20:01', 'А мне не очень. Мне больше нравится Angular.');

insert into posts (is_active, moderation_status, moderator_id, moderated_by, user_id, time, title, post_text, view_count)
values (true, 'ACCEPTED', 2, 2, 2, '2020-12-12 17:04:15',
'Что нового в Spring Framework 5',
'<h2>Что нового в Spring</h2>
<br>
<p>Так что же нового в Spring Boot
Если говорить о самом большом нововведении, то это обновление Spring Framework до 5-ой версии. С тех пор, как в сентябре 2017 вышла Spring Framework 5, 
большинство разработчиков (как и я) ждали релиза Spring Boot 2. Spring Framework 5 имеет немалый список новых функций, но я бы хотел рассказать только 
о нескольких самых главных.</p>
<br>
<h3>Что нового в Spring Framework 5</h3>
<br>
<h4>Поддержка версий Java 8+</h4>
<br>
<p>Если вы и впредь хотите создавать приложения на Spring Framework, вам нужно работать с версией Java 8+.
Вы, наверное, подумали, что это очень важное изменение для всех нас, но для команды Spring оно еще важнее.
Это позволило обновить базу исходного кода до Java 8 со всеми его новыми фишками вроде лямбда-выражений или стримов.
Это не только делает код более читабельным, но и улучшает производительность ядра платформы.</p>
<br>
<h4>Поддержка Java 9</h4>
<br>
<p>Если вы хотите использовать Java 9, вам нужно обновиться до Spring Framework 5 и также до Spring Boot 2.
Я знаю, что многие еще не используют новейшие версии Java в продакшене, и это отличная возможность поэкспериментировать
с новыми крутыми “игрушками”. Все должно работать без проблем при использовании стандартного classpath’а, но я читал о
некоторых затруднениях при переходе на модули Java 9.</p>
<br>
<h4>Spring MVC</h4>
<br>
<p>Хотя Spring MVC и не в центре повествования в этой статье, стоит сказать, что было несколько приятных апгрейдов.
Я не буду на них останавливаться, подробности можно посмотреть в&nbsp;
<a href="https://github.com/spring-projects/spring-framework/wiki/What%27s-New-in-Spring-Framework-5.x#spring-web-mvc">документации для Spring Framework 5</a>.
</p>
<br>
<h4>Spring Webflux</h4>
<br>
<p>Асинхронность потоков данных занимает центральное место в истории о Spring Framework 5.  Это совершенно другой тип
мышления, но, к счастью для нас, нет нужды переучиваться писать приложения совершенно новым способом.
Spring WebFlux — абсолютно асинхронный и неблокирующий фреймворк, построенный с нуля, позволяющий справляться с
большим количеством параллельных подключений. Хоть это и переворот  в парадигме, начать будет не так уж сложно.</p>
<h4>Поддержка Kotlin</h4>
<p>Поддержка Kotlin была добавлена еще в&nbsp;<a href="http://start.spring.io/">http://start.spring.io</a>,
но в Spring Framework 5 есть специализированная поддержка этого языка, привнесшая полезные функции,
прочитать о них можно&nbsp;<a href="https://github.com/spring-projects/spring-framework/wiki/What%27s-New-in-Spring-Framework-5.x#kotlin-support">здесь</a>.
</p>
<br>
<h4>Улучшения в механизмах тестирования</h4>
<br>
<p>Самое крупное изменение в системе тестирования — это полная поддержка&nbsp;<a href="http://junit.org/junit5/">JUnit</a>5 Jupiter.
Я еще расскажу об этом подробнее ниже, но когда вы запускаете новое приложение на Spring Boot 2, вы все еще используете по умолчанию JUnit 4, однако,
переключиться на JUnit 5 — задача тривиальная.</p>',
115);

insert into tags(name) values ('Spring');
insert into tags(name) values ('Spring boot 2');

insert into tag2post (post_id, tag_id) values (2, 3);
insert into tag2post (post_id, tag_id) values (2, 4);

insert into post_votes (user_id, post_id, time, value) values (1, 2, '2020-12-12 13:00:02', 1);
insert into post_votes (user_id, post_id, time, value) values (2, 2, '2020-12-13 13:21:02', 1);
insert into post_votes (user_id, post_id, time, value) values (3, 2, '2020-12-13 14:20:14', 1);
insert into post_votes (user_id, post_id, time, value) values (4, 2, '2020-12-14 15:23:23', 1);

insert into posts (is_active, moderation_status, moderator_id, moderated_by, user_id, time, title, post_text, view_count)
values (true, 'ACCEPTED', 2, 1, 1, '2020-12-09 10:03:21',
'Spring Security',
'Spring Security – это платформа для приложений Java и Java EE, которая предоставляет специализированные механизмы
для построения систем авторизации и аутентификации. Она представляет собой настраиваемую систему для проверки
подлинности и контроля доступа к определенным ресурсам приложений Java и Java EE, а также защиты корпоративных
приложений, созданных с помощью Spring Framework. Впервые разработку данной платформы начал Бен Алекс (Ben Alex)
в 2003 году, называлась она «Acegi Security», после этого в 2004 году был выпущен ее первый релиз.
Однако долго самостоятельным проектом она не просуществовала и была поглощена компанией , как следствие этого
она стала его официальным дочерним проектом. Под юрисдикцией компании «Spring» платформа была публично представлена
под новым именем «Spring Security»[Источник 3] версии релиза 2.0.0 в апреле 2008 года.',
56);

insert into tags(name) values ('Spring security');

insert into tag2post (post_id, tag_id) values (3, 4);
insert into tag2post (post_id, tag_id) values (3, 5);

insert into post_votes (user_id, post_id, time, value) values (1, 3, '2020-12-09 10:04:21', 1);
insert into post_votes (user_id, post_id, time, value) values (2, 3, '2020-12-09 11:03:21', -1);
insert into post_votes (user_id, post_id, time, value) values (3, 3, '2020-12-09 12:03:21', 1);
insert into post_votes (user_id, post_id, time, value) values (4, 3, '2020-12-09 12:23:21', 1);

insert into post_comments (parent_id, post_id, user_id, comment_time, comment_text)
values (null, 3, 2, '2020-12-09 10:04:21', 'Теперь я знаю Spring Security!');


insert into posts (is_active, moderation_status, moderator_id, moderated_by, user_id, time, title, post_text, view_count)
values (true, 'ACCEPTED', 1, 1, 3, '2020-12-08 10:03:21',
'Краткий обзор Apache Kafka',
'Apache Kafka — брокер сообщений, реализующий паттерн Producer-Consumer с хорошими способностями к
горизонтальному масштабированию. Это Open Source разработка, созданная компанией LinkedIn на JVM стеке (Scala).
Горизонтально масштабируя какую-либо систему, вы поневоле делаете её распределённой, а работа с распределённой системой
имеет свои особенности. Формально, для описания свойств распределённых систем существует CAP-теорема.
В распределённой системе невозможно обеспечить одновременное выполнение всех трёх свойств: консистентности, доступности,
устойчивости к сбоям узлов. Что это за свойства: <p>Консистентность (Consistency). Говорит о том, что система всегда
выдаёт только логически непротиворечивые ответы. Не бывает такого, что вы добавили в корзину товар, а после рефреша
страницы его там не видите. Доступность (Availability). Означает, что сервис отвечает на запросы, а не выдаёт
ошибки о том, что он недоступен. Устойчивость к сбоям сети (Partition tolerance). Означает, что распределённая по
кластеру система работает в расчёте на случаи произвольной потери пакетов внутри сети. С точки зрения CAP-теоремы, Kafka
имеет CA*, т.е. выполняются условия консистентности и доступности, но не гарантируется устойчивость к сбоям в сети —
по отзывам пользователей, Kafka не очень устойчива к netsplit (моменту, когда ваш кластер, например, разваливается пополам),
хотя официальной документации на этот счёт мы не нашли. На самом низком уровне Kafka — это просто распределённый
лог-файл. То есть, по сути, файл, разбитый на несколько частей (партиций) и «раскатанный» на несколько узлов кластера.
Запись в этот файл всегда происходит в конец. Разделение файла на части необходимо для ускорения чтения из очереди и
горизонтального масштабирования. Ваш Topic (тема) может быть «порезан» на сколько угодно частей. Соответственно, в
ы можете разделить Topic на сколько угодно серверов. Из каждой партиции может читать не более одного Consumer (читатель).
Это значит, что максимальное число параллельных читателей равно количеству частей, на которые разбит ваш Topic.
Соответственно, для одной партиции топика гарантируется очерёдность сообщений, так как из каждой партиции
может читать не более одного читателя. У каждого сообщения есть свой сквозной номер внутри патриции.
В терминах Kafka это называется offset. При чтении из партиции читатель делает коммит оффсета.
Это необходимо для того, чтобы, если, например, текущий читатель упадёт, то следующий (новый читатель)
начнёт с последнего коммита. Читатели объединяются в группы, что так и называется — consumer group.
При добавлении нового читателя или падении текущего, группа перебалансирутся. Это занимает какое-то время,
поэтому лучший способ чтения — подключить читателя и не переподключать его без необходимости. Что касается доступности,
Kafka обеспечивает репликацию сообщений и disk persistence, сохраняя сообщения на диск. Формат репликации называется
InSync. Это значит, что слейвы (в терминах Kafka это фолловеры) сами постоянно спрашивают мастера о новых сообщениях.
Это pull-модель. Синхронностью/асинхронностью репликации вы можете управлять сами, указывая какие гарантии
(acknowledgement) вы хотите получить при записи в очередь.
Kafka поддерживает три режима:
отправить и не дожидаться подтверждения записи;
отправить и дождаться подтверждения на мастер-ноде;
отправить и дождаться подтверждения на всех репликах.
Поскольку Kafka гарантирует консистентность, для читателей сообщение будет видно только после записи по всем репликам.
Репликация происходит отдельно для каждой партиции в топике.
Если вспомнить про disk persistence, то он вытекает из устройства Kafka. Так как вся система — это просто лог,
то все сообщения в любом случае попадают на диск и это невозможно выключить, но в конфигурации можно подкрутить ручку,
какими периодами сообщения падают на диск. Что, соответственно, уменьшит ваши гарантии на потерю сообщений,
но увеличит производительность.
Клиенты для Kafka достаточно интеллектуальные и работают на уровне TCP. В коробке с Kafka лежит клиент на
Java (так как сама Kafka написана на Scala) и библиотека на C.
Выводы:Apache Kafka менее удобна, чем тот же RabbitMQ, но если вы не можете
терять сообщения, то вариант с Kafka подходит больше. К тому же у Kafka гораздо больше scalability (расширяемость).',
178);

insert into tags(name) values ('Apache kafka');

insert into tag2post (post_id, tag_id) values (4, 6);

insert into post_votes (user_id, post_id, time, value) values (1, 4, '2020-12-08 10:04:21', 1);
insert into post_votes (user_id, post_id, time, value) values (3, 4, '2020-12-08 10:05:21', -1);

insert into posts (is_active, moderation_status, moderator_id, moderated_by, user_id, time, title, post_text, view_count)
values (true, 'ACCEPTED', 2, 2, 2, '2020-12-09 14:00:00',
'Краткий обзор Hibernate',
'Этот фреймворк—ORM, объектно-реляционная отображение, то есть он позволяет писать запросы к серверу баз данных
не на SQL, а на Java, что меняет привычный взгляд на базу данных как таковую.
есмотря на то, что Hibernate не является полноценным фреймворком, он позволяет с легкостью конвертировать информацию
для различных баз данных.
Эта особенность также упрощает масштабирование, независимо от размера приложения и количества его пользователей.
В целом, этот фреймворк можно охарактеризовать как быстрый, мощный, легко масштабируемый и настраиваемый.',
178);

insert into tags(name) values ('Hibernare');
insert into tags(name) values ('ORM');
insert into tags(name) values ('Базы данных');
insert into tags(name) values ('Spring JPA');

insert into tag2post (post_id, tag_id) values (5, 7);
insert into tag2post (post_id, tag_id) values (5, 8);
insert into tag2post (post_id, tag_id) values (5, 9);
insert into tag2post (post_id, tag_id) values (5, 10);

insert into post_votes (user_id, post_id, time, value) values (1, 5, '2020-12-09 14:00:00', 1);
insert into post_votes (user_id, post_id, time, value) values (3, 5, '2020-12-09 15:05:10', -1);
insert into post_votes (user_id, post_id, time, value) values (2, 5, '2020-12-10 15:04:07', -1);
insert into post_votes (user_id, post_id, time, value) values (4, 5, '2020-12-10 11:17:14', -1);
insert into post_votes (user_id, post_id, time, value) values (5, 5, '2020-12-11 12:06:21', 1);

insert into posts (is_active, moderation_status, moderator_id, moderated_by, user_id, time, title, post_text, view_count)
values (true, 'ACCEPTED', 1, 1, 4, '2020-12-09 14:00:00',
'Краткий обзор Struts',
'Struts позволяет разрабатывать для предприятий простой и удобный в использовании софт. Основным преимуществом в этом
фреймворке выступают его портативные плагины, являющиеся пакетами JAR. Плагины Hibernate и Spring в этом случае могут
использоваться для объектно-реляционного отображения и внедрения зависимостей, соответственно.
Этот фреймворк также позволяет сократить общее время разработки за счет удачной организации Java,
JSP и Action классов.',
155);

insert into tags(name) values ('Struts');

insert into tag2post (post_id, tag_id) values (6, 11);
insert into tag2post (post_id, tag_id) values (6, 7);
insert into tag2post (post_id, tag_id) values (6, 3);

insert into post_votes (user_id, post_id, time, value) values (1, 6, '2020-12-09 14:00:00', 1);
insert into post_votes (user_id, post_id, time, value) values (3, 6, '2020-12-09 15:05:10', 1);
insert into post_votes (user_id, post_id, time, value) values (2, 6, '2020-12-10 15:04:07', 1);
insert into post_votes (user_id, post_id, time, value) values (4, 6, '2020-12-10 11:17:14', 1);
insert into post_votes (user_id, post_id, time, value) values (5, 6, '2020-12-11 12:06:21', 1);

insert into posts (is_active, moderation_status, moderator_id, moderated_by, user_id, time, title, post_text, view_count)
values (true, 'ACCEPTED', 1, 1, 4, '2020-12-09 11:15:21',
'Краткий обзор Play Framework',
'Его применяют такие крупнейшие компании как LinkedIn, Samsung, The Guardian, Verizon и другие, что подтверждает его
безусловную надежность. К основным отличительным характеристикам можно отнести высокую скорость, качество и
хорошую масштабируемость. Пользовательский интерфейс Play является очень простым и осваивается разработчиками
мобильных приложений достаточно быстро. Применение же он чаще всего находит в тех приложениях, которые требуют
регулярного создания контента.',
50);

insert into tags(name) values ('Play');

insert into tag2post (post_id, tag_id) values (7, 12);

insert into post_votes (user_id, post_id, time, value) values (1, 7, '2020-12-09 14:00:00', 1);
insert into post_votes (user_id, post_id, time, value) values (3, 7, '2020-12-09 15:05:10', 1);
insert into post_votes (user_id, post_id, time, value) values (2, 7, '2020-12-09 15:04:07', 1);
insert into post_votes (user_id, post_id, time, value) values (4, 7, '2020-12-09 16:17:14', 1);
insert into post_votes (user_id, post_id, time, value) values (5, 7, '2020-12-09 18:06:21', 1);

insert into posts (is_active, moderation_status, moderator_id, moderated_by, user_id, time, title, post_text, view_count)
values (true, 'ACCEPTED', 1, 1, 4, '2020-12-12 17:00:00',
'Краткий обзор Google Web Toolkit',
'Этот бесплатный фреймворк применяется для разработки клиентской части приложений, например в Javascript.
Корпорация Google обширно применяла его в создании множества своих сервисов, включая AdSense, Google Wallet, AdWords и пр.
При помощи его кодов могут быть также легко разработаны и отлажены приложения Ajax. Разработчики выбирают этот
фреймворк при написании комплексных программ, а его основные фишки—это кросс-браузерная совместимость,
хранение истории, возможность ставить метки и др.',
74);

insert into tags(name) values ('GWT');
insert into tags(name) values ('Web Toolkit');
insert into tags(name) values ('Google');

insert into tag2post (post_id, tag_id) values (8, 13);
insert into tag2post (post_id, tag_id) values (8, 14);
insert into tag2post (post_id, tag_id) values (8, 15);

insert into post_votes (user_id, post_id, time, value) values (1, 8, '2020-12-13 09:00:00', 1);
insert into post_votes (user_id, post_id, time, value) values (3, 8, '2020-12-14 15:05:10', 1);

insert into posts (is_active, moderation_status, moderator_id, moderated_by, user_id, time, title, post_text, view_count)
values (true, 'ACCEPTED', 2, 2, 5, '2020-12-12 14:54:00',
'Краткий обзор Grails',
'Этот фреймворк также является бесплатным и довольно популярен в работе с Enterprise Java Beans.
Он может применяться для создания надежных, масштабируемых приложений, систем управления контентом,
RESTful сервисов и коммерческих сайтов. Grails можно применять вместе с другими технологиями Java—Spring, Hibernate,
quartz, SiteMesh и контейнерами EE. Его преимущества проявляются в виде наличия GORM, гибких профилей,
продвинутой системы многочисленных плагинов и библиотеки отображения объектов.',
174);

insert into tags(name) values ('Grails');

insert into tag2post (post_id, tag_id) values (9, 16);
insert into tag2post (post_id, tag_id) values (9, 7);
insert into tag2post (post_id, tag_id) values (9, 3);

insert into post_votes (user_id, post_id, time, value) values (1, 9, '2020-12-12 18:00:00', 1);
insert into post_votes (user_id, post_id, time, value) values (3, 9, '2020-12-12 21:05:10', 1);

insert into posts (is_active, moderation_status, moderator_id, moderated_by, user_id, time, title, post_text, view_count)
values (true, 'ACCEPTED', 2, 2, 4, '2020-12-15 13:00:00',
'Краткий обзор Blade',
'Практически любой разработчик приложений сможет разобраться в этом фреймворке в течение дня. Java Blade был выпущен в
2015 и сразу стал известен как простой и легкий инструмент. Самым ярким его преимуществом является возможность очень
быстро создавать web-приложения. Blade относится к полноценным фреймворкам, предлагающим простую и ясную структуру
написания кода, поддержку web jar ресурсов и плагинов. Основан он на Java 8, благодаря чему имеет интерфейс
маршрутизации в стиле RESTful.',
144);

insert into tags(name) values ('Blade');

insert into tag2post (post_id, tag_id) values (10, 17);

insert into post_votes (user_id, post_id, time, value) values (1, 10, '2020-12-15 18:00:00', 1);
insert into post_votes (user_id, post_id, time, value) values (2, 10, '2020-12-15 21:05:10', 1);
insert into post_votes (user_id, post_id, time, value) values (3, 10, '2020-12-15 21:05:10', 1);
insert into post_votes (user_id, post_id, time, value) values (4, 10, '2020-12-15 21:06:10', 1);
insert into post_votes (user_id, post_id, time, value) values (5, 10, '2020-12-15 22:06:10', 1);

insert into posts (is_active, moderation_status, moderator_id, moderated_by, user_id, time, title, post_text, view_count)
values (true, 'ACCEPTED', 1, 1, 2, '2020-12-16 17:00:00',
'Краткий обзор JavaServer Faces',
'Этот фреймворк был внедрен в индустрию компанией Oracle. JSF можно использовать для создания корпоративных приложений,
нативных программ, а также в web-разработке. Главная особенность заключается в возможности легко связывать уровень
представления с кодом приложения. Его API набор применяется для представления и управления компонентами UI.
JSF также имеет ясную архитектуру, которая различается между логикой приложения и его представлением.
Еще одним отличием является представление посредством XML, а не Java.',
112);

insert into tags(name) values ('JavaServer Faces');
insert into tags(name) values ('JSF');

insert into tag2post (post_id, tag_id) values (11, 18);
insert into tag2post (post_id, tag_id) values (11, 19);

insert into post_votes (user_id, post_id, time, value) values (1, 11, '2020-12-16 18:00:00', 1);
insert into post_votes (user_id, post_id, time, value) values (2, 11, '2020-12-16 21:05:10', 1);
insert into post_votes (user_id, post_id, time, value) values (3, 11, '2020-12-16 21:05:10', 1);
insert into post_votes (user_id, post_id, time, value) values (4, 11, '2020-12-16 21:06:10', 1);
insert into post_votes (user_id, post_id, time, value) values (5, 11, '2020-12-16 22:06:10', 1);

insert into posts (is_active, moderation_status, moderator_id, moderated_by, user_id, time, title, post_text, view_count)
values (true, 'ACCEPTED', 1, 1, 2, '2020-12-17 14:00:00',
'Краткий обзор Vaadin',
'Отличная платформа для упорядоченной разработки. Большим преимуществом этого фреймворка является плавное взаимодействие
между сервером и браузером. Vaadin предоставляет доступ к DOM напрямую из виртуальной машины Java.
В последнем релизе его разделили на две части и переименовали в Vaadin Flow.
Однако он по-прежнему является легковесным фреймворком, осуществляющим коммуникацию и маршрутизацию на стороне сервера.',
210);

insert into tags(name) values ('Vaadin');

insert into tag2post (post_id, tag_id) values (12, 20);

insert into post_votes (user_id, post_id, time, value) values (1, 12, '2020-12-17 18:00:00', 1);
insert into post_votes (user_id, post_id, time, value) values (2, 12, '2020-12-17 21:05:10', 1);
insert into post_votes (user_id, post_id, time, value) values (3, 12, '2020-12-17 21:05:10', -1);
insert into post_votes (user_id, post_id, time, value) values (4, 12, '2020-12-17 21:06:10', -1);
insert into post_votes (user_id, post_id, time, value) values (5, 12, '2020-12-17 22:06:10', 1);

insert into posts (is_active, moderation_status, moderator_id, moderated_by, user_id, time, title, post_text, view_count)
values (true, 'NEW', 2, 2, 3, '2020-12-18 13:20:00',
'Краткий обзор Vue.JS',
'<pre style="">Vue - один из самых популярных в настоящее время фронтенд фреймворков.
Он прост и понятен, в сравнении с<br>запудренным Angular. Кроме малых размеров, его основными преимуществами
являются визуальный DOM, компонентный<br>подход и двустороннее связывание данных. Vue универсален, многозадачен и
может легко обрабатывать как простые,<br>так и динамические процессы, от создания веб- и мобильных приложений до
прогрессивных веб-приложений.<br>Плюсы:<br>- обширная и подробная документация;<br>- простой синтаксис.
Программисты, знающие Javascript, легко разберутся с Vue.js;<br>- гибкость при разработке структуры приложения;
<br>- поддержка Typescript.<br>Минусы:<br>- нестабильность работы компонентов;<br>- относительно небольшое сообщество;
<br>- языковой барьер (большинство плагинов написано на китайском языке).<br>Когда использовать:
<br>Vue.js рекомендуется для гибких конструкций, позволяющих вам создавать все с нуля и успешно разрабатывать
большие проекты.<br>Когда не использовать:<br>Если вы надеетесь на помощь сообщества, Vue.js – не ваш выбор.
Кроме того, приложения, требующие стабильной работы<br>компонентов, не подходят для сборки с помощью Vue.js.</pre>',
0);

insert into tag2post (post_id, tag_id) values (13, 1);
insert into tag2post (post_id, tag_id) values (13, 2);

insert into posts (is_active, moderation_status, moderator_id, moderated_by, user_id, time, title, post_text, view_count)
values (true, 'NEW', 1, 1, 2, '2020-12-18 13:40:00',
'Краткий обзор REACT',
'React - один из простейших в изучении фреймворков - был разработан в Facebook для исправления проблем с поддержанием
кода из-за постоянного добавления функций в приложение. Современный React с открытым исходным кодом отличается своей
виртуальной объектной моделью документов (DOM), которая предлагает исключительные функциональные возможности. React –
идеальная платформа для тех, кто ожидает большой трафик и нуждается в стабильной платформе для его поддержания.
<div>&nbsp;<div>Плюсы:&nbsp;</div><div><ul><li>- возможность многократного использования компонентов, что упрощает их
совместную работу и позволяет повторно использовать в других частях приложения;&nbsp;</li><li>- стабильная и
бесперебойная работа с использованием виртуального DOM;&nbsp;</li><li>- возможность создания компонентов без написания
классов и что облегчает изучение React (лучшая альтернатива использованию хуков);&nbsp;</li><li>- продвинутые и
полезнейшие инструменты React Dev .&nbsp;</li></ul>Минусы:&nbsp;</div><div><ul><li>- проблемы с актуализацией
документации из-за многочисленных и постоянных обновлений, что затрудняет обучение начинающих;&nbsp;</li><li>-
разработчикам трудно разобраться со сложностями JSX, на начальном этапе работы с фреймворком;&nbsp;</li><li>-
подходит только для клиентских решений;&nbsp;</li></ul><span style="font-weight: bold;">Когда использовать:&nbsp;
</span></div><div>React используется для создания пользовательского интерфейса, особенно в одностраничных приложениях.
Это самый надежный фронтенд фреймворк, если вы хотите разработать интерактивный интерфейс с меньшими затратами времени,
ввиду возможности повторного использования компонентов.&nbsp;</div><div><span style="font-weight: bold;">Когда не
следует использовать:&nbsp;</span></div><div>Если у вас нет практического опыта работы с Javascript, React не
рекомендуется. Кроме того, для неопытных разработчиков возникнут трудности с освоением JSX.</div></div>',
0);

insert into tags(name) values ('React');

insert into tag2post (post_id, tag_id) values (14, 21);
insert into tag2post (post_id, tag_id) values (14, 2);


insert into posts (is_active, moderation_status, moderator_id, moderated_by, user_id, time, title, post_text, view_count)
values (true, 'NEW', 1, 1, 2, '2020-12-18 13:40:00',
'Краткий обзор jQuery',
'jQuery является одним из самых ранних фронтенд фреймворков, однако он актуален даже в современном мире технологий.
Помимо простоты и удобства JQuery отличает минимальная необходимость в обширных JavaScript-кодах.
За долгий период своего существования сформировалось большое сообщество jQuery.&nbsp;<div><br><div>Плюсы:&nbsp;
</div><div><ul><li>- гибкость при добавлении или удалении элементов DOM;&nbsp;</li><li>- упрощенные HTTP-запросы и
их отправка;&nbsp;</li><li>- облегченная динамическая подгрузка контента.&nbsp;</li></ul>Минусы:&nbsp;</div><div><ul>
<li>- сравнительно медленная работоспособность;&nbsp;</li><li>- помимо jQuery доступно много продвинутых альтернатив;
&nbsp;</li><li>- устаревшие API объектной модели документа.&nbsp;</li></ul><span style="font-weight: bold;">Когда
использовать:&nbsp;</span></div><div>jQuery используется для создания десктопных приложений на Javascript, при
обработке событий и реализации анимационных эффектов. Фреймворк делает код лаконичным и простым.&nbsp;</div><div>
<span style="font-weight: bold;">Когда не использовать:</span>&nbsp;</div><div>jQuery не подойдет для разработки
крупномасштабного приложения, так как оно получится слишком тяжелым из-за большого количества дополнительного
Javascript-кода. jQuery – не способен конкурировать с современными фреймворками с упрощенным использованием
JavaScript, меньшим количеством кода и возможностью повторного использования компонентов.</div></div>',
0);

insert into tags(name) values ('JQuery');

insert into tag2post (post_id, tag_id) values (15, 22);
insert into tag2post (post_id, tag_id) values (15, 2);


