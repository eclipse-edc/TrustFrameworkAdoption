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

import org.eclipse.dataspaceconnector.spi.EdcException;

public class PolicyConfigFileException extends EdcException {
    public PolicyConfigFileException(String message) {
        super(message);
    }

    public PolicyConfigFileException(String message, Throwable cause) {
        super(message, cause);
    }
}
