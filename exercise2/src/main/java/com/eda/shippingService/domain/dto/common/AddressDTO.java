package com.eda.shippingService.domain.dto.common;

import com.eda.shippingService.domain.entity.Address;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.swagger.v3.oas.annotations.media.Schema;

//Necessary?
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public record AddressDTO(
        @Schema(example = "Eventstraße 24")
        @JsonProperty("street")
        String street,
        @Schema(example = "Gummersbach")
        @JsonProperty("city")
        String city,
        @JsonProperty("state")
        @Schema(example = "NRW")
        String state,
        @Schema(example = "51643")
        @JsonProperty("postalCode")
        String postalCode,
        @Schema(example = "Germany")
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