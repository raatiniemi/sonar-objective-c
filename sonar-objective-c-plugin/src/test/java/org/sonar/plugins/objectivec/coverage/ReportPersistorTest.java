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

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.batch.fs.internal.DefaultInputFile;
import org.sonar.api.batch.sensor.internal.SensorContextTester;
import org.sonar.api.utils.log.LogTester;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

@RunWith(JUnit4.class)
public class ReportPersistorTest {
    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Rule
    public LogTester logTester = new LogTester();

    private DefaultInputFile classNameFile;
    private DefaultInputFile secondClassNameFile;
    private DefaultInputFile anotherTargetClassNameFile;

    private SensorContextTester context;
    private ReportPersistor persistor;

    @Before
    public void setUp() {
        context = SensorContextTester.create(temporaryFolder.getRoot());
        persistor = ReportPersistor.create(context);

        classNameFile = createFile("TargetName/ClassName.m");
        secondClassNameFile = createFile("TargetName/SecondClassName.m");
        anotherTargetClassNameFile = createFile("AnotherTargetName/ClassName.m");
    }

    @Nonnull
    private DefaultInputFile createFile(@Nonnull String relativePath) {
        return new DefaultInputFile(context.module().key(), relativePath)
                .setLanguage("bla")
                .setType(InputFile.Type.MAIN)
                .initMetadata("1\n2\n3\n4\n5\n6");
    }

    private void addFileToFs(@Nonnull DefaultInputFile inputFile) {
        context.fileSystem().add(inputFile);
    }

    @Test
    public void saveReports_withoutPackages() {
        List<CoberturaPackage> packages = Collections.emptyList();

        persistor.saveReports(packages);

        // TODO: How can we verify that no coverage are added.
    }

    @Test
    public void saveReports_withEmptyReports() {
        CoberturaPackage coberturaPackage = CoberturaPackage.from("TargetName", Collections.emptyList());
        List<CoberturaPackage> packages = Collections.singletonList(coberturaPackage);

        persistor.saveReports(packages);

        // TODO: How can we verify that no coverage are added.
    }

    @Test
    public void saveReports_withoutClassFile() {
        CoberturaLine line = CoberturaLine.from(1, 1);
        CoberturaClass coberturaClass = CoberturaClass.from("TargetName/ClassName.m", Collections.singletonList(line));
        CoberturaPackage coberturaPackage = CoberturaPackage.from("TargetName", Collections.singletonList(coberturaClass));
        List<CoberturaPackage> coberturaPackages = Collections.singletonList(coberturaPackage);

        persistor.saveReports(coberturaPackages);

        assertNull(context.lineHits(classNameFile.key(), 1));
        assertNull(context.conditions(classNameFile.key(), 1));
        assertNull(context.coveredConditions(classNameFile.key(), 1));
    }

    @Test
    public void saveReports_withZeroLineNumber() {
        CoberturaLine line = CoberturaLine.from(0, 0);
        CoberturaClass coberturaClass = CoberturaClass.from("TargetName/ClassName.m", Collections.singletonList(line));
        CoberturaPackage coberturaPackage = CoberturaPackage.from("TargetName", Collections.singletonList(coberturaClass));
        List<CoberturaPackage> coberturaPackages = Collections.singletonList(coberturaPackage);
        addFileToFs(classNameFile);

        persistor.saveReports(coberturaPackages);

        assertNull(context.lineHits(classNameFile.key(), 0));
        assertNull(context.conditions(classNameFile.key(), 0));
        assertNull(context.coveredConditions(classNameFile.key(), 0));
    }

    @Test
    public void saveReports_withSimpleClass() {
        CoberturaLine line = CoberturaLine.from(1, 1);
        CoberturaClass coberturaClass = CoberturaClass.from("TargetName/ClassName.m", Collections.singletonList(line));
        CoberturaPackage coberturaPackage = CoberturaPackage.from("TargetName", Collections.singletonList(coberturaClass));
        List<CoberturaPackage> coberturaPackages = Collections.singletonList(coberturaPackage);
        addFileToFs(classNameFile);

        persistor.saveReports(coberturaPackages);

        assertEquals(Integer.valueOf(1), context.lineHits(classNameFile.key(), 1));
        assertNull(context.conditions(classNameFile.key(), 1));
        assertNull(context.coveredConditions(classNameFile.key(), 1));
    }

    @Test
    public void saveReports_withConditions() {
        CoberturaLine line = CoberturaLine.from(1, 1, 2, 1);
        CoberturaClass coberturaClass = CoberturaClass.from("TargetName/ClassName.m", Collections.singletonList(line));
        CoberturaPackage coberturaPackage = CoberturaPackage.from("TargetName", Collections.singletonList(coberturaClass));
        List<CoberturaPackage> coberturaPackages = Collections.singletonList(coberturaPackage);
        addFileToFs(classNameFile);

        persistor.saveReports(coberturaPackages);

        assertEquals(Integer.valueOf(1), context.lineHits(classNameFile.key(), 1));
        assertEquals(Integer.valueOf(2), context.conditions(classNameFile.key(), 1));
        assertEquals(Integer.valueOf(1), context.coveredConditions(classNameFile.key(), 1));
    }

