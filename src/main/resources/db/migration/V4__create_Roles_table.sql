create table roles (
    id int8 generated by default as identity,
    role_name varchar(255) not null,
    primary key (id)
);

insert into roles(role_name) values ('ROLE_ADMIN');
insert into roles(role_name) values ('ROLE_USER');
insert into roles(role_name) values ('ROLE_MODERATOR');

