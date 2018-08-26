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

import org.sonar.api.batch.sensor.SensorContext;

import javax.annotation.Nonnull;
import java.util.Collection;

public class SampleSensorPersistence extends SensorPersistence<String> {
    private SampleSensorPersistence(@Nonnull SensorContext context) {
        super(context);
    }

    @Nonnull
    static SampleSensorPersistence create(@Nonnull SensorContext context) {
        return new SampleSensorPersistence(context);
    }

    @Override
    public void saveMeasures(@Nonnull Collection<String> measures) {
    }
}
