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

package org.eclipse.edc.trustframework.policy.seeding;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class TestUtil {
    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final String GAIAX_VERIFIABLE_CREDENTIAL_FILE = "gaiax-vc.json";

    public static Map<String, Object> gaiaxVerifiableCredential() {
        try (var in = Thread.currentThread().getContextClassLoader().getResourceAsStream(GAIAX_VERIFIABLE_CREDENTIAL_FILE)) {
            Objects.requireNonNull(in, "Failed to open file: " + GAIAX_VERIFIABLE_CREDENTIAL_FILE);
            var tr = new TypeReference<HashMap<String, Object>>() {
            };
            return MAPPER.readValue(in, tr);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
