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
package me.raatiniemi.sonar.core.xml;

import me.raatiniemi.sonar.core.ReportParser;
import org.sonar.api.utils.log.Logger;
import org.sonar.api.utils.log.Loggers;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.annotation.Nonnull;
import javax.xml.parsers.DocumentBuilder;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public abstract class XmlReportParser<T> implements ReportParser<T> {
    private static final Logger LOGGER = Loggers.get(XmlReportParser.class);

    private final DocumentBuilder documentBuilder;

    protected XmlReportParser(@Nonnull DocumentBuilder documentBuilder) {
        this.documentBuilder = documentBuilder;
    }

    @Nonnull
    private static Collection<Element> parseElementsFromNodeList(@Nonnull NodeList nodeList) {
        List<Element> elements = new ArrayList<>();

        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            if (isNotElement(node)) {
                continue;
            }

            Element element = (Element) node;
            elements.add(element);
        }

        return elements;
    }

    private static boolean isNotElement(@Nonnull Node node) {
        return node.getNodeType() != Node.ELEMENT_NODE;
    }

    @Nonnull
    public final Optional<T> parse(@Nonnull File reportFile) {
        if (!reportFile.exists()) {
            LOGGER.warn("No XML report exist at path: {}", reportFile);
            return Optional.empty();
        }

        try {
            Document document = documentBuilder.parse(reportFile);
            T report = parse(document);

            return Optional.of(report);
        } catch (SAXException | IOException e) {
            LOGGER.error("Unable to process XML file named: {}", reportFile, e);
            return Optional.empty();
        }
    }

    @Nonnull
    protected abstract T parse(@Nonnull Document document);

    @Nonnull
    protected static Collection<Element> getElements(@Nonnull Document document, @Nonnull String tagName) {
        NodeList nodeList = document.getElementsByTagName(tagName);

        return parseElementsFromNodeList(nodeList);
    }

    @Nonnull
    protected static Collection<Element> getElements(@Nonnull Element element, @Nonnull String tagName) {
        NodeList nodeList = element.getElementsByTagName(tagName);

        return parseElementsFromNodeList(nodeList);
    }
}
