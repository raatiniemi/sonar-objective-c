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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.batch.fs.FilePredicate;
import org.sonar.api.batch.fs.FileSystem;
import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.batch.sensor.SensorContext;
import org.sonar.api.batch.sensor.coverage.NewCoverage;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;

final class ReportPersistor {
    private static final Logger LOGGER = LoggerFactory.getLogger(ReportPersistor.class);
    private static final Predicate<CoberturaLine> excludeWithZeroLineNumber = line -> line.getNumber() > 0;

    private final SensorContext context;
    private final FileSystem fileSystem;

    private ReportPersistor(@Nonnull SensorContext context) {
        this.context = context;
        fileSystem = context.fileSystem();
    }

    @Nonnull
    static ReportPersistor create(@Nonnull SensorContext context) {
        return new ReportPersistor(context);
    }


    void saveReports(List<CoberturaPackage> coberturaPackages) {
        for (CoberturaPackage coberturaPackage : coberturaPackages) {
            for (CoberturaClass coberturaClass : coberturaPackage.getClasses()) {
                Optional<InputFile> value = buildInputFile(coberturaClass.getFilename());
                if (value.isPresent()) {
                    saveReportForClass(value.get(), coberturaClass);
                    continue;
                }

                LOGGER.warn("No path for {}", coberturaClass.getFilename());
            }
        }
    }

    @Nonnull
    private Optional<InputFile> buildInputFile(@Nonnull String filename) {
        FilePredicate predicate = fileSystem.predicates().hasPath(filename);
        InputFile inputFile = fileSystem.inputFile(predicate);

        return Optional.ofNullable(inputFile);
    }

    private void saveReportForClass(@Nonnull InputFile inputFile, @Nonnull CoberturaClass coberturaClass) {
        coberturaClass.getLines().stream()
                .filter(excludeWithZeroLineNumber)
                .forEach(saveCoverageForLine(inputFile));
    }

    private Consumer<CoberturaLine> saveCoverageForLine(InputFile inputFile) {
        return line -> {
            NewCoverage newCoverage = context.newCoverage()
                    .onFile(inputFile)
                    .lineHits(line.getNumber(), line.getHits());

            if (line.hasConditions()) {
                newCoverage.conditions(line.getNumber(), line.getConditions(), line.getConditionsCovered());
            }

            newCoverage.save();
        };
    }
}
