package com.vmware.tanzulabs.patterns.person.adapter.in;

import com.vmware.tanzulabs.patterns.person.application.PersonService;
import com.vmware.tanzulabs.patterns.person.domain.Address;
import com.vmware.tanzulabs.patterns.person.domain.Person;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.function.Consumer;

@Configuration
class PersonListener {

    private static final Logger log = LoggerFactory.getLogger( PersonListener.class );

    private final PersonService service;

    PersonListener( final PersonService service ) {

        this.service = service;

    }

    @Bean
    Consumer<PersonMessage> person() {

        return message -> {

            log.info( "PersonMessage : {}", message );

            this.service.savePerson(
                    new Person(null, message.firstName(), message.lastName(), message.email(),
                            new Address(null, message.address().address1(), message.address().address2(), message.address().city(), message.address().state(), message.address().postalCode())
                    )
            );
        };
    }

}


