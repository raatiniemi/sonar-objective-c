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
package com.sonar.objectivec.lexer;

import static com.sonar.sslr.api.GenericTokenType.LITERAL;
import static com.sonar.sslr.impl.channel.RegexpChannelBuilder.commentRegexp;
import static com.sonar.sslr.impl.channel.RegexpChannelBuilder.regexp;

import com.sonar.objectivec.ObjectiveCConfiguration;

import com.sonar.sslr.impl.Lexer;
import com.sonar.sslr.impl.channel.BlackHoleChannel;

public class ObjectiveCLexer {

    private ObjectiveCLexer() {
    }

    public static Lexer create() {
        return create(new ObjectiveCConfiguration());
    }

    public static Lexer create(ObjectiveCConfiguration conf) {
        return Lexer.builder()
                .withCharset(conf.getCharset())

                .withFailIfNoChannelToConsumeOneCharacter(false)

                // Comments
                .withChannel(commentRegexp("//[^\\n\\r]*+"))
                .withChannel(commentRegexp("/\\*[\\s\\S]*?\\*/"))

                // All other tokens
                .withChannel(regexp(LITERAL, "[^\r\n\\s/]+"))

                .withChannel(new BlackHoleChannel("[\\s]"))

                .build();
    }

}
