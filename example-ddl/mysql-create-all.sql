create table o_address (
  id                            bigint auto_increment not null,
  line1                         varchar(100),
  line2                         varchar(100),
  city                          varchar(100),
  country_code                  varchar(2),
  version                       bigint not null,
  when_created                  datetime(6) not null,
  when_updated                  datetime(6) not null,
  constraint pk_o_address primary key (id)
);

create table contact (
  id                            bigint auto_increment not null,
  customer_id                   bigint not null,
  first_name                    varchar(50),
  last_name                     varchar(50),
  email                         varchar(200),
  phone                         varchar(20),
  version                       bigint not null,
  when_created                  datetime(6) not null,
  when_updated                  datetime(6) not null,
  constraint pk_contact primary key (id)
);

create table xl_contact (
  id                            bigint auto_increment not null,
  customer_id                   bigint not null,
  first_name                    varchar(50),
  last_name                     varchar(50),
  email                         varchar(200),
  phone                         varchar(20),
  version                       bigint not null,
  when_created                  datetime(6) not null,
  when_updated                  datetime(6) not null,
  constraint pk_xl_contact primary key (id)
);

create table country (
  code                          varchar(2) not null,
  name                          varchar(60),
  constraint pk_country primary key (code)
);

create table customer (
  id                            bigint auto_increment not null,
  name                          varchar(100),
  registered                    date,
  comments                      varchar(1000),
  billing_address_id            bigint,
  shipping_address_id           bigint,
  version                       bigint not null,
  when_created                  datetime(6) not null,
  when_updated                  datetime(6) not null,
  constraint pk_customer primary key (id)
);

create table xl_customer (
  id                            bigint auto_increment not null,
  name                          varchar(100),
  registered                    date,
  comments                      varchar(1000),
  billing_address_id            bigint,
  shipping_address_id           bigint,
  version                       bigint not null,
  when_created                  datetime(6) not null,
  when_updated                  datetime(6) not null,
  constraint pk_xl_customer primary key (id)
);

create table feature (
  id                            bigint auto_increment not null,
  name                          varchar(60),
  notes                         varchar(255),
  version                       bigint not null,
  when_created                  datetime(6) not null,
  when_updated                  datetime(6) not null,
  constraint pk_feature primary key (id)
);

create table customer_feature (
  feature_id                    bigint not null,
  customer_id                   bigint not null,
  constraint pk_customer_feature primary key (feature_id,customer_id)
);

create table xl_feature (
  id                            bigint auto_increment not null,
  name                          varchar(60),
  version                       bigint not null,
  when_created                  datetime(6) not null,
  when_updated                  datetime(6) not null,
  constraint pk_xl_feature primary key (id)
);

create table xl_customer_feature (
  xl_feature_id                 bigint not null,
  xl_customer_id                bigint not null,
  constraint pk_xl_customer_feature primary key (xl_feature_id,xl_customer_id)
);

create table product (
  id                            bigint auto_increment not null,
  sku                           varchar(20),
  name                          varchar(255),
  version                       bigint not null,
  when_created                  datetime(6) not null,
  when_updated                  datetime(6) not null,
  constraint pk_product primary key (id)
);

alter table o_address add constraint fk_o_address_country_code foreign key (country_code) references country (code) on delete restrict on update restrict;
create index ix_o_address_country_code on o_address (country_code);

alter table contact add constraint fk_contact_customer_id foreign key (customer_id) references customer (id) on delete restrict on update restrict;
create index ix_contact_customer_id on contact (customer_id);

alter table xl_contact add constraint fk_xl_contact_customer_id foreign key (customer_id) references xl_contact (id) on delete restrict on update restrict;
create index ix_xl_contact_customer_id on xl_contact (customer_id);

alter table customer add constraint fk_customer_billing_address_id foreign key (billing_address_id) references o_address (id) on delete restrict on update restrict;
create index ix_customer_billing_address_id on customer (billing_address_id);

alter table customer add constraint fk_customer_shipping_address_id foreign key (shipping_address_id) references o_address (id) on delete restrict on update restrict;
create index ix_customer_shipping_address_id on customer (shipping_address_id);

alter table xl_customer add constraint fk_xl_customer_billing_address_id foreign key (billing_address_id) references o_address (id) on delete restrict on update restrict;
create index ix_xl_customer_billing_address_id on xl_customer (billing_address_id);

