create table if not exists `configs`(
    `app` varchar(64) not null,
    `env` varchar(64) not null,
    `ns` varchar(64) not null,
    `pkey` varchar(64) not null,
    `pval` varchar(128)  null

);

insert into configs (app, env, ns,pkey, pval)
values ('app1','dev','public','ub.a', 'dev-server100');

insert into configs (app, env, ns,pkey, pval)
values ('app1','dev','public','ub.b', 'http://localhost:9192');

insert into configs (app, env, ns,pkey, pval)
values ('app1','dev','public','ub.c', 'cc100');


create table if not exists `lock`(
    `id` int primary key ,
    `app` varchar(64) not null
);

insert into lock (id, app)
values (1,'ubconfig-server');