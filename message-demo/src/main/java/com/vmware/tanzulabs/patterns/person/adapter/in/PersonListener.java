package com.vmware.tanzulabs.patterns.person.adapter.in;

import com.vmware.tanzulabs.patterns.person.application.PersonService;
import com.vmware.tanzulabs.patterns.person.domain.Address;
import com.vmware.tanzulabs.patterns.person.domain.Person;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
class PersonListener {

    private final PersonService service;

    PersonListener( final PersonService service ) {

        this.service = service;

    }

    @KafkaListener( topics = "${topics.person}" )
    void receivePersonMessage( PersonMessage message ) {

        var person = new Person( null, message.firstName(), message.lastName(), message.email(), new Address( null, message.address().address1(), message.address().address2(), message.address().city(), message.address().state(), message.address().postalCode() ) );
        this.service.savePerson( person );

    }

}


