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

import org.eclipse.dataspaceconnector.core.policy.engine.PolicyEngineImpl;
import org.eclipse.dataspaceconnector.core.policy.engine.RuleBindingRegistryImpl;
import org.eclipse.dataspaceconnector.core.policy.engine.ScopeFilter;
import org.eclipse.dataspaceconnector.junit.extensions.DependencyInjectionExtension;
import org.eclipse.dataspaceconnector.policy.model.Action;
import org.eclipse.dataspaceconnector.policy.model.AtomicConstraint;
import org.eclipse.dataspaceconnector.policy.model.LiteralExpression;
import org.eclipse.dataspaceconnector.policy.model.Operator;
import org.eclipse.dataspaceconnector.policy.model.Permission;
import org.eclipse.dataspaceconnector.policy.model.Policy;
import org.eclipse.dataspaceconnector.spi.agent.ParticipantAgent;
import org.eclipse.dataspaceconnector.spi.policy.engine.PolicyEngine;
import org.eclipse.dataspaceconnector.spi.policy.engine.RuleBindingRegistry;
import org.eclipse.dataspaceconnector.spi.system.ServiceExtensionContext;
import org.eclipse.dataspaceconnector.spi.system.injection.ObjectFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.eclipse.edc.trustframework.policy.seeding.TestUtil.gaiaxVerifiableCredential;

@ExtendWith(DependencyInjectionExtension.class)
class PolicySeedingExtensionTest {


    private static final String POLICY_FILE_SETTING = "edc.trustframework.policies.file.path";

    private static final Action USE_ACTION = Action.Builder.newInstance().type("USE").build();
    private ServiceExtensionContext context;
    private PolicySeedingExtension extension;
    private PolicyEngine policyEngine;

    @ParameterizedTest(name = "{index} {0}")
    @MethodSource("providerTestScenario")
    void verifyPolicyEngineEvaluate(String name, Policy policy, boolean expected) {
        var claims = gaiaxVerifiableCredential();
        extension.initialize(context);

        var result = policyEngine.evaluate("catalog.scope", policy, new ParticipantAgent(claims, Collections.emptyMap()));
        assertThat(result.succeeded()).isEqualTo(expected);
    }

    private static Stream<Arguments> providerTestScenario() {
        return Stream.of(
                Arguments.of("PARTICIPANT NAME EQ", createPolicy("gx-participant:name", Operator.EQ, "foo"), true),
                Arguments.of("PARTICIPANT NAME NEQ", createPolicy("gx-participant:name", Operator.NEQ, "bar"), true),
                Arguments.of("PARTICIPANT HEADQUARTER ADDRESS COUNTRY IN", createPolicy("gx-participant:headquarterAddress.country-name", Operator.IN, List.of("DE", "ES")), true),
                Arguments.of("PARTICIPANT LEGAL ADDRESS COUNTRY IN", createPolicy("gx-participant:legalAddress.country", Operator.IN, List.of("DE", "ES")), false)
        );
    }

    private static Policy createPolicy(String type, Operator operator, Object right) {
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

    //    @BeforeAll
    //    static void setProps() {
    //        System.setProperty(POLICY_FILE_SETTING, "src/test/resources/policies.json");
    //    }
    //
    //    @AfterAll
    //    static void unsetProps() {
    //        System.clearProperty(POLICY_FILE_SETTING);
    //    }

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