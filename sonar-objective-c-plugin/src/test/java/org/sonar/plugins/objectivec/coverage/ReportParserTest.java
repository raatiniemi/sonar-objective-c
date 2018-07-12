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
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import javax.xml.parsers.DocumentBuilderFactory;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

@RunWith(JUnit4.class)
public class ReportParserTest {
    private final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    private final Path resourcePath = Paths.get("src", "test", "resources", "cobertura");
    private ReportParser reportParser;

    @Before
    public void setUp() throws Exception {
        reportParser = ReportParser.create(factory.newDocumentBuilder());
    }

    @Test
    public void parse_withoutReport() {
        Path documentPath = Paths.get(resourcePath.toString(), "non-existing.xml");

        List<CoberturaPackage> actual = reportParser.parse(documentPath.toFile());

        assertTrue(actual.isEmpty());
    }

    @Test
    public void parse_withoutPackages() {
        Path documentPath = Paths.get(resourcePath.toString(), "cobertura-without-packages.xml");

        List<CoberturaPackage> actual = reportParser.parse(documentPath.toFile());

        assertTrue(actual.isEmpty());
    }

    @Test
    public void parse_withoutClasses() {
        Path documentPath = Paths.get(resourcePath.toString(), "cobertura-without-classes.xml");
        CoberturaPackage coberturaPackage = CoberturaPackage.from("PackageName", Collections.emptyList());
        List<CoberturaPackage> expected = new ArrayList<>();
        expected.add(coberturaPackage);

        List<CoberturaPackage> actual = reportParser.parse(documentPath.toFile());

        assertFalse(actual.isEmpty());
        assertEquals(expected, actual);
    }

    @Test
    public void parse_withOneClass() {
        Path documentPath = Paths.get(resourcePath.toString(), "cobertura-with-one-class.xml");
        List<Line> lines = new ArrayList<>();
        lines.add(Line.from(13, false, 7));
        lines.add(Line.from(14, false, 7));
        List<CoberturaClass> classes = new ArrayList<>();
        classes.add(CoberturaClass.from("PackageName/ClassName.m", lines));
        CoberturaPackage coberturaPackage = CoberturaPackage.from("PackageName", classes);
        List<CoberturaPackage> expected = new ArrayList<>();
        expected.add(coberturaPackage);

        List<CoberturaPackage> actual = reportParser.parse(documentPath.toFile());

        assertFalse(actual.isEmpty());
        assertEquals(expected, actual);
    }
}
