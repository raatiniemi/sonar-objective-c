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

import org.sonar.api.batch.sensor.Sensor;
import org.sonar.api.batch.sensor.SensorContext;
import org.sonar.api.batch.sensor.SensorDescriptor;
import org.sonar.api.config.Settings;
import org.sonar.plugins.objectivec.core.ObjectiveC;

import javax.annotation.Nonnull;
import java.io.File;
import java.util.List;

public class SurefireSensor implements Sensor {
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
        List<File> availableReports = SurefireParser.collect(reportPath());
        List<TestReport> testReports = SurefireParser.parseFiles(availableReports);

        SurefireReportPersistor persistor = SurefireReportPersistor.create(context);
        persistor.saveReports(testReports);
    }

    private String reportPath() {
        String reportPath = settings.getString(REPORT_PATH_KEY);
        if (reportPath == null) {
            reportPath = DEFAULT_REPORT_PATH;
        }
        return reportPath;
    }
}
