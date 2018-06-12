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

package org.sonar.plugins.objectivec.violations.oclint;

import javax.annotation.Nonnull;
import java.util.Objects;

final class RuleDefinition {
    private final String key;
    private final String name;
    private final String description;
    private final String severity;

    private RuleDefinition(Builder builder) {
        key = builder.key;
        name = builder.name;
        description = builder.description;
        severity = builder.severity;
    }

    static Builder builder() {
        return new Builder();
    }

    String getKey() {
        return key;
    }

    String getName() {
        return name;
    }

    String getDescription() {
        return description;
    }

    String getSeverity() {
        return severity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof RuleDefinition)) {
            return false;
        }

        RuleDefinition ruleDefinition = (RuleDefinition) o;
        return Objects.equals(key, ruleDefinition.key) &&
                Objects.equals(name, ruleDefinition.name) &&
                Objects.equals(description, ruleDefinition.description) &&
                Objects.equals(severity, ruleDefinition.severity);
    }

    @Override
    public int hashCode() {
        return Objects.hash(key, name, description, severity);
    }

    static class Builder {
        private String key = "";
        private String name = "";
        private String description = "";
        private String severity = "";

        private Builder() {
        }

        @Nonnull
        Builder setKey(@Nonnull String key) {
            this.key = key;

            return this;
        }

        @Nonnull
        Builder setName(@Nonnull String name) {
            this.name = name;

            return this;
        }

        @Nonnull
        String getDescription() {
            return description;
        }

        @Nonnull
        Builder setDescription(@Nonnull String description) {
            this.description = description;

            return this;
        }

        @Nonnull
        Builder setSeverity(@Nonnull String severity) {
            this.severity = severity;

            return this;
        }

        @Nonnull
        RuleDefinition build() {
            return new RuleDefinition(this);
        }
    }
}
