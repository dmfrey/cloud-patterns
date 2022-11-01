package com.vmware.tanzulabs.multibuild.person.application.in;

import com.vmware.tanzulabs.multibuild.person.domain.Person;

public interface LoadPersonsUseCase {

    long execute( LoadPersonsCommand command );

    record LoadPersonsCommand( Person... persons ) {}

}
