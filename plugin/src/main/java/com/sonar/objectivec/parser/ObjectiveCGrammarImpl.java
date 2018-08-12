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
package com.sonar.objectivec.parser;

import static com.sonar.sslr.api.GenericTokenType.EOF;
import static com.sonar.sslr.api.GenericTokenType.LITERAL;
import static com.sonar.sslr.impl.matcher.GrammarFunctions.Standard.o2n;

import com.sonar.objectivec.api.ObjectiveCGrammar;

public class ObjectiveCGrammarImpl extends ObjectiveCGrammar {

    public ObjectiveCGrammarImpl() {

         program.is(o2n(LITERAL), EOF);

    }

}
