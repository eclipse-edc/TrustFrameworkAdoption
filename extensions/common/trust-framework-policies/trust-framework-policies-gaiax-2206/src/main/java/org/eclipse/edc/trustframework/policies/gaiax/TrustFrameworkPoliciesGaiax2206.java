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

package org.eclipse.edc.trustframework.policies.gaiax;

import java.util.stream.Stream;

/**
 * Defines set of policies applicable to Gaia-X Self-Description in <a href="https://registry.gaia-x.eu/v2206/api/shape">version 2206</a>.
 */
public enum TrustFrameworkPoliciesGaiax2206 {

    PARTICIPANT_NAME("gx-participant:name", "$.gx-participant:name"),

    PARTICIPANT_LEGAL_NAME("gx-participant:legalName", "$.gx-participant:legalName"),

    PARTICIPANT_REGISTRATION_NUMBER_TYPE("gx-participant:registrationNumberType", "$.gx-participant:registrationNumber.gx-participant:registrationNumberType"),

    PARTICIPANT_REGISTRATION_NUMBER_NUMBER("gx-participant:registrationNumberNumber", "$.gx-participant:registrationNumber.gx-participant:registrationNumberNumber"),

    PARTICIPANT_HEADQUARTER_ADDRESS_COUNTRY_CODE("gx-participant:headquarterAddress:addressCountryCode", "$.gx-participant:headquarterAddress.gx-participant:addressCountryCode"),
    PARTICIPANT_HEADQUARTER_ADDRESS_CODE("gx-participant:headquarterAddress:addressCode", "$.gx-participant:headquarterAddress.gx-participant:addressCode"),
    PARTICIPANT_HEADQUARTER_ADDRESS_STREET_ADDRESS("gx-participant:headquarterAddress:streetAddress", "$.gx-participant:headquarterAddress.gx-participant:streetAddress"),
    PARTICIPANT_HEADQUARTER_ADDRESS_POSTAL_CODE("gx-participant:headquarterAddress:postalCode", "$.gx-participant:headquarterAddress.gx-participant:postalCode"),

    PARTICIPANT_LEGAL_ADDRESS_COUNTRY_CODE("gx-participant:legalAddress:addressCountryCode", "$.gx-participant:legalAddress.gx-participant:addressCountryCode"),
    PARTICIPANT_LEGAL_ADDRESS_CODE("gx-participant:legalAddress:addressCode", "$.gx-participant:legalAddress.gx-participant:addressCode"),
    PARTICIPANT_LEGAL_ADDRESS_STREET_ADDRESS("gx-participant:legalAddress:streetAddress", "$.gx-participant:legalAddress.gx-participant:streetAddress"),
    PARTICIPANT_LEGAL_ADDRESS_POSTAL_CODE("gx-participant:legalAddress:postalCode", "$.gx-participant:legalAddress.gx-participant:postalCode"),

    PARTICIPANT_LEI_CODE("gx-participant:leiCode", "$.gx-participant:leiCode"),

    PARTICIPANT_TERMS_AND_CONDITIONS("gx-participant:termsAndConditions", "$.gx-participant:termsAndConditions");

    private final String name;
    private final String jsonPath;

    /**
     * Constructor for the Policy object.
     *
     * @param name     name that will be used as type for the {@link org.eclipse.edc.policy.model.Policy} and the associated evaluation function.
     * @param jsonPath json path of the field that should be evaluated expressed in <a href="https://github.com/json-path/JsonPath">JsonPath Java DSL</a>
     */
    TrustFrameworkPoliciesGaiax2206(String name, String jsonPath) {
        this.name = name;
        this.jsonPath = jsonPath;
    }

    public String getName() {
        return name;
    }

    public String getJsonPath() {
        return jsonPath;
    }

    public static Stream<TrustFrameworkPoliciesGaiax2206> stream() {
        return Stream.of(TrustFrameworkPoliciesGaiax2206.values());
    }
}
