insert into users (is_moderator, reg_time, name, email, password, photo, photo_public_id)
    values (true, CURRENT_TIMESTAMP, 'Alex Ivanov', 'alex@alex.com',
        '$2y$10$MTCPaqvZmGrufdnrsNXbnuSFPwytXot7ja.p0kwFoDeIcGnIFucQ.',
        'https://res.cloudinary.com/hg7rd5cd5/image/upload/v1608468983/mod1_x1ypwm.jpg', 'mod1_x1ypwm');

insert into users (is_moderator, reg_time, name, email, password, photo, photo_public_id)
values (true, CURRENT_TIMESTAMP, 'Artem Smirnov', 'artem@artem.com',
        '$2y$10$MTCPaqvZmGrufdnrsNXbnuSFPwytXot7ja.p0kwFoDeIcGnIFucQ.',
        'https://res.cloudinary.com/hg7rd5cd5/image/upload/v1608468983/user1_aohtfv.jpg', 'user1_aohtfv');

insert into users (is_moderator, reg_time, name, email, password, photo, photo_public_id)
values (false, CURRENT_TIMESTAMP, 'Vlad Popov', 'vlad@vlad.com',
        '$2y$10$MTCPaqvZmGrufdnrsNXbnuSFPwytXot7ja.p0kwFoDeIcGnIFucQ.',
        'https://res.cloudinary.com/hg7rd5cd5/image/upload/v1608468983/1438951906382231414_addoew.png', '1438951906382231414_addoew');

insert into users (is_moderator, reg_time, name, email, password, photo, photo_public_id)
values (false, CURRENT_TIMESTAMP, 'Sergey Vasiliev', 'serg@serg.com',
        '$2y$10$MTCPaqvZmGrufdnrsNXbnuSFPwytXot7ja.p0kwFoDeIcGnIFucQ.',
        'https://res.cloudinary.com/hg7rd5cd5/image/upload/v1608468983/user2_byqdzn.png', 'user2_byqdzn');

insert into users (is_moderator, reg_time, name, email, password, photo, photo_public_id)
values (false, CURRENT_TIMESTAMP, 'Egor Novikov', 'egor@egor.com',
        '$2y$10$MTCPaqvZmGrufdnrsNXbnuSFPwytXot7ja.p0kwFoDeIcGnIFucQ.',
        'https://res.cloudinary.com/hg7rd5cd5/image/upload/v1608468983/user3_qqootp.jpg', 'user3_qqootp');

insert into users_roles (user_id, role_id) values (1, 2);
insert into users_roles (user_id, role_id) values (2, 2);
insert into users_roles (user_id, role_id) values (3, 1);
insert into users_roles (user_id, role_id) values (4, 1);
insert into users_roles (user_id, role_id) values (5, 1);