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

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

import static org.junit.Assert.*;

/**
 * @author Andres Gil Herrera
 * @since 03/06/15.
 */
public class LizardReportParserTest {
    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    private LizardReportParser reportParser;

    private File correctFile;
    private File incorrectFile;

    @Before
    public void setup() throws IOException {
        reportParser = new LizardReportParser();

        correctFile = createCorrectFile();
        incorrectFile = createIncorrectFile();
    }

    private File createCorrectFile() throws IOException {
        File xmlFile = folder.newFile("correctFile.xml");
        BufferedWriter out = new BufferedWriter(new FileWriter(xmlFile));
        //header
        out.write("<?xml version=\"1.0\" ?>");
        out.write("<?xml-stylesheet type=\"text/xsl\" href=\"https://raw.github.com/terryyin/lizard/master/lizard.xsl\"?>");
        //root object and measure
        out.write("<cppncss><measure type=\"Function\"><labels><label>Nr.</label><label>NCSS</label><label>CCN</label></labels>");
        //items for function
        out.write("<item name=\"viewDidLoad(...) at App/Controller/Accelerate/AccelerationViewController.m:105\">");
        out.write("<value>2</value><value>15</value><value>1</value></item>");
        out.write("<item name=\"viewWillAppear:(...) at App/Controller/Accelerate/AccelerationViewController.m:130\">");
        out.write("<value>3</value><value>20</value><value>5</value></item>");
        //average and close funciton measure
        out.write("<average lable=\"NCSS\" value=\"17\"/><average lable=\"CCN\" value=\"3\"/><average lable=\"NCSS\" value=\"17\"/>");
        out.write("<average lable=\"CCN\" value=\"3\"/><average lable=\"NCSS\" value=\"17\"/><average lable=\"CCN\" value=\"3\"/>");
        out.write("<average lable=\"NCSS\" value=\"17\"/><average lable=\"CCN\" value=\"3\"/></measure>");
        //open file measure and add the labels
        out.write("<measure type=\"File\"><labels><label>Nr.</label><label>NCSS</label><label>CCN</label><label>Functions</label></labels>");
        //items for file
        out.write("<item name=\"App/Controller/Accelerate/AccelerationViewController.h\">");
        out.write("<value>1</value><value>2</value><value>0</value><value>0</value></item>");
        out.write("<item name=\"App/Controller/Accelerate/AccelerationViewController.m\">");
        out.write("<value>2</value><value>868</value><value>6</value><value>2</value></item>");
        //add averages
        out.write("<average lable=\"NCSS\" value=\"435\"/><average lable=\"CCN\" value=\"70\"/><average lable=\"Functions\" value=\"21\"/>");
        //add sum
        out.write("<sum lable=\"NCSS\" value=\"870\"/><sum lable=\"CCN\" value=\"141\"/><sum lable=\"Functions\" value=\"42\"/>");
        //close measures and root object
        out.write("</measure></cppncss>");

        out.close();

        return xmlFile;
    }

    private File createIncorrectFile() throws IOException {
        File xmlFile = folder.newFile("incorrectFile.xml");
        BufferedWriter out = new BufferedWriter(new FileWriter(xmlFile));
        //header
        out.write("<?xml version=\"1.0\" ?>");
        out.write("<?xml-stylesheet type=\"text/xsl\" href=\"https://raw.github.com/terryyin/lizard/master/lizard.xsl\"?>");
        //root object and measure
        out.write("<cppncss><measure type=\"Function\"><labels><label>Nr.</label><label>NCSS</label><label>CCN</label></labels>");
        //items for function
        out.write("<item name=\"viewDidLoad(...) at App/Controller/Accelerate/AccelerationViewController.m:105\">");
        out.write("<value>2</value><value>15</value><value>1</value></item>");
        out.write("<item name=\"viewWillAppear:(...) at App/Controller/Accelerate/AccelerationViewController.m:130\">");
        out.write("<value>3</value><value>20</value><value>5</value></item>");
        //average and close funciton measure
        out.write("<average lable=\"NCSS\" value=\"17\"/><average lable=\"CCN\" value=\"3\"/><average lable=\"NCSS\" value=\"17\"/>");
        out.write("<average lable=\"CCN\" value=\"3\"/><average lable=\"NCSS\" value=\"17\"/><average lable=\"CCN\" value=\"3\"/>");
        out.write("<average lable=\"NCSS\" value=\"17\"/><average lable=\"CCN\" value=\"3\"/></measure>");
        //open file measure and add the labels
        out.write("<measure type=\"File\"><labels><label>Nr.</label><label>NCSS</label><label>CCN</label><label>Functions</label></labels>");
        //items for file 3th value tag has no closing tag
        out.write("<item name=\"App/Controller/Accelerate/AccelerationViewController.h\">");
        out.write("<value>1</value><value>2</value><value>0<value>0</value></item>");
        out.write("<item name=\"App/Controller/Accelerate/AccelerationViewController.m\">");
        out.write("<value>2</value><value>868</value><value>6</value><value>2</value></item>");
        //add averages
        out.write("<average lable=\"NCSS\" value=\"435\"/><average lable=\"CCN\" value=\"70\"/><average lable=\"Functions\" value=\"21\"/>");
        //add sum
        out.write("<sum lable=\"NCSS\" value=\"870\"/><sum lable=\"CCN\" value=\"141\"/><sum lable=\"Functions\" value=\"42\"/>");
        //close measures and root object no close tag for measure
        out.write("</cppncss>");

        out.close();

        return xmlFile;
    }

    @Test
    public void parseReportShouldReturnMapWhenXMLFileIsCorrect() {
        Set<LizardMeasure> expected = new LinkedHashSet<>();
        expected.add(LizardMeasure.builder()
                .setPath("App/Controller/Accelerate/AccelerationViewController.h")
                .setNumberOfFunctions(0)
                .setComplexity(0)
                .build());
        expected.add(LizardMeasure.builder()
                .setPath("App/Controller/Accelerate/AccelerationViewController.m")
                .setNumberOfFunctions(2)
                .setComplexity(6)
                .build());

        Collection<LizardMeasure> actual = reportParser.parseReport(correctFile);

        assertFalse(actual.isEmpty());
        assertEquals(expected, actual);
    }

    @Test
    public void parseReportShouldReturnNullWhenXMLFileIsIncorrect() {
        Collection<LizardMeasure> actual = reportParser.parseReport(incorrectFile);

        assertTrue(actual.isEmpty());
    }
}
