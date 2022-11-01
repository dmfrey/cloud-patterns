package com.vmware.tanzulabs.multibuild.person.adapter.in.messaging;

import com.vmware.tanzulabs.multibuild.person.application.out.FindPersonByNamePort;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.stream.binder.test.InputDestination;
import org.springframework.cloud.stream.binder.test.TestChannelBinderConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.messaging.support.MessageBuilder;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest( properties = {
        "--spring.cloud.function.definition=loadPersons",
        "--spring.cloud.stream.bindings.loadPersons-in-0.destination='test-topic'"
})
@Import( TestChannelBinderConfiguration.class )
public class PersonLoaderConfigTests {

    @Autowired
    InputDestination inputDestination;

    @Autowired
    FindPersonByNamePort findPersonByNamePort;

    String firstName = "Marty";
    String lastName = "McFly";
    String email = "";

    String address1 = "9303 Lyon Drive";
    String address2 = "Lyon Estates";
    String city = "Hill Valley";
    String state = "CA";
    String postalCode = "95420";

    @Test
    void whenPersonMessageReceived_verifyPersonLoaded() {

        var fakeAddressMessage = new AddressMessage( address1, address2, city, state, postalCode );
        var fakePersonMessage = new PersonMessage( firstName, lastName, email, fakeAddressMessage );

        inputDestination.send(
                MessageBuilder
                        .withPayload( fakePersonMessage )
                        .build()
        );

        var found = findPersonByNamePort.findByName( firstName, lastName );
        assertThat( found.firstName() ).isEqualTo( firstName );
        assertThat( found.lastName() ).isEqualTo( lastName );
        assertThat( found.email() ).isEqualTo( email );


    }

    @SpringBootApplication( scanBasePackages = { "com.vmware.tanzulabs.multibuild.person" } )
    static class ApplicationTestConfiguration { }

}
