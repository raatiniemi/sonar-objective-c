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

package me.raatiniemi.sonarqube;

import org.sonar.api.batch.fs.FilePredicate;
import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.batch.sensor.SensorContext;
import org.sonar.api.measures.CoreMetrics;
import org.sonar.api.measures.Metric;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.Optional;

public class SampleSensorPersistence extends SensorPersistence<SampleReport> {
    private SampleSensorPersistence(@Nonnull SensorContext context) {
        super(context);
    }

    @Nonnull
    static SampleSensorPersistence create(@Nonnull SensorContext context) {
        return new SampleSensorPersistence(context);
    }

    @Override
    public void saveMeasures(@Nonnull Collection<SampleReport> measures) {
        for (SampleReport measure : measures) {
            FilePredicate filePredicate = getContext().fileSystem().predicates().hasPath(measure.getFilename());
            Optional<InputFile> inputFile = buildInputFile(filePredicate, measure.getFilename());
            if (!inputFile.isPresent()) {
                continue;
            }

            //noinspection unchecked
            getContext().newMeasure()
                    .on(inputFile.get())
                    .forMetric((Metric) CoreMetrics.COMPLEXITY)
                    .withValue(Integer.parseInt(measure.getValue()))
                    .save();
        }
    }
}
