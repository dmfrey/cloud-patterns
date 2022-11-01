package com.vmware.tanzulabs.multibuild.person.adapter.out.persistence;

import com.vmware.tanzulabs.multibuild.person.application.out.BatchLoadPersonsPort;
import com.vmware.tanzulabs.multibuild.person.application.out.CreatePersonPort;
import com.vmware.tanzulabs.multibuild.person.application.out.FindPersonByIdPort;
import com.vmware.tanzulabs.multibuild.person.application.out.FindPersonByNamePort;
import com.vmware.tanzulabs.multibuild.person.domain.Address;
import com.vmware.tanzulabs.multibuild.person.domain.Person;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.UUID;
import java.util.stream.StreamSupport;

@Service
@Transactional
public class PersonPersistenceAdapter implements FindPersonByIdPort, CreatePersonPort, BatchLoadPersonsPort, FindPersonByNamePort {

    private final PersonRepository personRepository;

    PersonPersistenceAdapter( final PersonRepository personRepository ) {

        this.personRepository = personRepository;

    }

    @Transactional( readOnly = true )
    @Override
    public Person findById( final UUID id ) {

        var found = this.personRepository.findById( id );
        if( found.isPresent() ) {

            var entity = found.get();

            return mapPersonEntity( entity );
        }

        throw new IllegalArgumentException( String.format( "PersonEntity [%s] not found!", id ) );
    }

    @Override
    public Person createPerson( final Person person ) {

        var entity = new PersonEntity();
        entity.setFirstName( person.firstName() );
        entity.setLastName( person.lastName() );
        entity.setEmail( person.email() );

        if( null != person.address() ) {

            entity.setAddress( mapAddress( person.address() ) );
        }

        var created = this.personRepository.save( entity );

        return mapPersonEntity( created );
    }

    @Override
    public long loadPersons( final Person... persons ) {

        var entities = Arrays.stream(persons).sequential()
                .map( person -> mapPerson( person ) )
                .toList();

        var loaded = this.personRepository.saveAll( entities );

        return StreamSupport.stream( loaded.spliterator(), false ).count();
    }

    @Override
    public Person findByName( final String firstName, final String lastName ) {

        var found = this.personRepository.findByFirstNameAndLastName( firstName, lastName );
        if( found.isPresent() ) {

            var entity = found.get();

            return mapPersonEntity( entity );
        }

        throw new IllegalArgumentException( String.format( "PersonEntity for name [%s, %s] not found!", firstName, lastName ) );
    }

    private Person mapPersonEntity( final PersonEntity personEntity ) {

        return new Person( personEntity.getId(), personEntity.getFirstName(), personEntity.getLastName(), personEntity.getEmail(), mapAddressEntity( personEntity.getAddress() ) );
    }

    private PersonEntity mapPerson( final Person person ) {

        var entity = new PersonEntity();

        if( null != person.id() ) {

            entity.setId( person.id() );

        }

        entity.setFirstName( person.firstName() );
        entity.setLastName( person.lastName() );
        entity.setEmail( person.email() );

        if( null != person.address() ) {

            entity.setAddress( mapAddress( person.address() ) );

        }

        return entity;
    }

    private Address mapAddressEntity( final AddressEntity addressEntity ) {

        if( null == addressEntity ) {

            return null;
        }

        return new Address( addressEntity.getId(), addressEntity.getAddress1(), addressEntity.getAddress2(), addressEntity.getCity(), addressEntity.getState(), addressEntity.getPostalCode() );
    }

    private AddressEntity mapAddress( final Address address ) {

        var entity = new AddressEntity();

        if( null != address.id() ) {
            entity.setId( address.id() );
        }

        entity.setAddress1( address.address1() );
        entity.setAddress2( address.address2() );
        entity.setCity( address.city() );
        entity.setState( address.state() );
        entity.setPostalCode( address.postalCode() );

        return entity;
    }

}
