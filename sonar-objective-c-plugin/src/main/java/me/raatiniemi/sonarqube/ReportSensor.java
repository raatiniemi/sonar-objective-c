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
package me.raatiniemi.sonarqube;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.batch.sensor.Sensor;
import org.sonar.api.config.Settings;

import javax.annotation.Nonnull;

public abstract class ReportSensor implements Sensor {
    private static final Logger LOGGER = LoggerFactory.getLogger(ReportSensor.class);

    private final Settings settings;

    protected ReportSensor(@Nonnull Settings settings) {
        this.settings = settings;
    }

    @Nonnull
    protected final String getSetting(@Nonnull String key, @Nonnull String defaultValue) {
        String value = settings.getString(key);

        if (value == null) {
            LOGGER.debug("No value specified for \"" + key + "\" using default value");
            return defaultValue;
        }

        return value;
    }
}
