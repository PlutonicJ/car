package net.cyberspirit.car;

import javax.persistence.Persistence;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

public class DatabaseScriptBuilder {
    public static void main(String[] arguments) throws URISyntaxException {
        Map<String, String> properties = new HashMap<>();
        properties.put("javax.persistence.database-product-name", "PostgreSQL");
        properties.put("javax.persistence.jdbc.driver", "org.postgresql.Driver");
        properties.put("javax.persistence.database-major-version", "9");
        properties.put("javax.persistence.database-minor-version", "5");
        properties.put("javax.persistence.schema-generation.scripts.action", "drop-and-create");
        properties.put("javax.persistence.schema-generation.scripts.drop-target", "database/target/classes/drop.sql");
        properties.put("javax.persistence.schema-generation.scripts.create-target", "database/target/classes/create.sql");

        Persistence.generateSchema("default", properties);

        addDelimiterToScripts(";\n", "create.sql", "drop.sql");

    }

    private static void addDelimiterToScripts(String delimiter, String... scripts) throws URISyntaxException {
        for (String script : scripts) {
            Path path = Paths.get(DatabaseScriptBuilder.class.getResource("/" + script).toURI());
            try (Stream<String> lines = Files.lines(path)) {
                Path pathNewFile = path.getParent().resolve("delimited_" + script);
                Path file = Files.createFile(pathNewFile);
                try (OutputStream out = Files.newOutputStream(file)) {
                    lines.forEach(l -> {
                        try {
                            out.write((l + delimiter).getBytes());
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    });
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

    }
}
