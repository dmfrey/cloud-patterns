package com.vmware.tanzulabs.multibuild.person.adapter.out.persistence;

import org.springframework.data.repository.CrudRepository;

import java.util.Optional;
import java.util.UUID;

interface PersonRepository extends CrudRepository<PersonEntity, UUID> {

    Optional<PersonEntity> findByFirstNameAndLastName( String firstName, String lastName );

}
