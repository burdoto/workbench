create table if not exists users
(
    username     varchar(255) not null
        primary key,
    display_name varchar(255) null
);

insert into users (username, display_name)
values ('dev', 'Developer');
