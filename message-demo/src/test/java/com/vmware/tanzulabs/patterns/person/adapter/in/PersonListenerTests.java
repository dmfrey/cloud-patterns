package com.vmware.tanzulabs.patterns.person.adapter.in;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vmware.tanzulabs.patterns.person.adapter.out.PersonEventMessage;
import com.vmware.tanzulabs.patterns.person.adapter.out.PersonEventType;
import com.vmware.tanzulabs.patterns.util.UuidGenerator;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
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
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.time.Duration;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static org.testcontainers.shaded.org.awaitility.Awaitility.waitAtMost;

@SpringBootTest
@DirtiesContext
@EmbeddedKafka(
        partitions = 1,
        brokerProperties = { "listeners=PLAINTEXT://localhost:9092", "port=9092" }
)
@ActiveProfiles( "test" )
class PersonListenerTests {

    private static final Logger log = LoggerFactory.getLogger( PersonListenerTests.class );

    @Autowired
    private EmbeddedKafkaBroker embeddedKafka;

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

        var consumerProperties =
                KafkaTestUtils.consumerProps( groupId, "false", embeddedKafka );

        var consumerFactory = new DefaultKafkaConsumerFactory<String, PersonEventMessage>( consumerProperties );

        container = new KafkaMessageListenerContainer<>( consumerFactory, containerProperties );
        container.setupMessageListener(( MessageListener<String, String>) record -> {

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
