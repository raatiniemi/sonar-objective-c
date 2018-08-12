/**
 * backelite-sonar-objective-c-plugin - Enables analysis of Objective-C projects into SonarQube.
 * Copyright © 2012 OCTO Technology, Backelite (${email})
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
package com.sonar.objectivec.checks;

import java.util.List;

import com.google.common.collect.ImmutableList;

public final class CheckList {

    public static final String REPOSITORY_KEY = "objectivec";

    public static final String SONAR_WAY_PROFILE = "Sonar way";

    private CheckList() {
    }

    public static List<Class> getChecks() {
        return ImmutableList.of(

                );
    }

}
