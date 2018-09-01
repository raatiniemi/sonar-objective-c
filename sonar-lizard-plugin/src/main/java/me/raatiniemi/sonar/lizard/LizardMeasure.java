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
package me.raatiniemi.sonar.lizard;

import javax.annotation.Nonnull;
import java.util.Objects;

class LizardMeasure {
    private final String path;
    private final int complexity;
    private final int numberOfFunctions;

    private LizardMeasure(@Nonnull Builder builder) {
        path = builder.path;
        complexity = builder.complexity;
        numberOfFunctions = builder.numberOfFunctions;
    }

    @Nonnull
    static Builder builder() {
        return new Builder();
    }

    @Nonnull
    String getPath() {
        return path;
    }

    int getComplexity() {
        return complexity;
    }

    int getNumberOfFunctions() {
        return numberOfFunctions;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof LizardMeasure)) {
            return false;
        }

        LizardMeasure that = (LizardMeasure) o;
        return Objects.equals(path, that.path);
    }

    @Override
    public int hashCode() {
        return Objects.hash(path);
    }

    @Override
    public String toString() {
        return "LizardMeasure{" +
                "path='" + path + '\'' +
                ", complexity=" + complexity +
                ", numberOfFunctions=" + numberOfFunctions +
                '}';
    }

    static class Builder {
        private String path = "";
        private int complexity = 0;
        private int numberOfFunctions = 0;

        private Builder() {
        }

        @Nonnull
        Builder setPath(@Nonnull String path) {
            this.path = path;
            return this;
        }

        @Nonnull
        Builder setComplexity(int complexity) {
            this.complexity = complexity;
            return this;
        }

        @Nonnull
        Builder setNumberOfFunctions(int numberOfFunctions) {
            this.numberOfFunctions = numberOfFunctions;
            return this;
        }

        @Nonnull
        LizardMeasure build() {
            return new LizardMeasure(this);
        }
    }
}
