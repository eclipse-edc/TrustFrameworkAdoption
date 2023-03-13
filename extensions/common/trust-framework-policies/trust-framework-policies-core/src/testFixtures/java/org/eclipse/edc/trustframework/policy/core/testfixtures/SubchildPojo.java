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
import org.eclipse.edc.trustframework.policy.core.ClassEvaluationProperty;

import java.util.ArrayList;
import java.util.List;

@ClassEvaluationProperty(getterPrefix = "")
@JsonDeserialize(builder = SubchildPojo.Builder.class)
public class SubchildPojo {

    private final List<String> values = new ArrayList<>();

    private SubchildPojo() {
    }

    @JsonProperty("values")
    public List<String> values() {
        return values;
    }

    @JsonPOJOBuilder(withPrefix = "")
    public static class Builder {
        private final SubchildPojo pojo;

        private Builder(SubchildPojo pojo) {
            this.pojo = pojo;
        }

        @JsonCreator
        public static SubchildPojo.Builder newInstance() {
            return new SubchildPojo.Builder(new SubchildPojo());
        }

        @JsonProperty("values")
        public Builder values(List<String> values) {
            pojo.values.addAll(values);
            return this;
        }

        public SubchildPojo build() {
            return pojo;
        }
    }
}
