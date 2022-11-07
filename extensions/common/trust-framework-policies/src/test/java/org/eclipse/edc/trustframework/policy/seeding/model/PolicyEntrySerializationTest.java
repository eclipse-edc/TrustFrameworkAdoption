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

package org.eclipse.edc.trustframework.policy.seeding.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.eclipse.edc.spi.types.TypeManager;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

class PolicyEntrySerializationTest {
    private static final ObjectMapper MAPPER = new TypeManager().getMapper();

    @Test
    void verifySerDeser() throws IOException {
        var entry = PolicyEntry.Builder.newInstance()
                .type("test")
                .scope("scope-test")
                .jsonPath("foo.bar")
                .build();

        var serialized = MAPPER.writeValueAsString(entry);

        var deser = MAPPER.readValue(serialized, PolicyEntry.class);

        assertThat(deser).usingRecursiveComparison().isEqualTo(entry);
    }
}