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

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.IOFileFilter;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.FilenameFilter;
import java.util.*;

final class ReportCollector {
    private static final IOFileFilter includeReports = FileFilterUtils.and(
            FileFilterUtils.prefixFileFilter("TEST"),
            FileFilterUtils.suffixFileFilter("xml")
    );

    private final File reportDirectory;

    private ReportCollector(@Nonnull File reportDirectory) {
        this.reportDirectory = reportDirectory;
    }

    @Nonnull
    static List<File> collect(@Nonnull String reportDirectoryPath) {
        ReportCollector collector = new ReportCollector(new File(reportDirectoryPath));

        return collector.getAvailableReports();
    }

    @Nonnull
    private List<File> getAvailableReports() {
        if (!reportDirectory.isDirectory() || !reportDirectory.exists()) {
            return Collections.emptyList();
        }

        Collection<File> availableReports = FileUtils.listFiles(
                reportDirectory,
                includeReports,
                FileFilterUtils.trueFileFilter()
        );
        return new ArrayList<>(availableReports);
    }
}
