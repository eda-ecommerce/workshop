package com.eda.shippingService.domain.dto.common;

import com.eda.shippingService.domain.entity.Address;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

//Necessary?
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public record AddressDTO(
        @JsonProperty("street")
        String street,
        @JsonProperty("city")
        String city,
        @JsonProperty("state")
        String state,
        @JsonProperty("postalCode")
        String postalCode,
        @JsonProperty("country")
        String country
) {
    public static AddressDTO fromEntity(Address address){
        return new AddressDTO(
                address.getStreet(),
                address.getCity(),
                address.getState(),
                address.getPostalCode(),
                address.getCountry()
        );
    }
    public Address toEntity(){
        return new Address(
                this.street(),
                this.city(),
                this.state(),
                this.postalCode(),
                this.country()
        );
    }
}