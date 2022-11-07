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

package org.eclipse.edc.trustframework.policy.seeding.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * POJO for seeding evaluation function into the EDC.
 */
@JsonDeserialize(builder = PolicyEntry.Builder.class)
public class PolicyEntry {

    private String type;
    private String scope;
    private String jsonPath;

    private PolicyEntry() {
    }

    @NotNull
    public String getType() {
        return type;
    }

    @NotNull
    public String getScope() {
        return scope;
    }

    @NotNull
    public String getJsonPath() {
        return jsonPath;
    }

    @JsonPOJOBuilder(withPrefix = "")
    public static class Builder {

        private final PolicyEntry entry;

        private Builder() {
            entry = new PolicyEntry();
        }

        public static Builder newInstance() {
            return new Builder();
        }

        public PolicyEntry build() {
            Objects.requireNonNull(entry.getType());
            Objects.requireNonNull(entry.getScope());
            Objects.requireNonNull(entry.getJsonPath());
            return entry;
        }

        public Builder type(String type) {
            entry.type = type;
            return this;
        }

        public Builder scope(String scope) {
            entry.scope = scope;
            return this;
        }

        public Builder jsonPath(String jsonPath) {
            entry.jsonPath = jsonPath;
            return this;
        }
    }
}
