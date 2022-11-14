package com.vmware.tanzulabs.patterns.person.adapter.in.config;

import com.vmware.tanzulabs.patterns.person.adapter.in.PersonMessage;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.*;
import org.springframework.kafka.support.converter.JsonMessageConverter;
import org.springframework.kafka.support.converter.MessageConverter;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.Map;

@Configuration
@EnableKafka
public class KafkaConsumerConfig {

    @Value( value = "${spring.kafka.bootstrap-servers}" )
    private String bootstrapAddress;

    @Value( value = "${spring.application.name}" )
    private String groupId;

    @Bean
    public ProducerFactory<String, Object> personMessageProducerFactory() {

        Map<String, Object> configProps = Map.of(
                ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress,
                ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class,
                ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class
        );

        return new DefaultKafkaProducerFactory<>( configProps );
    }

    @Bean
    public ConsumerFactory<String, PersonMessage> personMessageConsumerFactory() {

        Map<String, Object> configProps = Map.of(
                ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress,
                ConsumerConfig.GROUP_ID_CONFIG, groupId
        );

        return new DefaultKafkaConsumerFactory<>( configProps, new StringDeserializer(), new JsonDeserializer<>( PersonMessage.class ) );
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, PersonMessage> personMessageKafkaListenerContainerFactory() {

        var factory = new ConcurrentKafkaListenerContainerFactory<String, PersonMessage>();
        factory.setConsumerFactory( personMessageConsumerFactory() );

        return factory;
    }

    @Bean
    public KafkaTemplate<String, Object> personMessageKafkaTemplate() {

        return new KafkaTemplate<>( personMessageProducerFactory() );
    }

    @Bean
    MessageConverter jsonMessageConverter() {

        return new JsonMessageConverter();
    }

}
