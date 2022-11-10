package com.vmware.tanzulabs.patterns.config;

import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaAdmin;

import java.util.HashMap;

@Configuration
public class KafkaTopicConfig {

    @Value( value = "${kafka.bootstrapAddress}" )
    private String bootstrapAddress;

    @Value( value = "${topics.person}" )
    String personTopic;

    @Value( value = "${topics.person-events}" )
    String personEventTopic;

    @Bean
    public KafkaAdmin kafkaAdmin() {

        var configs = new HashMap<String, Object>();
        configs.put( AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress );

        return new KafkaAdmin(configs);
    }

    @Bean
    public NewTopic personTopic() {

        return new NewTopic( personTopic, 1, (short) 1 );
    }

    @Bean
    public NewTopic personEventTopic() {

        return new NewTopic( personEventTopic, 1, (short) 1 );
    }

}
