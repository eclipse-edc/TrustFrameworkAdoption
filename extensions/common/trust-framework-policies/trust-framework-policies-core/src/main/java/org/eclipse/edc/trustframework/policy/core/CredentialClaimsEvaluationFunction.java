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

package org.eclipse.edc.trustframework.policy.core;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.eclipse.edc.identityhub.spi.credentials.model.Credential;
import org.eclipse.edc.policy.engine.spi.AtomicConstraintFunction;
import org.eclipse.edc.policy.engine.spi.PolicyContext;
import org.eclipse.edc.policy.model.Operator;
import org.eclipse.edc.policy.model.Permission;
import org.eclipse.edc.spi.monitor.Monitor;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * A generic function for evaluating credential subject claims of <a href="https://www.w3.org/TR/vc-data-model/#credentials">W3C credential</a>.
 */
public abstract class CredentialClaimsEvaluationFunction<CLAIMS_SHAPE> implements AtomicConstraintFunction<Permission> {

    private final Monitor monitor;
    private final ObjectMapper mapper;
    private final Function<CLAIMS_SHAPE, Object> navigation;

    protected CredentialClaimsEvaluationFunction(Monitor monitor, ObjectMapper mapper, Function<CLAIMS_SHAPE, Object> navigation) {
        this.monitor = monitor;
        this.mapper = mapper;
        this.navigation = navigation;
    }

    protected abstract Predicate<Credential> credentialFilter();

    protected abstract Class<CLAIMS_SHAPE> getClaimsShapeType();

    @Override
    public boolean evaluate(Operator operator, Object rightValue, Permission rule, PolicyContext context) {
        try {
            return evaluate(leftOperand(context), rightOperand(rightValue), operator);
        } catch (Exception e) {
            monitor.warning("Function evaluation failed", e);
            return false;
        }
    }

    private Set<Object> leftOperand(PolicyContext context) {
        return context.getParticipantAgent().getClaims().values().stream()
                .map(this::toCredential)
                .filter(Objects::nonNull)
                .filter(credentialFilter())
                .map(credential -> credential.getCredentialSubject().getClaims())
                .map(this::shapeClaims)
                .map(navigation)
                .filter(Objects::nonNull)
                .flatMap(this::flatten)
                .collect(Collectors.toSet());
    }

    private Stream<Object> flatten(Object obj) {
        if (obj instanceof Collection) {
            return ((Collection) obj).stream();
        }
        return Stream.of(obj);
    }

    private CLAIMS_SHAPE shapeClaims(Map<String, Object> claims) {
        return mapper.convertValue(claims, getClaimsShapeType());
    }

    @Nullable
    private Credential toCredential(Object object) {
        try {
            return cast(object);
        } catch (Exception e) {
            monitor.warning("Cast to Credential failed", e);
            return null;
        }
    }

    private static Set<Object> rightOperand(Object rightValue) {
        if (rightValue instanceof Collection) {
            return new HashSet<>((Collection<?>) rightValue);
        }
        return Collections.singleton(rightValue);
    }

    private static boolean evaluate(Set<Object> left, Set<Object> right, Operator operator) {
        switch (operator) {
            case EQ:
                return left.equals(right);
            case NEQ:
                return !left.equals(right);
            case IN:
                return !Collections.disjoint(right, left);
            default:
                return false;
        }
    }

    private static <T> T cast(Object o) {
        return (T) o;
    }
}
