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
package org.sonar.plugins.objectivec.complexity;

import me.raatiniemi.sonarqube.SensorPersistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.batch.fs.FilePredicate;
import org.sonar.api.batch.fs.FileSystem;
import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.batch.measure.Metric;
import org.sonar.api.batch.sensor.SensorContext;
import org.sonar.api.measures.CoreMetrics;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.Serializable;
import java.util.Collection;
import java.util.Optional;

/**
 * This class is used to save the measures created by the lizardReportParser in the sonar database
 */
final class LizardSensorPersistence extends SensorPersistence<LizardMeasure> {
    private static final Logger LOGGER = LoggerFactory.getLogger(LizardSensorPersistence.class);

    private final FileSystem fileSystem;

    private LizardSensorPersistence(@Nonnull SensorContext context) {
        super(context);

        this.fileSystem = getContext().fileSystem();
    }

    @Nonnull
    static LizardSensorPersistence create(@Nonnull SensorContext context) {
        return new LizardSensorPersistence(context);
    }

    /**
     *
     * @param measures Map containing as key the name of the file and as value a list containing the measures for that file
     */
    public void saveMeasures(@Nonnull Collection<LizardMeasure> measures) {
        for (LizardMeasure measure : measures) {
            Optional<InputFile> value = buildInputFile(measure.getPath());
            if (value.isPresent()) {
                saveMeasures(value.get(), measure);
                continue;
            }

            LOGGER.warn("File not included {}", measure.getPath());
        }
    }

    @Nonnull
    private Optional<InputFile> buildInputFile(@Nonnull String relativeFilePath) {
        File file = new File(fileSystem.baseDir(), relativeFilePath);
        FilePredicate predicate = fileSystem.predicates().hasAbsolutePath(file.getAbsolutePath());

        return Optional.ofNullable(fileSystem.inputFile(predicate));
    }

    private void saveMeasures(@Nonnull InputFile inputFile, @Nonnull LizardMeasure measure) {
        saveMeasure(inputFile, CoreMetrics.COMPLEXITY, measure.getComplexity());
        saveMeasure(inputFile, CoreMetrics.FUNCTIONS, measure.getNumberOfFunctions());
    }

    private void saveMeasure(@Nonnull InputFile inputFile, @Nonnull Metric metric, @Nonnull Serializable value) {
        //noinspection unchecked
        getContext().newMeasure()
                .on(inputFile)
                .forMetric(metric)
                .withValue(value)
                .save();
    }
}
