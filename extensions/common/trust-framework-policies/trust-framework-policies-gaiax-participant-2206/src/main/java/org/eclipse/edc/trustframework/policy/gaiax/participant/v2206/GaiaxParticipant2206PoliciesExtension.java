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

import org.eclipse.edc.policy.engine.spi.PolicyEngine;
import org.eclipse.edc.policy.engine.spi.RuleBindingRegistry;
import org.eclipse.edc.policy.model.Permission;
import org.eclipse.edc.runtime.metamodel.annotation.Extension;
import org.eclipse.edc.runtime.metamodel.annotation.Inject;
import org.eclipse.edc.spi.system.ServiceExtension;
import org.eclipse.edc.spi.system.ServiceExtensionContext;
import org.eclipse.edc.spi.types.TypeManager;
import org.eclipse.edc.trustframework.policy.core.CredentialClaimsEvaluationFunctionFactory;
import org.eclipse.edc.trustframework.policy.gaiax.participant.v2206.claims.GaiaxParticipant2206Claims;
import org.eclipse.edc.trustframework.policy.gaiax.participant.v2206.function.GaiaxParticipant2206ClaimsEvaluationFunction;

@Extension(value = GaiaxParticipant2206PoliciesExtension.NAME)
public class GaiaxParticipant2206PoliciesExtension implements ServiceExtension {

    private static final String ALL_SCOPE = "*";

    public static final String NAME = "Gaia-X Participant 2206 Policies";

    @Inject
    private PolicyEngine policyEngine;

    @Inject
    private RuleBindingRegistry ruleBindingRegistry;

    @Inject
    private TypeManager typeManager;

    @Override
    public String name() {
        return NAME;
    }

    @Override
    public void initialize(ServiceExtensionContext context) {
        var factory = new CredentialClaimsEvaluationFunctionFactory();
        factory.create(GaiaxParticipant2206Claims.class)
                .forEach((name, navigation) -> {
                    var function = new GaiaxParticipant2206ClaimsEvaluationFunction(context.getMonitor(), typeManager.getMapper(), navigation);
                    policyEngine.registerFunction(ALL_SCOPE, Permission.class, name, function);
                    ruleBindingRegistry.bind(name, ALL_SCOPE);
                });
    }
}


