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

import me.raatiniemi.sonarqube.XmlReportSensor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.batch.sensor.SensorContext;
import org.sonar.api.batch.sensor.SensorDescriptor;
import org.sonar.api.config.Configuration;
import org.sonar.plugins.objectivec.ObjectiveCPlugin;
import org.sonar.plugins.objectivec.core.ObjectiveC;

import javax.annotation.Nonnull;
import javax.xml.parsers.DocumentBuilder;
import java.io.File;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;

/**
 * This sensor searches for the report generated from the tool Lizard
 * in order to save complexity metrics.
 */
public class LizardSensor extends XmlReportSensor {
    private static final Logger LOGGER = LoggerFactory.getLogger(LizardSensor.class);

    private static final String NAME = "Lizard complexity sensor";

    public static final String REPORT_PATH_KEY = ObjectiveCPlugin.PROPERTY_PREFIX + ".lizard.reportPath";
    public static final String DEFAULT_REPORT_PATH = "sonar-reports/lizard.xml";

    @SuppressWarnings("WeakerAccess")
    public LizardSensor(@Nonnull Configuration configuration) {
        super(configuration);
    }

    @Override
    public void describe(@Nonnull SensorDescriptor descriptor) {
        descriptor.name(NAME);
        descriptor.onlyOnLanguage(ObjectiveC.KEY);
    }

    @Override
    public void execute(@Nonnull SensorContext context) {
        Set<LizardMeasure> measures = collectAndParseAvailableReports(context.fileSystem().baseDir());
        if (measures.isEmpty()) {
            return;
        }

        LOGGER.info("Saving results of complexity analysis");
        LizardSensorPersistence persistence = LizardSensorPersistence.create(context);
        persistence.saveMeasures(measures);
    }

    @Nonnull
    private Set<LizardMeasure> collectAndParseAvailableReports(@Nonnull File reportDirectory) {
        Optional<DocumentBuilder> documentBuilder = createDocumentBuilder();
        if (!documentBuilder.isPresent()) {
            return Collections.emptySet();
        }

        LizardXmlReportParser parser = LizardXmlReportParser.create(documentBuilder.get());
        return collectAvailableReports(reportDirectory)
                .findFirst()
                .map(parser::parse)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .orElse(Collections.emptySet());
    }

    @Nonnull
    @Override
    protected String getReportPathKey() {
        return REPORT_PATH_KEY;
    }

    @Nonnull
    @Override
    protected String getDefaultReportPath() {
        return DEFAULT_REPORT_PATH;
    }
}
