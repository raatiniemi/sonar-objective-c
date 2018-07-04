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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

final class SurefireParser {

    private static final Logger LOGGER = LoggerFactory.getLogger(SurefireParser.class);

    private static final FilenameFilter includeReports = (dir, name) -> name.startsWith("TEST") && name.endsWith(".xml");

    SurefireParser() {
    }

    List<File> collect(@Nonnull File baseReportDirectory) {
        List<File> availableReports = getAvailableReports(baseReportDirectory);

        if (availableReports.isEmpty()) {
            return Collections.emptyList();
        }

        return availableReports;
    }

    @Nonnull
    private List<File> getAvailableReports(@Nonnull File baseReportDirectory) {
        if (!baseReportDirectory.isDirectory() || !baseReportDirectory.exists()) {
            return Collections.emptyList();
        }

        File[] availableReports = baseReportDirectory.listFiles(includeReports);
        if (null == availableReports) {
            return Collections.emptyList();
        }

        return Arrays.asList(availableReports);
    }

    @Nonnull
    static List<TestReport> parseFiles(List<File> reports) {
        try {
            List<TestReport> testReports = new ArrayList<>();
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            SurefireReportParser parser = SurefireReportParser.create(factory.newDocumentBuilder());
            for (File report : reports) {
                testReports.add(parser.parse(report));
            }

            return testReports;
        } catch (ParserConfigurationException e) {
            LOGGER.error("Unable to create new document builder", e);
            return Collections.emptyList();
        }
    }
}
