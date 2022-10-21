package com.vmware.tanzulabs.patterns.person.out;

import com.vmware.tanzulabs.patterns.person.domain.Address;
import com.vmware.tanzulabs.patterns.person.domain.Person;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;

@SpringBootTest
@DirtiesContext
@Testcontainers
@Tag( "IntegrationTest" )
class PersonPersistenceAdapterIntegrationTests {

    @Container
    private static final PostgreSQLContainer POSTGRE_SQL_CONTAINER =
            new PostgreSQLContainer( DockerImageName.parse( PostgreSQLContainer.IMAGE ).withTag( PostgreSQLContainer.DEFAULT_TAG ) );

    @DynamicPropertySource
    static void registerConfigurationProperties( DynamicPropertyRegistry registry ) {
        registry.add( "spring.datasource.ur", () -> POSTGRE_SQL_CONTAINER.getJdbcUrl() );
        registry.add( "spring.datasource.username", () -> POSTGRE_SQL_CONTAINER.getUsername() );
        registry.add( "spring.datasource.password", () -> POSTGRE_SQL_CONTAINER.getPassword() );
    }

    @Autowired
    PersonPersistenceAdapter subject;

    UUID personId = UUID.fromString( "87985a0c-f39c-40a9-9f66-136a0f36570f" );
    String firstName = "Marty";
    String lastName = "McFly";
    String email = "";
    LocalDate birthDate = LocalDate.parse( "1968-06-12", DateTimeFormatter.ISO_DATE );

    UUID addressId = UUID.fromString( "a979cdd3-19e2-4043-8ee6-2fc27f9584ab" );
    String address1 = "9303 Lyon Drive";
    String address2 = "Lyon Estates";
    String city = "Hill Valley";
    String state = "CA";
    String postalCode = "95420";

    @Test
    @Sql( "createPerson.sql" )
    void findById() {

        var actual = this.subject.findById( personId );

        var expected = new Person( personId, firstName, lastName, email, birthDate, new Address( addressId, address1, address2, city, state, postalCode ) );

        assertThat( actual ).isEqualTo( expected );

    }

    @Test
    void findById_verifyNotFound() {

        var fakeId = UUID.randomUUID();

        assertThrowsExactly( IllegalArgumentException.class, () -> this.subject.findById( fakeId ) );

    }

    @Test
    void createPerson() {

        var fakePerson = new Person( null, firstName, lastName, email, birthDate, new Address( null, address1, address2, city, state, postalCode ) );
        var actual = this.subject.createPerson( fakePerson );

        var expected = new Person( actual.id(), firstName, lastName, email, birthDate, new Address( actual.address().id(), address1, address2, city, state, postalCode ) );

        assertThat( actual ).isEqualTo( expected );

    }

}
