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
import java.util.Objects;

final class TestCase {
    private final Status status;
    private final String className;
    private final String methodName;
    private final double duration;

    private TestCase(@Nonnull Builder builder) {
        this.status = builder.status;
        this.className = builder.className;
        this.methodName = builder.methodName;
        this.duration = builder.duration;
    }

    @Nonnull
    private static Builder builder() {
        return new Builder();
    }

    @Nonnull
    static TestCase success(@Nonnull String className, @Nonnull String methodName, double duration) {
        return builder()
                .setStatus(Status.SUCCESS)
                .setClassName(className)
                .setMethodName(methodName)
                .setDuration(duration)
                .build();
    }

    @Nonnull
    static TestCase failure(@Nonnull String className, @Nonnull String methodName) {
        return builder()
                .setStatus(Status.FAILURE)
                .setClassName(className)
                .setMethodName(methodName)
                .build();
    }

    boolean isSuccess() {
        return status == Status.SUCCESS;
    }

    boolean isFailed() {
        return status == Status.FAILURE;
    }

    long getDurationInMilliseconds() {
        return (long) (duration * 1000);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        TestCase testCase = (TestCase) o;
        return Objects.equals(className, testCase.className) &&
                Objects.equals(methodName, testCase.methodName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(className, methodName);
    }

    @Override
    public String toString() {
        return "TestCase{" +
                "status=" + status +
                ", className='" + className + '\'' +
                ", methodName='" + methodName + '\'' +
                ", duration=" + duration +
                '}';
    }

    private enum Status {
        SUCCESS, FAILURE
    }

    private static class Builder {
        private Status status;
        private String className;
        private String methodName;
        private double duration = 0.0;

        @Nonnull
        private Builder setStatus(@Nonnull Status status) {
            this.status = status;

            return this;
        }

        @Nonnull
        private Builder setClassName(@Nonnull String className) {
            this.className = className;

            return this;
        }

        @Nonnull
        private Builder setMethodName(@Nonnull String name) {
            this.methodName = name;

            return this;
        }

        @Nonnull
        private Builder setDuration(double duration) {
            this.duration = duration;

            return this;
        }

        @Nonnull
        private TestCase build() {
            return new TestCase(this);
        }
    }
}
