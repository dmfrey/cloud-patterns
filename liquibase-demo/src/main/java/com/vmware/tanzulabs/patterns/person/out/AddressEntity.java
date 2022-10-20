package com.vmware.tanzulabs.patterns.person.out;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table( name = "ADDRESS" )
class AddressEntity {

    @Id
    @GeneratedValue( strategy = GenerationType.AUTO )
    @Column( name = "address_id", nullable = false, updatable = false, columnDefinition = "VARCHAR(40)" )
    private UUID id;

    @Column( name = "address_one", nullable = false )
    @NotEmpty
    private String address1;

    @Column( name = "address_two" )
    private String address2;

    @Column( name = "city", nullable = false )
    private String city;

    @Column( name = "state_abbr", nullable = false, columnDefinition = "VARCHAR(2)" )
    @Size( min = 2, max = 2 )
    private String state;

    @Column( name = "postal_code", nullable = false, columnDefinition = "VARCHAR(5)" )
    @Size( min = 5, max = 5 )
    private String postalCode;

    public UUID getId() {

        return id;
    }

    public void setId( UUID id ) {

        this.id = id;

    }

    public String getAddress1() {

        return address1;
    }

    public void setAddress1( String address1 ) {

        this.address1 = address1;

    }

    public String getAddress2() {

        return address2;
    }

    public void setAddress2( String address2 ) {

        this.address2 = address2;

    }

    public String getCity() {

        return city;
    }

    public void setCity( String city ) {

        this.city = city;

    }

    public String getState() {

        return state;
    }

    public void setState( String state ) {

        this.state = state;

    }

    public String getPostalCode() {

        return postalCode;
    }

    public void setPostalCode( String postalCode ) {

        this.postalCode = postalCode;

    }

    @Override
    public boolean equals( Object o ) {
        if( this == o ) return true;
        if( o == null || getClass() != o.getClass() ) return false;

        AddressEntity addressEntity = (AddressEntity) o;

        return id.equals( addressEntity.id );
    }

    @Override
    public int hashCode() {

        return Objects.hash( id );
    }

    @Override
    public String toString() {

        return "AddressEntity{" +
                "id=" + id +
                ", address1='" + address1 + '\'' +
                ", address2='" + address2 + '\'' +
                ", city='" + city + '\'' +
                ", state='" + state + '\'' +
                ", postalCode='" + postalCode + '\'' +
                '}';
    }

}
