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

package org.eclipse.edc.trustframework.policy.core;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.eclipse.edc.identityhub.spi.credentials.model.Credential;
import org.eclipse.edc.identityhub.spi.credentials.model.CredentialSubject;
import org.eclipse.edc.identityhub.spi.credentials.model.VerifiableCredential;
import org.eclipse.edc.policy.engine.PolicyContextImpl;
import org.eclipse.edc.policy.engine.spi.PolicyContext;
import org.eclipse.edc.policy.model.Operator;
import org.eclipse.edc.spi.agent.ParticipantAgent;
import org.eclipse.edc.spi.monitor.Monitor;
import org.eclipse.edc.trustframework.policy.core.testfixtures.ParentPojo;
import org.eclipse.edc.trustframework.policy.core.testfixtures.PojoFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;

import java.sql.Date;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class CredentialClaimsEvaluationFunctionTest {

    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final Monitor MONITOR = mock(Monitor.class);

    @ParameterizedTest(name = "{index} {0}")
    @ArgumentsSource(NavigationFunctionArgumentsProvider.class)
    void operatorEq(String name, Function<ParentPojo, Object> navigation, PolicyContext policyContext, Object expected) {
        var function = new NoFilterTestEvaluationFunction(navigation);

        assertThat(function.evaluate(Operator.EQ, expected, null, policyContext)).isTrue();
    }

    @ParameterizedTest(name = "{index} {0}")
    @ArgumentsSource(NavigationFunctionArgumentsProvider.class)
    void operatorNeq(String name, Function<ParentPojo, Object> navigation, PolicyContext policyContext, Object expected) {
        var function = new NoFilterTestEvaluationFunction(navigation);

        assertThat(function.evaluate(Operator.NEQ, expected, null, policyContext)).isFalse();
    }

    @ParameterizedTest(name = "{index} {0}")
    @ArgumentsSource(NavigationFunctionArgumentsProvider.class)
    void operatorIn(String name, Function<ParentPojo, Object> navigation, PolicyContext policyContext, Object expected) {
        var function = new NoFilterTestEvaluationFunction(navigation);

        assertThat(function.evaluate(Operator.IN, expected, null, policyContext)).isTrue();
    }

    @Test
    void verifyFilter() {
        var pojo1 = PojoFactory.createRandom();
        var pojo2 = PojoFactory.createRandom();
        var credential1 = createCredential(pojo1);
        var credential2 = createCredential(pojo2);
        var policyContext = createPolicyContext(List.of(credential1, credential2));
        Function<ParentPojo, Object> navigation = pojo -> pojo.getChild().getValue();

        var function = new CredentialClaimsEvaluationFunction<>(MONITOR, MAPPER, navigation) {

            @Override
            protected Predicate<Credential> credentialFilter() {
                // keeps only credential2
                return credential -> credential.equals(credential2);
            }

            @Override
            protected Class<ParentPojo> getClaimsShapeType() {
                return ParentPojo.class;
            }
        };

        assertThat(function.evaluate(Operator.EQ, pojo1.getChild().getValue(), null, policyContext)).isFalse();
        assertThat(function.evaluate(Operator.EQ, pojo2.getChild().getValue(), null, policyContext)).isTrue();
    }

    private static final class NoFilterTestEvaluationFunction extends CredentialClaimsEvaluationFunction<ParentPojo> {

        NoFilterTestEvaluationFunction(Function<ParentPojo, Object> navigation) {
            super(MONITOR, MAPPER, navigation);
        }

        @Override
        protected Predicate<Credential> credentialFilter() {
            return credential -> true;
        }

        @Override
        protected Class<ParentPojo> getClaimsShapeType() {
            return ParentPojo.class;
        }
    }

    private static final class NavigationFunctionArgumentsProvider implements ArgumentsProvider {

        private final ParentPojo pojo = PojoFactory.createRandom();

        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext extensionContext) {
            var credential = createCredential(pojo);
            var policyContext = createPolicyContext(List.of(credential));
            return Stream.of(
                    Arguments.of("name", (Function<ParentPojo, Object>) ParentPojo::getName, policyContext, pojo.getName()),
                    Arguments.of("child.value", (Function<ParentPojo, Object>) pojo -> pojo.getChild().getValue(), policyContext, pojo.getChild().getValue()),
                    Arguments.of("child.subchild.values", (Function<ParentPojo, Object>) pojo -> pojo.getChild().getSubchild().values(), policyContext, pojo.getChild().getSubchild().values())
            );
        }
    }

    private static PolicyContextImpl createPolicyContext(List<Credential> credentials) {
        var credentialsMap = credentials.stream()
                .collect(Collectors.toMap(Credential::getId, credential -> (Object) credential));
        var participantAgent = new ParticipantAgent(credentialsMap, Map.of());
        return new PolicyContextImpl(participantAgent, Map.of());
    }

    private static Credential createCredential(ParentPojo pojo) {
        Map<String, Object> claims = MAPPER.convertValue(pojo, Map.class);
        var credentialSubject = CredentialSubject.Builder.newInstance()
                .id("subject");
        claims.forEach(credentialSubject::claim);
        return Credential.Builder.newInstance()
                .id(UUID.randomUUID().toString())
                .context(VerifiableCredential.DEFAULT_CONTEXT)
                .type(VerifiableCredential.DEFAULT_TYPE)
                .issuer("issuer")
                .issuanceDate(Date.from(Instant.now().truncatedTo(ChronoUnit.SECONDS)))
                .credentialSubject(credentialSubject.build())
                .build();
    }
}