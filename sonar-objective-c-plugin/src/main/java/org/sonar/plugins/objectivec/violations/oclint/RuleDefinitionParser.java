/*
 * Copyright (c) 2018 Tobias Raatiniemi
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.sonar.plugins.objectivec.violations.oclint;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

class RuleDefinitionParser {
    private static final Logger LOGGER = LoggerFactory.getLogger(OCLintRulesDefinition.class);

    private static boolean isLineIgnored(String line) {
        return line.matches("\\=.*") || line.matches("Priority:.*");
    }

    private static boolean isLineSeparator(String line) {
        return line.matches("[\\-]{4,}.*");
    }

    private static boolean isSummary(String line) {
        return line.matches("Summary:.*");
    }

    private static boolean isCategory(String line) {
        return line.matches("Category:.*");
    }

    private static boolean isSeverity(String line) {
        return line.matches("Severity:.*");
    }

    RuleDefinitionParser() {
    }

    Set<RuleDefinition> parseRuleDefinitionsFromLines(@Nonnull List<String> listLines) {
        Set<RuleDefinition> rulesDefinitions = new LinkedHashSet<>();

        String previousLine = null;

        RuleDefinition.Builder builder = RuleDefinition.builder();
        for (String line : listLines) {
            if (isLineIgnored(line)) {
                previousLine = line;
                continue;
            }

            if (isLineSeparator(line)) {
                LOGGER.debug("RuleDefinition found : {}", previousLine);

                builder = RuleDefinition.builder();
                builder.setKey(Objects.requireNonNull(previousLine));
                String name = StringUtils.capitalize(previousLine);
                builder.setName(Objects.requireNonNull(name));
                previousLine = line;
                continue;
            }

            if (isSummary(line)) {
                builder.setDescription(line.substring(line.indexOf(':') + 1));
                previousLine = line;
                continue;
            }

            if (isCategory(line)) {
                rulesDefinitions.add(builder.build());
                previousLine = line;
                continue;
            }

            if (isSeverity(line)) {
                final String severity = line.substring("Severity: ".length());
                builder.setSeverity(OCLintRuleSeverity.valueOfInt(Integer.valueOf(severity)).name());
                previousLine = line;
                continue;
            }

            previousLine = line;
        }

        return rulesDefinitions;
    }
}
