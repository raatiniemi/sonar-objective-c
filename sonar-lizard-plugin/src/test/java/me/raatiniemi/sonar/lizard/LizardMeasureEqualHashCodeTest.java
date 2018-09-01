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
package me.raatiniemi.sonar.lizard;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;

@RunWith(Parameterized.class)
public class LizardMeasureEqualHashCodeTest {
    private final String message;
    private final Boolean expected;
    private final LizardMeasure measure;
    private final Object compareTo;

    public LizardMeasureEqualHashCodeTest(
            String message,
            Boolean expected,
            LizardMeasure measure,
            Object compareTo
    ) {
        this.message = message;
        this.expected = expected;
        this.measure = measure;
        this.compareTo = compareTo;
    }

    @Parameterized.Parameters
    public static Collection<Object[]> getParameters() {
        LizardMeasure measure = LizardMeasure.builder()
                .setPath("TargetName/ClassName.m")
                .setNumberOfFunctions(2)
                .setComplexity(10)
                .build();

        return Arrays.asList(
                new Object[][]{
                        {
                                "With same instance",
                                Boolean.TRUE,
                                measure,
                                measure
                        },
                        {
                                "With null",
                                Boolean.FALSE,
                                measure,
                                null
                        },
                        {
                                "With different type",
                                Boolean.FALSE,
                                measure,
                                ""
                        },
                        {
                                "With different path",
                                Boolean.FALSE,
                                measure,
                                LizardMeasure.builder()
                                        .setPath("TargetName/AnotherClassName.m")
                                        .setNumberOfFunctions(2)
                                        .setComplexity(10)
                                        .build()
                        },
                        {
                                "With different number of functions",
                                Boolean.TRUE,
                                measure,
                                LizardMeasure.builder()
                                        .setPath("TargetName/ClassName.m")
                                        .setNumberOfFunctions(3)
                                        .setComplexity(10)
                                        .build()
                        },
                        {
                                "With different complexity",
                                Boolean.TRUE,
                                measure,
                                LizardMeasure.builder()
                                        .setPath("TargetName/ClassName.m")
                                        .setNumberOfFunctions(2)
                                        .setComplexity(9)
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
        assertEquals(message, measure, compareTo);
        validateHashCodeWhenEqual();
    }

    private void validateHashCodeWhenEqual() {
        assertEquals(message, measure.hashCode(), compareTo.hashCode());
    }

    private void assertNotEqual() {
        assertNotSame(message, measure, compareTo);

        validateHashCodeWhenNotEqual();
    }

    private void validateHashCodeWhenNotEqual() {
        if (compareTo == null) {
            return;
        }

        assertNotSame(message, measure.hashCode(), compareTo.hashCode());
    }
}