alter table xl_customer add constraint fk_xl_customer_shipping_address_id foreign key (shipping_address_id) references o_address (id) on delete restrict on update restrict;
create index ix_xl_customer_shipping_address_id on xl_customer (shipping_address_id);

alter table customer_feature add constraint fk_customer_feature_feature foreign key (feature_id) references feature (id) on delete restrict on update restrict;
create index ix_customer_feature_feature on customer_feature (feature_id);

alter table customer_feature add constraint fk_customer_feature_customer foreign key (customer_id) references customer (id) on delete restrict on update restrict;
create index ix_customer_feature_customer on customer_feature (customer_id);

alter table xl_customer_feature add constraint fk_xl_customer_feature_xl_feature foreign key (xl_feature_id) references xl_feature (id) on delete restrict on update restrict;
create index ix_xl_customer_feature_xl_feature on xl_customer_feature (xl_feature_id);

alter table xl_customer_feature add constraint fk_xl_customer_feature_xl_customer foreign key (xl_customer_id) references xl_customer (id) on delete restrict on update restrict;
create index ix_xl_customer_feature_xl_customer on xl_customer_feature (xl_customer_id);

alter table o_address add column sys_period_start datetime(6) default now(6);
alter table o_address add column sys_period_end datetime(6);
update o_address set sys_period_start = when_created;
create table o_address_history(
  id                            bigint,
  line1                         varchar(100),
  line2                         varchar(100),
  city                          varchar(100),
  country_code                  varchar(2),
  version                       bigint,
  when_created                  datetime(6),
  when_updated                  datetime(6),
  sys_period_start              datetime(6),
  sys_period_end                datetime(6)
);
create view o_address_with_history as select * from o_address union all select * from o_address_history;

delimiter $$
create trigger o_address_history_upd before update on o_address for each row begin
    insert into o_address_history (sys_period_start,sys_period_end,id, line1, line2, city, country_code, version, when_created, when_updated) values (OLD.sys_period_start, now(6),OLD.id, OLD.line1, OLD.line2, OLD.city, OLD.country_code, OLD.version, OLD.when_created, OLD.when_updated);
    set NEW.sys_period_start = now(6);
end$$
delimiter $$
create trigger o_address_history_del before delete on o_address for each row begin
    insert into o_address_history (sys_period_start,sys_period_end,id, line1, line2, city, country_code, version, when_created, when_updated) values (OLD.sys_period_start, now(6),OLD.id, OLD.line1, OLD.line2, OLD.city, OLD.country_code, OLD.version, OLD.when_created, OLD.when_updated);
end$$
alter table contact add column sys_period_start datetime(6) default now(6);
alter table contact add column sys_period_end datetime(6);
update contact set sys_period_start = when_created;
create table contact_history(
  id                            bigint,
  customer_id                   bigint,
  first_name                    varchar(50),
  last_name                     varchar(50),
  email                         varchar(200),
  phone                         varchar(20),
  version                       bigint,
  when_created                  datetime(6),
  when_updated                  datetime(6),
  sys_period_start              datetime(6),
  sys_period_end                datetime(6)
);
create view contact_with_history as select * from contact union all select * from contact_history;

delimiter $$
create trigger contact_history_upd before update on contact for each row begin
    insert into contact_history (sys_period_start,sys_period_end,id, customer_id, first_name, last_name, email, phone, version, when_created, when_updated) values (OLD.sys_period_start, now(6),OLD.id, OLD.customer_id, OLD.first_name, OLD.last_name, OLD.email, OLD.phone, OLD.version, OLD.when_created, OLD.when_updated);
    set NEW.sys_period_start = now(6);
end$$
delimiter $$
create trigger contact_history_del before delete on contact for each row begin
    insert into contact_history (sys_period_start,sys_period_end,id, customer_id, first_name, last_name, email, phone, version, when_created, when_updated) values (OLD.sys_period_start, now(6),OLD.id, OLD.customer_id, OLD.first_name, OLD.last_name, OLD.email, OLD.phone, OLD.version, OLD.when_created, OLD.when_updated);
end$$
alter table xl_contact add column sys_period_start datetime(6) default now(6);
alter table xl_contact add column sys_period_end datetime(6);
update xl_contact set sys_period_start = when_created;
create table xl_contact_history(
  id                            bigint,
  customer_id                   bigint,
  first_name                    varchar(50),
  last_name                     varchar(50),
  email                         varchar(200),
  phone                         varchar(20),
  version                       bigint,
  when_created                  datetime(6),
  when_updated                  datetime(6),
  sys_period_start              datetime(6),
  sys_period_end                datetime(6)
);
create view xl_contact_with_history as select * from xl_contact union all select * from xl_contact_history;

