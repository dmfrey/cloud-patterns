package com.vmware.tanzulabs.patterns.person.adapter.in;

public record PersonMessage( String firstName, String lastName, String email, AddressMessage address ) {
}