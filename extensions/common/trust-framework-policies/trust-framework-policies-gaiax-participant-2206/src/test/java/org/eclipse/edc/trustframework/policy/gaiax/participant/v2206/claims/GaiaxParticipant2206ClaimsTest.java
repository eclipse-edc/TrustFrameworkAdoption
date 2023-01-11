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

import org.eclipse.edc.spi.types.TypeManager;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Objects;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class GaiaxParticipant2206ClaimsTest {

    private static final TypeManager TYPE_MANAGER = new TypeManager();

    private static final String CREDENTIAL_FILE = "gaiax-participant-claims-2206.json";

    @Test
    void verifyDeserialization() throws IOException {
        try (var in = Thread.currentThread().getContextClassLoader().getResourceAsStream(CREDENTIAL_FILE)) {
            Objects.requireNonNull(in, "Failed to open file: " + CREDENTIAL_FILE);
            var claims = TYPE_MANAGER.getMapper().readValue(in, GaiaxParticipant2206Claims.class);
            assertThat(claims.getName()).isEqualTo("Gaia-X AISBL");
            assertThat(claims.getLegalName()).isEqualTo("Gaia-X European Association for Data and Cloud AISBL");
            assertThat(claims.getTermsAndConditions()).isEqualTo("70c1d713215f95191a11d38fe2341faed27d19e083917bc8732ca4fea4976700");
            assertRegistrationNumber(claims.getRegistrationNumber(), "local", "0762747721");
            assertAddress(claims.getHeadquarterAddress(), "BE", "BE-BRU", "Avenue des Arts 6-9", "1210");
            assertAddress(claims.getLegalAddress(), "BE", "BE-BRU", "Avenue des Arts 6-9", "1210");
        }
    }

    private static void assertRegistrationNumber(ParticipantRegistrationNumber registrationNumber, String type, String number) {
        assertThat(registrationNumber.getType()).isEqualTo(type);
        assertThat(registrationNumber.getNumber()).isEqualTo(number);
    }

    private static void assertAddress(ParticipantAddress address, String countryCode, String addressCode, String streetAddress, String postalCode) {
        assertThat(address.getCountryCode()).isEqualTo(countryCode);
        assertThat(address.getAddressCode()).isEqualTo(addressCode);
        assertThat(address.getStreetAddress()).isEqualTo(streetAddress);
        assertThat(address.getPostalCode()).isEqualTo(postalCode);
    }
}