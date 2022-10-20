package com.vmware.tanzulabs.patterns.person.out;

import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

interface PersonRepository extends CrudRepository<PersonEntity, UUID> {
}
