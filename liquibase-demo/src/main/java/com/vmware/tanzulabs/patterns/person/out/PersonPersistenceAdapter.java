package com.vmware.tanzulabs.patterns.person.out;

import com.vmware.tanzulabs.patterns.person.domain.Address;
import com.vmware.tanzulabs.patterns.person.domain.Person;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
class PersonPersistenceAdapter {

    private final PersonRepository personRepository;

    public PersonPersistenceAdapter( final PersonRepository personRepository ) {

        this.personRepository = personRepository;

    }

    @Transactional( readOnly = true )
    Person findById( final UUID id ) {

        var found = this.personRepository.findById( id );
        if( found.isPresent() ) {

            var entity = found.get();

            return mapPersonEntity( entity );
        }

        throw new IllegalArgumentException( String.format( "PersonEntity [%s] not found!", id ) );
    }

    Person createPerson( final Person person ) {

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

    private Person mapPersonEntity( final PersonEntity personEntity ) {

        return new Person( personEntity.getId(), personEntity.getFirstName(), personEntity.getLastName(), personEntity.getEmail(), mapAddressEntity( personEntity.getAddress() ) );
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
