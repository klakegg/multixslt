package net.klakegg.xml.multixslt;

import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@SuppressWarnings("unused")
public class Main {

    public static void main(String... args) throws Exception {
        new Main().run(args);
    }

    @Option(name = "--folder", aliases = "-f", usage = "Folder")
    private String folder;
    @Option(name = "--recursive", aliases = "-r", usage = "Recursive traverse for files.")
    private boolean recursive;
    @Argument
    private List<String> files;

    public void run(String... args) throws Exception {
        // Parse arguments
        new CmdLineParser(this).parseArgument(args);

        // Populate files if not set
        files = Optional.ofNullable(files).orElse(Collections.singletonList("multixslt.xml"));

        // Detect home folder.
        Path home = Paths.get(Optional.ofNullable(folder).orElse(""));

        if (recursive) {
            Files.walk(home)
                    .filter(Files::isRegularFile)
                    .filter(p -> files.contains(p.getFileName().toString()))
                    .forEach(ManifestProcessor::perform);
        } else {
            files.stream()
                    .map(home::resolve)
                    .forEach(ManifestProcessor::perform);
        }
    }
}
