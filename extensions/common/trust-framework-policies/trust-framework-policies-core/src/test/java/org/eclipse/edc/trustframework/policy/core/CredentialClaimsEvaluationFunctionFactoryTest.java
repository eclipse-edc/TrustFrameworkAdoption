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

import org.eclipse.edc.trustframework.policy.core.testfixtures.ParentPojo;
import org.eclipse.edc.trustframework.policy.core.testfixtures.PojoFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class CredentialClaimsEvaluationFunctionFactoryTest {

    private final ParentPojo pojo = PojoFactory.createRandom();

    private Map<String, Function<ParentPojo, Object>> functions;

    @BeforeEach
    public void setUp() {
        functions = new CredentialClaimsEvaluationFunctionFactory().create(ParentPojo.class);
    }

    @Test
    void verifyUseDisplayName() {
        assertExistAndEvaluatesTo("test:name", pojo.getName());
        assertThat(functions.containsKey("name")).isFalse();
    }

    @Test
    void verifyUnwrapFields() {
        assertThat(functions.containsKey("child")).isFalse();
        assertExistAndEvaluatesTo("child.value", pojo.getChild().getValue());

        assertThat(functions.containsKey("child.subchild")).isFalse();
        assertExistAndEvaluatesTo("child.subchild.values", pojo.getChild().getSubchild().values());
    }

    @Test
    void verifyIgnoreStaticField() {
        assertThat(functions.containsKey("NAME_FIELD")).isFalse();
    }

    private void assertExistAndEvaluatesTo(String path, Object expected) {
        var function = functions.get(path);
        assertNotNull(function);
        assertThat(function.apply(pojo)).isEqualTo(expected);
    }
}