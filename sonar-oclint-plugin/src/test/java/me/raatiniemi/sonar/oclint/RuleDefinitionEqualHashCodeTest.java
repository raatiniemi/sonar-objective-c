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

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;

@RunWith(Parameterized.class)
public class RuleDefinitionEqualHashCodeTest {
    private final String message;
    private final Boolean expected;
    private final RuleDefinition ruleDefinition;
    private final Object compareTo;

    public RuleDefinitionEqualHashCodeTest(
            String message,
            Boolean expected,
            RuleDefinition ruleDefinition,
            Object compareTo
    ) {
        this.message = message;
        this.expected = expected;
        this.ruleDefinition = ruleDefinition;
        this.compareTo = compareTo;
    }

    @Parameterized.Parameters
    public static Collection<Object[]> getParameters() {
        RuleDefinition ruleDefinition = RuleDefinition.builder()
                .setKey("key")
                .setName("name")
                .setDescription("description")
                .setSeverity("severity")
                .build();

        return Arrays.asList(
                new Object[][]{
                        {
                                "With same instance",
                                Boolean.TRUE,
                                ruleDefinition,
                                ruleDefinition
                        },
                        {
                                "With null",
                                Boolean.FALSE,
                                ruleDefinition,
                                null
                        },
                        {
                                "With different type",
                                Boolean.FALSE,
                                ruleDefinition,
                                ""
                        },
                        {
                                "With same values",
                                Boolean.TRUE,
                                ruleDefinition,
                                RuleDefinition.builder()
                                        .setKey("key")
                                        .setName("name")
                                        .setDescription("description")
                                        .setSeverity("severity")
                                        .build()
                        },
                        {
                                "With different key",
                                Boolean.FALSE,
                                ruleDefinition,
                                RuleDefinition.builder()
                                        .setKey("key-2")
                                        .setName("name")
                                        .setDescription("description")
                                        .setSeverity("severity")
                                        .build()
                        },
                        {
                                "With different name",
                                Boolean.FALSE,
                                ruleDefinition,
                                RuleDefinition.builder()
                                        .setKey("key")
                                        .setName("name-2")
                                        .setDescription("description")
                                        .setSeverity("severity")
                                        .build()
                        },
                        {
                                "With different description",
                                Boolean.FALSE,
                                ruleDefinition,
                                RuleDefinition.builder()
                                        .setKey("key")
                                        .setName("name")
                                        .setDescription("description-2")
                                        .setSeverity("severity")
                                        .build()
                        },
                        {
                                "With different severity",
                                Boolean.FALSE,
                                ruleDefinition,
                                RuleDefinition.builder()
                                        .setKey("key")
                                        .setName("name")
                                        .setDescription("description")
                                        .setSeverity("severity-2")
                                        .build()
                        },
                        {
                                "With different type",
                                Boolean.FALSE,
                                ruleDefinition,
                                RuleDefinition.builder()
                                        .setKey("key")
                                        .setName("name")
                                        .setDescription("description")
                                        .setSeverity("severity")
                                        .setType("BUG")
                                        .build()
                        }
                }
        );
    }

    @Test
    public void equals() {
        if (shouldBeEqual()) {
            assertEqual();
            return;
        }

        assertNotEqual();
    }

    private Boolean shouldBeEqual() {
        return expected;
    }

    private void assertEqual() {
        assertEquals(message, ruleDefinition, compareTo);
        validateHashCodeWhenEqual();
    }

    private void validateHashCodeWhenEqual() {
        assertEquals(message, ruleDefinition.hashCode(), compareTo.hashCode());
    }

    private void assertNotEqual() {
        assertNotSame(message, ruleDefinition, compareTo);

        validateHashCodeWhenNotEqual();
    }

    private void validateHashCodeWhenNotEqual() {
        if (compareTo == null) {
            return;
        }

        assertNotSame(message, ruleDefinition.hashCode(), compareTo.hashCode());
    }
}
