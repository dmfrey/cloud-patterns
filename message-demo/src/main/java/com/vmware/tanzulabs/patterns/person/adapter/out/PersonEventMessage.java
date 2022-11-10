package com.vmware.tanzulabs.patterns.person.adapter.out;

import java.util.UUID;

public record PersonEventMessage( UUID id, PersonEventType type, String message ) {
}

