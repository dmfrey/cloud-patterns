package com.vmware.tanzulabs.multibuild.person.adapter.in.messaging;

import com.vmware.tanzulabs.multibuild.person.application.in.LoadPersonsUseCase;
import com.vmware.tanzulabs.multibuild.person.domain.Address;
import com.vmware.tanzulabs.multibuild.person.domain.Person;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.function.Consumer;

@Configuration
public class PersonLoaderConfig {

    @Bean
    @ConditionalOnMissingBean
    Consumer<PersonMessage> loadPersonsConsumer( final LoadPersonsUseCase useCase ) {

        return personMessage -> useCase.execute( new LoadPersonsUseCase.LoadPersonsCommand( mapPersonMessage( personMessage ) ) );
    }

    private Address mapAddressMessage( final AddressMessage addressMessage ) {

        if( null == addressMessage ) {

            return null;
        }

        return new Address( null, addressMessage.address1(), addressMessage.address2(), addressMessage.city(), addressMessage.state(), addressMessage.postalCode() );
    }

    private Person mapPersonMessage( final PersonMessage personMessage ) {

        return new Person( null, personMessage.firstName(), personMessage.lastName(), personMessage.email(), mapAddressMessage( personMessage.address() ) );
    }

}

record AddressMessage( String address1, String address2, String city, String state, String postalCode ) { }

record PersonMessage( String firstName, String lastName, String email, AddressMessage address ) { }