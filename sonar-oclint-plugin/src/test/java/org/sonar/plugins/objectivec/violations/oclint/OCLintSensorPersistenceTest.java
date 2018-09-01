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

import me.raatiniemi.sonar.core.FileSystemHelpers;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.sonar.api.batch.fs.internal.DefaultInputFile;
import org.sonar.api.batch.rule.internal.ActiveRulesBuilder;
import org.sonar.api.batch.rule.internal.DefaultActiveRules;
import org.sonar.api.batch.rule.internal.NewActiveRule;
import org.sonar.api.batch.sensor.internal.SensorContextTester;
import org.sonar.api.batch.sensor.issue.Issue;
import org.sonar.api.rule.RuleKey;
import org.sonar.api.utils.log.LogTester;
import org.sonar.api.utils.log.LoggerLevel;

import javax.annotation.Nonnull;
import java.util.*;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(JUnit4.class)
public class OCLintSensorPersistenceTest {
    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Rule
    public LogTester logTester = new LogTester();

    private SensorContextTester context;
    private FileSystemHelpers helpers;
    private OCLintSensorPersistence persistence;

    private DefaultInputFile classNameFile;

    @Before
    public void setUp() {
        context = SensorContextTester.create(temporaryFolder.getRoot());
        helpers = FileSystemHelpers.create(context);

        persistence = OCLintSensorPersistence.create(context);

        classNameFile = helpers.createFile("TargetName/ClassName.m", "objc");

        List<NewActiveRule> rules = new ArrayList<>();
        ActiveRulesBuilder builder = new ActiveRulesBuilder();
        rules.add(builder.create(RuleKey.of(OCLintRulesDefinition.REPOSITORY_KEY, "deep nested block")));
        rules.add(builder.create(RuleKey.of(OCLintRulesDefinition.REPOSITORY_KEY, "unused method parameter")));
        context.setActiveRules(new DefaultActiveRules(rules));
    }

    private boolean isIssuePresent(@Nonnull String ruleKey) {
        String ruleKeyWithRepository = "OCLint:" + ruleKey;

        return context.allIssues()
                .stream()
                .map(Issue::ruleKey)
                .map(RuleKey::toString)
                .anyMatch(rk -> rk.equalsIgnoreCase(ruleKeyWithRepository));
    }

    @Test
    public void saveMeasures_withoutMeasures() {
        helpers.addToFileSystem(classNameFile);

        persistence.saveMeasures(Collections.emptyList());

        assertTrue(context.allIssues().isEmpty());
        assertTrue(logTester.logs().isEmpty());
    }

    @Test
    public void saveMeasures_withoutMatchingFile() {
        Violation violation = Violation.builder()
                .setPath("TargetName/ClassName.m")
                .setStartLine(1)
                .setMessage("Block depth of 6 exceeds limit of 5")
                .setRule("deep nested block")
                .build();
        Set<Violation> violations = Collections.singleton(violation);

        persistence.saveMeasures(violations);

        assertTrue(context.allIssues().isEmpty());
        assertTrue(logTester.logs(LoggerLevel.WARN).contains("No path available for TargetName/ClassName.m"));
    }

    @Test
    public void saveMeasures_withMeasure() {
        Violation violation = Violation.builder()
                .setPath("TargetName/ClassName.m")
                .setStartLine(1)
                .setMessage("Block depth of 6 exceeds limit of 5")
                .setRule("deep nested block")
                .build();
        Set<Violation> violations = Collections.singleton(violation);
        helpers.addToFileSystem(classNameFile);

        persistence.saveMeasures(violations);

        assertTrue(isIssuePresent("deep nested block"));
        assertTrue(logTester.logs().isEmpty());
    }

    @Test
    public void saveMeasures_withMeasures() {
        Set<Violation> violations = new LinkedHashSet<>();
        violations.add(
                Violation.builder()
                        .setPath("TargetName/ClassName.m")
                        .setStartLine(1)
                        .setMessage("Block depth of 6 exceeds limit of 5")
                        .setRule("deep nested block")
                        .build()
        );
        violations.add(
                Violation.builder()
                        .setPath("TargetName/ClassName.m")
                        .setStartLine(1)
                        .setMessage("The parameter 'commit' is unused.")
                        .setRule("unused method parameter")
                        .build()
        );
        helpers.addToFileSystem(classNameFile);

        persistence.saveMeasures(violations);

        assertTrue(isIssuePresent("deep nested block"));
        assertTrue(isIssuePresent("unused method parameter"));
        assertTrue(logTester.logs().isEmpty());
    }

    @Test
    public void saveMeasures_withUnknownRule() {
        Set<Violation> violations = new LinkedHashSet<>();
        violations.add(
                Violation.builder()
                        .setPath("TargetName/ClassName.m")
                        .setStartLine(1)
                        .setMessage("Message for unknown rule")
                        .setRule("unknown rule")
                        .build()
        );
        helpers.addToFileSystem(classNameFile);

        persistence.saveMeasures(violations);

        assertFalse(isIssuePresent("unknown rule"));
        assertTrue(logTester.logs().contains("\"OCLint:unknown rule\" is not an active rule"));
    }

    @Test
    public void saveMeasures_withFileForAnotherLanguage() {
        Set<Violation> violations = new LinkedHashSet<>();
        violations.add(
                Violation.builder()
                        .setPath("TargetName/ClassName.swift")
                        .setStartLine(1)
                        .setMessage("Block depth of 6 exceeds limit of 5")
                        .setRule("deep nested block")
                        .build()
        );
        helpers.addToFileSystem(helpers.createFile("TargetName/ClassName.swift", "swift"));

        persistence.saveMeasures(violations);

        assertFalse(isIssuePresent("deep nested block"));
        assertTrue(logTester.logs(LoggerLevel.DEBUG).contains("TargetName/ClassName.swift belong to language swift"));
    }
}
