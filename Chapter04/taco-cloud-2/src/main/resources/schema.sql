drop table if exists authorities;
drop table if exists users;
drop index if exists ix_auth_username;

create table if not exists users(
    username varchar2(50) not null primary key,
    password varchar2(100) not null,
    enabled boolean default true);

create table if not exists authorities (
    username varchar2(50) not null,
    authority varchar2(50) not null,
    constraint fk_authorities_users foreign key(username) references users(username));

create unique index ix_auth_username on authorities (username, authority);