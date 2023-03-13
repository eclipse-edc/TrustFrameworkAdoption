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

package org.eclipse.edc.trustframework.policy.gaiax.participant.v2206.function;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.eclipse.edc.identityhub.spi.credentials.model.Credential;
import org.eclipse.edc.identityhub.spi.credentials.model.CredentialSubject;
import org.eclipse.edc.spi.monitor.Monitor;
import org.eclipse.edc.trustframework.policy.gaiax.participant.v2206.claims.GaiaxParticipant2206Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Date;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class GaiaxParticipant2206ClaimsEvaluationFunctionTest {

    private static final ObjectMapper MAPPER = mock(ObjectMapper.class);
    private static final Monitor MONITOR = mock(Monitor.class);

    private GaiaxParticipant2206ClaimsEvaluationFunction function;

    @BeforeEach
    public void setUp() {
        function = new GaiaxParticipant2206ClaimsEvaluationFunction(MONITOR, MAPPER, null);
    }

    @Test
    void verifyGetClaimsShapeType() {
        assertThat(function.getClaimsShapeType()).isEqualTo(GaiaxParticipant2206Claims.class);
    }

    @Test
    void verifyCredentialFilter() {
        var credential = createCredential(List.of("https://registry.gaia-x.eu/v2206/api/shape"), List.of("LegalPerson"));

        assertThat(function.credentialFilter().test(credential)).isTrue();
    }

    @Test
    void verifyCredentialFilter_missingMandatoryContext() {
        var credential = createCredential(List.of("test"), List.of("LegalPerson"));

        assertThat(function.credentialFilter().test(credential)).isFalse();
    }

    @Test
    void verifyCredentialFilter_missingMandatoryType() {
        var credential = createCredential(List.of("https://registry.gaia-x.eu/v2206/api/shape"), List.of("test"));

        assertThat(function.credentialFilter().test(credential)).isFalse();
    }

    private static Credential createCredential(List<String> contexts, List<String> types) {
        return Credential.Builder.newInstance()
                .id(UUID.randomUUID().toString())
                .contexts(contexts)
                .types(types)
                .issuer("issuer")
                .issuanceDate(Date.from(Instant.now().truncatedTo(ChronoUnit.SECONDS)))
                .credentialSubject(CredentialSubject.Builder.newInstance()
                        .id("subject")
                        .build())
                .build();
    }
}