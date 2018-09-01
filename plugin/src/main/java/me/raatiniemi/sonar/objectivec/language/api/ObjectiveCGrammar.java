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
package me.raatiniemi.sonar.objectivec.language.api;

import com.sonar.sslr.api.Grammar;
import com.sonar.sslr.api.Rule;

public class ObjectiveCGrammar extends Grammar {

    public Rule identifierName;

    // A.1 Lexical

    public Rule literal;
    public Rule nullLiteral;
    public Rule booleanLiteral;
    public Rule stringLiteral;

    public Rule program;

    public Rule sourceElements;
    public Rule sourceElement;

    @Override
    public Rule getRootRule() {
        return program;
    }

}
