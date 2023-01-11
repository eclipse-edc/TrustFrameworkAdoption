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

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import org.eclipse.edc.trustframework.policy.core.FieldEvaluationProperty;

@JsonDeserialize(builder = ParentPojo.Builder.class)
public class ParentPojo {

    private static final String NAME_FIELD = "test:name";

    @FieldEvaluationProperty(displayName = NAME_FIELD)
    @JsonProperty(NAME_FIELD)
    private String name;

    @FieldEvaluationProperty(unwrap = true)
    private ChildPojo child;

    private ParentPojo() {
    }

    public String getName() {
        return name;
    }

    public ChildPojo getChild() {
        return child;
    }

    @JsonPOJOBuilder(withPrefix = "")
    public static class Builder {
        private final ParentPojo pojo;

        private Builder(ParentPojo pojo) {
            this.pojo = pojo;
        }

        @JsonCreator
        public static ParentPojo.Builder newInstance() {
            return new ParentPojo.Builder(new ParentPojo());
        }

        @JsonProperty(NAME_FIELD)
        public Builder name(String name) {
            pojo.name = name;
            return this;
        }

        public Builder child(ChildPojo child) {
            pojo.child = child;
            return this;
        }

        public ParentPojo build() {
            return pojo;
        }
    }
}
