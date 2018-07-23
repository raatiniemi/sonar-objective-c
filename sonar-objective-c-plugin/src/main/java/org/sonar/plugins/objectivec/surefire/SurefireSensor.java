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
package org.sonar.plugins.objectivec.surefire;

import me.raatiniemi.sonarqube.ReportFinder;
import me.raatiniemi.sonarqube.ReportPatternFinder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.batch.sensor.Sensor;
import org.sonar.api.batch.sensor.SensorContext;
import org.sonar.api.batch.sensor.SensorDescriptor;
import org.sonar.api.config.Settings;
import org.sonar.plugins.objectivec.core.ObjectiveC;

import javax.annotation.Nonnull;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class SurefireSensor implements Sensor {
    private static final Logger LOGGER = LoggerFactory.getLogger(SurefireSensor.class);

    private static final String NAME = "Surefire sensor";
    private static final String REPORT_PATH_KEY = "sonar.junit.reportsPath";
    private static final String DEFAULT_REPORT_PATH = "sonar-reports/";

    private final Settings settings;

    @SuppressWarnings("WeakerAccess")
    public SurefireSensor(final Settings config) {
        this.settings = config;
    }

    @Override
    public void describe(@Nonnull SensorDescriptor descriptor) {
        descriptor.name(NAME);
        descriptor.onlyOnLanguage(ObjectiveC.KEY);
    }

    @Override
    public void execute(@Nonnull SensorContext context) {
        ReportPatternFinder reportFinder = ReportFinder.create(new File(getReportDirectoryPath()));
        List<File> availableReports = new ArrayList<>(reportFinder.findReportsMatching("TEST-*.xml"));
        List<TestReport> testReports = parseFiles(availableReports);

        ReportPersistor persistor = ReportPersistor.create(context);
        persistor.saveReports(testReports);
    }

    @Nonnull
    private String getReportDirectoryPath() {
        String reportDirectoryPath = settings.getString(REPORT_PATH_KEY);
        if (reportDirectoryPath == null) {
            LOGGER.info("No 'sonar.junit.reportsPath' specified, using default path");
            return DEFAULT_REPORT_PATH;
        }

        return reportDirectoryPath;
    }

    @Nonnull
    private static List<TestReport> parseFiles(@Nonnull List<File> reports) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            ReportParser parser = ReportParser.create(factory.newDocumentBuilder());

            return reports.stream()
                    .map(parser::parse)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .collect(Collectors.toList());
        } catch (ParserConfigurationException e) {
            LOGGER.error("Unable to create new document builder", e);
            return Collections.emptyList();
        }
    }
}
