package com.vmware.tanzulabs.patterns.person.domain;

import java.time.LocalDate;
import java.util.UUID;

public record Person(UUID id, String firstName, String lastName, String email, LocalDate birthDate, Address address ) {
}
