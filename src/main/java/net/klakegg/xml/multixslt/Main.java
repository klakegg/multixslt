package net.klakegg.xml.multixslt;

import net.klakegg.xml.multixslt.util.SimpleFileVisitor;
import org.apache.commons.cli.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.List;

public class Main {

    private static Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String... args) throws ParseException {
        Options options = new Options();
        options.addOption("f", "folder", true, "Folder");
        options.addOption("r", "recursive", false, "Recursive traverse for files.");

        CommandLineParser parser = new DefaultParser();
        CommandLine cli = parser.parse(options, args);

        Path home = Paths.get(cli.getOptionValue("folder", ""));
        logger.debug("Home folder: {}", home.toAbsolutePath());

        final List<String> files = cli.getArgList();
        if (files.size() == 0)
            files.add("multixslt.xml");

        if (cli.hasOption("recursive")) {
            try {
                Files.walkFileTree(home, new SimpleFileVisitor() {
                    @Override
                    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                        if (files.contains(file.getFileName().toString()))
                            new FileProcessor(file).perform();

                        return super.visitFile(file, attrs);
                    }
                });
            } catch (IOException e) {
                logger.error(e.getMessage(), e);
            }
        } else {
            for (String file : files)
                new FileProcessor(home.resolve(file)).perform();
        }
    }
}
