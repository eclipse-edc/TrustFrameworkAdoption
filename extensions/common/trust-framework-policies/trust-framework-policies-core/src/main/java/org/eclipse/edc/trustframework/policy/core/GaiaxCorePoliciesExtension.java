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
 *       GAIA-X - initial API and implementation
 *
 */

package org.eclipse.edc.trustframework.policy.core;

import org.eclipse.edc.policy.engine.spi.PolicyEngine;
import org.eclipse.edc.policy.engine.spi.RuleBindingRegistry;
import org.eclipse.edc.policy.model.Permission;
import org.eclipse.edc.runtime.metamodel.annotation.Extension;
import org.eclipse.edc.runtime.metamodel.annotation.Inject;
import org.eclipse.edc.spi.monitor.Monitor;
import org.eclipse.edc.spi.system.ServiceExtension;
import org.eclipse.edc.spi.system.ServiceExtensionContext;
import org.eclipse.edc.trustframework.policy.core.compliance.GaiaxComplianceConstraintFunction;

import static org.eclipse.edc.policy.engine.spi.PolicyEngine.ALL_SCOPES;

@Extension(value = GaiaxCorePoliciesExtension.GAIA_X_CORE_POLICIES_NAME)
public class GaiaxCorePoliciesExtension implements ServiceExtension {

    private static final String ALL_SCOPE = "*";

    public static final String GAIA_X_CORE_POLICIES_NAME = "Gaia-X Core Policies";

    private static final String GAIAX_COMPLIANCE = "gx:compliance";

    @Inject
    private PolicyEngine policyEngine;

    @Inject
    private RuleBindingRegistry ruleBindingRegistry;

    @Inject
    private Monitor monitor;

    @Override
    public String name() {
        return GAIA_X_CORE_POLICIES_NAME;
    }

    @Override
    public void initialize(ServiceExtensionContext context) {
        policyEngine.registerFunction(ALL_SCOPES, Permission.class, GAIAX_COMPLIANCE, new GaiaxComplianceConstraintFunction(monitor));
        ruleBindingRegistry.bind(GAIAX_COMPLIANCE, ALL_SCOPE);
    }
}
