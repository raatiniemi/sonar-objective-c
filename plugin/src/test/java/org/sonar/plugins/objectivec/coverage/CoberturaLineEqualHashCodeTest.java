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

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;

@RunWith(Parameterized.class)
public class CoberturaLineEqualHashCodeTest {
    private final String message;
    private final Boolean expected;
    private final CoberturaLine line;
    private final Object compareTo;

    public CoberturaLineEqualHashCodeTest(
            String message,
            Boolean expected,
            CoberturaLine line,
            Object compareTo
    ) {
        this.message = message;
        this.expected = expected;
        this.line = line;
        this.compareTo = compareTo;
    }

    @Parameterized.Parameters
    public static Collection<Object[]> getParameters() {
        CoberturaLine line = CoberturaLine.from(1, 2);

        return Arrays.asList(
                new Object[][]{
                        {
                                "With same instance",
                                Boolean.TRUE,
                                line,
                                line
                        },
                        {
                                "With null",
                                Boolean.FALSE,
                                line,
                                null
                        },
                        {
                                "With different type",
                                Boolean.FALSE,
                                line,
                                ""
                        },
                        {
                                "With different number",
                                Boolean.FALSE,
                                line,
                                CoberturaLine.from(2, 2)
                        },
                        {
                                "With different conditions",
                                Boolean.FALSE,
                                CoberturaLine.from(1, 5, 1, 0),
                                CoberturaLine.from(1, 5, 2, 0)
                        },
                        {
                                "With different conditions covered",
                                Boolean.FALSE,
                                CoberturaLine.from(1, 5, 2, 2),
                                CoberturaLine.from(1, 5, 2, 1)
                        },
                        {
                                "With different hits",
                                Boolean.TRUE,
                                line,
                                CoberturaLine.from(1, 5)
                        },
                        {
                                "With same values",
                                Boolean.TRUE,
                                line,
                                CoberturaLine.from(1, 2)
                        },
                        {
                                "With same values and conditions",
                                Boolean.TRUE,
                                CoberturaLine.from(1, 2, 4,3),
                                CoberturaLine.from(1, 2, 4,3)
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
        assertEquals(message, line, compareTo);
        validateHashCodeWhenEqual();
    }

    private void validateHashCodeWhenEqual() {
        assertEquals(message, line.hashCode(), compareTo.hashCode());
    }

    private void assertNotEqual() {
        assertNotSame(message, line, compareTo);

        validateHashCodeWhenNotEqual();
    }

    private void validateHashCodeWhenNotEqual() {
        if (compareTo == null) {
            return;
        }

        assertNotSame(message, line.hashCode(), compareTo.hashCode());
    }
}
