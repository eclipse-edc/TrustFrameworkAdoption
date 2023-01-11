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

package org.eclipse.edc.trustframework.policy.gaiax.participant.v2206.claims;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import org.eclipse.edc.trustframework.policy.core.FieldEvaluationProperty;

@JsonDeserialize(builder = ParticipantRegistrationNumber.Builder.class)
public class ParticipantRegistrationNumber {

    private static final String TYPE_FIELD = "gx-participant:registrationNumberType";
    private static final String NUMBER_FIELD = "gx-participant:registrationNumberNumber";

    @JsonProperty(TYPE_FIELD)
    @FieldEvaluationProperty(displayName = TYPE_FIELD)
    private String type;

    @JsonProperty(NUMBER_FIELD)
    @FieldEvaluationProperty(displayName = NUMBER_FIELD)
    private String number;

    private ParticipantRegistrationNumber() {
    }

    public String getType() {
        return type;
    }

    public String getNumber() {
        return number;
    }

    @JsonPOJOBuilder(withPrefix = "")
    public static class Builder {
        private final ParticipantRegistrationNumber registrationNumber;

        private Builder(ParticipantRegistrationNumber registrationNumber) {
            this.registrationNumber = registrationNumber;
        }

        @JsonCreator
        public static ParticipantRegistrationNumber.Builder newInstance() {
            return new ParticipantRegistrationNumber.Builder(new ParticipantRegistrationNumber());
        }

        @JsonProperty(TYPE_FIELD)
        public Builder type(String type) {
            registrationNumber.type = type;
            return this;
        }

        @JsonProperty(NUMBER_FIELD)
        public Builder number(String number) {
            registrationNumber.number = number;
            return this;
        }

        public ParticipantRegistrationNumber build() {
            return registrationNumber;
        }
    }
}
