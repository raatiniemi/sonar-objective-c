/*
 * Copyright © 2012 OCTO Technology, Backelite (${email})
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
package me.raatiniemi.sonar.lizard;

import me.raatiniemi.sonar.core.xml.XmlReportParser;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.annotation.Nonnull;
import javax.xml.parsers.DocumentBuilder;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * This class parses xml Reports form the tool Lizard in order to extract this measures: COMPLEXITY, FUNCTIONS
 */
final class LizardXmlReportParser extends XmlReportParser<Set<LizardMeasure>> {
    private static final String MEASURE = "measure";
    private static final String MEASURE_TYPE = "type";
    private static final String MEASURE_ITEM = "item";
    private static final String FILE_MEASURE = "file";
    private static final String NAME = "name";
    private static final String VALUE = "value";
    private static final int CYCLOMATIC_COMPLEXITY_INDEX = 2;
    private static final int FUNCTIONS_INDEX = 3;

    private LizardXmlReportParser(@Nonnull DocumentBuilder documentBuilder) {
        super(documentBuilder);
    }

    @Nonnull
    static LizardXmlReportParser create(@Nonnull DocumentBuilder documentBuilder) {
        return new LizardXmlReportParser(documentBuilder);
    }

    @Nonnull
    private static Collection<Element> getMeasureElements(@Nonnull Document document) {
        return getElements(document, MEASURE);
    }

    private static boolean isNotFileMeasure(@Nonnull Element measureElement) {
        return !measureElement.getAttribute(MEASURE_TYPE).equalsIgnoreCase(FILE_MEASURE);
    }

    @Nonnull
    private Collection<Element> getMeasureItemElements(@Nonnull Element measureElement) {
        return getElements(measureElement, MEASURE_ITEM);
    }

    @Nonnull
    @Override
    protected Set<LizardMeasure> parse(@Nonnull Document document) {
        Set<LizardMeasure> measures = new LinkedHashSet<>();

        for (Element measureElement : getMeasureElements(document)) {
            if (isNotFileMeasure(measureElement)) {
                continue;
            }

            for (Element measureItemElement : getMeasureItemElements(measureElement)) {
                measures.add(buildLizardMeasure(measureItemElement));
            }
        }

        return measures;
    }

    @Nonnull
    private LizardMeasure buildLizardMeasure(@Nonnull Element element) {
        String fileName = element.getAttribute(NAME);
        NodeList values = element.getElementsByTagName(VALUE);

        int complexity = Integer.parseInt(values.item(CYCLOMATIC_COMPLEXITY_INDEX).getTextContent());
        int numberOfFunctions = Integer.parseInt(values.item(FUNCTIONS_INDEX).getTextContent());

        return LizardMeasure.builder()
                .setPath(fileName)
                .setComplexity(complexity)
                .setNumberOfFunctions(numberOfFunctions)
                .build();
    }
}
