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
package me.raatiniemi.sonar.cobertura;

import me.raatiniemi.sonar.core.SensorPersistence;
import org.sonar.api.batch.fs.FileSystem;
import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.batch.sensor.SensorContext;
import org.sonar.api.batch.sensor.coverage.NewCoverage;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;

final class CoberturaSensorPersistence extends SensorPersistence<CoberturaPackage> {
    private static final Predicate<CoberturaLine> excludeWithZeroLineNumber = line -> line.getNumber() > 0;

    private final FileSystem fileSystem;

    private CoberturaSensorPersistence(@Nonnull SensorContext context) {
        super(context);

        fileSystem = context.fileSystem();
    }

    @Nonnull
    static CoberturaSensorPersistence create(@Nonnull SensorContext context) {
        return new CoberturaSensorPersistence(context);
    }

    @Override
    public void saveMeasures(@Nonnull Collection<CoberturaPackage> measures) {
        for (CoberturaPackage coberturaPackage : measures) {
            for (CoberturaClass coberturaClass : coberturaPackage.getClasses()) {
                Optional<InputFile> value = buildInputFile(coberturaClass.getFilename());
                value.ifPresent(inputFile -> saveReportForClass(inputFile, coberturaClass));
            }
        }
    }

    @Nonnull
    private Optional<InputFile> buildInputFile(@Nonnull String filename) {
        return buildInputFile(fileSystem.predicates().hasPath(filename), filename);
    }

    private void saveReportForClass(@Nonnull InputFile inputFile, @Nonnull CoberturaClass coberturaClass) {
        coberturaClass.getLines().stream()
                .filter(excludeWithZeroLineNumber)
                .forEach(saveCoverageForLine(inputFile));
    }

    @Nonnull
    private Consumer<CoberturaLine> saveCoverageForLine(@Nonnull InputFile inputFile) {
        return line -> {
            NewCoverage newCoverage = getContext().newCoverage()
                    .onFile(inputFile)
                    .lineHits(line.getNumber(), line.getHits());

            if (line.hasConditions()) {
                newCoverage.conditions(line.getNumber(), line.getConditions(), line.getConditionsCovered());
            }

            newCoverage.save();
        };
    }
}
