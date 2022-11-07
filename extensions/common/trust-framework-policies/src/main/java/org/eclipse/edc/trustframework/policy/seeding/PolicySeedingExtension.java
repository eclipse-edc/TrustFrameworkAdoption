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
import org.eclipse.edc.policy.engine.spi.PolicyEngine;
import org.eclipse.edc.policy.engine.spi.RuleBindingRegistry;
import org.eclipse.edc.policy.model.Permission;
import org.eclipse.edc.runtime.metamodel.annotation.Extension;
import org.eclipse.edc.runtime.metamodel.annotation.Inject;
import org.eclipse.edc.runtime.metamodel.annotation.Setting;
import org.eclipse.edc.spi.system.ServiceExtension;
import org.eclipse.edc.spi.system.ServiceExtensionContext;
import org.eclipse.edc.spi.system.configuration.Config;
import org.eclipse.edc.trustframework.policy.seeding.exception.PolicyLoadingException;
import org.eclipse.edc.trustframework.policy.seeding.function.VerifiableCredentialEvaluationFunction;
import org.eclipse.edc.trustframework.policy.seeding.model.PolicyEntry;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Extension for seeding {@link PolicyEngine} and {@link RuleBindingRegistry} based on a static config json file.
 */
@Extension(value = PolicySeedingExtension.NAME)
public class PolicySeedingExtension implements ServiceExtension {

    public static final String NAME = "Trust Framework Policy Seeding";

    @Setting(value = "Path to the static configuration file containing the policy entries")
    private static final String CONFIG_FILE_PATH_SETTING = "edc.trustframework.policies.file.path";

    private static final String DEFAULT_CONFIG_FILE_PATH = "gaiax-policies-22_10.json";

    @Inject
    private RuleBindingRegistry ruleBindingRegistry;

    @Inject
    private PolicyEngine policyEngine;

    @Override
    public String name() {
        return NAME;
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
        return Optional.ofNullable(config.getString(CONFIG_FILE_PATH_SETTING, null))
                .map(path -> loadFromFileSystem(mapper, path))
                .orElse(loadFromClassPath(mapper));
    }

    private List<PolicyEntry> loadFromClassPath(ObjectMapper mapper) {
        try (var is = this.getClass().getClassLoader().getResourceAsStream(DEFAULT_CONFIG_FILE_PATH)) {
            return mapper.readValue(is, new TypeReference<>() {
            });
        } catch (IOException e) {
            throw new PolicyLoadingException(e);
        }
    }


    private List<PolicyEntry> loadFromFileSystem(ObjectMapper mapper, String path) {
        Objects.requireNonNull(path);
        try (var is = Files.newInputStream(Path.of(path))) {
            return mapper.readValue(is, new TypeReference<>() {
            });
        } catch (IOException e) {
            throw new PolicyLoadingException(e);
        }
    }
}


