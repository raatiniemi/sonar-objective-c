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
import java.util.Optional;

import static org.junit.Assert.*;

@RunWith(JUnit4.class)
public class SurefireXmlReportParserTest {
    private final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    private final Path resourcePath = Paths.get("src", "test", "resources", "surefire");
    private SurefireXmlReportParser reportParser;

    @Before
    public void setUp() throws Exception {
        reportParser = SurefireXmlReportParser.create(factory.newDocumentBuilder());
    }

    @Test
    public void parse_withoutReport() {
        Path documentPath = Paths.get(resourcePath.toString(), "TEST-non-existing.xml");

        Optional<TestReport> actual = reportParser.parse(documentPath.toFile());

        assertFalse(actual.isPresent());
    }

    @Test
    public void parse_withEmptyReport() {
        Path documentPath = Paths.get(resourcePath.toString(), "empty", "TEST-empty.xml");
        TestReport expected = TestReport.create("TestTarget.xctest", Collections.emptyList());

        Optional<TestReport> actual = reportParser.parse(documentPath.toFile());

        assertTrue(actual.isPresent());
        assertEquals(expected, actual.get());
    }

    @Test
    public void parse_withOneTestCaseReport() {
        Path documentPath = Paths.get(resourcePath.toString(), "reports", "TEST-one-test-case.xml");
        TestCase testCase = TestCase.success("ClassNameTest", "testMethodName", 0.002);
        TestSuite testSuite = TestSuite.create("ClassNameTest", Collections.singletonList(testCase));
        TestReport expected = TestReport.create("TestTarget.xctest", Collections.singletonList(testSuite));

        Optional<TestReport> actual = reportParser.parse(documentPath.toFile());

        assertTrue(actual.isPresent());
        assertEquals(expected, actual.get());
    }

    @Test
    public void parse_withMultipleTestCasesReport() {
        Path documentPath = Paths.get(resourcePath.toString(), "reports", "TEST-multiple-test-cases.xml");
        List<TestCase> firstTestCases = new ArrayList<>();
        firstTestCases.add(TestCase.success("FirstClassNameTest", "testMethodName_withCondition", 0.003));
        firstTestCases.add(TestCase.success("FirstClassNameTest", "testMethodName", 0.001));
        List<TestCase> secondTestCases = new ArrayList<>();
        secondTestCases.add(TestCase.success("SecondClassNameTest", "testMethodName_withCondition", 0.001));
        secondTestCases.add(TestCase.success("SecondClassNameTest", "testMethodName", 0.001));
        List<TestSuite> testSuites = new ArrayList<>();
        testSuites.add(TestSuite.create("FirstClassNameTest", firstTestCases));
        testSuites.add(TestSuite.create("SecondClassNameTest", secondTestCases));
        TestReport expected = TestReport.create("TestTarget.xctest", testSuites);

        Optional<TestReport> actual = reportParser.parse(documentPath.toFile());

        assertTrue(actual.isPresent());
        assertEquals(expected, actual.get());
    }

    @Test
    public void parse_withErrorReport() {
        Path documentPath = Paths.get(resourcePath.toString(), "error", "TEST-error.xml");
        TestCase testCase = TestCase.failure("ClassNameTest", "testMethodName_withFailure");
        TestSuite testSuite = TestSuite.create("ClassNameTest", Collections.singletonList(testCase));
        TestReport expected = TestReport.create("TestTarget.xctest", Collections.singletonList(testSuite));

        Optional<TestReport> actual = reportParser.parse(documentPath.toFile());

        assertTrue(actual.isPresent());
        assertEquals(expected, actual.get());
    }
}
