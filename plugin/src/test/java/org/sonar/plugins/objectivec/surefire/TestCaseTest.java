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
import org.junit.runners.JUnit4;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(JUnit4.class)
public class TestCaseTest {
    @Test
    public void isSuccess_withSuccessTestCase() {
        TestCase testCase = TestCase.success("TestClass", "TestMethod", 0.001);

        assertTrue(testCase.isSuccess());
    }

    @Test
    public void isSuccess_withFailureTestCase() {
        TestCase testCase = TestCase.failure("TestClass", "TestMethod");

        assertFalse(testCase.isSuccess());
    }

    @Test
    public void getDurationInMilliseconds_withLowestPossibleValue() {
        TestCase testCase = TestCase.success("TestClass", "TestMethod", 0.001);

        assertEquals(1, testCase.getDurationInMilliseconds());
    }

    @Test
    public void getDurationInMilliseconds_withLongerTest() {
        TestCase testCase = TestCase.success("TestClass", "TestMethod", 0.123);

        assertEquals(123, testCase.getDurationInMilliseconds());
    }

    @Test
    public void getDurationInMilliseconds_withLessThanOneMillisecond() {
        TestCase testCase = TestCase.success("TestClass", "TestMethod", 0.0001);

        assertEquals(0, testCase.getDurationInMilliseconds());
    }
}
