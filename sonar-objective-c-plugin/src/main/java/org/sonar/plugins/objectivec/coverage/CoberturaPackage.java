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

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

final class CoberturaPackage {
    private final String name;
    private final Set<CoberturaClass> classes;

    private CoberturaPackage(String name, Set<CoberturaClass> classes) {
        this.name = name;
        this.classes = classes;
    }

    static CoberturaPackage from(String name, List<CoberturaClass> classes) {
        return new CoberturaPackage(name, new LinkedHashSet<>(classes));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof CoberturaPackage)) {
            return false;
        }

        CoberturaPackage that = (CoberturaPackage) o;
        return Objects.equals(name, that.name) &&
                Objects.equals(classes, that.classes);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, classes);
    }

    @Override
    public String toString() {
        return "CoberturaPackage{" +
                "name='" + name + '\'' +
                ", classes=" + classes +
                '}';
    }
}
