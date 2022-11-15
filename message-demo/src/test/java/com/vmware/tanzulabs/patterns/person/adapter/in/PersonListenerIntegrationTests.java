package com.vmware.tanzulabs.patterns.person.adapter.in;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vmware.tanzulabs.patterns.person.adapter.out.PersonEventMessage;
import com.vmware.tanzulabs.patterns.person.adapter.out.PersonEventProducer;
import com.vmware.tanzulabs.patterns.person.adapter.out.PersonEventType;
import com.vmware.tanzulabs.patterns.person.application.PersonService;
import com.vmware.tanzulabs.patterns.util.UuidGenerator;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.KafkaMessageListenerContainer;
import org.springframework.kafka.listener.MessageListener;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@SpringBootTest(
        properties = {
                "spring.cloud.stream.bindings.person-in-0.binder=kafka",
                "spring.cloud.stream.bindings.person-events-out-0.binder=kafka"
        }
)
@DirtiesContext
@Testcontainers
@Tag( "IntegrationTest" )
class PersonListenerIntegrationTests {

    @Container
    private static final KafkaContainer KAFKA_CONTAINER =
            new KafkaContainer( DockerImageName.parse( "confluentinc/cp-kafka" ) );

    @DynamicPropertySource
    static void kafkaProperties( DynamicPropertyRegistry registry ) {

        registry.add( "spring.kafka.bootstrap-servers", KAFKA_CONTAINER::getBootstrapServers );

    }

    @Autowired
    ObjectMapper mapper;

    @MockBean
    UuidGenerator mockUuidGenerator;

    private KafkaTemplate<String, String> senderKafkaTemplate;

    private final BlockingQueue<ConsumerRecord<String, String>> records = new LinkedBlockingQueue<>();
    private KafkaMessageListenerContainer<String, String> listenerContainer;

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

        Map<String, Object> producerProperties = Map.of(
                ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, KAFKA_CONTAINER.getBootstrapServers(),
                ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class,
                ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class
        );

        var senderProducerFactory = new DefaultKafkaProducerFactory<String, String>( producerProperties );
        this.senderKafkaTemplate = new KafkaTemplate<>( senderProducerFactory );

        var containerProperties = new ContainerProperties( "person-events-topic" );
        containerProperties.setMessageListener( (MessageListener<String, String>) records::add );

        this.listenerContainer = new KafkaMessageListenerContainer<>(
                new DefaultKafkaConsumerFactory<>(
                        KafkaTestUtils.consumerProps( KAFKA_CONTAINER.getBootstrapServers(), this.getClass().getName(), "true" ),
                        new StringDeserializer(), new StringDeserializer(), true
                ),
                containerProperties
        );
        this.listenerContainer.start();

    }

    @AfterEach
    void teardown() {

        this.listenerContainer.stop();

    }

    @Test
    void givenPersonMessageSent_verifyPersonMessageReceived() throws InterruptedException, JsonProcessingException {

        var fakeMessage = String.format( "{\"firstName\":\"%s\",\"lastName\":\"%s\",\"email\":\"%s\",\"address\":{\"address1\":\"%s\",\"address2\":\"%s\",\"city\":\"%s\",\"state\":\"%s\",\"postalCode\":\"%s\"}}", firstName, lastName, email, address1, address2, city, state, postalCode );

        when( this.mockUuidGenerator.generate() ).thenReturn( personId, addressId );

        senderKafkaTemplate.send( "person-topic", fakeMessage );

        var record = records.poll( 10, TimeUnit.SECONDS );

        assertThat( record ).isNotNull();
        assertThat( record.value() ).isNotNull();

        var actual = this.mapper.readValue( record.value(), PersonEventMessage.class );

        var expected = new PersonEventMessage( personId, PersonEventType.Created, "person created" );

        assertThat( actual ).isEqualTo( expected );

        verify( this.mockUuidGenerator, times( 2 ) ).generate();
        verifyNoMoreInteractions( this.mockUuidGenerator );

    }


    @SpringBootApplication
    @Import({ PersonListener.class, PersonService.class, PersonEventProducer.class })
    static class EmbeddedTestConfiguration { }

}
