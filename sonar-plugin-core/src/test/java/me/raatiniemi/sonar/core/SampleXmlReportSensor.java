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

package me.raatiniemi.sonar.core;

import org.sonar.api.batch.sensor.SensorContext;
import org.sonar.api.batch.sensor.SensorDescriptor;
import org.sonar.api.config.Configuration;

import javax.annotation.Nonnull;
import javax.xml.parsers.DocumentBuilder;
import java.io.File;
import java.util.Collections;
import java.util.Optional;

class SampleXmlReportSensor extends XmlReportSensor {
    private SampleXmlReportSensor(@Nonnull Configuration configuration) {
        super(configuration);
    }

    @Nonnull
    static SampleXmlReportSensor create(@Nonnull Configuration configuration) {
        return new SampleXmlReportSensor(configuration);
    }

    @Nonnull
    @Override
    protected String getReportPathKey() {
        return "report.path.key";
    }

    @Nonnull
    @Override
    protected String getDefaultReportPath() {
        return "report.xml";
    }

    @Override
    public void describe(@Nonnull SensorDescriptor descriptor) {
    }

    @Override
    public void execute(@Nonnull SensorContext context) {
        Optional<SampleReport> value = collectAndParseAvailableReports(context.fileSystem().baseDir());
        if (value.isPresent()) {
            SampleSensorPersistence persistence = SampleSensorPersistence.create(context);
            persistence.saveMeasures(Collections.singletonList(value.get()));
        }
    }

    @Nonnull
    private Optional<SampleReport> collectAndParseAvailableReports(@Nonnull File projectDirectory) {
        Optional<DocumentBuilder> documentBuilder = createDocumentBuilder();
        if (!documentBuilder.isPresent()) {
            return Optional.empty();
        }

        Optional<File> value = collectAvailableReports(projectDirectory).findFirst();
        if (!value.isPresent()) {
            return Optional.empty();
        }

        SampleXmlReportParser parser = SampleXmlReportParser.create(documentBuilder.get());
        return parser.parse(value.get());
    }
}
