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
package org.sonar.plugins.objectivec.complexity;

import org.sonar.api.batch.measure.Metric;

import java.io.Serializable;

final class LizardMeasure<T extends Serializable> {
    private final Metric<T> metric;
    private final T value;

    private LizardMeasure(Metric<T> metric, T value) {
        this.metric = metric;
        this.value = value;
    }

    static <T extends Serializable> LizardMeasure of(Metric<T> metric, T value) {
        return new LizardMeasure<>(metric, value);
    }

    Metric<T> getMetric() {
        return metric;
    }

    T getValue() {
        return value;
    }
}
