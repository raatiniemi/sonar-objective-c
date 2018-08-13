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
package org.sonar.plugins.objectivec;

import com.google.common.collect.ImmutableList;
import com.sonar.objectivec.ObjectiveCAstScanner;
import com.sonar.objectivec.ObjectiveCConfiguration;
import com.sonar.objectivec.api.ObjectiveCGrammar;
import com.sonar.objectivec.api.ObjectiveCMetric;
import com.sonar.objectivec.checks.CheckList;
import org.sonar.api.batch.Sensor;
import org.sonar.api.batch.SensorContext;
import org.sonar.api.batch.fs.FilePredicate;
import org.sonar.api.batch.fs.FileSystem;
import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.batch.rule.CheckFactory;
import org.sonar.api.batch.rule.Checks;
import org.sonar.api.component.ResourcePerspectives;
import org.sonar.api.issue.Issuable;
import org.sonar.api.measures.CoreMetrics;
import org.sonar.api.resources.Project;
import org.sonar.api.resources.Resource;
import org.sonar.api.rule.RuleKey;
import org.sonar.api.scan.filesystem.PathResolver;
import org.sonar.plugins.objectivec.core.ObjectiveC;
import org.sonar.squidbridge.AstScanner;
import org.sonar.squidbridge.api.CheckMessage;
import org.sonar.squidbridge.api.SourceCode;
import org.sonar.squidbridge.api.SourceFile;
import org.sonar.squidbridge.checks.SquidCheck;
import org.sonar.squidbridge.indexer.QueryByType;

import java.util.Collection;
import java.util.Locale;


public class ObjectiveCSquidSensor implements Sensor {
    private final FileSystem fileSystem;
    private final PathResolver pathResolver;
    private final ResourcePerspectives resourcePerspectives;
    private final Checks<SquidCheck<ObjectiveCGrammar>> checks;
    private final FilePredicate mainFilePredicates;

    private SensorContext context;

    public ObjectiveCSquidSensor(FileSystem fileSystem, PathResolver pathResolver, ResourcePerspectives resourcePerspectives, CheckFactory checkFactory) {

        this.fileSystem = fileSystem;
        this.pathResolver = pathResolver;
        this.resourcePerspectives = resourcePerspectives;
        this.checks = checkFactory.<SquidCheck<ObjectiveCGrammar>>create(CheckList.REPOSITORY_KEY).addAnnotatedChecks(CheckList.getChecks());
        this.mainFilePredicates = fileSystem.predicates().and(fileSystem.predicates().hasLanguage(ObjectiveC.KEY), fileSystem.predicates().hasType(InputFile.Type.MAIN));
    }

    public boolean shouldExecuteOnProject(Project project) {

        return project.isRoot() && fileSystem.hasFiles(fileSystem.predicates().hasLanguage(ObjectiveC.KEY));

    }

    public void analyse(Project project, SensorContext context) {
        this.context = context;

        ObjectiveCConfiguration configuration = ObjectiveCConfiguration.create(context.fileSystem().encoding());
        AstScanner<ObjectiveCGrammar> scanner = ObjectiveCAstScanner.create(configuration);


        scanner.scanFiles(ImmutableList.copyOf(fileSystem.files(mainFilePredicates)));

        Collection<SourceCode> squidSourceFiles = scanner.getIndex().search(new QueryByType(SourceFile.class));
        save(squidSourceFiles);
    }

    private void save(Collection<SourceCode> squidSourceFiles) {

        for (SourceCode squidSourceFile : squidSourceFiles) {
            SourceFile squidFile = (SourceFile) squidSourceFile;

            String relativePath = pathResolver.relativePath(fileSystem.baseDir(), new java.io.File(squidFile.getKey()));
            InputFile inputFile = fileSystem.inputFile(fileSystem.predicates().hasRelativePath(relativePath));

            saveMeasures(inputFile, squidFile);
            saveIssues(inputFile, squidFile);
        }
    }

    private void saveMeasures(InputFile inputFile, SourceFile squidFile) {
        context.saveMeasure(inputFile, CoreMetrics.FILES, squidFile.getDouble(ObjectiveCMetric.FILES));
        context.saveMeasure(inputFile, CoreMetrics.LINES, squidFile.getDouble(ObjectiveCMetric.LINES));
        context.saveMeasure(inputFile, CoreMetrics.NCLOC, squidFile.getDouble(ObjectiveCMetric.LINES_OF_CODE));
        context.saveMeasure(inputFile, CoreMetrics.STATEMENTS, squidFile.getDouble(ObjectiveCMetric.STATEMENTS));
        context.saveMeasure(inputFile, CoreMetrics.COMMENT_LINES, squidFile.getDouble(ObjectiveCMetric.COMMENT_LINES));
    }

    private void saveIssues(InputFile inputFile, SourceFile squidFile) {

        Collection<CheckMessage> messages = squidFile.getCheckMessages();

        Resource resource = context.getResource(inputFile);

        if (messages != null && resource != null) {
            for (CheckMessage message : messages) {
                RuleKey ruleKey = checks.ruleKey((SquidCheck<ObjectiveCGrammar>) message.getCheck());
                Issuable issuable = resourcePerspectives.as(Issuable.class, resource);

                if (issuable != null) {
                    Issuable.IssueBuilder issueBuilder = issuable.newIssueBuilder()
                            .ruleKey(ruleKey)
                            .line(message.getLine())
                            .message(message.getText(Locale.ENGLISH));

                    if (message.getCost() != null) {
                        issueBuilder.effortToFix(message.getCost());
                    }

                    issuable.addIssue(issueBuilder.build());
                }

            }
        }
    }

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }

}
