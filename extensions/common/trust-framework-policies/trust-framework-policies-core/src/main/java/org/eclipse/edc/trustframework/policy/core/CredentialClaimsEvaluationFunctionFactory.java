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

import org.eclipse.edc.spi.EdcException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

public class CredentialClaimsEvaluationFunctionFactory {

    private static final String SEPARATOR = ".";

    public <CLAIMS_SHAPE> Map<String, Function<CLAIMS_SHAPE, Object>> create(Class<CLAIMS_SHAPE> shape) {
        var functions = new HashMap<String, Function<CLAIMS_SHAPE, Object>>();
        createFunctions(shape, "", m -> m, functions);
        return functions;
    }

    private static <CLAIMS_SHAPE> void createFunctions(Class<?> clazz, String path, Function<CLAIMS_SHAPE, Object> navigation, Map<String, Function<CLAIMS_SHAPE, Object>> functions) {
        Arrays.stream(clazz.getDeclaredFields())
                // filter out the non-static fields
                .filter(f -> !Modifier.isStatic(f.getModifiers()))
                .forEach(field -> {
                    boolean unwrap = false;
                    var fieldName = field.getName();
                    var displayName = fieldName;

                    var property = field.getAnnotation(FieldEvaluationProperty.class);
                    if (property != null) {
                        unwrap = property.unwrap();
                        displayName = !property.displayName().isBlank() ? property.displayName() : fieldName;
                    }

                    var getter = getFieldGetterMethod(clazz, fieldName);
                    if (unwrap) {
                        createFunctions(field.getType(), path + displayName + SEPARATOR, navigation.andThen(stepInto(getter)), functions);
                    } else {
                        functions.put(path + displayName, createFunction(getter, navigation));
                    }
                });
    }

    private static <CLAIMS_SHAPE> Function<CLAIMS_SHAPE, Object> createFunction(Method getter, Function<CLAIMS_SHAPE, Object> navigation) {
        return claims -> {
            var obj = navigation.apply(claims);
            return getFieldValue(getter, obj);
        };
    }

    private static Function<Object, Object> stepInto(Method getter) {
        return obj -> {
            try {
                return getFieldValue(getter, obj);
            } catch (IllegalArgumentException e) {
                throw new EdcException(e);
            }
        };
    }

    private static Object getFieldValue(Method getter, Object obj) {
        try {
            return getter.invoke(obj);
        } catch (InvocationTargetException | IllegalAccessException e) {
            throw new EdcException(e);
        }
    }

    private static Method getFieldGetterMethod(Class<?> clazz, String field) {
        var methodName = getFieldGetterMethodName(clazz, field);
        return Arrays.stream(clazz.getDeclaredMethods())
                .filter(method -> method.getName().equalsIgnoreCase(methodName))
                .findFirst()
                .orElseThrow(() -> new EdcException("No public getter found with name: " + methodName));
    }

    private static String getFieldGetterMethodName(Class<?> clazz, String field) {
        var prefix = Optional.ofNullable(clazz.getAnnotation(ClassEvaluationProperty.class))
                .map(ClassEvaluationProperty::getterPrefix)
                .orElse(ClassEvaluationProperty.DEFAULT_GETTER_PREFIX);
        if (prefix.isBlank()) {
            return field;
        }
        // upper case the first character of the field name
        return prefix + field.substring(0, 1).toUpperCase() + field.substring(1);
    }
}
