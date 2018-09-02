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
package me.raatiniemi.sonar.surefire;

import me.raatiniemi.sonar.core.internal.FileSystemHelpers;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.sonar.api.batch.fs.internal.DefaultInputFile;
import org.sonar.api.batch.sensor.internal.DefaultSensorDescriptor;
import org.sonar.api.batch.sensor.internal.SensorContextTester;
import org.sonar.api.batch.sensor.measure.Measure;
import org.sonar.api.config.internal.MapSettings;
import org.sonar.api.measures.CoreMetrics;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

@RunWith(JUnit4.class)
public class SurefireSensorTest {
    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    private final Path resourcePath = Paths.get("src", "test", "resources", "surefire", "reports");
    private final MapSettings settings = new MapSettings();

    private SurefireSensor sensor;
    private SensorContextTester context;
    private FileSystemHelpers helpers;

    private DefaultInputFile firstClassNameTestFile;
    private DefaultInputFile secondClassNameTestFile;

    @Before
    public void setUp() {
        sensor = new SurefireSensor(settings.asConfig());
        context = SensorContextTester.create(temporaryFolder.getRoot());
        helpers = FileSystemHelpers.create(context);

        firstClassNameTestFile = helpers.createFile("FirstClassNameTest.m", "objc");
        secondClassNameTestFile = helpers.createFile("SecondClassNameTest.m", "objc");
    }

    private void createReportFile(@Nonnull String relativePath) {
        try {
            List<String> reportLines = Files.readAllLines(Paths.get(resourcePath.toString(), "TEST-multiple-test-cases.xml"));

            Path destination = Paths.get(temporaryFolder.getRoot().getAbsolutePath(), relativePath);
            Files.createDirectories(destination.getParent());
            Files.createFile(destination);
            Files.write(destination, reportLines);
        } catch (IOException e) {
            fail(String.format("Unable to create report file: %s", e.getMessage()));
        }
    }

    @Nonnull
    private <T extends Serializable> T getMeasure(@Nonnull String componentKey, @Nonnull String testKey) {
        Measure<T> measure = context.measure(componentKey, testKey);

        return measure.value();
    }

    @Test
    public void describe() {
        DefaultSensorDescriptor descriptor = new DefaultSensorDescriptor();

        sensor.describe(descriptor);

        assertEquals("Surefire sensor", descriptor.name());
        assertTrue(descriptor.languages().contains("objc"));
    }

    @Test
    public void execute_withDefaultReportPath() {
        helpers.addToFileSystem(firstClassNameTestFile);
        helpers.addToFileSystem(secondClassNameTestFile);
        createReportFile("sonar-reports/TEST-report.xml");

        sensor.execute(context);

        assertEquals(Integer.valueOf(2), getMeasure(firstClassNameTestFile.key(), CoreMetrics.TESTS_KEY));
        assertEquals(Integer.valueOf(0), getMeasure(firstClassNameTestFile.key(), CoreMetrics.TEST_FAILURES_KEY));
        assertEquals(Long.valueOf(4), getMeasure(firstClassNameTestFile.key(), CoreMetrics.TEST_EXECUTION_TIME_KEY));
        assertEquals(Integer.valueOf(2), getMeasure(secondClassNameTestFile.key(), CoreMetrics.TESTS_KEY));
        assertEquals(Integer.valueOf(0), getMeasure(secondClassNameTestFile.key(), CoreMetrics.TEST_FAILURES_KEY));
        assertEquals(Long.valueOf(2), getMeasure(secondClassNameTestFile.key(), CoreMetrics.TEST_EXECUTION_TIME_KEY));
    }

    @Test
    public void execute_withReportPath() {
        settings.setProperty("sonar.objectivec.surefire.reportPath", "TEST-*.xml");
        helpers.addToFileSystem(firstClassNameTestFile);
        helpers.addToFileSystem(secondClassNameTestFile);
        createReportFile("TEST-report.xml");

        sensor.execute(context);

        assertEquals(Integer.valueOf(2), getMeasure(firstClassNameTestFile.key(), CoreMetrics.TESTS_KEY));
        assertEquals(Integer.valueOf(0), getMeasure(firstClassNameTestFile.key(), CoreMetrics.TEST_FAILURES_KEY));
        assertEquals(Long.valueOf(4), getMeasure(firstClassNameTestFile.key(), CoreMetrics.TEST_EXECUTION_TIME_KEY));
        assertEquals(Integer.valueOf(2), getMeasure(secondClassNameTestFile.key(), CoreMetrics.TESTS_KEY));
        assertEquals(Integer.valueOf(0), getMeasure(secondClassNameTestFile.key(), CoreMetrics.TEST_FAILURES_KEY));
        assertEquals(Long.valueOf(2), getMeasure(secondClassNameTestFile.key(), CoreMetrics.TEST_EXECUTION_TIME_KEY));
    }

    @Test
    public void execute_withoutReports() {
        sensor.execute(context);
    }
}
