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
package org.sonar.plugins.objectivec.coverage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.batch.SensorContext;
import org.sonar.api.batch.sensor.Sensor;
import org.sonar.api.batch.sensor.SensorDescriptor;
import org.sonar.api.config.Settings;
import org.sonar.plugins.objectivec.ObjectiveCPlugin;
import org.sonar.plugins.objectivec.core.ObjectiveC;

import javax.annotation.Nonnull;
import java.io.File;

public final class CoberturaSensor implements Sensor {
    private static final Logger LOGGER = LoggerFactory.getLogger(CoberturaSensor.class);

    private static final String NAME = "Cobertura sensor";
    public static final String REPORT_PATTERN_KEY = ObjectiveCPlugin.PROPERTY_PREFIX
            + ".coverage.reportPattern";
    public static final String DEFAULT_REPORT_PATTERN = "sonar-reports/coverage*.xml";

    private final ReportFilesFinder reportFilesFinder;

    public CoberturaSensor(final Settings settings) {
        reportFilesFinder = new ReportFilesFinder(settings, REPORT_PATTERN_KEY, DEFAULT_REPORT_PATTERN);
    }

    private void analyse(final SensorContext context) {
        final String projectBaseDir = context.fileSystem().baseDir().getPath();

        for (final File report : reportFilesFinder.reportsIn(projectBaseDir)) {
            LOGGER.info("Processing coverage report {}", report);
            CoberturaReportParser.parseReport(report, context.fileSystem(), null, context);
        }
    }

    @Override
    public void describe(@Nonnull SensorDescriptor descriptor) {
        descriptor.name(NAME);
        descriptor.onlyOnLanguage(ObjectiveC.KEY);
    }

    @Override
    public void execute(@Nonnull org.sonar.api.batch.sensor.SensorContext context) {
        analyse((SensorContext) context);
    }
}
