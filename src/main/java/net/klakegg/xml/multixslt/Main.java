package net.klakegg.xml.multixslt;

import org.apache.commons.cli.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class Main {

    public static void main(String... args) throws ParseException, IOException {
        Options options = new Options();
        options.addOption("f", "folder", true, "Folder");
        options.addOption("r", "recursive", false, "Recursive traverse for files.");

        CommandLineParser parser = new DefaultParser();
        CommandLine cli = parser.parse(options, args);

        Path home = Paths.get(cli.getOptionValue("folder", ""));

        List<String> files = cli.getArgList();
        if (files.isEmpty())
            files.add("multixslt.xml");

        if (cli.hasOption("recursive")) {
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
