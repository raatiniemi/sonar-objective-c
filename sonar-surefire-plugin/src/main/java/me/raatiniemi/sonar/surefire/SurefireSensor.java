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
package me.raatiniemi.sonar.surefire;

import me.raatiniemi.sonar.core.XmlReportSensor;
import org.sonar.api.Properties;
import org.sonar.api.Property;
import org.sonar.api.batch.sensor.SensorContext;
import org.sonar.api.batch.sensor.SensorDescriptor;
import org.sonar.api.config.Configuration;

import javax.annotation.Nonnull;
import javax.xml.parsers.DocumentBuilder;
import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Properties(
        @Property(
                key = SurefireSensor.REPORT_PATH_KEY,
                defaultValue = SurefireSensor.DEFAULT_REPORT_PATH,
                name = "Path to Surefire test report(s)",
                description = "Relative to projects' root.",
                global = false,
                project = true
        )
)
public class SurefireSensor extends XmlReportSensor {
    static final String REPORT_PATH_KEY = "sonar.objectivec.surefire.reportPath";
    static final String DEFAULT_REPORT_PATH = "sonar-reports/TEST-*.xml";

    private static final String NAME = "Surefire sensor";

    @SuppressWarnings("WeakerAccess")
    public SurefireSensor(@Nonnull Configuration configuration) {
        super(configuration);
    }

    @Override
    public void describe(@Nonnull SensorDescriptor descriptor) {
        descriptor.name(NAME);
        descriptor.onlyOnLanguage("objc");
    }

    @Override
    public void execute(@Nonnull SensorContext context) {
        List<TestReport> testReports = collectAndParseAvailableReports(context.fileSystem().baseDir());

        SurefireSensorPersistence persistence = SurefireSensorPersistence.create(context);
        persistence.saveMeasures(testReports);
    }

    @Nonnull
    private List<TestReport> collectAndParseAvailableReports(@Nonnull File projectDirectory) {
        Optional<DocumentBuilder> documentBuilder = createDocumentBuilder();
        if (!documentBuilder.isPresent()) {
            return Collections.emptyList();
        }

        SurefireXmlReportParser parser = SurefireXmlReportParser.create(documentBuilder.get());
        return collectAvailableReports(projectDirectory)
                .map(parser::parse)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
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
