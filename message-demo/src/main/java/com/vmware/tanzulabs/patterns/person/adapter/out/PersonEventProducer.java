package com.vmware.tanzulabs.patterns.person.adapter.out;

import com.vmware.tanzulabs.patterns.person.domain.Person;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.stereotype.Component;

@Component
public class PersonEventProducer {

    private static final Logger log = LoggerFactory.getLogger( PersonEventProducer.class );

    private final StreamBridge streamBridge;

    PersonEventProducer( final StreamBridge streamBridge ) {

        this.streamBridge = streamBridge;

    }

    public void sendPersonEvent( Person person, PersonEventType type, String message ) {

        var personEventMessage = new PersonEventMessage( person.id(), type, message );
        log.info( "PersonEventMessage: {}", personEventMessage );

        this.streamBridge.send( "person-events-out-0", personEventMessage );

    }

}
