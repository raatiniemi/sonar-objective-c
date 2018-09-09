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
package me.raatiniemi.sonar.objectivec.language.api;

import org.sonar.squidbridge.measures.CalculatedMetricFormula;
import org.sonar.squidbridge.measures.MetricDef;

public enum ObjectiveCMetric implements MetricDef {
    FILES,
    LINES,
    LINES_OF_CODE,
    COMMENT_LINES,
    STATEMENTS,
    COMPLEXITY,
    FUNCTIONS;

    public String getName() {
        return name();
    }

    public boolean isCalculatedMetric() {
        return false;
    }

    public boolean aggregateIfThereIsAlreadyAValue() {
        return true;
    }

    public boolean isThereAggregationFormula() {
        return true;
    }

    public CalculatedMetricFormula getCalculatedMetricFormula() {
        return null;
    }
}