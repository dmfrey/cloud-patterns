package com.vmware.tanzulabs.patterns.person.adapter.in;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vmware.tanzulabs.patterns.person.adapter.out.PersonEventMessage;
import com.vmware.tanzulabs.patterns.person.adapter.out.PersonEventProducer;
import com.vmware.tanzulabs.patterns.person.adapter.out.PersonEventType;
import com.vmware.tanzulabs.patterns.person.application.PersonService;
import com.vmware.tanzulabs.patterns.util.UuidGenerator;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cloud.stream.binder.test.InputDestination;
import org.springframework.cloud.stream.binder.test.OutputDestination;
import org.springframework.cloud.stream.binder.test.TestChannelBinderConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.test.annotation.DirtiesContext;

import java.io.IOException;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@SpringBootTest
@DirtiesContext
@Tag( "UnitTest" )
class PersonListenerTests {

    @Autowired
    InputDestination inputDestination;

    @Autowired
    OutputDestination outputDestination;

    @Autowired
    ObjectMapper mapper;

    @MockBean
    UuidGenerator mockUuidGenerator;

    UUID personId = UUID.randomUUID();
    String firstName = "Marty";
    String lastName = "McFly";
    String email = "";

    UUID addressId = UUID.randomUUID();
    String address1 = "9303 Lyon Drive";
    String address2 = "Lyon Estates";
    String city = "Hill Valley";
    String state = "CA";
    String postalCode = "95420";

    @Test
    void givenPersonMessageSent_verifyPersonMessageReceived() throws IOException {

        when( this.mockUuidGenerator.generate() ).thenReturn( personId, addressId );

        String fakeMessage = String.format( "{\"firstName\":\"%s\",\"lastName\":\"%s\",\"email\":\"%s\",\"address\":{\"address1\":\"%s\",\"address2\":\"%s\",\"city\":\"%s\",\"state\":\"%s\",\"postalCode\":\"%s\"}}", firstName, lastName, email, address1, address2, city, state, postalCode );
        inputDestination.send( MessageBuilder.withPayload( fakeMessage ).build() );

        var received = outputDestination.receive( 10_000 );
        var actual = mapper.readValue( received.getPayload(), PersonEventMessage.class );

        var expected = new PersonEventMessage( personId, PersonEventType.Created, "person created" );
        assertThat( actual ).isEqualTo( expected );

        verify( this.mockUuidGenerator, times( 2 ) ).generate();
        verifyNoMoreInteractions( this.mockUuidGenerator );

    }

    @SpringBootApplication
    @Import({ TestChannelBinderConfiguration.class, PersonService.class, PersonEventProducer.class })
    static class EmbeddedTestConfiguration { }

}
