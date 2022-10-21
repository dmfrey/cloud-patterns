insert into ADDRESS (address_id, address_one, address_two, city, state_abbr, postal_code)
    values ('a979cdd3-19e2-4043-8ee6-2fc27f9584ab', '9303 Lyon Drive', 'Lyon Estates', 'Hill Valley', 'CA', '95420');

insert into PERSON (person_id, first_name, last_name, email_address, birth_date, address_id)
    values ('87985a0c-f39c-40a9-9f66-136a0f36570f', 'Marty', 'McFly', '', '1968-06-12', 'a979cdd3-19e2-4043-8ee6-2fc27f9584ab');