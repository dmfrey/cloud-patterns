package com.vmware.tanzulabs.multibuild.person.domain;

import java.util.UUID;

public record Person( UUID id, String firstName, String lastName, String email, Address address ) {
}
