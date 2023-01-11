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
 * Defines properties of a class on which Trust Framework policies may apply.
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ClassEvaluationProperty {

    String DEFAULT_GETTER_PREFIX = "get";

    /**
     * Defines prefix used to build the name of getter method based on the field name.
     * Typical getters will use the "get" prefix (default value), but others can be used, or even a no prefix:
     * <pre>
     * {@literal @ClassEvaluationProperty}(getterPrefix = "")
     * public enum MyClass {
     *     private final MyType aField;
     *
     *     public MyType aField() {
     *         return aField;
     *     }
     * }
     * </pre>
     */
    String getterPrefix() default DEFAULT_GETTER_PREFIX;
}
