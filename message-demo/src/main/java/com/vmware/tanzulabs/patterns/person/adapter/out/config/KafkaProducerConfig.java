package com.vmware.tanzulabs.patterns.person.adapter.out.config;

import com.vmware.tanzulabs.patterns.person.adapter.out.PersonEventMessage;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.Map;

@Configuration
public class KafkaProducerConfig {

    @Value( value = "${kafka.bootstrapAddress}" )
    private String bootstrapAddress;

    @Bean
    public ProducerFactory<String, PersonEventMessage> personEventMessageProducerFactory() {

        Map<String, Object> configProps = Map.of(
                ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress,
                ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class,
                ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class
        );

        return new DefaultKafkaProducerFactory<>( configProps, new StringSerializer(), new JsonSerializer<>() );
    }

    @Bean
    public KafkaTemplate<String, PersonEventMessage> personEventMessageKafkaTemplate() {

        return new KafkaTemplate<>( personEventMessageProducerFactory() );
    }

}
