
create type setting_value as enum ('YES', 'NO');

create table global_settings (
    id int8 generated by default as identity,
    code varchar(255) not null,
    name varchar(255) not null,
    value setting_value not null,
    primary key (id)
);

insert into global_settings(code, name, value)
    values ('MULTIUSER_MODE', 'Многопользовательский режим', 'YES'),
    ('POST_PREMODERATION', 'Премодерация постов', 'YES'),
    ('STATISTICS_IS_PUBLIC', 'Показывать всем статистику блога', 'YES');

create table captcha_codes (
    id int8 generated by default as identity,
    time timestamp with time zone not null,
    code varchar(255) not null,
    secret_code varchar(255) not null,
    primary key (id)
);

create table users (
    id int8 generated by default as identity,
    is_moderator boolean not null,
    reg_time timestamp with time zone not null,
    name varchar(255) not null,
    email varchar(255) not null,
    password varchar(255) not null,
    code varchar(255) null,
    photo varchar(255) null,
    primary key (id)
);

create table roles (
    id int8 generated by default as identity,
    role_name varchar(255) not null,
    primary key (id)
);

insert into roles(role_name) values ('ROLE_ADMIN');
insert into roles(role_name) values ('ROLE_USER');
insert into roles(role_name) values ('ROLE_MODERATOR');

create table users_roles (
    id int8 generated by default as identity,
    user_id int8 not null,
    role_id int8 not null,
    primary key (user_id, role_id),
    constraint fk_users foreign key (user_id) references users (id),
    constraint fk_roles foreign key (role_id) references roles (id)
);

create type moderation_status AS ENUM ('NEW', 'ACCEPTED', 'DECLINED');
create table posts (
    id int8 generated by default as identity,
    is_active boolean not null,
    moderation_status moderation_status,
    moderator_id int8 null,
    user_id int8 not null,
    time timestamp with time zone not null,
    title varchar(255) not null,
    post_text text not null,
    view_count int4 not null,
    primary key (id),
    constraint fk_moderators foreign key (moderator_id) references users (id),
    constraint fk_users foreign key (user_id) references users (id)
);

create table post_votes (
    id int8 generated by default as identity,
    is_active boolean not null,
    user_id int8 not null,
    post_id int8 not null,
    time timestamp with time zone not null,
    value int4 not null,
    primary key (id),
    constraint fk_users foreign key (user_id) references users (id),
    constraint fk_posts foreign key (post_id) references posts (id)
);

create table tags (
    id int8 generated by default as identity,
    name varchar(255) not null,
    primary key (id)
);

create table tag2post (
    id int8 generated by default as identity,
    post_id int8 not null,
    tag_id int8 not null,
    primary key (id),
    constraint fk_posts foreign key (post_id) references posts (id),
    constraint fk_tags foreign key (tag_id) references tags (id)
);

create table post_comments (
    id int8 generated by default as identity,
    parent_id int8,
    post_id int8 not null,
    user_id int8 not null,
    comment_time timestamp with time zone,
    comment_text text not null,
    primary key (id),
    constraint fk_parent_child_comments foreign key (parent_id) references post_comments (id),
    constraint fk_post_comments foreign key (post_id) references posts (id),
    constraint fk_users foreign key (user_id) references users (id)
);


