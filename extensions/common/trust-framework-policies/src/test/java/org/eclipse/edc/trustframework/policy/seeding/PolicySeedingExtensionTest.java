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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;

import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.eclipse.edc.trustframework.policy.seeding.TestUtil.gaiaxVerifiableCredential;

@ExtendWith(DependencyInjectionExtension.class)
class PolicySeedingExtensionTest {

    private static final Action USE_ACTION = Action.Builder.newInstance().type("USE").build();
    private ServiceExtensionContext context;
    private PolicySeedingExtension extension;
    private PolicyEngine policyEngine;

    @ParameterizedTest(name = "{index} {0}")
    @ArgumentsSource(TestScenarioProvider.class)
    void verifyPolicyEngineEvaluate(String name, Policy policy, boolean expected) {
        var claims = gaiaxVerifiableCredential();
        extension.initialize(context);

        var result = policyEngine.evaluate("catalog.scope", policy, new ParticipantAgent(claims, Collections.emptyMap()));
        assertThat(result.succeeded()).isEqualTo(expected);
    }

    public static class TestScenarioProvider implements ArgumentsProvider {
        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
            return Stream.of(
                    Arguments.of("PARTICIPANT NAME EQ", createPolicy("gx-participant:name", Operator.EQ, "foo"), true),
                    Arguments.of("PARTICIPANT NAME NEQ", createPolicy("gx-participant:name", Operator.NEQ, "bar"), true),
                    Arguments.of("PARTICIPANT HEADQUARTER ADDRESS COUNTRY IN", createPolicy("gx-participant:headquarterAddress.country-name", Operator.IN, List.of("DE", "ES")), true),
                    Arguments.of("PARTICIPANT LEGAL ADDRESS COUNTRY IN", createPolicy("gx-participant:legalAddress.country", Operator.IN, List.of("DE", "ES")), false)
            );
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
    }

    @BeforeEach
    void setUp(ServiceExtensionContext context, ObjectFactory factory) {
        var ruleBindingRegistry = new RuleBindingRegistryImpl();
        ruleBindingRegistry.bind(USE_ACTION.getType(), "*");
        var scopeFilter = new ScopeFilter(ruleBindingRegistry);
        policyEngine = new PolicyEngineImpl(scopeFilter);

        context.registerService(PolicyEngine.class, policyEngine);
        context.registerService(RuleBindingRegistry.class, ruleBindingRegistry);

        this.context = context;
        extension = factory.constructInstance(PolicySeedingExtension.class);
    }
}