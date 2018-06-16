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
import java.util.List;

public final class OCLintRulesDefinition implements RulesDefinition {
    static final String REPOSITORY_KEY = "OCLint";
    static final String REPOSITORY_NAME = REPOSITORY_KEY;

    private static final Logger LOGGER = LoggerFactory.getLogger(OCLintRulesDefinition.class);
    private static final String RULES_FILE = "/org/sonar/plugins/oclint/rules.txt";

    private static final RuleDefinitionParser parser = new RuleDefinitionParser();

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

    void loadRules(NewRepository repository) throws IOException {
        Reader reader = new BufferedReader(new InputStreamReader(getClass()
                .getResourceAsStream(RULES_FILE), CharEncoding.UTF_8));

        final List<String> listLines = IOUtils.readLines(reader);

        populateRepositoryWithRulesFromLines(repository, listLines);
    }

    void populateRepositoryWithRulesFromLines(@Nonnull NewRepository repository, @Nonnull List<String> listLines) {
        parser.parseRuleDefinitionsFromLines(listLines)
                .forEach(ruleDefinition -> {
                    RulesDefinition.NewRule newRule = repository.createRule(ruleDefinition.getKey());
                    newRule.setName(ruleDefinition.getName());
                    newRule.setSeverity(ruleDefinition.getSeverity());
                    newRule.setHtmlDescription(ruleDefinition.getDescription());
                });
    }
}
