/*
 *  Copyright (c) 2023 Amadeus
 *
 *  This program and the accompanying materials are made available under the
 *  terms of the Apache License, Version 2.0 which is available at
 *  https://www.apache.org/licenses/LICENSE-2.0
 *
 *  SPDX-License-Identifier: Apache-2.0
 *
 *  Contributors:
 *       Amadeus - initial API and implementation
 *
 */

package org.eclipse.edc.trustframework.policy.gaiax.participant.v2206.claims;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import org.eclipse.edc.trustframework.policy.core.FieldEvaluationProperty;

@JsonDeserialize(builder = ParticipantAddress.Builder.class)
public class ParticipantAddress {

    private static final String COUNTRY_CODE_FIELD = "gx-participant:addressCountryCode";
    private static final String ADDRESS_CODE_FIELD = "gx-participant:addressCode";
    private static final String STREET_ADDRESS_FIELD = "gx-participant:streetAddress";
    private static final String POSTAL_CODE_FIELD = "gx-participant:postalCode";

    @JsonProperty(COUNTRY_CODE_FIELD)
    @FieldEvaluationProperty(displayName = COUNTRY_CODE_FIELD)
    private String countryCode;

    @JsonProperty(ADDRESS_CODE_FIELD)
    @FieldEvaluationProperty(displayName = ADDRESS_CODE_FIELD)
    private String addressCode;

    @JsonProperty(STREET_ADDRESS_FIELD)
    @FieldEvaluationProperty(displayName = STREET_ADDRESS_FIELD)
    private String streetAddress;

    @JsonProperty(POSTAL_CODE_FIELD)
    @FieldEvaluationProperty(displayName = POSTAL_CODE_FIELD)
    private String postalCode;

    private ParticipantAddress() {
    }

    public String getCountryCode() {
        return countryCode;
    }

    public String getAddressCode() {
        return addressCode;
    }

    public String getStreetAddress() {
        return streetAddress;
    }

    public String getPostalCode() {
        return postalCode;
    }

    @JsonPOJOBuilder(withPrefix = "")
    public static class Builder {
        private final ParticipantAddress address;

        private Builder(ParticipantAddress address) {
            this.address = address;
        }

        @JsonCreator
        public static ParticipantAddress.Builder newInstance() {
            return new ParticipantAddress.Builder(new ParticipantAddress());
        }

        @JsonProperty(COUNTRY_CODE_FIELD)
        public Builder countryCode(String countryCode) {
            address.countryCode = countryCode;
            return this;
        }

        @JsonProperty(ADDRESS_CODE_FIELD)
        public Builder addressCode(String addressCode) {
            address.addressCode = addressCode;
            return this;
        }

        @JsonProperty(STREET_ADDRESS_FIELD)
        public Builder streetAddress(String streetAddress) {
            address.streetAddress = streetAddress;
            return this;
        }

        @JsonProperty(POSTAL_CODE_FIELD)
        public Builder postalCode(String postalCode) {
            address.postalCode = postalCode;
            return this;
        }

        public ParticipantAddress build() {
            return address;
        }
    }
}
