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

package org.eclipse.edc.trustframework.policy.gaiax.participant.v2206;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.eclipse.edc.identityhub.spi.credentials.model.Credential;
import org.eclipse.edc.junit.extensions.DependencyInjectionExtension;
import org.eclipse.edc.policy.engine.PolicyEngineImpl;
import org.eclipse.edc.policy.engine.RuleBindingRegistryImpl;
import org.eclipse.edc.policy.engine.ScopeFilter;
import org.eclipse.edc.policy.engine.spi.PolicyEngine;
import org.eclipse.edc.policy.engine.spi.RuleBindingRegistry;
import org.eclipse.edc.policy.model.Action;
import org.eclipse.edc.policy.model.AtomicConstraint;
import org.eclipse.edc.policy.model.LiteralExpression;
import org.eclipse.edc.policy.model.Operator;
import org.eclipse.edc.policy.model.Permission;
import org.eclipse.edc.policy.model.Policy;
import org.eclipse.edc.spi.agent.ParticipantAgent;
import org.eclipse.edc.spi.system.ServiceExtensionContext;
import org.eclipse.edc.spi.system.injection.ObjectFactory;
import org.eclipse.edc.spi.types.TypeManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(DependencyInjectionExtension.class)
class GaiaxParticipant2206PoliciesExtensionTest {

    private static final String CREDENTIAL_FILE = "gaiax-participant-credential-2206.json";
    private static final Action USE_ACTION = Action.Builder.newInstance().type("USE").build();
    private static final ObjectMapper MAPPER = new ObjectMapper();

    private ServiceExtensionContext context;
    private GaiaxParticipant2206PoliciesExtension extension;
    private PolicyEngine policyEngine;

    @BeforeEach
    void setUp(ServiceExtensionContext context, ObjectFactory factory) {
        var ruleBindingRegistry = new RuleBindingRegistryImpl();
        ruleBindingRegistry.bind(USE_ACTION.getType(), "*");
        var scopeFilter = new ScopeFilter(ruleBindingRegistry);
        policyEngine = new PolicyEngineImpl(scopeFilter);

        context.registerService(PolicyEngine.class, policyEngine);
        context.registerService(RuleBindingRegistry.class, ruleBindingRegistry);
        context.registerService(TypeManager.class, new TypeManager());

        this.context = context;
        extension = factory.constructInstance(GaiaxParticipant2206PoliciesExtension.class);
    }

    @ParameterizedTest(name = "{index} {0} EQ {1}")
    @ArgumentsSource(TestScenarioProvider.class)
    void verifyEq(String name, String expected) {
        var credential = loadCredential();
        var participantAgent = new ParticipantAgent(Map.of(credential.getId(), credential), Collections.emptyMap());
        var policy = createPolicy(name, Operator.EQ, expected);
        extension.initialize(context);

        var result = policyEngine.evaluate("catalog.scope", policy, participantAgent);

        assertThat(result.succeeded()).isTrue();
    }

    @ParameterizedTest(name = "{index} {0} EQ {1}")
    @ArgumentsSource(TestScenarioProvider.class)
    void verifyNeq(String name, String expected) {
        var credential = loadCredential();
        var participantAgent = new ParticipantAgent(Map.of(credential.getId(), credential), Collections.emptyMap());
        var policy = createPolicy(name, Operator.NEQ, "dummy");
        extension.initialize(context);

        var result = policyEngine.evaluate("catalog.scope", policy, participantAgent);

        assertThat(result.succeeded()).isTrue();
    }

    public static class TestScenarioProvider implements ArgumentsProvider {
        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
            return Stream.of(
                    Arguments.of("gx-participant:name", "Gaia-X AISBL"),
                    Arguments.of("gx-participant:legalName", "Gaia-X European Association for Data and Cloud AISBL"),
                    Arguments.of("gx-participant:termsAndConditions", "70c1d713215f95191a11d38fe2341faed27d19e083917bc8732ca4fea4976700"),
                    Arguments.of("gx-participant:registrationNumber.gx-participant:registrationNumberType", "local"),
                    Arguments.of("gx-participant:registrationNumber.gx-participant:registrationNumberNumber", "0762747721"),
                    Arguments.of("gx-participant:headquarterAddress.gx-participant:addressCountryCode", "BE"),
                    Arguments.of("gx-participant:headquarterAddress.gx-participant:addressCode", "BE-BRU"),
                    Arguments.of("gx-participant:headquarterAddress.gx-participant:streetAddress", "Avenue des Arts 6-9"),
                    Arguments.of("gx-participant:headquarterAddress.gx-participant:postalCode", "1210"),
                    Arguments.of("gx-participant:legalAddress.gx-participant:addressCountryCode", "BE"),
                    Arguments.of("gx-participant:legalAddress.gx-participant:addressCode", "BE-BRU"),
                    Arguments.of("gx-participant:legalAddress.gx-participant:streetAddress", "Avenue des Arts 6-9"),
                    Arguments.of("gx-participant:legalAddress.gx-participant:postalCode", "1210")
            );
        }
    }

    private Policy createPolicy(String type, Operator operator, Object right) {
        var spatialExpression = new LiteralExpression(type);
        var permission = Permission.Builder.newInstance()
                .action(Action.Builder.newInstance()
                        .type(USE_ACTION.getType())
                        .build())
                .constraint(AtomicConstraint.Builder.newInstance()
                        .leftExpression(spatialExpression)
                        .operator(operator)
                        .rightExpression(new LiteralExpression(right))
                        .build())
                .build();
        return Policy.Builder.newInstance()
                .permission(permission)
                .build();
    }

    public static Credential loadCredential() {
        try (var in = Thread.currentThread().getContextClassLoader().getResourceAsStream(CREDENTIAL_FILE)) {
            Objects.requireNonNull(in, "Failed to open file: " + CREDENTIAL_FILE);
            return MAPPER.readValue(in, Credential.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}