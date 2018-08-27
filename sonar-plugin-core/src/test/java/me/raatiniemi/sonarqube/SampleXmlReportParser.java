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

package me.raatiniemi.sonarqube;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.annotation.Nonnull;
import javax.xml.parsers.DocumentBuilder;
import java.util.Optional;

class SampleXmlReportParser extends XmlReportParser<SampleReport> {
    private SampleXmlReportParser(@Nonnull DocumentBuilder documentBuilder) {
        super(documentBuilder);
    }

    static SampleXmlReportParser create(@Nonnull DocumentBuilder documentBuilder) {
        return new SampleXmlReportParser(documentBuilder);
    }

    @Nonnull
    @Override
    protected SampleReport parse(@Nonnull Document document) {
        String filename = "";
        String value = "";

        for (Element collections : getElements(document, "modules")) {
            Optional<Element> file = getElements(collections, "file")
                    .stream()
                    .findFirst();

            if (!file.isPresent()) {
                break;
            }

            Element element = file.get();
            filename = element.getAttribute("filename");
            value = element.getAttribute("value");
        }

        return SampleReport.from(filename, value);
    }
}
