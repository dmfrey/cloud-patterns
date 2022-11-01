package com.vmware.tanzulabs.multibuild.person.application.out;

import com.vmware.tanzulabs.multibuild.person.domain.Person;

public interface FindPersonByNamePort {

    Person findByName( final String firstName, final String lastName );

}
