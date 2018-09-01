/*
 * Copyright © 2012 OCTO Technology, Backelite (${email})
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
package me.raatiniemi.sonar.oclint;

import me.raatiniemi.sonar.core.SensorPersistence;
import org.sonar.api.batch.fs.FileSystem;
import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.batch.sensor.SensorContext;
import org.sonar.api.batch.sensor.issue.NewIssue;
import org.sonar.api.batch.sensor.issue.NewIssueLocation;
import org.sonar.api.rule.RuleKey;
import org.sonar.api.utils.log.Logger;
import org.sonar.api.utils.log.Loggers;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

final class OCLintSensorPersistence extends SensorPersistence<Violation> {
    private static final Logger LOGGER = Loggers.get(OCLintSensorPersistence.class);

    private final FileSystem fileSystem;

    private OCLintSensorPersistence(@Nonnull final SensorContext context, @Nonnull final FileSystem fileSystem) {
        super(context);

        this.fileSystem = fileSystem;
    }

    @Nonnull
    static OCLintSensorPersistence create(@Nonnull final SensorContext context) {
        return new OCLintSensorPersistence(context, context.fileSystem());
    }

    @Override
    public void saveMeasures(@Nonnull Collection<Violation> measures) {
        measures.stream()
                .collect(Collectors.groupingBy(Violation::getPath))
                .forEach(this::saveViolationsGroupedByFile);
    }

    private void saveViolationsGroupedByFile(@Nonnull String path, @Nonnull List<Violation> violations) {
        Optional<InputFile> value = buildInputFile(path);
        value.ifPresent(inputFile -> {
            for (Violation violation : violations) {
                RuleKey rule = RuleKey.of(OCLintRulesDefinition.REPOSITORY_KEY, violation.getRule());
                if (isRuleActive(rule)) {
                    LOGGER.warn("\"{}\" is not an active rule", rule.toString());
                    continue;
                }

                NewIssue newIssue = getContext().newIssue().forRule(rule);

                NewIssueLocation location = newIssue.newLocation()
                        .on(inputFile)
                        .at(inputFile.selectLine(violation.getStartLine()))
                        .message(violation.getMessage());

                newIssue.at(location)
                        .save();
            }
        });
    }

    @Nonnull
    private Optional<InputFile> buildInputFile(@Nonnull String path) {
        return buildInputFile(fileSystem.predicates().hasPath(path), path);
    }

    private boolean isRuleActive(@Nonnull RuleKey rule) {
        return null == getContext().activeRules().find(rule);
    }
}