delimiter $$
create trigger xl_contact_history_upd before update on xl_contact for each row begin
    insert into xl_contact_history (sys_period_start,sys_period_end,id, customer_id, first_name, last_name, email, phone, version, when_created, when_updated) values (OLD.sys_period_start, now(6),OLD.id, OLD.customer_id, OLD.first_name, OLD.last_name, OLD.email, OLD.phone, OLD.version, OLD.when_created, OLD.when_updated);
    set NEW.sys_period_start = now(6);
end$$
delimiter $$
create trigger xl_contact_history_del before delete on xl_contact for each row begin
    insert into xl_contact_history (sys_period_start,sys_period_end,id, customer_id, first_name, last_name, email, phone, version, when_created, when_updated) values (OLD.sys_period_start, now(6),OLD.id, OLD.customer_id, OLD.first_name, OLD.last_name, OLD.email, OLD.phone, OLD.version, OLD.when_created, OLD.when_updated);
end$$
alter table customer add column sys_period_start datetime(6) default now(6);
alter table customer add column sys_period_end datetime(6);
update customer set sys_period_start = when_created;
create table customer_history(
  id                            bigint,
  name                          varchar(100),
  registered                    date,
  comments                      varchar(1000),
  billing_address_id            bigint,
  shipping_address_id           bigint,
  version                       bigint,
  when_created                  datetime(6),
  when_updated                  datetime(6),
  sys_period_start              datetime(6),
  sys_period_end                datetime(6)
);
create view customer_with_history as select * from customer union all select * from customer_history;

delimiter $$
create trigger customer_history_upd before update on customer for each row begin
    insert into customer_history (sys_period_start,sys_period_end,id, name, registered, comments, billing_address_id, shipping_address_id, version, when_created, when_updated) values (OLD.sys_period_start, now(6),OLD.id, OLD.name, OLD.registered, OLD.comments, OLD.billing_address_id, OLD.shipping_address_id, OLD.version, OLD.when_created, OLD.when_updated);
    set NEW.sys_period_start = now(6);
end$$
delimiter $$
create trigger customer_history_del before delete on customer for each row begin
    insert into customer_history (sys_period_start,sys_period_end,id, name, registered, comments, billing_address_id, shipping_address_id, version, when_created, when_updated) values (OLD.sys_period_start, now(6),OLD.id, OLD.name, OLD.registered, OLD.comments, OLD.billing_address_id, OLD.shipping_address_id, OLD.version, OLD.when_created, OLD.when_updated);
end$$
alter table xl_customer add column sys_period_start datetime(6) default now(6);
alter table xl_customer add column sys_period_end datetime(6);
update xl_customer set sys_period_start = when_created;
create table xl_customer_history(
  id                            bigint,
  name                          varchar(100),
  registered                    date,
  comments                      varchar(1000),
  billing_address_id            bigint,
  shipping_address_id           bigint,
  version                       bigint,
  when_created                  datetime(6),
  when_updated                  datetime(6),
  sys_period_start              datetime(6),
  sys_period_end                datetime(6)
);
create view xl_customer_with_history as select * from xl_customer union all select * from xl_customer_history;

delimiter $$
create trigger xl_customer_history_upd before update on xl_customer for each row begin
    insert into xl_customer_history (sys_period_start,sys_period_end,id, name, registered, comments, billing_address_id, shipping_address_id, version, when_created, when_updated) values (OLD.sys_period_start, now(6),OLD.id, OLD.name, OLD.registered, OLD.comments, OLD.billing_address_id, OLD.shipping_address_id, OLD.version, OLD.when_created, OLD.when_updated);
    set NEW.sys_period_start = now(6);
end$$
delimiter $$
create trigger xl_customer_history_del before delete on xl_customer for each row begin
    insert into xl_customer_history (sys_period_start,sys_period_end,id, name, registered, comments, billing_address_id, shipping_address_id, version, when_created, when_updated) values (OLD.sys_period_start, now(6),OLD.id, OLD.name, OLD.registered, OLD.comments, OLD.billing_address_id, OLD.shipping_address_id, OLD.version, OLD.when_created, OLD.when_updated);
end$$
