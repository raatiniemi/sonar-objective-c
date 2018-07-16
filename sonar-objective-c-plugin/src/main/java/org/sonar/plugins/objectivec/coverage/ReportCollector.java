/*
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

import org.apache.tools.ant.DirectoryScanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

final class ReportCollector {
    private static final Logger LOGGER = LoggerFactory.getLogger(ReportCollector.class);

    private final String baseDirectoryPath;
    private final String reportPattern;

    private ReportCollector(@Nonnull String baseDirectoryPath, @Nonnull String reportPattern) {
        this.baseDirectoryPath = baseDirectoryPath;
        this.reportPattern = reportPattern;
    }

    @Nonnull
    static List<File> collect(@Nonnull String baseDirectoryPath, @Nonnull String reportPattern) {
        ReportCollector collector = new ReportCollector(baseDirectoryPath, reportPattern);

        return collector.getAvailableReports();
    }

    @Nonnull
    private List<File> getAvailableReports() {
        return getBasenameForAvailableReports()
                .map(prependBaseDirectoryPath())
                .map(Path::toFile)
                .collect(Collectors.toList());
    }

    @Nonnull
    private Function<String, Path> prependBaseDirectoryPath() {
        return filename -> Paths.get(baseDirectoryPath, filename);
    }

    @Nonnull
    private Stream<String> getBasenameForAvailableReports() {
        DirectoryScanner scanner = new DirectoryScanner();
        scanner.setIncludes(new String[]{reportPattern});
        scanner.setBasedir(new File(baseDirectoryPath));
        scanner.scan();

        String[] availableReports = scanner.getIncludedFiles();
        if (availableReports == null || availableReports.length == 0) {
            LOGGER.warn("No Cobertura reports are available");
            return Stream.empty();
        }

        LOGGER.debug("Found {} Cobertura report(s)", availableReports.length);
        return Arrays.stream(availableReports);
    }
}
