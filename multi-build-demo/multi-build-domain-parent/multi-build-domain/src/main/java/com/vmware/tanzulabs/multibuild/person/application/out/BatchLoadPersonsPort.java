package com.vmware.tanzulabs.multibuild.person.application.out;

import com.vmware.tanzulabs.multibuild.person.domain.Person;

public interface BatchLoadPersonsPort {

    long loadPersons( final Person... persons );

}