    @Test
    public void saveReports_withLines() {
        List<CoberturaLine> lines = Arrays.asList(
                CoberturaLine.from(1, 1),
                CoberturaLine.from(2, 1)
        );
        CoberturaClass coberturaClass = CoberturaClass.from("TargetName/ClassName.m", lines);
        CoberturaPackage coberturaPackage = CoberturaPackage.from("TargetName", Collections.singletonList(coberturaClass));
        List<CoberturaPackage> coberturaPackages = Collections.singletonList(coberturaPackage);
        addFileToFs(classNameFile);

        persistor.saveReports(coberturaPackages);

        assertEquals(Integer.valueOf(1), context.lineHits(classNameFile.key(), 1));
        assertNull(context.conditions(classNameFile.key(), 1));
        assertNull(context.coveredConditions(classNameFile.key(), 1));
        assertEquals(Integer.valueOf(1), context.lineHits(classNameFile.key(), 2));
        assertNull(context.conditions(classNameFile.key(), 2));
        assertNull(context.coveredConditions(classNameFile.key(), 2));
    }

    @Test
    public void saveReports_withFiles() {
        List<CoberturaLine> lines = Arrays.asList(
                CoberturaLine.from(1, 1),
                CoberturaLine.from(2, 2, 2, 2)
        );
        List<CoberturaClass> coberturaClasses = Arrays.asList(
                CoberturaClass.from("TargetName/ClassName.m", lines),
                CoberturaClass.from("TargetName/SecondClassName.m", lines)
        );
        CoberturaPackage coberturaPackage = CoberturaPackage.from("TargetName", coberturaClasses);
        List<CoberturaPackage> coberturaPackages = Collections.singletonList(coberturaPackage);
        addFileToFs(classNameFile);
        addFileToFs(secondClassNameFile);

        persistor.saveReports(coberturaPackages);

        assertEquals(Integer.valueOf(1), context.lineHits(classNameFile.key(), 1));
        assertNull(context.conditions(classNameFile.key(), 1));
        assertNull(context.coveredConditions(classNameFile.key(), 1));
        assertEquals(Integer.valueOf(2), context.lineHits(classNameFile.key(), 2));
        assertEquals(Integer.valueOf(2), context.conditions(classNameFile.key(), 2));
        assertEquals(Integer.valueOf(2), context.coveredConditions(classNameFile.key(), 2));
        assertEquals(Integer.valueOf(1), context.lineHits(secondClassNameFile.key(), 1));
        assertNull(context.conditions(secondClassNameFile.key(), 1));
        assertNull(context.coveredConditions(secondClassNameFile.key(), 1));
        assertEquals(Integer.valueOf(2), context.lineHits(secondClassNameFile.key(), 2));
        assertEquals(Integer.valueOf(2), context.conditions(secondClassNameFile.key(), 2));
        assertEquals(Integer.valueOf(2), context.coveredConditions(secondClassNameFile.key(), 2));
    }

    @Test
    public void saveReports_withTargets() {
        List<CoberturaLine> lines = Arrays.asList(
                CoberturaLine.from(1, 1),
                CoberturaLine.from(2, 2, 2, 2)
        );
        List<CoberturaPackage> coberturaPackages = Arrays.asList(
                CoberturaPackage.from(
                        "TargetName",
                        Arrays.asList(
                                CoberturaClass.from("TargetName/ClassName.m", lines),
                                CoberturaClass.from("TargetName/SecondClassName.m", lines)
                        )
                ),
                CoberturaPackage.from(
                        "AnotherTargetName",
                        Collections.singletonList(CoberturaClass.from("AnotherTargetName/ClassName.m", lines))
                )
        );
        addFileToFs(classNameFile);
        addFileToFs(secondClassNameFile);
        addFileToFs(anotherTargetClassNameFile);

        persistor.saveReports(coberturaPackages);

        assertEquals(Integer.valueOf(1), context.lineHits(classNameFile.key(), 1));
        assertNull(context.conditions(classNameFile.key(), 1));
        assertNull(context.coveredConditions(classNameFile.key(), 1));
        assertEquals(Integer.valueOf(2), context.lineHits(classNameFile.key(), 2));
        assertEquals(Integer.valueOf(2), context.conditions(classNameFile.key(), 2));
        assertEquals(Integer.valueOf(2), context.coveredConditions(classNameFile.key(), 2));
        assertEquals(Integer.valueOf(1), context.lineHits(secondClassNameFile.key(), 1));
        assertNull(context.conditions(secondClassNameFile.key(), 1));
        assertNull(context.coveredConditions(secondClassNameFile.key(), 1));
        assertEquals(Integer.valueOf(2), context.lineHits(secondClassNameFile.key(), 2));
        assertEquals(Integer.valueOf(2), context.conditions(secondClassNameFile.key(), 2));
        assertEquals(Integer.valueOf(2), context.coveredConditions(secondClassNameFile.key(), 2));
        assertEquals(Integer.valueOf(1), context.lineHits(anotherTargetClassNameFile.key(), 1));
        assertNull(context.conditions(anotherTargetClassNameFile.key(), 1));
        assertNull(context.coveredConditions(anotherTargetClassNameFile.key(), 1));
        assertEquals(Integer.valueOf(2), context.lineHits(anotherTargetClassNameFile.key(), 2));
        assertEquals(Integer.valueOf(2), context.conditions(anotherTargetClassNameFile.key(), 2));
        assertEquals(Integer.valueOf(2), context.coveredConditions(anotherTargetClassNameFile.key(), 2));
    }
}
