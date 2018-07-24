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

import me.raatiniemi.sonarqube.XmlReportParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.annotation.Nonnull;
import javax.xml.parsers.DocumentBuilder;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

final class ReportParser extends XmlReportParser<TestReport> {
    private static final Logger LOGGER = LoggerFactory.getLogger(ReportParser.class);

    private static final String TEST_SUITE = "testsuite";
    private static final String TEST_CASE = "testcase";
    private static final String CLASS_NAME = "classname";
    private static final String NAME = "name";
    private static final String TIME = "time";

    private ReportParser(@Nonnull DocumentBuilder documentBuilder) {
        super(documentBuilder);
    }

    @Nonnull
    static ReportParser create(@Nonnull DocumentBuilder documentBuilder) {
        return new ReportParser(documentBuilder);
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

    @Nonnull
    @Override
    protected TestReport parse(@Nonnull Document document) {
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
            Optional<TestCase> value = parseTestCase(element);
            value.ifPresent(testCases::add);
        }

        return testCases;
    }

    @Nonnull
    private Optional<TestCase> parseTestCase(@Nonnull Element element) {
        String className = element.getAttribute(CLASS_NAME);
        String methodName = element.getAttribute(NAME);

        if (isSuccessTestCase(element)) {
            String time = element.getAttribute(TIME);
            TestCase testCase = TestCase.success(className, methodName, Double.valueOf(time));
            return Optional.of(testCase);
        }

        if (isFailureTestCase(element)) {
            TestCase testCase = TestCase.failure(className, methodName);
            return Optional.of(testCase);
        }

        LOGGER.error("Unable to parse element: " + element);
        return Optional.empty();
    }

    private boolean isSuccessTestCase(@Nonnull Element element) {
        return !element.hasChildNodes();
    }

    private boolean isFailureTestCase(@Nonnull Element element) {
        NodeList failureElements = element.getElementsByTagName("failure");

        return 1 == failureElements.getLength();
    }
}
