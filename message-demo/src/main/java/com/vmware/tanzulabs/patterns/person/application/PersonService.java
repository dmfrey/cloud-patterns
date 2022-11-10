package com.vmware.tanzulabs.patterns.person.application;

import com.vmware.tanzulabs.patterns.person.adapter.out.PersonEventProducer;
import com.vmware.tanzulabs.patterns.person.adapter.out.PersonEventType;
import com.vmware.tanzulabs.patterns.person.domain.Address;
import com.vmware.tanzulabs.patterns.person.domain.Person;
import com.vmware.tanzulabs.patterns.util.UuidGenerator;
import org.springframework.stereotype.Component;

@Component
public class PersonService {

    private final PersonEventProducer personEventProducer;
    private final UuidGenerator uuidGenerator;

    PersonService( final PersonEventProducer personEventProducer, final UuidGenerator uuidGenerator ) {

        this.personEventProducer = personEventProducer;
        this.uuidGenerator = uuidGenerator;

    }
    public void savePerson( Person person ) {

        var personId = this.uuidGenerator.generate();
        var addressId = this.uuidGenerator.generate();
        var created = new Person( personId, person.firstName(), person.lastName(), person.email(), new Address( addressId, person.address().address1(), person.address().address2(), person.address().city(), person.address().state(), person.address().postalCode() ) );

        this.personEventProducer.sendPersonEvent( created, PersonEventType.Created, "person created" );

    }

}
