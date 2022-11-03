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

package org.eclipse.edc.trustframework.policy.seeding;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.eclipse.dataspaceconnector.policy.model.Permission;
import org.eclipse.dataspaceconnector.runtime.metamodel.annotation.Inject;
import org.eclipse.dataspaceconnector.runtime.metamodel.annotation.Setting;
import org.eclipse.dataspaceconnector.spi.policy.engine.PolicyEngine;
import org.eclipse.dataspaceconnector.spi.policy.engine.RuleBindingRegistry;
import org.eclipse.dataspaceconnector.spi.system.ServiceExtension;
import org.eclipse.dataspaceconnector.spi.system.ServiceExtensionContext;
import org.eclipse.dataspaceconnector.spi.system.configuration.Config;
import org.eclipse.edc.trustframework.policy.seeding.exception.PolicyConfigFileException;
import org.eclipse.edc.trustframework.policy.seeding.function.VerifiableCredentialEvaluationFunction;
import org.eclipse.edc.trustframework.policy.seeding.model.PolicyEntry;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Extension for seeding {@link PolicyEngine} and {@link RuleBindingRegistry} based on a static config json file.
 */
public class PolicySeedingExtension implements ServiceExtension {
    @Setting(value = "Path to the static configuration file containing the policy entries")
    private static final String CONFIG_FILE_PATH_SETTING = "edc.trustframework.policies.file.path";

    private static final String DEFAULT_CONFIG_FILE_PATH = "gaiax-policies-22_10.json";

    @Inject
    private RuleBindingRegistry ruleBindingRegistry;

    @Inject
    private PolicyEngine policyEngine;

    @Override
    public String name() {
        return "Trust Framework Policy Seeding";
    }

    @Override
    public void initialize(ServiceExtensionContext context) {
        var entries = loadEntries(context.getConfig(), context.getTypeManager().getMapper());
        entries.forEach(entry -> {
                    var function = new VerifiableCredentialEvaluationFunction(context.getMonitor(), context.getTypeManager().getMapper(), entry.getJsonPath());
                    policyEngine.registerFunction(entry.getScope(), Permission.class, entry.getType(), function);
                    ruleBindingRegistry.bind(entry.getType(), entry.getScope());
                }
        );
    }


    private List<PolicyEntry> loadEntries(Config config, ObjectMapper mapper) {
        var tr = new TypeReference<List<PolicyEntry>>() {
        };
        try (var is = Optional.ofNullable(config.getString(CONFIG_FILE_PATH_SETTING, null))
                .map(PolicySeedingExtension::fromFileSystem).orElse(fromClassPath())) {
            return mapper.readValue(is, tr);
        } catch (IOException e) {
            throw new PolicyConfigFileException("Failed to load policy entries", e);
        }
    }

    private static InputStream fromFileSystem(String path) {
        try (var is = Files.newInputStream(Path.of(path))) {
            return is;
        } catch (IOException e) {
            throw new PolicyConfigFileException(path, e);
        }
    }

    private InputStream fromClassPath() {
        return Objects.requireNonNull(this.getClass().getClassLoader().getResourceAsStream(DEFAULT_CONFIG_FILE_PATH));
    }
}


