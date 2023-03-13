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

package org.eclipse.edc.trustframework.policy.gaiax.participant.v2206.function;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.eclipse.edc.identityhub.spi.credentials.model.Credential;
import org.eclipse.edc.spi.monitor.Monitor;
import org.eclipse.edc.trustframework.policy.core.CredentialClaimsEvaluationFunction;
import org.eclipse.edc.trustframework.policy.gaiax.participant.v2206.claims.GaiaxParticipant2206Claims;

import java.util.function.Function;
import java.util.function.Predicate;

public class GaiaxParticipant2206ClaimsEvaluationFunction extends CredentialClaimsEvaluationFunction<GaiaxParticipant2206Claims> {

    private static final String GAIAX_2206_CREDENTIAL_CLAIMS_SHAPE_CONTEXT = "https://registry.gaia-x.eu/v2206/api/shape";
    private static final String GAIAX_2206_LEGAL_PERSON_TYPE = "LegalPerson";

    public GaiaxParticipant2206ClaimsEvaluationFunction(Monitor monitor, ObjectMapper mapper, Function<GaiaxParticipant2206Claims, Object> navigation) {
        super(monitor, mapper, navigation);
    }

    @Override
    protected Predicate<Credential> credentialFilter() {
        return credential -> credential.getContexts().contains(GAIAX_2206_CREDENTIAL_CLAIMS_SHAPE_CONTEXT) &&
                credential.getTypes().contains(GAIAX_2206_LEGAL_PERSON_TYPE);
    }

    @Override
    protected Class<GaiaxParticipant2206Claims> getClaimsShapeType() {
        return GaiaxParticipant2206Claims.class;
    }
}
