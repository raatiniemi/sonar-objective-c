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

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;

@RunWith(Parameterized.class)
public class TestSuiteEqualHashCodeTest {
    private final String message;
    private final Boolean expected;
    private final TestSuite testSuite;
    private final Object compareTo;

    public TestSuiteEqualHashCodeTest(
            String message,
            Boolean expected,
            TestSuite testSuite,
            Object compareTo
    ) {
        this.message = message;
        this.expected = expected;
        this.testSuite = testSuite;
        this.compareTo = compareTo;
    }

    @Parameterized.Parameters
    public static Collection<Object[]> getParameters() {
        TestCase testCase = TestCase.success("ClassName", "methodName", 0.002);
        TestSuite testSuite = TestSuite.create("ClassName", Collections.singletonList(testCase));

        return Arrays.asList(
                new Object[][]{
                        {
                                "With same instance",
                                Boolean.TRUE,
                                testSuite,
                                testSuite
                        },
                        {
                                "With null",
                                Boolean.FALSE,
                                testSuite,
                                null
                        },
                        {
                                "With different type",
                                Boolean.FALSE,
                                testSuite,
                                ""
                        },
                        {
                                "With same values",
                                Boolean.TRUE,
                                testSuite,
                                TestSuite.create(
                                        "ClassName",
                                        Collections.singletonList(
                                                TestCase.success(
                                                        "ClassName",
                                                        "methodName",
                                                        0.002
                                                )
                                        )
                                )
                        },
                        {
                                "With different class name",
                                Boolean.FALSE,
                                testSuite,
                                TestSuite.create("DifferentClassName", Collections.singletonList(testCase))
                        },
                        {
                                "Without test cases",
                                Boolean.FALSE,
                                testSuite,
                                TestSuite.create("ClassName", Collections.emptyList())
                        },
                        {
                                "With different test cases",
                                Boolean.FALSE,
                                testSuite,
                                TestSuite.create(
                                        "ClassName",
                                        Collections.singletonList(
                                                TestCase.success(
                                                        "ClassName",
                                                        "differentMethodName",
                                                        0.002
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
        assertEquals(message, testSuite, compareTo);
        validateHashCodeWhenEqual();
    }

    private void validateHashCodeWhenEqual() {
        assertEquals(message, testSuite.hashCode(), compareTo.hashCode());
    }

    private void assertNotEqual() {
        assertNotSame(message, testSuite, compareTo);

        validateHashCodeWhenNotEqual();
    }

    private void validateHashCodeWhenNotEqual() {
        if (compareTo == null) {
            return;
        }

        assertNotSame(message, testSuite.hashCode(), compareTo.hashCode());
    }
}
