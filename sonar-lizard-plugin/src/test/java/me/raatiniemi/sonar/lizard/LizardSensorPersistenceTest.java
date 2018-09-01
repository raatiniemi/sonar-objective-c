package me.raatiniemi.sonar.lizard;

import me.raatiniemi.sonar.core.FileSystemHelpers;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.sonar.api.batch.fs.internal.DefaultInputFile;
import org.sonar.api.batch.sensor.internal.SensorContextTester;
import org.sonar.api.batch.sensor.measure.Measure;
import org.sonar.api.measures.CoreMetrics;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.Serializable;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

@RunWith(JUnit4.class)
public class LizardSensorPersistenceTest {
    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    private DefaultInputFile classNameFile;

    private SensorContextTester context;
    private FileSystemHelpers helpers;
    private LizardSensorPersistence persistence;

    @Before
    public void setUp() {
        context = SensorContextTester.create(temporaryFolder.getRoot());
        helpers = FileSystemHelpers.create(context);
        persistence = LizardSensorPersistence.create(context);

        classNameFile = helpers.createFile("TargetName/ClassName.m", "objc");
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
    public void saveMeasures_withoutFile() {
        LizardMeasure measure = LizardMeasure.builder()
                .setPath("TargetName/ClassName.m")
                .setNumberOfFunctions(2)
                .setComplexity(5)
                .build();
        List<LizardMeasure> measures = Collections.singletonList(measure);

        persistence.saveMeasures(measures);

        assertNull(getMeasure(CoreMetrics.FUNCTIONS_KEY));
        assertNull(getMeasure(CoreMetrics.COMPLEXITY_KEY));
    }

    @Test
    public void saveMeasures_withFile() {
        LizardMeasure measure = LizardMeasure.builder()
                .setPath("TargetName/ClassName.m")
                .setNumberOfFunctions(2)
                .setComplexity(5)
                .build();
        List<LizardMeasure> measures = Collections.singletonList(measure);
        helpers.addToFileSystem(classNameFile);

        persistence.saveMeasures(measures);

        assertEquals(Integer.valueOf(2), getMeasure(CoreMetrics.FUNCTIONS_KEY));
        assertEquals(Integer.valueOf(5), getMeasure(CoreMetrics.COMPLEXITY_KEY));
    }

    @Test
    public void saveMeasures_withFileForAnotherLanguage() {
        LizardMeasure measure = LizardMeasure.builder()
                .setPath("TargetName/ClassName.swift")
                .setNumberOfFunctions(2)
                .setComplexity(5)
                .build();
        List<LizardMeasure> measures = Collections.singletonList(measure);
        DefaultInputFile classNameFile = helpers.createFile("TargetName/ClassName.swift", "swift");
        helpers.addToFileSystem(classNameFile);

        persistence.saveMeasures(measures);

        assertNull(context.measure(classNameFile.key(), CoreMetrics.FUNCTIONS_KEY));
        assertNull(context.measure(classNameFile.key(), CoreMetrics.COMPLEXITY_KEY));
    }
}
