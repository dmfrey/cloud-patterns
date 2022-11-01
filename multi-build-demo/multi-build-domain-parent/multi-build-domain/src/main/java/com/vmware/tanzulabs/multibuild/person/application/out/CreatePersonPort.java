package com.vmware.tanzulabs.multibuild.person.application.out;

import com.vmware.tanzulabs.multibuild.person.domain.Person;

public interface CreatePersonPort {

    Person createPerson( final Person person );

}
