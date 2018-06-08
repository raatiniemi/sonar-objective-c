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

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.junit.Assert.assertEquals;

@RunWith(JUnit4.class)
public class OCLintRuleSeverityTest {
    @Test
    public void valueOfInt_withInfo() {
        OCLintRuleSeverity severity = OCLintRuleSeverity.valueOfInt(0);

        assertEquals(OCLintRuleSeverity.INFO, severity);
    }

    @Test
    public void valueOfInt_withMinor() {
        OCLintRuleSeverity severity = OCLintRuleSeverity.valueOfInt(1);

        assertEquals(OCLintRuleSeverity.MINOR, severity);
    }

    @Test
    public void valueOfInt_withMajor() {
        OCLintRuleSeverity severity = OCLintRuleSeverity.valueOfInt(2);

        assertEquals(OCLintRuleSeverity.MAJOR, severity);
    }

    @Test
    public void valueOfInt_withCritical() {
        OCLintRuleSeverity severity = OCLintRuleSeverity.valueOfInt(3);

        assertEquals(OCLintRuleSeverity.CRITICAL, severity);
    }

    @Test
    public void valueOfInt_withBlocker() {
        OCLintRuleSeverity severity = OCLintRuleSeverity.valueOfInt(4);

        assertEquals(OCLintRuleSeverity.BLOCKER, severity);
    }
}
