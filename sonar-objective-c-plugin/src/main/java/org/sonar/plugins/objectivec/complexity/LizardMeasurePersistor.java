/**
 * backelite-sonar-objective-c-plugin - Enables analysis of Objective-C projects into SonarQube.
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
package org.sonar.plugins.objectivec.complexity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.batch.SensorContext;
import org.sonar.api.batch.fs.FileSystem;
import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.batch.measure.Metric;

import java.io.File;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * This class is used to save the measures created by the lizardReportParser in the sonar database
 *
 * @author Andres Gil Herrera
 * @since 28/05/15.
 */
public class LizardMeasurePersistor {

    private static final Logger LOGGER = LoggerFactory.getLogger(LizardMeasurePersistor.class);

    private final SensorContext sensorContext;
    private final FileSystem fileSystem;

    public LizardMeasurePersistor(final SensorContext sensorContext, final FileSystem fileSystem) {
        this.sensorContext = sensorContext;
        this.fileSystem = fileSystem;
    }

    /**
     *
     * @param measures Map containing as key the name of the file and as value a list containing the measures for that file
     */
    public <T extends Serializable> void saveMeasures(final Map<String, List<LizardMeasure<T>>> measures) {

        if (measures == null) {
            return;
        }

        for (Map.Entry<String, List<LizardMeasure<T>>> entry : measures.entrySet()) {
            File file = new File(fileSystem.baseDir(), entry.getKey());
            InputFile inputFile = fileSystem.inputFile(fileSystem.predicates().hasAbsolutePath(file.getAbsolutePath()));

            if (inputFile == null) {
                LOGGER.warn("file not included in sonar {}", entry.getKey());
                continue;
            }

            for (LizardMeasure<T> measure : entry.getValue()) {
                saveMeasure(inputFile, measure);
            }
        }
    }

    private <T extends Serializable> void saveMeasure(InputFile inputFile, LizardMeasure<T> measure) {
        Metric<T> metric = measure.getMetric();

        try {
            LOGGER.debug("Save measure {} for file {}", metric.key(), inputFile.relativePath());
            sensorContext.<T>newMeasure()
                    .on(inputFile)
                    .forMetric(metric)
                    .withValue(measure.getValue())
                    .save();
        } catch (Exception e) {
            LOGGER.error(" Exception -> {} -> {}", inputFile.relativePath(), metric.key(), e);
        }
    }

}