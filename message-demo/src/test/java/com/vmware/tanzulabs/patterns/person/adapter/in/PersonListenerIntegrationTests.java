package com.vmware.tanzulabs.patterns.person.adapter.in;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vmware.tanzulabs.patterns.person.adapter.out.PersonEventMessage;
import com.vmware.tanzulabs.patterns.person.adapter.out.PersonEventType;
import com.vmware.tanzulabs.patterns.util.UuidGenerator;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.KafkaMessageListenerContainer;
import org.springframework.kafka.listener.MessageListener;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.time.Duration;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static org.testcontainers.shaded.org.awaitility.Awaitility.waitAtMost;

@SpringBootTest
@DirtiesContext
@Testcontainers
@Tag( "IntegrationTest" )
class PersonListenerIntegrationTests {

    private static final Logger log = LoggerFactory.getLogger( PersonListenerIntegrationTests.class );

    @Container
    private static final KafkaContainer KAFKA_CONTAINER = new KafkaContainer( DockerImageName.parse( "confluentinc/cp-kafka" ) );

    @DynamicPropertySource
    static void kafkaProperties( DynamicPropertyRegistry registry ) {

        registry.add( "spring.kafka.bootstrap-servers", KAFKA_CONTAINER::getBootstrapServers );

    }

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Autowired
    ObjectMapper mapper;

    @Value( value = "${topics.person}" )
    String personTopic;

    @Value( value = "${topics.person-events}" )
    String personEventTopic;

    @Value( value = "${spring.kafka.consumer.group-id}" )
    String groupId;

    @MockBean
    UuidGenerator mockUuidGenerator;

    private KafkaMessageListenerContainer<String, PersonEventMessage> container;

    private BlockingQueue<ConsumerRecord<String, String>> consumerRecords;

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

        consumerRecords = new LinkedBlockingQueue<>();

        var containerProperties = new ContainerProperties( personEventTopic );

        Map<String, Object> consumerProperties = Map.of(
                ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, KAFKA_CONTAINER.getBootstrapServers(),
                ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest",
                ConsumerConfig.GROUP_ID_CONFIG, "test",
                ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class,
                ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class

        );

        var consumerFactory = new DefaultKafkaConsumerFactory<String, PersonEventMessage>( consumerProperties );

        container = new KafkaMessageListenerContainer<>( consumerFactory, containerProperties );
        container.setupMessageListener( (MessageListener<String, String>) record -> {

            log.debug( "Listened message='{}'", record );
            consumerRecords.add( record );

        });
        container.start();

    }

    @AfterEach
    void teardown() {

        container.stop();

    }

    @Test
    void givenPersonMessageSent_verifyPersonMessageReceived() {

        when( this.mockUuidGenerator.generate() ).thenReturn( personId, addressId );

        String fakeMessage = String.format( "{\"firstName\":\"%s\",\"lastName\":\"%s\",\"email\":\"%s\",\"address\":{\"address1\":\"%s\",\"address2\":\"%s\",\"city\":\"%s\",\"state\":\"%s\",\"postalCode\":\"%s\"}}", firstName, lastName, email, address1, address2, city, state, postalCode );

        kafkaTemplate.send( personTopic, fakeMessage );

        waitAtMost( Duration.ofSeconds( 10 ) )
                .untilAsserted( () -> {

                    var received = consumerRecords.poll( 10, TimeUnit.SECONDS );
                    var actual = mapper.readValue( received.value(), PersonEventMessage.class );

                    var expected = new PersonEventMessage( personId, PersonEventType.Created, "person created" );

                    assertThat( actual ).isEqualTo( expected );

                    verify( this.mockUuidGenerator, times( 2 ) ).generate();
                    verifyNoMoreInteractions( this.mockUuidGenerator );

                });

    }

}
