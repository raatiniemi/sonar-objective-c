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
package org.sonar.plugins.objectivec.core;

import com.google.common.collect.Lists;
import org.apache.commons.lang.StringUtils;
import org.sonar.api.config.Configuration;
import org.sonar.api.resources.AbstractLanguage;
import org.sonar.plugins.objectivec.ObjectiveCPlugin;

import javax.annotation.Nonnull;
import java.util.List;

public class ObjectiveC extends AbstractLanguage {
    public static final String KEY = "objc";

    private Configuration configuration;

    public ObjectiveC(@Nonnull Configuration configuration) {
        super(KEY, "Objective-C");

        this.configuration = configuration;
    }

    public String[] getFileSuffixes() {
        String[] suffixes = filterEmptyStrings(configuration.getStringArray(ObjectiveCPlugin.FILE_SUFFIXES_KEY));
        if (suffixes == null || suffixes.length == 0) {
            suffixes = StringUtils.split(ObjectiveCPlugin.FILE_SUFFIXES_DEFVALUE, ",");
        }
        return suffixes;
    }

    private String[] filterEmptyStrings(String[] stringArray) {
        List<String> nonEmptyStrings = Lists.newArrayList();
        for (String string : stringArray) {
          if (StringUtils.isNotBlank(string.trim())) {
            nonEmptyStrings.add(string.trim());
          }
        }
        return nonEmptyStrings.toArray(new String[nonEmptyStrings.size()]);
      }
}