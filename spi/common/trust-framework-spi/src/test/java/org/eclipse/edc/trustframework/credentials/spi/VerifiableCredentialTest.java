/*
 *  Copyright (c) 2022 Amadeus
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

package org.eclipse.edc.trustframework.credentials.spi;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class VerifiableCredentialTest {

    @Test
    void verifySerDes() throws JsonProcessingException {
        var mapper = new ObjectMapper();
        var vc = VerifiableCredential.Builder.newInstance()
                .id("test")
                .credentialSubject(Map.of("foo", "bar"))
                .build();

        var json = mapper.writeValueAsString(vc);

        assertNotNull(json);

        var deser = mapper.readValue(json, VerifiableCredential.class);
        assertThat(deser).usingRecursiveComparison().isEqualTo(vc);
    }
}