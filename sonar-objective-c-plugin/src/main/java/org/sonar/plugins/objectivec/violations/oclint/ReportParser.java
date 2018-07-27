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

import me.raatiniemi.sonarqube.XmlReportParser;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.annotation.Nonnull;
import javax.xml.parsers.DocumentBuilder;
import java.util.ArrayList;
import java.util.List;

final class ReportParser extends XmlReportParser<List<Violation>> {
    private static final String VIOLATION = "violation";
    private static final String PATH = "path";
    private static final String START_LINE = "startline";
    private static final String RULE = "rule";
    private static final String MESSAGE = "message";

    private ReportParser(@Nonnull DocumentBuilder documentBuilder) {
        super(documentBuilder);
    }

    @Nonnull
    static ReportParser create(@Nonnull DocumentBuilder documentBuilder) {
        return new ReportParser(documentBuilder);
    }

    @Nonnull
    private static NodeList getViolationElements(@Nonnull Document document) {
        return document.getElementsByTagName(VIOLATION);
    }

    @Nonnull
    @Override
    protected List<Violation> parse(@Nonnull final Document document) {
        List<Violation> violations = new ArrayList<>();

        NodeList violationElements = getViolationElements(document);
        for (int i = 0; i < violationElements.getLength(); i++) {
            Node node = violationElements.item(i);
            if (isNotElement(node)) {
                continue;
            }

            Element element = (Element) node;
            violations.add(buildViolation(element));
        }

        return violations;
    }

    private boolean isNotElement(@Nonnull Node item) {
        return item.getNodeType() != Node.ELEMENT_NODE;
    }

    @Nonnull
    private Violation buildViolation(@Nonnull Element element) {
        return Violation.builder()
                .setPath(element.getAttribute(PATH))
                .setStartLine(Integer.parseInt(element.getAttribute(START_LINE)))
                .setRule(element.getAttribute(RULE))
                .setMessage(element.getAttribute(MESSAGE))
                .build();
    }
}
