package com.vmware.tanzulabs.multibuild.person.application.out;

import com.vmware.tanzulabs.multibuild.person.domain.Person;

import java.util.UUID;

public interface FindPersonByIdPort {

    Person findById( final UUID id );

}
