package com.vmware.tanzulabs.multibuild.person.adapter.out.persistence;

import com.vmware.tanzulabs.multibuild.person.domain.Address;
import com.vmware.tanzulabs.multibuild.person.domain.Person;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrowsExactly;

@SpringBootTest
@DirtiesContext
@Testcontainers( disabledWithoutDocker = true )
@Tag( "IntegrationTest" )
class PostgresqlPersonPersistenceAdapterIntegrationTests {

    @Container
    private static final PostgreSQLContainer POSTGRE_SQL_CONTAINER =
            new PostgreSQLContainer( DockerImageName.parse( PostgreSQLContainer.IMAGE ).withTag( PostgreSQLContainer.DEFAULT_TAG ) );

    @DynamicPropertySource
    static void registerConfigurationProperties(DynamicPropertyRegistry registry) {
        registry.add( "spring.datasource.url", () -> POSTGRE_SQL_CONTAINER.getJdbcUrl() );
        registry.add( "spring.datasource.username", () -> POSTGRE_SQL_CONTAINER.getUsername() );
        registry.add( "spring.datasource.password", () -> POSTGRE_SQL_CONTAINER.getPassword() );
        registry.add( "spring.jpa.generate-ddl", () -> true );
    }

    @Autowired
    PersonPersistenceAdapter subject;

    @Autowired
    PersonRepository repository;

    String firstName = "Marty";
    String lastName = "McFly";
    String email = "";

    String address1 = "9303 Lyon Drive";
    String address2 = "Lyon Estates";
    String city = "Hill Valley";
    String state = "CA";
    String postalCode = "95420";

    @Test
    void findById() {

        var fakeAddress = new AddressEntity();
        fakeAddress.setAddress1( address1 );
        fakeAddress.setAddress2( address2 );
        fakeAddress.setCity( city );
        fakeAddress.setState( state );
        fakeAddress.setPostalCode( postalCode );

        var fakePerson = new PersonEntity();
        fakePerson.setFirstName( firstName );
        fakePerson.setLastName( lastName );
        fakePerson.setEmail( email );
        fakePerson.setAddress( fakeAddress );

        var created = this.repository.save( fakePerson );

        var actual = this.subject.findById( created.getId() );

        var expected = new Person( created.getId(), firstName, lastName, email, new Address( created.getAddress().getId(), address1, address2, city, state, postalCode ) );

        Assertions.assertThat( actual ).isEqualTo( expected );

    }

    @Test
    void findById_verifyNotFound() {

        var fakeId = UUID.randomUUID();

        assertThrowsExactly( IllegalArgumentException.class, () -> this.subject.findById( fakeId ) );

    }

    @Test
    void createPerson() {

        var fakePerson = new Person( null, firstName, lastName, email, new Address( null, address1, address2, city, state, postalCode ) );
        var actual = this.subject.createPerson( fakePerson );

        var expected = new Person( actual.id(), firstName, lastName, email, new Address( actual.address().id(), address1, address2, city, state, postalCode ) );

        Assertions.assertThat( actual ).isEqualTo( expected );

    }

    @SpringBootApplication
    static class ApplicationTestConfiguration { }

}
