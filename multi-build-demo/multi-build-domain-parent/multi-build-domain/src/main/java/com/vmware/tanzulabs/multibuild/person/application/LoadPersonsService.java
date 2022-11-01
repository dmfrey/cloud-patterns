package com.vmware.tanzulabs.multibuild.person.application;

import com.vmware.tanzulabs.multibuild.person.application.in.LoadPersonsUseCase;
import com.vmware.tanzulabs.multibuild.person.application.out.BatchLoadPersonsPort;
import org.springframework.stereotype.Component;

@Component
class LoadPersonsService implements LoadPersonsUseCase {

    private final BatchLoadPersonsPort batchLoadPersonsPort;

    LoadPersonsService( final BatchLoadPersonsPort batchLoadPersonsPort ) {

        this.batchLoadPersonsPort = batchLoadPersonsPort;

    }

    @Override
    public long execute( final LoadPersonsCommand command ) {

        return this.batchLoadPersonsPort.loadPersons( command.persons() );
    }

}
