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

final class RuleDefinitionParser {
    private static final Logger LOGGER = LoggerFactory.getLogger(OCLintRulesDefinition.class);

    private static boolean isLineIgnored(@Nonnull String line) {
        return line.matches("\\=.*") || line.matches("Priority:.*");
    }

    private static boolean isLineSeparator(@Nonnull String line) {
        return line.matches("[\\-]{4,}.*");
    }

    private static boolean isSummary(@Nonnull String line) {
        return line.matches("Summary:.*");
    }

    private static boolean isCategory(@Nonnull String line) {
        return line.matches("Category:.*");
    }

    private static boolean isSeverity(@Nonnull String line) {
        return line.matches("Severity:.*");
    }

    private RuleDefinitionParser() {
    }

    static RuleDefinitionParser create() {
        return new RuleDefinitionParser();
    }

    @Nonnull
    Set<RuleDefinition> parseRuleDefinitionsFromLines(@Nonnull List<String> lines) {
        Set<RuleDefinition> rulesDefinitions = new LinkedHashSet<>();

        String previousLine = null;
        boolean inDescription = false;

        RuleDefinition.Builder builder = RuleDefinition.builder();
        for (String line : lines) {
            if (isLineIgnored(line)) {
                inDescription = false;
                previousLine = line;
                continue;
            }

            if (isLineSeparator(line)) {
                LOGGER.debug("RuleDefinition found : {}", previousLine);

                builder = RuleDefinition.builder();
                builder.setKey(Objects.requireNonNull(previousLine));
                String name = StringUtils.capitalize(previousLine);
                builder.setName(Objects.requireNonNull(name));
                inDescription = false;
                previousLine = line;
                continue;
            }

            if (isSummary(line)) {
                String summary = line.substring(line.indexOf(':') + 1);
                builder.setDescription("<p>" + summary + "</p>");
                inDescription = true;
                previousLine = line;
                continue;
            }

            if (isCategory(line)) {
                rulesDefinitions.add(builder.build());
                inDescription = false;
                previousLine = line;
                continue;
            }

            if (isSeverity(line)) {
                final String severity = line.substring("Severity: ".length());
                builder.setSeverity(RuleSeverity.valueOfInt(Integer.valueOf(severity)).name());
                inDescription = false;
                previousLine = line;
                continue;
            }

            if (inDescription) {
                builder.appendToDescription(line);
            }
            previousLine = line;
        }

        return rulesDefinitions;
    }
}
