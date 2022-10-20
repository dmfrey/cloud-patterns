drop table if exists address CASCADE;
drop table if exists person CASCADE;

create table address (address_id VARCHAR(40) not null, address_one varchar(255) not null, address_two varchar(255), city varchar(255) not null, postal_code VARCHAR(5) not null, state_abbr VARCHAR(2) not null, primary key (address_id));
create table person (person_id VARCHAR(40) not null, email_address varchar(255) not null, first_name varchar(255) not null, last_name varchar(255) not null, address_id VARCHAR(40), primary key (person_id));

alter table person add constraint person_address_fk foreign key (address_id) references address;
