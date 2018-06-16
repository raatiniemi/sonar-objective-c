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

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;

@RunWith(JUnit4.class)
public class RuleDefinitionParserTest {
    private final Path resourcePath = Paths.get("src", "test", "resources", "oclint");

    private final RuleDefinitionParser parser = new RuleDefinitionParser();

    @Test
    public void parseRuleDefinitionsFromLines() throws IOException {
        Set<RuleDefinition> expected = new LinkedHashSet<>();
        expected.add(
                RuleDefinition.builder()
                        .setKey("avoid branching statement as last in loop")
                        .setName("Avoid branching statement as last in loop")
                        .setSeverity("MAJOR")
                        .setDescription(" Name: avoid branching statement as last in loop<br>")
                        .build()
        );
        expected.add(
                RuleDefinition.builder()
                        .setKey("bitwise operator in conditional")
                        .setName("Bitwise operator in conditional")
                        .setSeverity("CRITICAL")
                        .setDescription(" Name: bitwise operator in conditional<br>")
                        .build()
        );
        Path rulesPath = Paths.get(resourcePath.toString(), "rules.txt");
        List<String> lines = Files.lines(rulesPath)
                .collect(Collectors.toList());

        Set<RuleDefinition> actual = parser.parseRuleDefinitionsFromLines(lines);

        assertEquals(expected, actual);
    }

    @Test
    public void parseRuleDefinitionsFromLines_withoutSummary() throws IOException {
        Set<RuleDefinition> expected = new LinkedHashSet<>();
        expected.add(
                RuleDefinition.builder()
                        .setKey("unused method parameter")
                        .setName("Unused method parameter")
                        .setSeverity("INFO")
                        .setDescription("<br>")
                        .build()
        );
        Path rulesPath = Paths.get(resourcePath.toString(), "rules-without-summary.txt");
        List<String> lines = Files.lines(rulesPath)
                .collect(Collectors.toList());

        Set<RuleDefinition> actual = parser.parseRuleDefinitionsFromLines(lines);

        assertEquals(expected, actual);
    }
}
