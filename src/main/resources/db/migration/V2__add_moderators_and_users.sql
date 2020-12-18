insert into users (id, is_moderator, reg_time, name, email, password, photo)
    values (1, true, CURRENT_TIMESTAMP, 'Alex Ivanov', 'alex@alex.com',
        '$2y$10$MTCPaqvZmGrufdnrsNXbnuSFPwytXot7ja.p0kwFoDeIcGnIFucQ.',
        'https://res.cloudinary.com/hg7rd5cd5/image/upload/v1608149015/arni_1_wyq2s8.jpg');

insert into users (id, is_moderator, reg_time, name, email, password, photo)
values (2, true, CURRENT_TIMESTAMP, 'Artem Smirnov', 'artem@artem.com',
        '$2y$10$MTCPaqvZmGrufdnrsNXbnuSFPwytXot7ja.p0kwFoDeIcGnIFucQ.',
        'https://res.cloudinary.com/hg7rd5cd5/image/upload/v1608149518/mod1_j45wb9.jpg');

insert into users (id, is_moderator, reg_time, name, email, password, photo)
values (3, false, CURRENT_TIMESTAMP, 'Vlad Popov', 'vlad@vlad.com',
        '$2y$10$MTCPaqvZmGrufdnrsNXbnuSFPwytXot7ja.p0kwFoDeIcGnIFucQ.',
        'https://res.cloudinary.com/hg7rd5cd5/image/upload/v1608150078/user1_aql8v4.jpg');

insert into users (id, is_moderator, reg_time, name, email, password, photo)
values (4, false, CURRENT_TIMESTAMP, 'Sergey Vasiliev', 'serg@serg.com',
        '$2y$10$MTCPaqvZmGrufdnrsNXbnuSFPwytXot7ja.p0kwFoDeIcGnIFucQ.',
        'https://res.cloudinary.com/hg7rd5cd5/image/upload/v1608150078/user2_ylcljj.png');

insert into users (id, is_moderator, reg_time, name, email, password, photo)
values (5, false, CURRENT_TIMESTAMP, 'Egor Novikov', 'egor@egor.com',
        '$2y$10$MTCPaqvZmGrufdnrsNXbnuSFPwytXot7ja.p0kwFoDeIcGnIFucQ.',
        'https://res.cloudinary.com/hg7rd5cd5/image/upload/v1608150078/user3_oatdqp.jpg');

insert into users_roles (user_id, role_id) values (1, 3);
insert into users_roles (user_id, role_id) values (2, 3);
insert into users_roles (user_id, role_id) values (3, 2);
insert into users_roles (user_id, role_id) values (4, 2);
insert into users_roles (user_id, role_id) values (5, 2);