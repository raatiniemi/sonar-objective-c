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

import javax.annotation.Nonnull;
import java.util.*;

final class TestReport {
    private final String targetName;
    private final Set<TestSuite> testSuites;

    private TestReport(@Nonnull String targetName, @Nonnull Set<TestSuite> testSuites) {
        this.targetName = targetName;
        this.testSuites = testSuites;
    }

    @Nonnull
    static TestReport create(@Nonnull String targetName, @Nonnull List<TestSuite> testSuites) {
        return new TestReport(targetName, new LinkedHashSet<>(testSuites));
    }

    @Nonnull
    Collection<TestSuite> getTestSuites() {
        return Collections.unmodifiableCollection(testSuites);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        TestReport that = (TestReport) o;
        return Objects.equals(targetName, that.targetName) &&
                Objects.equals(testSuites, that.testSuites);
    }

    @Override
    public int hashCode() {
        return Objects.hash(targetName, testSuites);
    }

    @Override
    public String toString() {
        return "TestReport{" +
                "targetName='" + targetName + '\'' +
                ", testSuites=" + testSuites +
                '}';
    }
}
