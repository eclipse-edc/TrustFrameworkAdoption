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
 *       GAIA-X - initial implementation
 *
 */

package org.eclipse.edc.trustframework.policy.core.compliance;

import org.eclipse.edc.identityhub.spi.credentials.model.Credential;
import org.eclipse.edc.policy.engine.spi.AtomicConstraintFunction;
import org.eclipse.edc.policy.engine.spi.PolicyContext;
import org.eclipse.edc.policy.model.Operator;
import org.eclipse.edc.policy.model.Permission;
import org.eclipse.edc.spi.monitor.Monitor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A function for evaluating Gaia-X compliance credentials.
 */
public class GaiaxComplianceConstraintFunction implements AtomicConstraintFunction<Permission> {

    private static final String GAIAX_COMPLIANCE_ID = "id";
    private static final String GAIAX_COMPLIANCE_TYPE = "type";
    private static final String GAIAX_COMPLIANCE_INTEGRITY = "integrity";
    private static final String GAIAX_COMPLIANCE_TYPE_VALUE = "gx:compliance";
    private final Monitor monitor;

    public GaiaxComplianceConstraintFunction(Monitor monitor) {
        this.monitor = monitor;
    }

    /**
     * Performs the evaluation
     *
     * @param operator the operator
     * @param rightValue the right-side expression for the constraint; the concrete type may be a string, primitive or object such as a JSON-LD encoded collection.
     * @param rule the rule associated with the constraint
     * @param context the policy context
     * @return match with right value and operator and validity of Gaia-x compliance credential
     */
    @Override
    public boolean evaluate(Operator operator, Object rightValue, Permission rule, PolicyContext context) {
        var complianceCredentials = getComplianceCredentials(context.getParticipantAgent().getClaims());
        return switch (operator) {
            case EQ -> rightValue.equals(areComplianceCredentialsValid(complianceCredentials));
            case NEQ -> !rightValue.equals(areComplianceCredentialsValid(complianceCredentials));
            default -> {
                monitor.warning("Provided operator is not implemented");
                yield false;
            }
        };
    }

    private boolean areComplianceCredentialsValid(List<Map<String, String>> complianceCredentials) {
        return !complianceCredentials.isEmpty() && complianceCredentials.stream()
                .noneMatch(credential -> credential.get(GAIAX_COMPLIANCE_ID).isBlank() ||
                        credential.get(GAIAX_COMPLIANCE_INTEGRITY).isBlank());
    }

    private List<Map<String, String>> getComplianceCredentials(Map<String, Object> claims) {
        return claims.values().stream()
                .map(this::buildComplianceObject)
                .filter(vc -> GAIAX_COMPLIANCE_TYPE_VALUE.equals(vc.get(GAIAX_COMPLIANCE_TYPE)))
                .filter(vc -> vc.get(GAIAX_COMPLIANCE_ID) != null &&
                        vc.get(GAIAX_COMPLIANCE_INTEGRITY) != null)
                .toList();
    }

    private Map<String, String> buildComplianceObject(Object object) {
        var complianceCredentialSubject = new HashMap<String, String>();
        var credential = (Credential) object;
        complianceCredentialSubject.put(GAIAX_COMPLIANCE_ID, credential.getCredentialSubject().getId());
        complianceCredentialSubject.put(GAIAX_COMPLIANCE_TYPE, getCredentialProperty(credential, GAIAX_COMPLIANCE_TYPE));
        complianceCredentialSubject.put(GAIAX_COMPLIANCE_INTEGRITY, getCredentialProperty(credential, GAIAX_COMPLIANCE_INTEGRITY));
        return complianceCredentialSubject;
    }

    private String getCredentialProperty(Object object, String claimKey) {
        var credential = (Credential) object;
        var claims = credential.getCredentialSubject().getClaims();
        return (String) claims.get(claimKey);
    }

}
