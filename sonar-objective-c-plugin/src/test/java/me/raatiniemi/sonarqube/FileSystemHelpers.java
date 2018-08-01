package me.raatiniemi.sonarqube;

import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.batch.fs.internal.DefaultInputFile;
import org.sonar.api.batch.sensor.internal.SensorContextTester;

import javax.annotation.Nonnull;

/**
 * Collection of file system related helper functionality.
 * <p>
 * TODO: Improve name of class, {@link FileSystemHelpers} is non-descriptive.
 */
public final class FileSystemHelpers {
    private final SensorContextTester context;

    private FileSystemHelpers(@Nonnull SensorContextTester context) {
        this.context = context;
    }

    @Nonnull
    public static FileSystemHelpers create(@Nonnull SensorContextTester context) {
        return new FileSystemHelpers(context);
    }

    @Nonnull
    private DefaultInputFile buildInputFile(@Nonnull String relativePath) {
        return new DefaultInputFile(context.module().key(), relativePath)
                .setLanguage("bla")
                .initMetadata("1\n2\n3\n4\n5\n6\n7\n8\n9\n10\n");
    }

    @Nonnull
    public DefaultInputFile createFile(@Nonnull String relativePath) {
        return buildInputFile(relativePath)
                .setType(InputFile.Type.MAIN);
    }

    @Nonnull
    public DefaultInputFile createTestFile(@Nonnull String relativePath) {
        return buildInputFile(relativePath)
                .setType(InputFile.Type.TEST);
    }

    public void addToFileSystem(@Nonnull DefaultInputFile inputFile) {
        context.fileSystem().add(inputFile);
    }
}
