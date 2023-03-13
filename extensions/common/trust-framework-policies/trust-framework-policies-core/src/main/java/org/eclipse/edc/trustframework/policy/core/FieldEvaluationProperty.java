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

package org.eclipse.edc.trustframework.policy.core;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Defines properties of a field on which Trust Framework policies may apply.
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface FieldEvaluationProperty {

    /**
     * Defines how the field subjected to evaluation should be referred to. If not provided, then
     * the field name is used.
     */
    String displayName() default "";

    /**
     * Indicates whether we do not want to evaluate the object represented as is,
     * but instead evaluate its own internal fields, i.e. recursive field evaluation.
     */
    boolean unwrap() default false;
}
