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

import me.raatiniemi.sonarqube.XmlReportParser;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.annotation.Nonnull;
import javax.xml.parsers.DocumentBuilder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

final class ReportParser extends XmlReportParser<List<CoberturaPackage>> {
    private static final Logger LOGGER = LoggerFactory.getLogger(ReportParser.class);

    private ReportParser(@Nonnull DocumentBuilder documentBuilder) {
        super(documentBuilder);
    }

    @Nonnull
    static ReportParser create(@Nonnull DocumentBuilder documentBuilder) {
        return new ReportParser(documentBuilder);
    }

    @Nonnull
    @Override
    protected List<CoberturaPackage> parse(@Nonnull Document document) {
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

        List<CoberturaLine> lines = new ArrayList<>();
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
    private CoberturaLine parseLine(@Nonnull Element lineElement) {
        int number = Integer.valueOf(lineElement.getAttribute("number"));
        int hits = Integer.valueOf(lineElement.getAttribute("hits"));

        Optional<ConditionCoverage> conditionCoverageValue = parseConditionCoverage(lineElement);
        if (conditionCoverageValue.isPresent()) {
            ConditionCoverage conditionCoverage = conditionCoverageValue.get();
            return CoberturaLine.from(number, hits, conditionCoverage.conditions, conditionCoverage.conditionsCovered);
        }

        return CoberturaLine.from(number, hits);
    }

    @Nonnull
    private Optional<ConditionCoverage> parseConditionCoverage(@Nonnull Element lineElement) {
        if (isNotBranch(lineElement)) {
            return Optional.empty();
        }

        return parseConditionCoverageValues(lineElement);

    }

    private boolean isNotBranch(@Nonnull Element lineElement) {
        return !lineElement.getAttribute("branch").equalsIgnoreCase("true");
    }

    @Nonnull
    private Optional<ConditionCoverage> parseConditionCoverageValues(@Nonnull Element lineElement) {
        String rawConditionCoverageValue = lineElement.getAttribute("condition-coverage");
        if (isConditionCoverageValueMissing(rawConditionCoverageValue)) {
            return Optional.empty();
        }

        return extractConditionCoverageValue(rawConditionCoverageValue);
    }

    private boolean isConditionCoverageValueMissing(String rawConditionCoverageValue) {
        return rawConditionCoverageValue == null || rawConditionCoverageValue.isEmpty();
    }

    @Nonnull
    private Optional<ConditionCoverage> extractConditionCoverageValue(String rawConditionCoverageValue) {
        String conditionCoverage = StringUtils.substringBetween(rawConditionCoverageValue, "(", ")");
        String[] conditionCoverageValues = StringUtils.split(conditionCoverage, "/");
        if (isConditionCoverageValuesValid(conditionCoverageValues)) {
            return Optional.empty();
        }

        try {
            int conditionsCovered = Integer.valueOf(conditionCoverageValues[0]);
            int conditions = Integer.valueOf(conditionCoverageValues[1]);

            if (0 == conditions) {
                LOGGER.warn("Condition coverage exists but number conditions are zero");
                return Optional.empty();
            }

            if (conditions < conditionsCovered) {
                LOGGER.warn("Number of covered conditions are higher than the number of conditions");
                return Optional.empty();
            }

            return Optional.of(ConditionCoverage.from(conditions, conditionsCovered));
        } catch (NumberFormatException e) {
            LOGGER.error("Unable to parse condition coverage from: {}", rawConditionCoverageValue, e);
            return Optional.empty();
        }
    }

    private boolean isConditionCoverageValuesValid(String[] conditionCoverageValues) {
        return conditionCoverageValues == null || 2 != conditionCoverageValues.length;
    }

    private static class ConditionCoverage {
        private final int conditions;
        private final int conditionsCovered;

        private ConditionCoverage(int conditions, int conditionsCovered) {
            this.conditions = conditions;
            this.conditionsCovered = conditionsCovered;
        }

        static ConditionCoverage from(int conditions, int conditionsCovered) {
            return new ConditionCoverage(conditions, conditionsCovered);
        }
    }
}
