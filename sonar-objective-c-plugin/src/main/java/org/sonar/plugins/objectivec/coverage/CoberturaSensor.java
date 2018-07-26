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
import me.raatiniemi.sonarqube.XmlReportSensor;
import org.sonar.api.batch.sensor.SensorContext;
import org.sonar.api.batch.sensor.SensorDescriptor;
import org.sonar.api.config.Settings;
import org.sonar.plugins.objectivec.ObjectiveCPlugin;
import org.sonar.plugins.objectivec.core.ObjectiveC;

import javax.annotation.Nonnull;
import javax.xml.parsers.DocumentBuilder;
import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class CoberturaSensor extends XmlReportSensor {
    private static final String NAME = "Cobertura sensor";
    public static final String REPORT_PATTERN_KEY = ObjectiveCPlugin.PROPERTY_PREFIX
            + ".coverage.reportPattern";
    public static final String DEFAULT_REPORT_PATTERN = "sonar-reports/coverage*.xml";

    @SuppressWarnings("WeakerAccess")
    public CoberturaSensor(@Nonnull Settings settings) {
        super(settings);
    }

    @Override
    public void describe(@Nonnull SensorDescriptor descriptor) {
        descriptor.name(NAME);
        descriptor.onlyOnLanguage(ObjectiveC.KEY);
    }

    @Override
    public void execute(@Nonnull SensorContext context) {
        List<CoberturaPackage> availableReports = collectAndParseAvailableReports(context.fileSystem().baseDir());

        CoberturaSensorPersistence persistence = CoberturaSensorPersistence.create(context);
        persistence.saveMeasures(availableReports);
    }

    @Nonnull
    private List<CoberturaPackage> collectAndParseAvailableReports(@Nonnull File projectDirectory) {
        Optional<DocumentBuilder> documentBuilder = createDocumentBuilder();
        if (!documentBuilder.isPresent()) {
            return Collections.emptyList();
        }

        CoberturaXmlReportParser reportParser = CoberturaXmlReportParser.create(documentBuilder.get());
        return collectAvailableReports(projectDirectory)
                .map(reportParser::parse)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .flatMap(List::stream)
                .collect(Collectors.toList());
    }

    @Nonnull
    private Stream<File> collectAvailableReports(@Nonnull File projectDirectory) {
        ReportPatternFinder reportFinder = ReportFinder.create(projectDirectory);

        return reportFinder
                .findReportsMatching(getSetting(REPORT_PATTERN_KEY, DEFAULT_REPORT_PATTERN))
                .stream();
    }
}
