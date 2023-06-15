/*
 *  Copyright (c) 2023 GAIA-X
 *
 *  This program and the accompanying materials are made available under the
 *  terms of the Apache License, Version 2.0 which is available at
 *  https://www.apache.org/licenses/LICENSE-2.0
 *
 *  SPDX-License-Identifier: Apache-2.0
 *
 *  Contributors:
 *       GAIA-X - initial implementation
 *
 */

package org.eclipse.edc.trustframework.policy.core.compliance;

import org.eclipse.edc.identityhub.spi.credentials.model.Credential;
import org.eclipse.edc.identityhub.spi.credentials.model.CredentialSubject;
import org.eclipse.edc.identityhub.spi.credentials.model.VerifiableCredential;
import org.eclipse.edc.policy.engine.PolicyContextImpl;
import org.eclipse.edc.policy.model.Operator;
import org.eclipse.edc.policy.model.Permission;
import org.eclipse.edc.spi.agent.ParticipantAgent;
import org.eclipse.edc.spi.monitor.Monitor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Date;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class GaiaxComplianceConstraintFunctionTest {

    private static final Monitor MONITOR = mock(Monitor.class);
    private GaiaxComplianceConstraintFunction function;

    @BeforeEach
    public void setUp() {
        function = new GaiaxComplianceConstraintFunction(MONITOR);
    }

    @Test
    void shouldBeTrueWhenValidCredentialIsPresent() {
        var credentialSubject = CredentialSubject.Builder.newInstance()
                .id("did:web:did-host:company1")
                .claim("integrity", "sha256-40f94d5a0da1afb4b48cb5472dd04407123dbd60a0c8ab30cf2b00b1c9ea42f4")
                .claim("type", "gx:compliance")
                .build();
        var policyContext = createPolicyContext(List.of(buildCredential(credentialSubject)));

        var result = function.evaluate(Operator.EQ, true, anyPermission(), policyContext);

        assertThat(result).isTrue();
    }

    @Test
    void shouldBeFalseWhenNoCredentialIsPresent() {
        var policyContext = createPolicyContext(new ArrayList<>());

        var result = function.evaluate(Operator.EQ, true, anyPermission(), policyContext);

        assertThat(result).isFalse();
    }

    @Test
    void shouldBeFalseWhenCredentialsAreNotValid() {
        var credentialSubject = CredentialSubject.Builder.newInstance()
                .id("did:web:did-host:company1")
                .claim("type", "gx:compliance")
                .build();
        var policyContext = createPolicyContext(List.of(buildCredential(credentialSubject)));

        var result = function.evaluate(Operator.EQ, true, anyPermission(), policyContext);

        assertThat(result).isFalse();
    }

    @Test
    void shouldBeFalseWhenCredentialsTypeIsNotPresent() {
        var credentialSubject = CredentialSubject.Builder.newInstance()
                .id("did:web:did-host:company1")
                .claim("integrity", "sha256-40f94d5a0da1afb4b48cb5472dd04407123dbd60a0c8ab30cf2b00b1c9ea42f4")
                .build();
        var policyContext = createPolicyContext(List.of(buildCredential(credentialSubject)));

        var result = function.evaluate(Operator.EQ, true, anyPermission(), policyContext);

        assertThat(result).isFalse();
    }

    private static PolicyContextImpl createPolicyContext(List<Credential> credentials) {
        var credentialsMap = credentials.stream()
                .collect(Collectors.toMap(Credential::getId, credential -> (Object) credential));
        var participantAgent = new ParticipantAgent(credentialsMap, Map.of());
        return new PolicyContextImpl(participantAgent, Map.of());
    }

    private Credential buildCredential(CredentialSubject credentialSubject) {
        return Credential.Builder.newInstance()
                .id(UUID.randomUUID().toString())
                .context(VerifiableCredential.DEFAULT_CONTEXT)
                .issuer("issuer")
                .issuanceDate(Date.from(Instant.now().truncatedTo(ChronoUnit.SECONDS)))
                .credentialSubject(credentialSubject)
                .build();
    }

    private Permission anyPermission() {
        return Permission.Builder.newInstance().build();
    }
}
