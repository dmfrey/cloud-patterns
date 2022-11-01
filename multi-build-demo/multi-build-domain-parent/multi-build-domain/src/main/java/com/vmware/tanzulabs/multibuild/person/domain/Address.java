package com.vmware.tanzulabs.multibuild.person.domain;

import java.util.UUID;

public record Address( UUID id, String address1, String address2, String city, String state, String postalCode ) {
}
