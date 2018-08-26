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

package me.raatiniemi.sonarqube;

import javax.annotation.Nonnull;
import java.util.Objects;

final class SampleReport {
    private final String filename;
    private final String value;

    private SampleReport(@Nonnull String filename, @Nonnull String value) {
        this.filename = filename;
        this.value = value;
    }

    static SampleReport from(@Nonnull String filename, @Nonnull String value) {
        return new SampleReport(filename, value);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof SampleReport)) {
            return false;
        }

        SampleReport other = (SampleReport) o;
        return Objects.equals(filename, other.filename)
                && Objects.equals(value, other.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(filename, value);
    }

    @Override
    public String toString() {
        return "SampleReport{" +
                "filename='" + filename + '\'' +
                "value='" + value + '\'' +
                '}';
    }
}
