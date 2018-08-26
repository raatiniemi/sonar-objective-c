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
package org.sonar.plugins.objectivec.surefire;

import me.raatiniemi.sonarqube.SensorPersistence;
import org.sonar.api.batch.fs.FilePredicate;
import org.sonar.api.batch.fs.FileSystem;
import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.batch.sensor.SensorContext;
import org.sonar.api.measures.CoreMetrics;
import org.sonar.api.measures.Metric;

import javax.annotation.Nonnull;
import java.io.Serializable;
import java.util.Collection;
import java.util.Optional;

final class SurefireSensorPersistence extends SensorPersistence<TestReport> {
    private final FileSystem fileSystem;

    private SurefireSensorPersistence(@Nonnull SensorContext context) {
        super(context);

        fileSystem = context.fileSystem();
    }

    @Nonnull
    static SurefireSensorPersistence create(@Nonnull SensorContext sensorContext) {
        return new SurefireSensorPersistence(sensorContext);
    }

    @Override
    public void saveMeasures(@Nonnull Collection<TestReport> measures) {
        for (TestReport testReport : measures) {
            for (TestSuite testSuite : testReport.getTestSuites()) {
                Optional<InputFile> value = buildInputFile(testSuite.getClassName());
                value.ifPresent(inputFile -> saveMeasures(inputFile, testSuite));
            }
        }
    }

    @Nonnull
    private Optional<InputFile> buildInputFile(@Nonnull String className) {
        String filename = buildFilename(className);

        FilePredicate predicate = fileSystem.predicates().matchesPathPattern("**/" + filename);
        return buildInputFile(predicate, className);
    }

    @Nonnull
    private String buildFilename(@Nonnull String className) {
        className = replaceCategorySeparator(className);
        return appendFileExtension(className);
    }

    @Nonnull
    private String replaceCategorySeparator(@Nonnull String className) {
        return className.replace("_", "+");
    }

    @Nonnull
    private String appendFileExtension(@Nonnull String className) {
        return className + ".*";
    }

    private void saveMeasures(@Nonnull InputFile inputFile, @Nonnull TestSuite testSuite) {
        saveMeasure(inputFile, CoreMetrics.TESTS, testSuite.getNumberOfTests());
        saveMeasure(inputFile, CoreMetrics.TEST_FAILURES, testSuite.getNumberOfFailedTests());
        saveMeasure(inputFile, CoreMetrics.TEST_EXECUTION_TIME, testSuite.getDurationInMilliseconds());
    }

    private void saveMeasure(@Nonnull InputFile inputFile, @Nonnull Metric metric, Serializable value) {
        //noinspection unchecked
        getContext().newMeasure()
                .forMetric(metric)
                .on(inputFile)
                .withValue(value)
                .save();
    }
}
