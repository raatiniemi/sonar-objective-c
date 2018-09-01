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

import me.raatiniemi.sonar.core.XmlReportParser;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.annotation.Nonnull;
import javax.xml.parsers.DocumentBuilder;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

final class OCLintXmlReportParser extends XmlReportParser<List<Violation>> {
    private static final String VIOLATION = "violation";
    private static final String PATH = "path";
    private static final String START_LINE = "startline";
    private static final String RULE = "rule";
    private static final String MESSAGE = "message";

    private static final Function<Element, Violation> buildViolation = element -> Violation.builder()
            .setPath(parsePath(element))
            .setStartLine(parseStartLine(element))
            .setRule(parseRule(element))
            .setMessage(parseMessage(element))
            .build();

    @Nonnull
    private static String parsePath(@Nonnull Element element) {
        return element.getAttribute(PATH);
    }

    private static int parseStartLine(@Nonnull Element element) {
        return Integer.parseInt(element.getAttribute(START_LINE));
    }

    @Nonnull
    private static String parseRule(@Nonnull Element element) {
        return element.getAttribute(RULE);
    }

    @Nonnull
    private static String parseMessage(@Nonnull Element element) {
        return element.getAttribute(MESSAGE);
    }

    private OCLintXmlReportParser(@Nonnull DocumentBuilder documentBuilder) {
        super(documentBuilder);
    }

    @Nonnull
    static OCLintXmlReportParser create(@Nonnull DocumentBuilder documentBuilder) {
        return new OCLintXmlReportParser(documentBuilder);
    }

    @Nonnull
    private static Collection<Element> getViolationElements(@Nonnull Document document) {
        return getElements(document, VIOLATION);
    }

    @Nonnull
    @Override
    protected List<Violation> parse(@Nonnull final Document document) {
        return getViolationElements(document)
                .stream()
                .map(buildViolation)
                .collect(Collectors.toList());
    }
}
