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

package org.eclipse.edc.trustframework.policy.core;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.eclipse.edc.policy.engine.PolicyContextImpl;
import org.eclipse.edc.policy.engine.spi.PolicyContext;
import org.eclipse.edc.policy.model.Operator;
import org.eclipse.edc.spi.agent.ParticipantAgent;
import org.eclipse.edc.spi.types.TypeManager;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class VerifiableCredentialEvaluationFunctionTest {

    private static final ObjectMapper MAPPER = new TypeManager().getMapper();

    @ParameterizedTest(name = "{index} {0}")
    @ArgumentsSource(TestScenarioProvider.class)
    void verifyEvaluate(String name, String jsonPath, Operator operator, Object right, boolean expected) {
        var claims = createClaims();
        var constraint = new VerifiableCredentialEvaluationFunction(MAPPER, jsonPath);
        var policyContext = createPolicyContext(claims);

        var result = constraint.evaluate(operator, right, null, policyContext);

        assertThat(result).isEqualTo(expected);
    }

    @Test
    void voidIgnoreVcIfPathDoesNotExist() {
        var claims = createClaims();
        var constraint = new VerifiableCredentialEvaluationFunction(MAPPER, "$.unknown-field");
        var policyContext = createPolicyContext(claims);

        var result = constraint.evaluate(Operator.EQ, "any", null, policyContext);

        assertThat(result).isFalse();
    }

    private PolicyContext createPolicyContext(Map<String, Object> claims) {
        var participantAgent = new ParticipantAgent(claims, Collections.emptyMap());
        return new PolicyContextImpl(participantAgent, Map.of());
    }

    private static final class TestScenarioProvider implements ArgumentsProvider {
        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
            return Stream.of(
                    Arguments.of("ROOT FIELD EQ OK", "$.a", Operator.EQ, "b", true),
                    Arguments.of("ROOT FIELD EQ KO", "$.a", Operator.EQ, "c", false),
                    Arguments.of("NESTED FIELD EQ OK", "$.c.d", Operator.EQ, "e", true),
                    Arguments.of("NESTED FIELD EQ OK", "$.c.d", Operator.EQ, "f", false),

                    Arguments.of("ROOT FIELD NEQ OK", "$.a", Operator.NEQ, "c", true),
                    Arguments.of("ROOT FIELD NEQ KO", "$.a", Operator.NEQ, "b", false),
                    Arguments.of("NESTED FIELD NEQ OK", "$.c.d", Operator.NEQ, "f", true),
                    Arguments.of("NESTED FIELD NEQ KO", "$.c.d", Operator.NEQ, "e", false),

                    Arguments.of("ROOT FIELD IN OK", "$.a", Operator.IN, List.of("b", "c"), true),
                    Arguments.of("ROOT FIELD IN KO", "$.a", Operator.IN, List.of("c", "d"), false),
                    Arguments.of("NESTED FIELD IN OK", "$.c.d", Operator.IN, List.of("e", "f"), true),
                    Arguments.of("NESTED FIELD IN KO", "$.c.d", Operator.IN, List.of("f", "g"), false)
            );
        }
    }

    private static Map<String, Object> createClaims() {
        var id = UUID.randomUUID().toString();
        return Map.of(
                UUID.randomUUID().toString(), Map.of("vc", Map.of(
                                "id", id,
                                "credentialSubject", Map.of(
                                        "a", "b",
                                        "c", Map.of("d", "e")
                                )
                        )
                )
        );
    }
}