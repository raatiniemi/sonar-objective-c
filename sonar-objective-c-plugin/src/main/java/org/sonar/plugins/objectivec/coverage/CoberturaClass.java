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
package org.sonar.plugins.objectivec.coverage;

import javax.annotation.Nonnull;
import java.util.*;

final class CoberturaClass {
    private final String filename;
    private final Set<CoberturaLine> lines;

    private CoberturaClass(@Nonnull String filename, @Nonnull List<CoberturaLine> lines) {
        this.filename = filename;
        this.lines = new LinkedHashSet<>(lines);
    }

    static CoberturaClass from(@Nonnull String filename, @Nonnull List<CoberturaLine> lines) {
        return new CoberturaClass(filename, lines);
    }

    @Nonnull
    String getFilename() {
        return filename;
    }

    @Nonnull
    Collection<CoberturaLine> getLines() {
        return Collections.unmodifiableCollection(lines);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof CoberturaClass)) {
            return false;
        }

        CoberturaClass other = (CoberturaClass) o;
        return Objects.equals(filename, other.filename) &&
                Objects.equals(lines, other.lines);
    }

    @Override
    public int hashCode() {
        return Objects.hash(filename, lines);
    }

    @Override
    public String toString() {
        return "CoberturaClass{" +
                "filename='" + filename + '\'' +
                ", lines=" + lines +
                '}';
    }
}
