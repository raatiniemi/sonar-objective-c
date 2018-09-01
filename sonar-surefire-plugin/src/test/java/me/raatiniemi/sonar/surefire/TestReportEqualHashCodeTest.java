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

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;

@RunWith(Parameterized.class)
public class TestReportEqualHashCodeTest {
    private final String message;
    private final Boolean expected;
    private final TestReport testReport;
    private final Object compareTo;

    public TestReportEqualHashCodeTest(
            String message,
            Boolean expected,
            TestReport testReport,
            Object compareTo
    ) {
        this.message = message;
        this.expected = expected;
        this.testReport = testReport;
        this.compareTo = compareTo;
    }

    @Parameterized.Parameters
    public static Collection<Object[]> getParameters() {
        TestCase testCase = TestCase.success("ClassName", "methodName", 0.002);
        TestSuite testSuite = TestSuite.create("ClassName", Collections.singletonList(testCase));
        TestReport testReport = TestReport.create("TestTarget.xctest", Collections.singletonList(testSuite));

        return Arrays.asList(
                new Object[][]{
                        {
                                "With same instance",
                                Boolean.TRUE,
                                testReport,
                                testReport
                        },
                        {
                                "With null",
                                Boolean.FALSE,
                                testReport,
                                null
                        },
                        {
                                "With different type",
                                Boolean.FALSE,
                                testReport,
                                ""
                        },
                        {
                                "With same values",
                                Boolean.TRUE,
                                testReport,
                                TestReport.create(
                                        "TestTarget.xctest",
                                        Collections.singletonList(
                                                TestSuite.create(
                                                        "ClassName",
                                                        Collections.singletonList(
                                                                TestCase.success(
                                                                        "ClassName",
                                                                        "methodName",
                                                                        0.002)
                                                        )
                                                )
                                        )
                                )
                        },
                        {
                                "With different target name",
                                Boolean.FALSE,
                                testReport,
                                TestReport.create(
                                        "DifferentTestTarget.xctest",
                                        Collections.singletonList(testSuite)
                                )
                        },
                        {
                                "Without test suits",
                                Boolean.FALSE,
                                testReport,
                                TestReport.create("TestTarget.xctest", Collections.emptyList())
                        },
                        {
                                "With different test cases",
                                Boolean.FALSE,
                                testReport,
                                TestReport.create(
                                        "TestTarget.xctest",
                                        Collections.singletonList(
                                                TestSuite.create(
                                                        "",
                                                        Collections.singletonList(
                                                                TestCase.success(
                                                                        "ClassName",
                                                                        "differentMethodName",
                                                                        0.002
                                                                )
                                                        )
                                                )
                                        )
                                )
                        }
                }
        );
    }

    @Test
    public void equals() {
        if (shouldBeEqual()) {
            assertEqual();
            return;
        }

        assertNotEqual();
    }

    private Boolean shouldBeEqual() {
        return expected;
    }

    private void assertEqual() {
        assertEquals(message, testReport, compareTo);
        validateHashCodeWhenEqual();
    }

    private void validateHashCodeWhenEqual() {
        assertEquals(message, testReport.hashCode(), compareTo.hashCode());
    }

    private void assertNotEqual() {
        assertNotSame(message, testReport, compareTo);

        validateHashCodeWhenNotEqual();
    }

    private void validateHashCodeWhenNotEqual() {
        if (compareTo == null) {
            return;
        }

        assertNotSame(message, testReport.hashCode(), compareTo.hashCode());
    }
}
