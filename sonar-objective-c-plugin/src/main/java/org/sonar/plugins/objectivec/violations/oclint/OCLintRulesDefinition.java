/*
 * Copyright © 2012 OCTO Technology, Backelite (${email})
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

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.CharEncoding;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.server.rule.RulesDefinition;
import org.sonar.plugins.objectivec.core.ObjectiveC;
import org.sonar.squidbridge.rules.SqaleXmlLoader;

import javax.annotation.Nonnull;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class OCLintRulesDefinition implements RulesDefinition {
    static final String REPOSITORY_KEY = "OCLint";
    static final String REPOSITORY_NAME = REPOSITORY_KEY;

    private static final Logger LOGGER = LoggerFactory.getLogger(OCLintRulesDefinition.class);
    private static final String RULES_FILE = "/org/sonar/plugins/oclint/rules.txt";

    @Override
    public void define(@Nonnull Context context) {
        NewRepository repository = createRepository(context);

        try {
            loadRules(repository);
        } catch (IOException e) {
            LOGGER.error("Failed to load OCLint rules", e);
        }

        SqaleXmlLoader.load(repository, "/com/sonar/sqale/oclint-model.xml");

        repository.done();
    }

    @Nonnull
    NewRepository createRepository(@Nonnull Context context) {
        return context.createRepository(REPOSITORY_KEY, ObjectiveC.KEY)
                .setName(REPOSITORY_NAME);
    }

    private void loadRules(NewRepository repository) throws IOException {
        Reader reader = new BufferedReader(new InputStreamReader(getClass()
                .getResourceAsStream(RULES_FILE), CharEncoding.UTF_8));

        final List<String> listLines = IOUtils.readLines(reader);

        populateRepositoryWithRulesFromLines(repository, listLines);
    }

    void populateRepositoryWithRulesFromLines(@Nonnull NewRepository repository, @Nonnull List<String> listLines) {
        parseRuleDefinitionsFromLines(listLines)
                .forEach(ruleDefinition -> {
                    RulesDefinition.NewRule newRule = repository.createRule(ruleDefinition.getKey());
                    newRule.setName(ruleDefinition.getName());
                    newRule.setSeverity(ruleDefinition.getSeverity());
                    newRule.setHtmlDescription(ruleDefinition.getDescription());
                });
    }

    Set<RuleDefinition> parseRuleDefinitionsFromLines(@Nonnull List<String> listLines) {
        Set<RuleDefinition> rulesDefinitions = new LinkedHashSet<>();

        String previousLine = null;
        boolean inDescription = false;
        RuleDefinition.Builder builder = RuleDefinition.builder();
        for (String line : listLines) {
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
                previousLine = line;
                continue;
            }

            if (isSummary(line)) {
                inDescription = true;

                builder.setDescription(line.substring(line.indexOf(':') + 1));
                previousLine = line;
                continue;
            }

            if (isCategory(line)) {
                inDescription = true;

                rulesDefinitions.add(builder.build());
                previousLine = line;
                continue;
            }

            if (isSeverity(line)) {
                inDescription = false;

                final String severity = line.substring("Severity: ".length());
                builder.setSeverity(OCLintRuleSeverity.valueOfInt(Integer.valueOf(severity)).name());
                previousLine = line;
                continue;
            }

            if (inDescription) {
                line = ruleDescriptionLink(line);
                String description = builder.getDescription();
                builder.setDescription(description + "<br>" + line);
            }
            previousLine = line;
        }

        return rulesDefinitions;
    }

    private boolean isLineIgnored(String line) {
        return line.matches("\\=.*") || line.matches("Priority:.*");
    }

    private boolean isLineSeparator(String line) {
        return line.matches("[\\-]{4,}.*");
    }

    private boolean isSummary(String line) {
        return line.matches("Summary:.*");
    }

    private boolean isCategory(String line) {
        return line.matches("Category:.*");
    }

    private boolean isSeverity(String line) {
        return line.matches("Severity:.*");
    }

    private String ruleDescriptionLink(final String line) {
        String result = line;
        final int indexOfLink = line.indexOf("http://");
        if (0 <= indexOfLink) {
            final String link = line.substring(indexOfLink);
            final StringBuilder htmlText = new StringBuilder("<a href=\"");
            htmlText.append(link);
            htmlText.append("\" target=\"_blank\">");
            htmlText.append(link);
            htmlText.append("</a>");
            result = htmlText.toString();
        }
        return result;
    }
}
