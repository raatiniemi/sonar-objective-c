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

import javax.annotation.Nonnull;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

final class TestSuite {
    private final String className;
    private final Set<TestCase> testCases;

    private TestSuite(@Nonnull String className, @Nonnull Set<TestCase> testCases) {
        this.className = className;
        this.testCases = testCases;
    }

    @Nonnull
    static TestSuite create(@Nonnull String className, @Nonnull List<TestCase> testCases) {
        return new TestSuite(className, new LinkedHashSet<>(testCases));
    }

    @Nonnull
    String getClassName() {
        return className;
    }

    int getNumberOfTests() {
        return testCases.size();
    }

    int getNumberOfFailedTests() {
        return testCases.stream()
                .filter(TestCase::isFailed)
                .collect(Collectors.toList())
                .size();
    }

    long getDurationInMilliseconds() {
        return testCases.parallelStream()
                .filter(TestCase::isSuccess)
                .map(TestCase::getDurationInMilliseconds)
                .reduce(0L, (a, b) -> a + b);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        TestSuite testSuite = (TestSuite) o;
        return Objects.equals(className, testSuite.className) &&
                Objects.equals(testCases, testSuite.testCases);
    }

    @Override
    public int hashCode() {
        return Objects.hash(className, testCases);
    }

    @Override
    public String toString() {
        return "TestSuite{" +
                "className='" + className + '\'' +
                ", testCases=" + testCases +
                '}';
    }
}
