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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.annotation.Nonnull;
import javax.xml.parsers.DocumentBuilder;
import java.io.File;
import java.io.IOException;
import java.util.Optional;

public abstract class XmlReportParser<T> implements ReportParser<T> {
    private static final Logger LOGGER = LoggerFactory.getLogger(XmlReportParser.class);

    private final DocumentBuilder documentBuilder;

    protected XmlReportParser(@Nonnull DocumentBuilder documentBuilder) {
        this.documentBuilder = documentBuilder;
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
}
