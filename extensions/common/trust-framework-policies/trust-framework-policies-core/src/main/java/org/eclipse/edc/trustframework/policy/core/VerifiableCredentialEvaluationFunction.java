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
import com.jayway.jsonpath.JsonPath;
import org.eclipse.edc.policy.engine.spi.AtomicConstraintFunction;
import org.eclipse.edc.policy.engine.spi.PolicyContext;
import org.eclipse.edc.policy.model.Operator;
import org.eclipse.edc.policy.model.Permission;
import org.eclipse.edc.spi.result.AbstractResult;
import org.eclipse.edc.spi.result.Result;
import org.eclipse.edc.trustframework.credentials.spi.VerifiableCredential;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * A function for evaluating Verifiable Credentials returned by the <a href="https://github.com/eclipse-dataspaceconnector/IdentityHub/tree/main/docs/developer/decision-records/2022-07-01-get-claims">Identity Hub</a>.
 * Each individual evaluation function defines a constraint on the value(s) that one claim can take. The path to the claim on which the constraint applies is defined through the
 * <a href="https://github.com/json-path/JsonPath">JsonPath Java DSL</a>
 */
public class VerifiableCredentialEvaluationFunction implements AtomicConstraintFunction<Permission> {

    private static final String VERIFIABLE_CREDENTIALS_KEY = "vc";

    private final ObjectMapper mapper;
    private final String jsonPath;

    public VerifiableCredentialEvaluationFunction(ObjectMapper mapper, String jsonPath) {
        this.mapper = mapper;
        this.jsonPath = jsonPath;
    }

    @Override
    public boolean evaluate(Operator operator, Object rightValue, Permission rule, PolicyContext context) {
        var claims = context.getParticipantAgent().getClaims();
        var values = claims.values().stream()
                .map(this::toVerifiableCredential)
                .filter(AbstractResult::succeeded)
                .map(result -> extractVerifiableCredentialClaim(result.getContent()))
                .collect(Collectors.toList());
        return evaluate(values, rightValue, operator);
    }

    private boolean evaluate(List<String> left, Object right, Operator operator) {
        switch (operator) {
            case EQ:
                return left.contains(right);
            case NEQ:
                return !left.contains(right);
            case IN:
                return !Collections.disjoint((Collection<?>) right, left);
            default:
                return false;
        }
    }

    private Result<VerifiableCredential> toVerifiableCredential(Object object) {
        try {
            var vc = ((Map<String, Object>) object).get(VERIFIABLE_CREDENTIALS_KEY);
            Objects.requireNonNull(vc, "Missing 'vc' field in claims");
            return Result.success(mapper.convertValue(vc, VerifiableCredential.class));
        } catch (Exception e) {
            return Result.failure("Failed to extract VC: " + e.getMessage());
        }
    }

    @Nullable
    private String extractVerifiableCredentialClaim(VerifiableCredential vc) {
        try {
            return JsonPath.read(vc.getCredentialSubject(), this.jsonPath);
        } catch (Exception e) {
            return null;
        }
    }
}
