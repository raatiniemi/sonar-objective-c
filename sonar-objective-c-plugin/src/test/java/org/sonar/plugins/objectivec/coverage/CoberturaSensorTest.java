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

import me.raatiniemi.sonarqube.FileSystemHelpers;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.sonar.api.batch.fs.internal.DefaultInputFile;
import org.sonar.api.batch.sensor.internal.DefaultSensorDescriptor;
import org.sonar.api.batch.sensor.internal.SensorContextTester;
import org.sonar.api.config.internal.MapSettings;
import org.sonar.plugins.objectivec.core.ObjectiveC;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.Assert.*;

@RunWith(JUnit4.class)
public class CoberturaSensorTest {
    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    private final Path resourcePath = Paths.get("src", "test", "resources", "cobertura");
    private final MapSettings settings = new MapSettings();

    private SensorContextTester context;
    private FileSystemHelpers helpers;
    private CoberturaSensor sensor;

    private DefaultInputFile classNameFile;

    @Before
    public void setUp() {
        context = SensorContextTester.create(temporaryFolder.getRoot());
        helpers = FileSystemHelpers.create(context);
        sensor = new CoberturaSensor(settings.asConfig());

        classNameFile = helpers.createFile("TargetName/ClassName.m", ObjectiveC.KEY);
    }

    private void createReportFile(@Nonnull String relativePath) {
        try {
            List<String> reportLines = Files.readAllLines(Paths.get(resourcePath.toString(), "cobertura.xml"));

            Path destination = Paths.get(temporaryFolder.getRoot().getAbsolutePath(), relativePath);
            Files.createDirectories(destination.getParent());
            Files.createFile(destination);
            Files.write(destination, reportLines);
        } catch (IOException e) {
            fail(String.format("Unable to create report file: %s", e.getMessage()));
        }
    }

    @Test
    public void describe() {
        DefaultSensorDescriptor descriptor = new DefaultSensorDescriptor();

        sensor.describe(descriptor);

        assertEquals("Cobertura sensor", descriptor.name());
        assertTrue(descriptor.languages().contains(ObjectiveC.KEY));
    }

    @Test
    public void execute_withDefaultReportPattern() {
        helpers.addToFileSystem(classNameFile);
        createReportFile("sonar-reports/cobertura.xml");

        sensor.execute(context);

        assertEquals(Integer.valueOf(1), context.lineHits(classNameFile.key(), 1));
    }

    @Test
    public void execute_withReportPattern() {
        settings.setProperty("sonar.objectivec.coverage.reportPattern", "cobertura.xml");
        helpers.addToFileSystem(classNameFile);
        createReportFile("cobertura.xml");

        sensor.execute(context);

        assertEquals(Integer.valueOf(1), context.lineHits(classNameFile.key(), 1));
    }

    @Test
    public void execute_withoutReports() {
        sensor.execute(context);
    }
}
