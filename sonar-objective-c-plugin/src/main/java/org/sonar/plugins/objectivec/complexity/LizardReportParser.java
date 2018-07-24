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
package org.sonar.plugins.objectivec.complexity;

import me.raatiniemi.sonarqube.XmlReportParser;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.annotation.Nonnull;
import javax.xml.parsers.DocumentBuilder;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * This class parses xml Reports form the tool Lizard in order to extract this measures: COMPLEXITY, FUNCTIONS
 */
final class LizardReportParser extends XmlReportParser<Set<LizardMeasure>> {
    private static final String MEASURE = "measure";
    private static final String MEASURE_TYPE = "type";
    private static final String MEASURE_ITEM = "item";
    private static final String FILE_MEASURE = "file";
    private static final String NAME = "name";
    private static final String VALUE = "value";
    private static final int CYCLOMATIC_COMPLEXITY_INDEX = 2;
    private static final int FUNCTIONS_INDEX = 3;

    private LizardReportParser(@Nonnull DocumentBuilder documentBuilder) {
        super(documentBuilder);
    }

    @Nonnull
    static LizardReportParser create(@Nonnull DocumentBuilder documentBuilder) {
        return new LizardReportParser(documentBuilder);
    }

    @Nonnull
    @Override
    protected Set<LizardMeasure> parse(@Nonnull Document document) {
        NodeList nodeList = document.getElementsByTagName(MEASURE);
        Set<LizardMeasure> measures = new LinkedHashSet<>();

        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            if (node.getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }

            Element element = (Element) node;

            if (element.getAttribute(MEASURE_TYPE).equalsIgnoreCase(FILE_MEASURE)) {
                NodeList itemList = element.getElementsByTagName(MEASURE_ITEM);
                measures.addAll(addComplexityFileMeasures(itemList));
            }
        }

        return measures;
    }

    @Nonnull
    private Set<LizardMeasure> addComplexityFileMeasures(@Nonnull NodeList itemList) {
        Set<LizardMeasure> measures = new LinkedHashSet<>();

        for (int i = 0; i < itemList.getLength(); i++) {
            Node item = itemList.item(i);
            if (item.getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }

            Element itemElement = (Element) item;
            String fileName = itemElement.getAttribute(NAME);
            NodeList values = itemElement.getElementsByTagName(VALUE);
            int complexity = Integer.parseInt(values.item(CYCLOMATIC_COMPLEXITY_INDEX).getTextContent());
            int numberOfFunctions = Integer.parseInt(values.item(FUNCTIONS_INDEX).getTextContent());

            LizardMeasure measure = LizardMeasure.builder()
                    .setPath(fileName)
                    .setComplexity(complexity)
                    .setNumberOfFunctions(numberOfFunctions)
                    .build();

            measures.add(measure);
        }

        return measures;
    }
}
