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
import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;

@RunWith(Parameterized.class)
public class CoberturaPackageEqualHashCodeTest {
    private final String message;
    private final Boolean expected;
    private final CoberturaPackage actual;
    private final Object compareTo;

    public CoberturaPackageEqualHashCodeTest(
            String message,
            Boolean expected,
            CoberturaPackage actual,
            Object compareTo
    ) {
        this.message = message;
        this.expected = expected;
        this.actual = actual;
        this.compareTo = compareTo;
    }

    @Parameterized.Parameters
    public static Collection<Object[]> getParameters() {
        Line line = Line.from(1, false, 2);
        CoberturaClass coberturaClass = CoberturaClass.from("RASqlite/RASqlite.m", Collections.singletonList(line));
        CoberturaPackage coberturaPackage = CoberturaPackage.from("RASqlite", Collections.singletonList(coberturaClass));

        return Arrays.asList(
                new Object[][]{
                        {
                                "With same instance",
                                Boolean.TRUE,
                                coberturaPackage,
                                coberturaPackage
                        },
                        {
                                "With null",
                                Boolean.FALSE,
                                coberturaPackage,
                                null
                        },
                        {
                                "With different type",
                                Boolean.FALSE,
                                coberturaPackage,
                                ""
                        },
                        {
                                "With different name",
                                Boolean.FALSE,
                                coberturaPackage,
                                CoberturaPackage.from("RAToast", Collections.singletonList(coberturaClass))
                        },
                        {
                                "With different class",
                                Boolean.FALSE,
                                coberturaPackage,
                                CoberturaPackage.from(
                                        "RASqlite",
                                        Collections.singletonList(
                                                CoberturaClass.from(
                                                        "RASqlite/NSError+RASqlite.m",
                                                        Collections.singletonList(line)
                                                )
                                        )
                                )
                        },
                        {
                                "With same values",
                                Boolean.TRUE,
                                coberturaPackage,
                                CoberturaPackage.from(
                                        "RASqlite",
                                        Collections.singletonList(coberturaClass)
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
        assertEquals(message, actual, compareTo);
        validateHashCodeWhenEqual();
    }

    private void validateHashCodeWhenEqual() {
        assertEquals(message, actual.hashCode(), compareTo.hashCode());
    }

    private void assertNotEqual() {
        assertNotSame(message, actual, compareTo);

        validateHashCodeWhenNotEqual();
    }

    private void validateHashCodeWhenNotEqual() {
        if (compareTo == null) {
            return;
        }

        assertNotSame(message, actual.hashCode(), compareTo.hashCode());
    }
}
