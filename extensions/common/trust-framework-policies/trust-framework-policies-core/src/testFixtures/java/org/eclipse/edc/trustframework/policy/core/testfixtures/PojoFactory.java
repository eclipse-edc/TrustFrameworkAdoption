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

package org.eclipse.edc.trustframework.policy.core.testfixtures;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;

public class PojoFactory {

    private static final Random RANDOM = new Random();

    private PojoFactory() {
    }

    @JsonIgnore
    public static ParentPojo createRandom() {
        var subchildBuilder = SubchildPojo.Builder.newInstance();
        var values = RANDOM.ints(3)
                .mapToObj(i -> UUID.randomUUID().toString())
                .collect(Collectors.toList());
        subchildBuilder.values(values);
        var child = ChildPojo.Builder.newInstance()
                .value(RANDOM.nextInt())
                .subchild(subchildBuilder.build())
                .build();
        return ParentPojo.Builder.newInstance()
                .name(UUID.randomUUID().toString())
                .child(child)
                .build();
    }
}
