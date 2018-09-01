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

import me.raatiniemi.sonar.core.XmlReportParser;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.xml.parsers.DocumentBuilder;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

final class CoberturaXmlReportParser extends XmlReportParser<List<CoberturaPackage>> {
    private static final Logger LOGGER = LoggerFactory.getLogger(CoberturaXmlReportParser.class);

    private static final String PACKAGE = "package";
    private static final String CLASS = "class";
    private static final String LINE = "line";

    private CoberturaXmlReportParser(@Nonnull DocumentBuilder documentBuilder) {
        super(documentBuilder);
    }

    @Nonnull
    static CoberturaXmlReportParser create(@Nonnull DocumentBuilder documentBuilder) {
        return new CoberturaXmlReportParser(documentBuilder);
    }

    @Nonnull
    private static Collection<Element> getPackageElements(@Nonnull Document document) {
        return getElements(document, PACKAGE);
    }

    @Nonnull
    private static Collection<Element> getClassElements(@Nonnull Element element) {
        return getElements(element, CLASS);
    }

    @Nonnull
    private static Collection<Element> getLineElements(@Nonnull Element classElement) {
        return getElements(classElement, LINE);
    }

    @Nonnull
    @Override
    protected List<CoberturaPackage> parse(@Nonnull Document document) {
        return getPackageElements(document)
                .stream()
                .map(this::parsePackage)
                .collect(Collectors.toList());
    }

    @Nonnull
    private CoberturaPackage parsePackage(@Nonnull Element packageElement) {
        String name = packageElement.getAttribute("name");
        List<CoberturaClass> classes = getClassElements(packageElement)
                .stream()
                .map(this::parseClass)
                .collect(Collectors.toList());

        return CoberturaPackage.from(name, classes);
    }

    @Nonnull
    private CoberturaClass parseClass(@Nonnull Element classElement) {
        String filename = classElement.getAttribute("filename");
        List<CoberturaLine> lines = getLineElements(classElement)
                .stream()
                .map(this::parseLine)
                .collect(Collectors.toList());

        return CoberturaClass.from(filename, lines);
    }

    @Nonnull
    private CoberturaLine parseLine(@Nonnull Element lineElement) {
        int number = Integer.parseInt(lineElement.getAttribute("number"));
        int hits = Integer.parseInt(lineElement.getAttribute("hits"));

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

    private boolean isConditionCoverageValueMissing(@Nullable String rawConditionCoverageValue) {
        return rawConditionCoverageValue == null || rawConditionCoverageValue.isEmpty();
    }

    @Nonnull
    private Optional<ConditionCoverage> extractConditionCoverageValue(@Nonnull String rawConditionCoverageValue) {
        String conditionCoverage = StringUtils.substringBetween(rawConditionCoverageValue, "(", ")");
        String[] conditionCoverageValues = StringUtils.split(conditionCoverage, "/");
        if (isConditionCoverageValuesValid(conditionCoverageValues)) {
            return Optional.empty();
        }

        try {
            int conditionsCovered = Integer.parseInt(conditionCoverageValues[0]);
            int conditions = Integer.parseInt(conditionCoverageValues[1]);

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

    private boolean isConditionCoverageValuesValid(@Nullable String[] conditionCoverageValues) {
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
