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
package me.raatiniemi.sonar.cobertura;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;

@RunWith(Parameterized.class)
public class CoberturaClassEqualHashCodeTest {
    private final String message;
    private final Boolean expected;
    private final CoberturaClass clazz;
    private final Object compareTo;

    public CoberturaClassEqualHashCodeTest(
            String message,
            Boolean expected,
            CoberturaClass clazz,
            Object compareTo
    ) {
        this.message = message;
        this.expected = expected;
        this.clazz = clazz;
        this.compareTo = compareTo;
    }

    @Parameterized.Parameters
    public static Collection<Object[]> getParameters() {
        CoberturaLine line = CoberturaLine.from(1, 2);
        CoberturaClass clazz = CoberturaClass.from("RASqlite/RASqlite.m", Collections.singletonList(line));

        return Arrays.asList(
                new Object[][]{
                        {
                                "With same instance",
                                Boolean.TRUE,
                                clazz,
                                clazz
                        },
                        {
                                "With null",
                                Boolean.FALSE,
                                clazz,
                                null
                        },
                        {
                                "With different type",
                                Boolean.FALSE,
                                clazz,
                                ""
                        },
                        {
                                "With different filename",
                                Boolean.FALSE,
                                clazz,
                                CoberturaClass.from("RASqlite/NSError+RASqlite.m", Collections.singletonList(line))
                        },
                        {
                                "With different line",
                                Boolean.FALSE,
                                clazz,
                                CoberturaClass.from(
                                        "RASqlite/RASqlite.m",
                                        Collections.singletonList(CoberturaLine.from(2, 4))
                                )
                        },
                        {
                                "With same values",
                                Boolean.TRUE,
                                clazz,
                                CoberturaClass.from(
                                        "RASqlite/RASqlite.m",
                                        Collections.singletonList(line)
                                )
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
        assertEquals(message, clazz, compareTo);
        validateHashCodeWhenEqual();
    }

    private void validateHashCodeWhenEqual() {
        assertEquals(message, clazz.hashCode(), compareTo.hashCode());
    }

    private void assertNotEqual() {
        assertNotSame(message, clazz, compareTo);

        validateHashCodeWhenNotEqual();
    }

    private void validateHashCodeWhenNotEqual() {
        if (compareTo == null) {
            return;
        }

        assertNotSame(message, clazz.hashCode(), compareTo.hashCode());
    }
}
