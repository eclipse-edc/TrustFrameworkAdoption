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

package org.eclipse.edc.trustframework.policy.seeding.exception;

import org.eclipse.edc.spi.EdcException;

public class PolicyEvaluationException extends EdcException {
    public PolicyEvaluationException(String message) {
        super(message);
    }
}
