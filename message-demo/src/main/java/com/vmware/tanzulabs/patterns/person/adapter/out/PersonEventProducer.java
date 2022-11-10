package com.vmware.tanzulabs.patterns.person.adapter.out;

import com.vmware.tanzulabs.patterns.person.domain.Person;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class PersonEventProducer {

    private final KafkaTemplate<String, PersonEventMessage> personEventMessageKafkaTemplate;

    private final String topic;

    PersonEventProducer(
            final KafkaTemplate<String, PersonEventMessage> personEventMessageKafkaTemplate,
            @Value( "${topics.person-events}" ) final String topic
    ) {

        this.personEventMessageKafkaTemplate = personEventMessageKafkaTemplate;
        this.topic = topic;

    }

    public void sendPersonEvent( Person person, PersonEventType type, String message ) {

        this.personEventMessageKafkaTemplate.send( topic, new PersonEventMessage( person.id(), type, message ) );

    }

}
