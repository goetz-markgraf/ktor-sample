create table if not exists articles
(
    id        bigserial primary key,
    published boolean       not null,
    name      varchar(128)  not null,
    content   varchar(1000) not null
);

insert into articles(published, name, content)
values (false, 'Article 1', 'Content');

create table if not exists session
(
    id      varchar not null primary key,
    value   varchar not null,
    expires bigint  not null
);


commit;
