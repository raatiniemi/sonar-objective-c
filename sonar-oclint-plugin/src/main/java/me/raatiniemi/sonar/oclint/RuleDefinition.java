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

package me.raatiniemi.sonar.oclint;

import javax.annotation.Nonnull;
import java.util.Objects;

import static org.apache.commons.lang.StringEscapeUtils.escapeHtml;

final class RuleDefinition {
    private final String key;
    private final String name;
    private final String description;
    private final String severity;
    private final String type;

    private RuleDefinition(@Nonnull Builder builder) {
        key = builder.key;
        name = builder.name;
        description = builder.description.toString();
        severity = builder.severity;
        type = builder.type;
    }

    @Nonnull
    static Builder builder() {
        return new Builder();
    }

    @Nonnull
    String getKey() {
        return key;
    }

    @Nonnull
    String getName() {
        return name;
    }

    @Nonnull
    String getDescription() {
        return description;
    }

    @Nonnull
    String getSeverity() {
        return severity;
    }

    @Nonnull
    String getType() {
        return type;
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
                Objects.equals(severity, ruleDefinition.severity) &&
                Objects.equals(type, ruleDefinition.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(key, name, description, severity, type);
    }

    @Nonnull
    @Override
    public String toString() {
        return "RuleDefinition{" +
                "key='" + key + '\'' +
                ", name='" + name + '\'' +
                ", description='" + escapeHtml(description) + '\'' +
                ", severity='" + severity + '\'' +
                ", type='" + type + '\'' +
                '}';
    }

    static class Builder {
        private String key = "";
        private String name = "";
        private StringBuilder description = new StringBuilder();
        private String severity = "";
        private String type = "CODE_SMELL";

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
        Builder setDescription(@Nonnull String description) {
            this.description.append(description);

            return this;
        }

        void appendToDescription(@Nonnull String description) {
            this.description.append("\n");
            this.description.append(description);
        }

        @Nonnull
        Builder setSeverity(@Nonnull String severity) {
            this.severity = severity;

            return this;
        }

        @Nonnull
        Builder setType(@Nonnull String type) {
            switch (type) {
                case "BUG":
                case "VULNERABILITY":
                    this.type = type;
                    break;

                default:
                    this.type = "CODE_SMELL";
            }

            return this;
        }

        @Nonnull
        RuleDefinition build() {
            return new RuleDefinition(this);
        }
    }
}
