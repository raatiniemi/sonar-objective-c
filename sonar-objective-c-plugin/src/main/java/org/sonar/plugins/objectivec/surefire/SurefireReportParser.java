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
package org.sonar.plugins.objectivec.surefire;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.xml.parsers.DocumentBuilder;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

final class SurefireReportParser {
    private static final Logger LOGGER = LoggerFactory.getLogger(SurefireReportParser.class);

    private static final String TEST_SUITE = "testsuite";
    private static final String TEST_CASE = "testcase";
    private static final String CLASS_NAME = "classname";
    private static final String NAME = "name";
    private static final String TIME = "time";

    private final DocumentBuilder documentBuilder;

    private SurefireReportParser(@Nonnull DocumentBuilder documentBuilder) {
        this.documentBuilder = documentBuilder;
    }

    @Nonnull
    public static SurefireReportParser create(@Nonnull DocumentBuilder documentBuilder) {
        return new SurefireReportParser(documentBuilder);
    }

    @Nonnull
    private static String getTargetName(@Nonnull Element documentElement) {
        // TODO: How should we handle no name?
        return Objects.requireNonNull(documentElement.getAttribute("name"));
    }

    @Nonnull
    private static NodeList getTestSuiteElements(@Nonnull Document document) {
        return document.getElementsByTagName(TEST_SUITE);
    }

    private static boolean isNotElement(@Nonnull Node item) {
        return item.getNodeType() != Node.ELEMENT_NODE;
    }

    @Nonnull
    private static NodeList getTestCaseElements(@Nonnull Element element) {
        return element.getElementsByTagName(TEST_CASE);
    }

    @Nullable
    TestReport parse(@Nonnull File xmlFile) {
        try {
            Document document = documentBuilder.parse(xmlFile);

            return parseTestReport(document);
        } catch (FileNotFoundException e) {
            LOGGER.error("Surefire report not found {}", xmlFile, e);
        } catch (IOException | SAXException e) {
            LOGGER.error("Error processing file named {}", xmlFile, e);
        }

        // TODO: Prevent returning `null`.
        return null;
    }

    @Nonnull
    private TestReport parseTestReport(@Nonnull Document document) {
        String targetName = getTargetName(document.getDocumentElement());
        List<TestSuite> testSuites = parseTestSuites(document);

        return TestReport.create(targetName, testSuites);
    }

    @Nonnull
    private List<TestSuite> parseTestSuites(@Nonnull Document document) {
        List<TestSuite> testSuites = new ArrayList<>();

        NodeList elements = getTestSuiteElements(document);
        for (int i = 0; i < elements.getLength(); i++) {
            Node node = elements.item(i);
            if (isNotElement(node)) {
                continue;
            }

            Element element = (Element) node;
            testSuites.add(parseTestSuite(element));
        }

        return testSuites;
    }

    @Nonnull
    private TestSuite parseTestSuite(@Nonnull Element element) {
        String className = element.getAttribute(NAME);

        return TestSuite.create(className, parseTestCasesFromTestSuite(element));
    }

    @Nonnull
    private List<TestCase> parseTestCasesFromTestSuite(@Nonnull Element testSuiteElement) {
        List<TestCase> testCases = new ArrayList<>();

        NodeList elements = getTestCaseElements(testSuiteElement);
        for (int i = 0; i < elements.getLength(); i++) {
            Node node = elements.item(i);
            if (isNotElement(node)) {
                continue;
            }

            Element element = (Element) node;
            testCases.add(parseTestCase(element));
        }

        return testCases;
    }

    @Nonnull
    private TestCase parseTestCase(@Nonnull Element element) {
        String className = element.getAttribute(CLASS_NAME);
        String methodName = element.getAttribute(NAME);

        if (element.hasChildNodes()) {
            // TODO: Properly handle child nodes, i.e. failure, skipped, etc.
            return TestCase.failure(className, methodName);
        }

        String time = element.getAttribute(TIME);
        return TestCase.success(className, methodName, Double.valueOf(time));
    }
}
