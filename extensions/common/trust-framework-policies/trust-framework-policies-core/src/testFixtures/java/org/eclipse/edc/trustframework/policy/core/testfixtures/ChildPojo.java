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
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import org.eclipse.edc.trustframework.policy.core.FieldEvaluationProperty;

@JsonDeserialize(builder = ChildPojo.Builder.class)
public class ChildPojo {

    private int value;

    @FieldEvaluationProperty(unwrap = true)
    private SubchildPojo subchild;

    private ChildPojo() {
    }

    public int getValue() {
        return value;
    }

    public SubchildPojo getSubchild() {
        return subchild;
    }

    @JsonPOJOBuilder(withPrefix = "")
    public static class Builder {
        private final ChildPojo pojo;

        private Builder(ChildPojo pojo) {
            this.pojo = pojo;
        }

        @JsonCreator
        public static ChildPojo.Builder newInstance() {
            return new Builder(new ChildPojo());
        }

        public Builder value(int value) {
            pojo.value = value;
            return this;
        }

        public Builder subchild(SubchildPojo subchild) {
            pojo.subchild = subchild;
            return this;
        }

        public ChildPojo build() {
            return pojo;
        }
    }
}
