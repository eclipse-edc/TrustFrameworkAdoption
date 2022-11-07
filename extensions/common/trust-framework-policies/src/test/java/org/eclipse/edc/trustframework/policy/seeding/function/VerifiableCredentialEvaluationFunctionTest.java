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

package org.eclipse.edc.trustframework.policy.seeding.function;

import org.eclipse.edc.policy.engine.PolicyContextImpl;
import org.eclipse.edc.policy.engine.spi.PolicyContext;
import org.eclipse.edc.policy.model.Operator;
import org.eclipse.edc.spi.agent.ParticipantAgent;
import org.eclipse.edc.spi.monitor.Monitor;
import org.eclipse.edc.spi.types.TypeManager;
import org.eclipse.edc.trustframework.policy.seeding.exception.PolicyEvaluationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.eclipse.edc.trustframework.policy.seeding.TestUtil.gaiaxVerifiableCredential;
import static org.mockito.Mockito.mock;

class VerifiableCredentialEvaluationFunctionTest {
    private static final String GAIAX_VC_PARTICIPANT_NAME = "$.gx-participant:name";
    private static final String GAIAX_VC_PARTICIPANT_HEADQUARTER_ADDRESS_COUNTRY = "$.gx-participant:headquarterAddress.gx-participant:country-name";
    private static final String GAIAX_VC_PARTICIPANT_LEGAL_ADDRESS_COUNTRY = "$.gx-participant:legalAddress.gx-participant:country";

    private static final TypeManager TYPE_MANAGER = new TypeManager();

    @ParameterizedTest(name = "{index} {0}")
    @ArgumentsSource(TestScenarioProvider.class)
    void verifyEvaluate(String name, String jsonPath, Operator operator, Object right, boolean expected) throws IOException {
        var claims = gaiaxVerifiableCredential();
        var constraint = new VerifiableCredentialEvaluationFunction(mock(Monitor.class), TYPE_MANAGER.getMapper(), jsonPath);
        var policyContext = createPolicyContext(claims);

        var result = constraint.evaluate(operator, right, null, policyContext);

        assertThat(result).isEqualTo(expected);
    }

    @Test
    void voidThrowsIfPathDoesNotExist() throws IOException {
        var constraint = new VerifiableCredentialEvaluationFunction(mock(Monitor.class), TYPE_MANAGER.getMapper(), "$.unknown-field");
        var policyContext = createPolicyContext(gaiaxVerifiableCredential());

        assertThatExceptionOfType(PolicyEvaluationException.class).isThrownBy(() -> constraint.evaluate(Operator.EQ, null, null, policyContext));

    }

    private PolicyContext createPolicyContext(Map<String, Object> claims) {
        var participantAgent = new ParticipantAgent(claims, Collections.emptyMap());
        return new PolicyContextImpl(participantAgent, Map.of());
    }

    private static final class TestScenarioProvider implements ArgumentsProvider {
        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext context) throws Exception {
            return Stream.of(
                    Arguments.of("PARTICIPANT NAME EQ", GAIAX_VC_PARTICIPANT_NAME, Operator.EQ, "foo", true),
                    Arguments.of("PARTICIPANT NAME NEQ", GAIAX_VC_PARTICIPANT_NAME, Operator.NEQ, "bar", true),
                    Arguments.of("PARTICIPANT HEADQUARTER ADDRESS COUNTRY IN", GAIAX_VC_PARTICIPANT_HEADQUARTER_ADDRESS_COUNTRY, Operator.IN, List.of("DE", "ES"), true),
                    Arguments.of("PARTICIPANT LEGAL ADDRESS COUNTRY IN", GAIAX_VC_PARTICIPANT_LEGAL_ADDRESS_COUNTRY, Operator.IN, List.of("DE", "ES"), false)
            );
        }
    }
}