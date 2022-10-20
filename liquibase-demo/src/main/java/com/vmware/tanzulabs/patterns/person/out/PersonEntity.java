package com.vmware.tanzulabs.patterns.person.out;

import org.hibernate.annotations.Type;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table( name = "PERSON" )
class PersonEntity {

    @Id
    @GeneratedValue( strategy = GenerationType.AUTO )
    @Type( type="uuid-char" )
    @Column( name = "person_id", nullable = false, updatable = false, columnDefinition = "VARCHAR(40)" )
    private UUID id;

    @Column( name = "first_name", nullable = false )
    @NotEmpty
    private String firstName;

    @Column( name = "last_name", nullable = false )
    @NotEmpty
    private String lastName;

    @Column( name = "email_address", nullable = false )
    @Email
    private String email;

    @ManyToOne
    @JoinColumn( name = "address_id" )
    private AddressEntity address;

    public UUID getId() {

        return id;
    }

    public void setId( UUID id ) {

        this.id = id;

    }

    public String getFirstName() {

        return firstName;
    }

    public void setFirstName( String firstName ) {

        this.firstName = firstName;

    }

    public String getLastName() {

        return lastName;
    }

    public void setLastName( String lastName ) {

        this.lastName = lastName;

    }

    public String getEmail() {

        return email;
    }

    public void setEmail( String email ) {

        this.email = email;

    }

    public AddressEntity getAddress() {

        return address;
    }

    public void setAddress( AddressEntity addressEntity ) {

        this.address = addressEntity;

    }

    @Override
    public boolean equals( Object o ) {

        if( this == o ) return true;
        if( o == null || getClass() != o.getClass() ) return false;

        PersonEntity personEntity = (PersonEntity) o;

        return id.equals( personEntity.id );
    }

    @Override
    public int hashCode() {

        return Objects.hash( id );
    }

    @Override
    public String toString() {

        return "PersonEntity{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", address=" + address +
                '}';
    }

}
