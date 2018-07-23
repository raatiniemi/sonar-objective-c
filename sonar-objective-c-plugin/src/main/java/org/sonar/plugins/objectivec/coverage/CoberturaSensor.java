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

import me.raatiniemi.sonarqube.ReportFinder;
import me.raatiniemi.sonarqube.ReportPatternFinder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.batch.sensor.Sensor;
import org.sonar.api.batch.sensor.SensorContext;
import org.sonar.api.batch.sensor.SensorDescriptor;
import org.sonar.api.config.Settings;
import org.sonar.plugins.objectivec.ObjectiveCPlugin;
import org.sonar.plugins.objectivec.core.ObjectiveC;

import javax.annotation.Nonnull;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class CoberturaSensor implements Sensor {
    private static final Logger LOGGER = LoggerFactory.getLogger(CoberturaSensor.class);
    private static final String NAME = "Cobertura sensor";
    public static final String REPORT_PATTERN_KEY = ObjectiveCPlugin.PROPERTY_PREFIX
            + ".coverage.reportPattern";
    public static final String DEFAULT_REPORT_PATTERN = "sonar-reports/coverage*.xml";

    private final Settings settings;

    @SuppressWarnings("WeakerAccess")
    public CoberturaSensor(final Settings settings) {
        this.settings = settings;
    }

    @Override
    public void describe(@Nonnull SensorDescriptor descriptor) {
        descriptor.name(NAME);
        descriptor.onlyOnLanguage(ObjectiveC.KEY);
    }

    @Override
    public void execute(@Nonnull SensorContext context) {
        List<CoberturaPackage> availableReports = collectAndParseAvailableReports(context);

        ReportPersistor reportPersistor = ReportPersistor.create(context);
        reportPersistor.saveReports(availableReports);
    }

    @Nonnull
    private List<CoberturaPackage> collectAndParseAvailableReports(@Nonnull SensorContext context) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            ReportParser reportParser = ReportParser.create(factory.newDocumentBuilder());

            return collectAvailableReports(context)
                    .map(reportParser::parse)
                    .flatMap(List::stream)
                    .collect(Collectors.toList());
        } catch (ParserConfigurationException e) {
            LOGGER.error("Unable to create document builder", e);
            return Collections.emptyList();
        }
    }

    @Nonnull
    private Stream<File> collectAvailableReports(@Nonnull SensorContext context) {
        ReportPatternFinder reportFinder = ReportFinder.create(context.fileSystem().baseDir());

        return reportFinder.findReportsMatching(getReportFilePattern()).stream();
    }

    @Nonnull
    private String getReportFilePattern() {
        String reportPath = settings.getString(REPORT_PATTERN_KEY);

        if (reportPath == null) {
            LOGGER.debug("No value specified for \"" + REPORT_PATTERN_KEY + "\" using default report pattern");
            reportPath = DEFAULT_REPORT_PATTERN;
        }

        return reportPath;
    }
}
