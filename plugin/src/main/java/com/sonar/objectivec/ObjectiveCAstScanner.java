/**
 * backelite-sonar-objective-c-plugin - Enables analysis of Objective-C projects into SonarQube.
 * Copyright © 2012 OCTO Technology, Backelite (${email})
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
package com.sonar.objectivec;

import com.sonar.objectivec.api.ObjectiveCGrammar;
import com.sonar.objectivec.api.ObjectiveCMetric;
import com.sonar.objectivec.parser.ObjectiveCParser;
import com.sonar.sslr.impl.Parser;
import org.sonar.squidbridge.AstScanner;
import org.sonar.squidbridge.CommentAnalyser;
import org.sonar.squidbridge.SquidAstVisitorContextImpl;
import org.sonar.squidbridge.api.SourceCode;
import org.sonar.squidbridge.api.SourceFile;
import org.sonar.squidbridge.api.SourceProject;
import org.sonar.squidbridge.indexer.QueryByType;
import org.sonar.squidbridge.metrics.CommentsVisitor;
import org.sonar.squidbridge.metrics.LinesOfCodeVisitor;
import org.sonar.squidbridge.metrics.LinesVisitor;

import javax.annotation.Nonnull;
import java.io.File;
import java.nio.charset.Charset;
import java.util.Collection;

public class ObjectiveCAstScanner {

    private ObjectiveCAstScanner() {
    }

    /**
     * Helper method for testing checks without having to deploy them on a Sonar instance.
     */
    public static SourceFile scanSingleFile(File file) {
        if (!file.isFile()) {
            throw new IllegalArgumentException("File '" + file + "' not found.");
        }
        AstScanner<ObjectiveCGrammar> scanner = create(ObjectiveCConfiguration.create(Charset.defaultCharset()));
        scanner.scanFile(file);
        Collection<SourceCode> sources = scanner.getIndex().search(new QueryByType(SourceFile.class));
        if (sources.size() != 1) {
            throw new IllegalStateException("Only one SourceFile was expected whereas " + sources.size() + " has been returned.");
        }
        return (SourceFile) sources.iterator().next();
    }

    public static AstScanner<ObjectiveCGrammar> create(ObjectiveCConfiguration conf) {
        final SquidAstVisitorContextImpl<ObjectiveCGrammar> context = new SquidAstVisitorContextImpl<>(new SourceProject("Objective-C Project"));
        final Parser<ObjectiveCGrammar> parser = ObjectiveCParser.create(conf);

        AstScanner.Builder<ObjectiveCGrammar> builder = AstScanner.builder(context).setBaseParser(parser);

        /* Metrics */
        builder.withMetrics(ObjectiveCMetric.values());

        /* Comments */
        builder.setCommentAnalyser(
                new CommentAnalyser() {
                    @Override
                    public boolean isBlank(@Nonnull String line) {
                        for (int i = 0; i < line.length(); i++) {
                            if (Character.isLetterOrDigit(line.charAt(i))) {
                                return false;
                            }
                        }
                        return true;
                    }

                    @Override
                    public String getContents(@Nonnull String comment) {
                        return comment.startsWith("//") ? comment.substring(2) : comment.substring(2, comment.length() - 2);
                    }
                });

        /* Files */
      builder.setFilesMetric(ObjectiveCMetric.FILES);

        /* Metrics */
	builder.withSquidAstVisitor(new LinesVisitor<>(ObjectiveCMetric.LINES));
	builder.withSquidAstVisitor(new LinesOfCodeVisitor<>(ObjectiveCMetric.LINES_OF_CODE));
      	builder.withSquidAstVisitor(CommentsVisitor.<ObjectiveCGrammar> builder().withCommentMetric(ObjectiveCMetric.COMMENT_LINES)
      			.withNoSonar(true)
      			.withIgnoreHeaderComment(conf.getIgnoreHeaderComments())
      			.build());

        return builder.build();
    }

}
