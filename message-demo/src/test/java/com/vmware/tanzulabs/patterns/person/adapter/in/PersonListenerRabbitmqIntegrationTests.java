package com.vmware.tanzulabs.patterns.person.adapter.in;

import com.vmware.tanzulabs.patterns.person.adapter.out.PersonEventMessage;
import com.vmware.tanzulabs.patterns.person.adapter.out.PersonEventProducer;
import com.vmware.tanzulabs.patterns.person.adapter.out.PersonEventType;
import com.vmware.tanzulabs.patterns.person.application.PersonService;
import com.vmware.tanzulabs.patterns.util.UuidGenerator;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Declarables;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.test.annotation.DirtiesContext;
import org.testcontainers.containers.RabbitMQContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.mockito.Mockito.*;

@SpringBootTest(
        properties = {
                "spring.cloud.stream.bindings.person-in-0.binder=rabbit",
                "spring.cloud.stream.bindings.person-in-0.content-type=application/json",
                "spring.cloud.stream.bindings.person-in-0.consumer.use-native-decoding=false",
                "spring.cloud.stream.bindings.person-events-out-0.binder=rabbit",
                "spring.cloud.stream.bindings.person-events-out-0.content-type=application/json",
                "spring.cloud.stream.bindings.person-events-out-0.producer.use-native-encoding=false"
        }
)
@DirtiesContext
@Testcontainers
@Tag( "IntegrationTest" )
class PersonListenerRabbitmqIntegrationTests {

    private static final Logger log = LoggerFactory.getLogger( PersonListenerRabbitmqIntegrationTests.class );

    @Container
    private static final RabbitMQContainer RABBIT_MQ_CONTAINER =
            new RabbitMQContainer( DockerImageName.parse( "rabbitmq" ) );

    @Autowired
    RabbitTemplate rabbitTemplate;

    @Autowired
    Queue testQueue;

    @MockBean
    UuidGenerator mockUuidGenerator;

    @Autowired
    RabbitTemplate senderRabbitTemplate;

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

    @BeforeEach
    void setup() {

    }

    @AfterEach
    void teardown() {

    }

    @Test
    void givenPersonMessageSent_verifyPersonMessageReceived() {

        var fakeMessage = new PersonMessage( firstName, lastName, email, new AddressMessage( address1, address2, city, state, postalCode ) );

        when( this.mockUuidGenerator.generate() ).thenReturn( personId, addressId );

        senderRabbitTemplate.convertAndSend(  "person-topic", "#", fakeMessage );

        await().atMost( 10, TimeUnit.SECONDS )
                .untilAsserted( () -> {

                    var actual = this.rabbitTemplate.receiveAndConvert( testQueue.getName(), 10_000, new ParameterizedTypeReference<PersonEventMessage>() { } );

                    var expected = new PersonEventMessage( personId, PersonEventType.Created, "person created" );

                    assertThat( actual ).isEqualTo( expected );

                    verify( this.mockUuidGenerator, times( 2 ) ).generate();
                    verifyNoMoreInteractions( this.mockUuidGenerator );

                });

    }


    @SpringBootApplication
    @Import({ PersonListener.class, PersonService.class, PersonEventProducer.class })
    static class EmbeddedTestConfiguration {

        @Bean
        Jackson2JsonMessageConverter jackson2JsonMessageConverter() {

            return new Jackson2JsonMessageConverter();
        }

        @Bean
        Queue testQueue() {

            return new Queue( "test-queue", true );
        }

        @Bean
        Declarables topicBindings( Queue testQueue, @Value( "${spring.cloud.stream.bindings.person-events-out-0.destination}") String exchange ) {

            var topicExchange = new TopicExchange( exchange );

            return new Declarables( testQueue, topicExchange, BindingBuilder.bind( testQueue ).to( topicExchange ).with( "#" ) );
        }

    }

}
