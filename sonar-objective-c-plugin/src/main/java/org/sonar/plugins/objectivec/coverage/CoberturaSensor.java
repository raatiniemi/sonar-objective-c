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
import org.sonar.api.batch.Sensor;
import org.sonar.api.batch.SensorContext;
import org.sonar.api.batch.fs.FileSystem;
import org.sonar.api.config.Settings;
import org.sonar.api.resources.Project;
import org.sonar.plugins.objectivec.ObjectiveCPlugin;
import org.sonar.plugins.objectivec.core.ObjectiveC;

import java.io.File;

public final class CoberturaSensor implements Sensor {
    private static final Logger LOGGER = LoggerFactory.getLogger(CoberturaSensor.class);

    public static final String REPORT_PATTERN_KEY = ObjectiveCPlugin.PROPERTY_PREFIX
            + ".coverage.reportPattern";
    public static final String DEFAULT_REPORT_PATTERN = "sonar-reports/coverage*.xml";

    private final ReportFilesFinder reportFilesFinder;

    private final FileSystem fileSystem;

    public CoberturaSensor(final FileSystem fileSystem, final Settings settings) {
        this.fileSystem = fileSystem;

        reportFilesFinder = new ReportFilesFinder(settings, REPORT_PATTERN_KEY, DEFAULT_REPORT_PATTERN);
    }

    public boolean shouldExecuteOnProject(final Project project) {
        return project.isRoot() && fileSystem.languages().contains(ObjectiveC.KEY);
    }

    public void analyse(final Project project, final SensorContext context) {
        final String projectBaseDir = fileSystem.baseDir().getPath();

        for (final File report : reportFilesFinder.reportsIn(projectBaseDir)) {
            LOGGER.info("Processing coverage report {}", report);
            CoberturaReportParser.parseReport(report, fileSystem, project, context);
        }
    }
}
