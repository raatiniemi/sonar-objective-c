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
package org.sonar.plugins.objectivec;

import com.google.common.collect.ImmutableList;
import com.sonar.objectivec.ObjectiveCAstScanner;
import com.sonar.objectivec.ObjectiveCConfiguration;
import com.sonar.objectivec.api.ObjectiveCGrammar;
import com.sonar.objectivec.api.ObjectiveCMetric;
import org.sonar.api.batch.fs.FilePredicate;
import org.sonar.api.batch.fs.FileSystem;
import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.batch.sensor.Sensor;
import org.sonar.api.batch.sensor.SensorContext;
import org.sonar.api.batch.sensor.SensorDescriptor;
import org.sonar.api.measures.CoreMetrics;
import org.sonar.api.measures.Metric;
import org.sonar.api.scan.filesystem.PathResolver;
import org.sonar.plugins.objectivec.core.ObjectiveC;
import org.sonar.squidbridge.AstScanner;
import org.sonar.squidbridge.api.SourceCode;
import org.sonar.squidbridge.api.SourceFile;
import org.sonar.squidbridge.indexer.QueryByType;

import javax.annotation.Nonnull;
import java.io.Serializable;
import java.util.Collection;


public class ObjectiveCSquidSensor implements Sensor {
    private final FileSystem fileSystem;

    private final PathResolver pathResolver;
    private SensorContext context;

    @SuppressWarnings("WeakerAccess")
    public ObjectiveCSquidSensor(FileSystem fileSystem, PathResolver pathResolver) {
        this.fileSystem = fileSystem;
        this.pathResolver = pathResolver;
    }

    @Nonnull
    private static FilePredicate createFilePredicate(@Nonnull FileSystem fs) {
        return fs.predicates().and(
                fs.predicates().hasLanguage(ObjectiveC.KEY),
                fs.predicates().hasType(InputFile.Type.MAIN)
        );
    }

    @Override
    public void describe(@Nonnull SensorDescriptor descriptor) {
        descriptor.name(getClass().getSimpleName());
        descriptor.onlyOnLanguage(ObjectiveC.KEY);
    }

    @Override
    public void execute(@Nonnull SensorContext context) {
        analyse(context);
    }

    private void analyse(SensorContext context) {
        this.context = context;

        ObjectiveCConfiguration configuration = ObjectiveCConfiguration.create(context.fileSystem().encoding());
        AstScanner<ObjectiveCGrammar> scanner = ObjectiveCAstScanner.create(configuration);

        scanner.scanFiles(ImmutableList.copyOf(fileSystem.files(createFilePredicate(fileSystem))));

        Collection<SourceCode> squidSourceFiles = scanner.getIndex().search(new QueryByType(SourceFile.class));
        save(squidSourceFiles);
    }

    private void save(Collection<SourceCode> squidSourceFiles) {
        for (SourceCode squidSourceFile : squidSourceFiles) {
            SourceFile squidFile = (SourceFile) squidSourceFile;

            String relativePath = pathResolver.relativePath(fileSystem.baseDir(), new java.io.File(squidFile.getKey()));
            InputFile inputFile = fileSystem.inputFile(fileSystem.predicates().hasRelativePath(relativePath));

            saveMeasures(inputFile, squidFile);
        }
    }

    private void saveMeasures(InputFile inputFile, SourceFile squidFile) {
        saveMeasure(inputFile, CoreMetrics.NCLOC, squidFile.getInt(ObjectiveCMetric.LINES_OF_CODE));
        saveMeasure(inputFile, CoreMetrics.STATEMENTS, squidFile.getInt(ObjectiveCMetric.STATEMENTS));
        saveMeasure(inputFile, CoreMetrics.COMMENT_LINES, squidFile.getInt(ObjectiveCMetric.COMMENT_LINES));
    }

    private void saveMeasure(@Nonnull InputFile inputFile, @Nonnull Metric metric, Serializable value) {
        //noinspection unchecked
        context.newMeasure()
                .on(inputFile)
                .forMetric(metric)
                .withValue(value)
                .save();
    }
}
