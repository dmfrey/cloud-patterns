package com.vmware.tanzulabs.patterns.person.adapter.in;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude( JsonInclude.Include.NON_NULL )
public record AddressMessage(
        @JsonProperty String address1,
        @JsonProperty String address2,
        @JsonProperty String city,
        @JsonProperty String state,
        @JsonProperty String postalCode
) { }
