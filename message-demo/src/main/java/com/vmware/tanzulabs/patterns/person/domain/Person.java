package com.vmware.tanzulabs.patterns.person.domain;

import java.util.UUID;

public record Person( UUID id, String firstName, String lastName, String email, Address address ) {
}
