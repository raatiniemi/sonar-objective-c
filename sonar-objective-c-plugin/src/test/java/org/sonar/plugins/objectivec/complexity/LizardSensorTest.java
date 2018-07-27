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
package org.sonar.plugins.objectivec.complexity;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.batch.fs.internal.DefaultInputFile;
import org.sonar.api.batch.sensor.internal.DefaultSensorDescriptor;
import org.sonar.api.batch.sensor.internal.SensorContextTester;
import org.sonar.api.batch.sensor.measure.Measure;
import org.sonar.api.config.MapSettings;
import org.sonar.api.measures.CoreMetrics;
import org.sonar.plugins.objectivec.core.ObjectiveC;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.Assert.*;

@RunWith(JUnit4.class)
public class LizardSensorTest {
    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    private final Path resourcePath = Paths.get("src", "test", "resources", "lizard");
    private final MapSettings settings = new MapSettings();

    private SensorContextTester context;
    private LizardSensor sensor;

    private DefaultInputFile classNameFile;

    @Before
    public void setUp() {
        context = SensorContextTester.create(temporaryFolder.getRoot());
        sensor = new LizardSensor(context.fileSystem(), settings);

        classNameFile = createFile();
    }

    @Nonnull
    private DefaultInputFile createFile() {
        return new DefaultInputFile(context.module().key(), "TargetName/ClassName.m")
                .setLanguage("bla")
                .setType(InputFile.Type.MAIN)
                .initMetadata("1\n2\n3\n4\n5\n6");
    }

    private void addFileToFs(@Nonnull DefaultInputFile inputFile) {
        context.fileSystem().add(inputFile);
    }

    private void createReportFile(@Nonnull String relativePath) {
        try {
            List<String> reportLines = Files.readAllLines(Paths.get(resourcePath.toString(), "correctFile.xml"));

            Path destination = Paths.get(temporaryFolder.getRoot().getAbsolutePath(), relativePath);
            Files.createDirectories(destination.getParent());
            Files.createFile(destination);
            Files.write(destination, reportLines);
        } catch (IOException e) {
            fail(String.format("Unable to create report file: %s", e.getMessage()));
        }
    }

    @Nullable
    private <T extends Serializable> T getMeasure(@Nonnull String key) {
        Measure<T> measure = context.measure(classNameFile.key(), key);
        if (null == measure) {
            return null;
        }

        return measure.value();
    }

    @Test
    public void describe() {
        DefaultSensorDescriptor descriptor = new DefaultSensorDescriptor();

        sensor.describe(descriptor);

        assertEquals("Lizard complexity sensor", descriptor.name());
        assertTrue(descriptor.languages().contains(ObjectiveC.KEY));
    }

    @Test
    public void execute_withDefaultReportPattern() {
        addFileToFs(classNameFile);
        createReportFile("sonar-reports/lizard-report.xml");

        sensor.execute(context);

        assertEquals(Integer.valueOf(2), getMeasure(CoreMetrics.FUNCTIONS_KEY));
        assertEquals(Integer.valueOf(6), getMeasure(CoreMetrics.COMPLEXITY_KEY));
    }

    @Test
    public void execute_withReportPattern() {
        settings.setProperty("sonar.objectivec.lizard.report", "lizard.xml");
        addFileToFs(classNameFile);
        createReportFile("lizard.xml");

        sensor.execute(context);

        assertEquals(Integer.valueOf(2), getMeasure(CoreMetrics.FUNCTIONS_KEY));
        assertEquals(Integer.valueOf(6), getMeasure(CoreMetrics.COMPLEXITY_KEY));
    }

    @Test
    public void buildReportPath_withoutConfiguredReportPath() {
        String basePath = temporaryFolder.getRoot().getPath();
        String expected = String.format("%s/%s", basePath, LizardSensor.DEFAULT_REPORT_PATH);

        String actual = sensor.buildReportPath(basePath);

        assertEquals(expected, actual);
    }

    @Test
    public void buildReportPath_withConfiguredReportPath() {
        String basePath = temporaryFolder.getRoot().getPath();
        String path = "lizard-report.xml";
        String expected = String.format("%s/%s", basePath, path);
        settings.setProperty(LizardSensor.REPORT_PATH_KEY, path);

        String actual = sensor.buildReportPath(basePath);

        assertEquals(expected, actual);
    }
}
