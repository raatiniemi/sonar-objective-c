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
package org.sonar.plugins.objectivec.coverage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import java.util.Collections;
import java.util.List;

final class ReportParser {
    private static final Logger LOGGER = LoggerFactory.getLogger(ReportParser.class);

    private final DocumentBuilder documentBuilder;

    private ReportParser(@Nonnull DocumentBuilder documentBuilder) {
        this.documentBuilder = documentBuilder;
    }

    @Nonnull
    static ReportParser create(@Nonnull DocumentBuilder documentBuilder) {
        return new ReportParser(documentBuilder);
    }

    @Nonnull
    List<CoberturaPackage> parse(@Nonnull File xmlReportFile) {
        if (!xmlReportFile.exists()) {
            LOGGER.warn("Coverage report do not exist at path: {}", xmlReportFile.getPath());
            return Collections.emptyList();
        }

        try {
            Document document = documentBuilder.parse(xmlReportFile);
            return parseCoberturaReport(document);
        } catch (IOException | SAXException e) {
            LOGGER.error("Unable to process coverage report: {}", xmlReportFile, e);
        }

        return Collections.emptyList();
    }

    @Nonnull
    private List<CoberturaPackage> parseCoberturaReport(@Nonnull Document document) {
        NodeList elements = document.getElementsByTagName("package");
        if (elements.getLength() == 0) {
            return Collections.emptyList();
        }

        List<CoberturaPackage> packages = new ArrayList<>();
        for (int i = 0; i < elements.getLength(); i++) {
            Node node = elements.item(i);
            if (node.getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }

            Element element = (Element) node;
            packages.add(parsePackage(element));
        }

        return packages;
    }

    @Nonnull
    private CoberturaPackage parsePackage(@Nonnull Element packageElement) {
        String name = packageElement.getAttribute("name");

        List<CoberturaClass> classes = new ArrayList<>();
        NodeList classElements = packageElement.getElementsByTagName("class");
        for (int i = 0; i < classElements.getLength(); i++) {
            Node node = classElements.item(i);
            if (node.getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }

            Element classElement = (Element) node;
            classes.add(parseClass(classElement));
        }

        return CoberturaPackage.from(name, classes);
    }

    @Nonnull
    private CoberturaClass parseClass(@Nonnull Element classElement) {
        String filename = classElement.getAttribute("filename");

        List<Line> lines = new ArrayList<>();
        NodeList lineElements = classElement.getElementsByTagName("line");
        for (int i = 0; i < lineElements.getLength(); i++) {
            Node node = lineElements.item(i);
            if (node.getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }

            Element lineElement = (Element) node;
            lines.add(parseLine(lineElement));
        }

        return CoberturaClass.from(filename, lines);
    }

    @Nonnull
    private Line parseLine(@Nonnull Element lineElement) {
        int number = Integer.valueOf(lineElement.getAttribute("number"));
        boolean branch = lineElement.getAttribute("branch").equalsIgnoreCase("true");
        int hits = Integer.valueOf(lineElement.getAttribute("hits"));

        return Line.from(number, branch, hits);
    }
}
