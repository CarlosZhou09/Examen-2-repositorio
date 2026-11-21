package cr.ac.tec.examen2;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public final class OccupancyCsvLoader {
    private static final String HEADER = "codigo,nombre,ala,ocupantes";

    private OccupancyCsvLoader() {
    }

    public static BPlusTree load(Path path) throws IOException {
        BPlusTree tree = new BPlusTree();
        try (BufferedReader reader = Files.newBufferedReader(path)) {
            String header = reader.readLine();
            if (header != null && header.startsWith("\uFEFF")) {
                header = header.substring(1);
            }
            if (!HEADER.equals(header)) {
                throw new IllegalArgumentException("Encabezado CSV inválido");
            }

            String line;
            int lineNumber = 1;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                String[] fields = line.split(",", -1);
                if (fields.length != 4) {
                    throw new IllegalArgumentException("Fila CSV inválida en la línea " + lineNumber);
                }

                int occupants;
                try {
                    occupants = Integer.parseInt(fields[3]);
                } catch (NumberFormatException exception) {
                    throw new IllegalArgumentException(
                            "Ocupantes inválidos en la línea " + lineNumber, exception);
                }
                tree.insert(new Room(fields[0], fields[1], fields[2], occupants));
            }
        }
        return tree;
    }
}
