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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;

@RunWith(Parameterized.class)
public class TestCaseEqualHashCodeTest {
    private final String message;
    private final Boolean expected;
    private final TestCase testCase;
    private final Object compareTo;

    public TestCaseEqualHashCodeTest(
            String message,
            Boolean expected,
            TestCase testCase,
            Object compareTo
    ) {
        this.message = message;
        this.expected = expected;
        this.testCase = testCase;
        this.compareTo = compareTo;
    }

    @Parameterized.Parameters
    public static Collection<Object[]> getParameters() {
        TestCase testCase = TestCase.success("ClassName", "methodName", 0.002);

        return Arrays.asList(
                new Object[][]{
                        {
                                "With same instance",
                                Boolean.TRUE,
                                testCase,
                                testCase
                        },
                        {
                                "With null",
                                Boolean.FALSE,
                                testCase,
                                null
                        },
                        {
                                "With different type",
                                Boolean.FALSE,
                                testCase,
                                ""
                        },
                        {
                                "With different class name",
                                Boolean.FALSE,
                                testCase,
                                TestCase.success("DifferentClassName", "methodName", 0.002)
                        },
                        {
                                "With different method name",
                                Boolean.FALSE,
                                testCase,
                                TestCase.success("ClassName", "differentMethodName", 0.002)
                        },
                        {
                                "With different duration",
                                Boolean.TRUE,
                                testCase,
                                TestCase.success("ClassName", "methodName", 0.001)
                        },
                        {
                                "With different status",
                                Boolean.TRUE,
                                testCase,
                                TestCase.failure("ClassName", "methodName")
                        },
                        {
                                "With different status and class name",
                                Boolean.FALSE,
                                testCase,
                                TestCase.failure("DifferentClassName", "methodName")
                        },
                        {
                                "With different status and method name",
                                Boolean.FALSE,
                                testCase,
                                TestCase.failure("ClassName", "differentMethodName")
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
        assertEquals(message, testCase, compareTo);
        validateHashCodeWhenEqual();
    }

    private void validateHashCodeWhenEqual() {
        assertEquals(message, testCase.hashCode(), compareTo.hashCode());
    }

    private void assertNotEqual() {
        assertNotSame(message, testCase, compareTo);

        validateHashCodeWhenNotEqual();
    }

    private void validateHashCodeWhenNotEqual() {
        if (compareTo == null) {
            return;
        }

        assertNotSame(message, testCase.hashCode(), compareTo.hashCode());
    }
}
