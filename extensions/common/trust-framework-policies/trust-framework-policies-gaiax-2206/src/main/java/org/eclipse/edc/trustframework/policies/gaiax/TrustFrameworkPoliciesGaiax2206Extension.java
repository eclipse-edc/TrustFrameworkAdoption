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

package org.eclipse.edc.trustframework.policies.gaiax;

import org.eclipse.edc.policy.engine.spi.PolicyEngine;
import org.eclipse.edc.policy.engine.spi.RuleBindingRegistry;
import org.eclipse.edc.policy.model.Permission;
import org.eclipse.edc.runtime.metamodel.annotation.Extension;
import org.eclipse.edc.runtime.metamodel.annotation.Inject;
import org.eclipse.edc.spi.system.ServiceExtension;
import org.eclipse.edc.spi.system.ServiceExtensionContext;
import org.eclipse.edc.spi.types.TypeManager;
import org.eclipse.edc.trustframework.policy.core.VerifiableCredentialEvaluationFunction;

@Extension(value = TrustFrameworkPoliciesGaiax2206Extension.NAME)
public class TrustFrameworkPoliciesGaiax2206Extension implements ServiceExtension {

    private static final String ALL_SCOPE = "*";

    public static final String NAME = "Trust Framework Policies Gaia-X 2206";

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
        TrustFrameworkPoliciesGaiax2206.stream().forEach(this::loadEntry);
    }

    public void loadEntry(TrustFrameworkPoliciesGaiax2206 entry) {
        var function = new VerifiableCredentialEvaluationFunction(typeManager.getMapper(), entry.getJsonPath());
        policyEngine.registerFunction(ALL_SCOPE, Permission.class, entry.getName(), function);
        ruleBindingRegistry.bind(entry.getName(), ALL_SCOPE);
    }
}


