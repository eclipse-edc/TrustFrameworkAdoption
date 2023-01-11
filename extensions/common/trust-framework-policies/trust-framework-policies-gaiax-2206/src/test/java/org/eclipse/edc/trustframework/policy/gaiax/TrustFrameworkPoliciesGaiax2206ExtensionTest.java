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

package org.eclipse.edc.trustframework.policy.gaiax;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.eclipse.edc.trustframework.policies.gaiax.TrustFrameworkPoliciesGaiax2206;
import org.eclipse.edc.trustframework.policies.gaiax.TrustFrameworkPoliciesGaiax2206Extension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(DependencyInjectionExtension.class)
class TrustFrameworkPoliciesGaiax2206ExtensionTest {

    private static final String VC_FILE_NAME = "gaiax-participant-vc-2206.json";
    private static final Action USE_ACTION = Action.Builder.newInstance().type("USE").build();
    private static final ObjectMapper MAPPER = new TypeManager().getMapper();

    private ServiceExtensionContext context;
    private TrustFrameworkPoliciesGaiax2206Extension extension;
    private PolicyEngine policyEngine;

    @BeforeEach
    void setUp(ServiceExtensionContext context, ObjectFactory factory) {
        var ruleBindingRegistry = new RuleBindingRegistryImpl();
        ruleBindingRegistry.bind(USE_ACTION.getType(), "*");
        var scopeFilter = new ScopeFilter(ruleBindingRegistry);
        policyEngine = new PolicyEngineImpl(scopeFilter);

        context.registerService(PolicyEngine.class, policyEngine);
        context.registerService(RuleBindingRegistry.class, ruleBindingRegistry);

        this.context = context;
        extension = factory.constructInstance(TrustFrameworkPoliciesGaiax2206Extension.class);
    }

    @ParameterizedTest(name = "{index} {0} EQ {1}")
    @ArgumentsSource(TestScenarioProvider.class)
    void verifyEq(String name, String expected) {
        var claims = loadVerifiableCredential();
        var policy = createPolicy(name, Operator.EQ, expected);
        extension.initialize(context);

        var result = policyEngine.evaluate("catalog.scope", policy, new ParticipantAgent(claims, Collections.emptyMap()));

        assertThat(result.succeeded()).isTrue();
    }

    public static class TestScenarioProvider implements ArgumentsProvider {
        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext context) {

            var expected = new HashMap<TrustFrameworkPoliciesGaiax2206, String>();
            expected.put(TrustFrameworkPoliciesGaiax2206.PARTICIPANT_NAME, "Gaia-X AISBL");
            expected.put(TrustFrameworkPoliciesGaiax2206.PARTICIPANT_LEGAL_NAME, "Gaia-X European Association for Data and Cloud AISBL");
            expected.put(TrustFrameworkPoliciesGaiax2206.PARTICIPANT_REGISTRATION_NUMBER_TYPE, "local");
            expected.put(TrustFrameworkPoliciesGaiax2206.PARTICIPANT_REGISTRATION_NUMBER_NUMBER, "0762747721");
            expected.put(TrustFrameworkPoliciesGaiax2206.PARTICIPANT_HEADQUARTER_ADDRESS_COUNTRY_CODE, "BE");
            expected.put(TrustFrameworkPoliciesGaiax2206.PARTICIPANT_HEADQUARTER_ADDRESS_CODE, "BE-BRU");
            expected.put(TrustFrameworkPoliciesGaiax2206.PARTICIPANT_HEADQUARTER_ADDRESS_STREET_ADDRESS, "Avenue des Arts 6-9");
            expected.put(TrustFrameworkPoliciesGaiax2206.PARTICIPANT_HEADQUARTER_ADDRESS_POSTAL_CODE, "1210");
            expected.put(TrustFrameworkPoliciesGaiax2206.PARTICIPANT_LEGAL_ADDRESS_COUNTRY_CODE, "BE");
            expected.put(TrustFrameworkPoliciesGaiax2206.PARTICIPANT_LEGAL_ADDRESS_CODE, "BE-BRU");
            expected.put(TrustFrameworkPoliciesGaiax2206.PARTICIPANT_LEGAL_ADDRESS_STREET_ADDRESS, "Avenue des Arts 6-9");
            expected.put(TrustFrameworkPoliciesGaiax2206.PARTICIPANT_LEGAL_ADDRESS_POSTAL_CODE, "1210");
            expected.put(TrustFrameworkPoliciesGaiax2206.PARTICIPANT_LEI_CODE, "test");
            expected.put(TrustFrameworkPoliciesGaiax2206.PARTICIPANT_TERMS_AND_CONDITIONS, "70c1d713215f95191a11d38fe2341faed27d19e083917bc8732ca4fea4976700");

            return expected.entrySet()
                    .stream()
                    .map(entry -> Arguments.of(entry.getKey().getName(), entry.getValue()));
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

    public static Map<String, Object> loadVerifiableCredential() {
        try (var in = Thread.currentThread().getContextClassLoader().getResourceAsStream(VC_FILE_NAME)) {
            Objects.requireNonNull(in, "Failed to open file: " + VC_FILE_NAME);
            var tr = new TypeReference<HashMap<String, Object>>() {
            };
            return MAPPER.readValue(in, tr);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}