package cr.ac.tec.examen2;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTimeout;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class F2Test {
    @TempDir
    Path temporaryDirectory;

    @Test
    void ordenaLasSalasDelCsvPorCantidadDescendente() throws IOException {
        BPlusTree tree = OccupancyCsvLoader.load(Path.of("../datos/version-b/ocupantes.csv"));
        Room[] rooms = tree.listRooms();

        RoomQuickSort.sortByOccupantsDescending(rooms);

        assertArrayEquals(new int[] {45, 45, 30, 30, 24, 6}, occupantCounts(rooms));
        assertContainsExactlyOnce(rooms, new String[] {
            "LAB-101", "LAB-102", "RM-103", "RM-104", "OFF-105", "LIB-001"
        });
        assertEquals(6, rooms.length);
    }

    @Test
    void ordenaArregloVacio() {
        Room[] rooms = new Room[0];

        RoomQuickSort.sortByOccupantsDescending(rooms);

        assertArrayEquals(new Room[0], rooms);
    }

    @Test
    void conservaElUnicoElemento() {
        Room[] rooms = {new Room("A-1", "Sala", "Ala", 7)};

        RoomQuickSort.sortByOccupantsDescending(rooms);

        assertArrayEquals(new Room[] {new Room("A-1", "Sala", "Ala", 7)}, rooms);
    }

    @Test
    void empatesConservanTodasLasSalasSinDefinirOrdenEntreEllas() {
        Room[] rooms = {
            new Room("A", "Sala A", "Ala", 10),
            new Room("B", "Sala B", "Ala", 10),
            new Room("C", "Sala C", "Ala", 10)
        };

        RoomQuickSort.sortByOccupantsDescending(rooms);

        assertArrayEquals(new int[] {10, 10, 10}, occupantCounts(rooms));
        assertContainsExactlyOnce(rooms, new String[] {"A", "B", "C"});
    }

    @Test
    void arregloYaOrdenadoPermaneceEnOrdenDescendente() {
        Room[] rooms = {
            new Room("A", "Sala A", "Ala", 9),
            new Room("B", "Sala B", "Ala", 6),
            new Room("C", "Sala C", "Ala", 2)
        };

        RoomQuickSort.sortByOccupantsDescending(rooms);

        assertArrayEquals(new int[] {9, 6, 2}, occupantCounts(rooms));
        assertArrayEquals(new String[] {"A", "B", "C"}, codes(rooms));
    }

    @Test
    void arregloNuloSeRechaza() {
        IllegalArgumentException error = assertThrows(IllegalArgumentException.class,
                () -> RoomQuickSort.sortByOccupantsDescending(null));

        assertEquals("El arreglo de salas no puede ser nulo", error.getMessage());
    }

    @Test
    void salaConOcupantesNegativosSeRechazaSinModificarElArreglo() {
        Room[] rooms = {
            new Room("A", "Sala A", "Ala", 5),
            new Room("B", "Sala B", "Ala", -1)
        };
        Room[] original = rooms.clone();

        IllegalArgumentException error = assertThrows(IllegalArgumentException.class,
                () -> RoomQuickSort.sortByOccupantsDescending(rooms));

        assertEquals("La cantidad de ocupantes no puede ser negativa", error.getMessage());
        assertArrayEquals(original, rooms);
    }

    @Test
    void csvConCantidadMalFormadaSeRechaza() throws IOException {
        Path csv = temporaryDirectory.resolve("malformado.csv");
        Files.writeString(csv, "codigo,nombre,ala,ocupantes\nA-1,Sala,Ala,desconocido\n");

        IllegalArgumentException error = assertThrows(IllegalArgumentException.class,
                () -> OccupancyCsvLoader.load(csv));

        assertEquals("Ocupantes inválidos en la línea 2", error.getMessage());
    }

    @Test
    void ordenaCuatrocientasSalasEnMenosDeUnSegundo() {
        assertTimeout(Duration.ofSeconds(1), () -> {
            Room[] rooms = new Room[400];
            for (int i = 0; i < rooms.length; i++) {
                rooms[i] = new Room("R-" + i, "Sala " + i, "Ala", (i * 37) % 401);
            }

            RoomQuickSort.sortByOccupantsDescending(rooms);

            assertEquals(400, rooms.length);
            for (int i = 1; i < rooms.length; i++) {
                assertTrue(rooms[i - 1].occupants() >= rooms[i].occupants(),
                        "Orden incorrecto en las posiciones " + (i - 1) + " y " + i);
            }
        });
    }

    private static int[] occupantCounts(Room[] rooms) {
        int[] counts = new int[rooms.length];
        for (int i = 0; i < rooms.length; i++) {
            counts[i] = rooms[i].occupants();
        }
        return counts;
    }

    private static String[] codes(Room[] rooms) {
        String[] result = new String[rooms.length];
        for (int i = 0; i < rooms.length; i++) {
            result[i] = rooms[i].code();
        }
        return result;
    }

    private static void assertContainsExactlyOnce(Room[] rooms, String[] expectedCodes) {
        assertEquals(expectedCodes.length, rooms.length);
        for (String expectedCode : expectedCodes) {
            int matches = 0;
            for (Room room : rooms) {
                if (expectedCode.equals(room.code())) {
                    matches++;
                }
            }
            assertEquals(1, matches, "Cantidad de apariciones de " + expectedCode);
        }
    }
}
