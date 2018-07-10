/*
 * Copyright © 2012 OCTO Technology, Backelite (${email})
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

import java.util.Objects;

final class Line {
    private final int number;
    private final boolean branch;
    private final int hits;

    private Line(int number, boolean branch, int hits) {
        this.number = number;
        this.branch = branch;
        this.hits = hits;
    }

    static Line from(int number, boolean branch, int hits) {
        return new Line(number, branch, hits);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof Line)) {
            return false;
        }

        Line line = (Line) o;
        return number == line.number &&
                branch == line.branch;
    }

    @Override
    public int hashCode() {
        return Objects.hash(number, branch);
    }
}
