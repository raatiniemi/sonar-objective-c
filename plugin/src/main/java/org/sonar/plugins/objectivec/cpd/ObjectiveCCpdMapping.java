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
package org.sonar.plugins.objectivec.cpd;

import java.nio.charset.Charset;

import net.sourceforge.pmd.cpd.Tokenizer;

import org.sonar.api.batch.AbstractCpdMapping;
import org.sonar.api.batch.fs.FileSystem;
import org.sonar.api.resources.Language;
import org.sonar.plugins.objectivec.core.ObjectiveC;

public class ObjectiveCCpdMapping extends AbstractCpdMapping {

    private final ObjectiveC language;
    private final Charset charset;

    public ObjectiveCCpdMapping(ObjectiveC language, FileSystem fs) {
        this.language = language;
        this.charset = fs.encoding();
    }

    public Tokenizer getTokenizer() {
        return new ObjectiveCTokenizer(charset);
    }

    public Language getLanguage() {
        return language;
    }

}
