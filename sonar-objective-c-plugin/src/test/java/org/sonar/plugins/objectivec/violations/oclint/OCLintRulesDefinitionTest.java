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

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.sonar.api.server.rule.RulesDefinition;
import org.sonar.api.server.rule.RulesDefinition.NewRepository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(JUnit4.class)
public class OCLintRulesDefinitionTest {
    private final Path resourcePath = Paths.get("src", "test", "resources", "oclint");

    private RulesDefinition.Context context;
    private OCLintRulesDefinition rulesDefinition;

    @Before
    public void setUp() {
        context = new RulesDefinition.Context();
        rulesDefinition = new OCLintRulesDefinition();
    }

    @Test
    public void createRepository() {
        NewRepository actual = rulesDefinition.createRepository(context);

        assertEquals(OCLintRulesDefinition.REPOSITORY_KEY, actual.key());
    }

    @Test
    public void loadRules() throws IOException {
        NewRepository repository = rulesDefinition.createRepository(context);

        rulesDefinition.loadRules(repository);

        assertEquals(70, repository.rules().size());
    }

    @Test
    public void populateRepositoryWithRulesFromLines() throws IOException {
        NewRepository repository = rulesDefinition.createRepository(context);
        Path rulesPath = Paths.get(resourcePath.toString(), "rules.txt");
        List<String> lines = Files.lines(rulesPath)
                .collect(Collectors.toList());

        rulesDefinition.populateRepositoryWithRulesFromLines(repository, lines);

        assertEquals(2, repository.rules().size());
        assertNotNull(repository.rule("avoid branching statement as last in loop"));
        assertNotNull(repository.rule("bitwise operator in conditional"));
    }
}
