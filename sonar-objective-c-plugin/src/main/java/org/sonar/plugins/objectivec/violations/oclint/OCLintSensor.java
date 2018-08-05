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
package org.sonar.plugins.objectivec.violations.oclint;

import me.raatiniemi.sonarqube.ReportFinder;
import me.raatiniemi.sonarqube.ReportPatternFinder;
import me.raatiniemi.sonarqube.XmlReportSensor;
import org.sonar.api.batch.sensor.SensorContext;
import org.sonar.api.batch.sensor.SensorDescriptor;
import org.sonar.api.config.Configuration;
import org.sonar.plugins.objectivec.ObjectiveCPlugin;
import org.sonar.plugins.objectivec.core.ObjectiveC;

import javax.annotation.Nonnull;
import javax.xml.parsers.DocumentBuilder;
import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public final class OCLintSensor extends XmlReportSensor {
    public static final String REPORT_PATH_KEY = ObjectiveCPlugin.PROPERTY_PREFIX + ".oclint.report";
    public static final String DEFAULT_REPORT_PATH = "sonar-reports/oclint.xml";

    private static final String NAME = "OCLint violation sensor";

    @SuppressWarnings("WeakerAccess")
    public OCLintSensor(@Nonnull Configuration configuration) {
        super(configuration);
    }

    @Override
    public void describe(@Nonnull SensorDescriptor descriptor) {
        descriptor.name(NAME);
        descriptor.onlyOnLanguage(ObjectiveC.KEY);
    }

    @Override
    public void execute(@Nonnull SensorContext context) {
        List<Violation> violations = parseReportIn(context.fileSystem().baseDir());

        OCLintSensorPersistence persistence = OCLintSensorPersistence.create(context);
        persistence.saveMeasures(violations);
    }

    @Nonnull
    private List<Violation> parseReportIn(@Nonnull File projectDirectory) {
        Optional<DocumentBuilder> documentBuilder = createDocumentBuilder();
        if (!documentBuilder.isPresent()) {
            return Collections.emptyList();
        }

        OCLintXmlReportParser parser = OCLintXmlReportParser.create(documentBuilder.get());
        ReportPatternFinder reportFinder = ReportFinder.create(projectDirectory);
        return reportFinder.findReportMatching(getSetting(REPORT_PATH_KEY, DEFAULT_REPORT_PATH))
                .map(parser::parse)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .orElse(Collections.emptyList());
    }
}
