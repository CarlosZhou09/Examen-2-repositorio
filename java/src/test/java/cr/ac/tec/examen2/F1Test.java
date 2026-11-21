package cr.ac.tec.examen2;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class F1Test {
    @TempDir
    Path temporaryDirectory;

    @Test
    void cargaContenidoOrdenadoYCuentaPorAlaYEdificio() throws IOException {
        BPlusTree tree = OccupancyCsvLoader.load(Path.of("../datos/version-b/ocupantes.csv"));

        assertArrayEquals(new Room[] {
            new Room("LAB-101", "Laboratorio de química", "Oeste", 24),
            new Room("LAB-102", "Laboratorio de física", "Oeste", 30),
            new Room("LIB-001", "Biblioteca", "Este", 45),
            new Room("OFF-105", "Oficina de docentes", "Este", 6),
            new Room("RM-103", "Sala de clases", "Central", 45),
            new Room("RM-104", "Sala de seminarios", "Central", 30)
        }, tree.listRooms());
        assertEquals(54, tree.countOccupantsByWing("Oeste"));
        assertEquals(75, tree.countOccupantsByWing("Central"));
        assertEquals(51, tree.countOccupantsByWing("Este"));
        assertEquals(180, tree.countBuildingOccupants());
    }

    @Test
    void alaSinSalasNoTieneOcupantes() throws IOException {
        BPlusTree tree = OccupancyCsvLoader.load(Path.of("../datos/version-b/ocupantes.csv"));

        assertEquals(0, tree.countOccupantsByWing("Norte"));
    }

    @Test
    void actualizaOcupacionYLaEnumeracionOrdenadaMuestraElNuevoValor() throws IOException {
        BPlusTree tree = OccupancyCsvLoader.load(Path.of("../datos/version-b/ocupantes.csv"));

        tree.updateOccupants("LAB-101", 27);

        assertEquals(new Room("LAB-101", "Laboratorio de química", "Oeste", 27),
                tree.listRooms()[0]);
        assertEquals(57, tree.countOccupantsByWing("Oeste"));
        assertEquals(183, tree.countBuildingOccupants());
    }

    @Test
    void arbolConUnElementoSeListaYSeCuenta() {
        BPlusTree tree = new BPlusTree();
        tree.insert(new Room("A-1", "Sala única", "Ala única", 3));

        assertArrayEquals(new Room[] {
            new Room("A-1", "Sala única", "Ala única", 3)
        }, tree.listRooms());
        assertEquals(3, tree.countOccupantsByWing("Ala única"));
        assertEquals(3, tree.countBuildingOccupants());
    }

    @Test
    void hojasEnlazadasYNodosInternosMantienenElOrdenTrasMultiplesDivisiones() {
        BPlusTree tree = new BPlusTree();
        Room[] expected = new Room[20];
        for (int i = 19; i >= 0; i--) {
            String code = "R-" + (i < 10 ? "0" : "") + i;
            tree.insert(new Room(code, "Sala " + i, "Ala", i));
        }
        for (int i = 0; i < expected.length; i++) {
            String code = "R-" + (i < 10 ? "0" : "") + i;
            expected[i] = new Room(code, "Sala " + i, "Ala", i);
        }

        assertArrayEquals(expected, tree.listRooms());
        assertEquals(190, tree.countOccupantsByWing("Ala"));
        assertEquals(190, tree.countBuildingOccupants());
    }

    @Test
    void actualizacionDeSalaInexistenteFalla() {
        BPlusTree tree = new BPlusTree();

        assertThrows(IllegalArgumentException.class,
                () -> tree.updateOccupants("NO-EXISTE", 4));
    }

    @Test
    void noPermiteDuplicarElCodigoDeUnaSala() {
        BPlusTree tree = new BPlusTree();
        tree.insert(new Room("A-1", "Sala", "Ala", 1));

        assertThrows(IllegalArgumentException.class,
                () -> tree.insert(new Room("A-1", "Otra sala", "Otra ala", 2)));
    }

    @Test
    void ocupacionNegativaSeRechaza() {
        BPlusTree tree = new BPlusTree();

        assertThrows(IllegalArgumentException.class,
                () -> tree.insert(new Room("A-1", "Sala", "Ala", -1)));
    }

    @Test
    void csvMalFormadoSeRechaza() throws IOException {
        Path csv = temporaryDirectory.resolve("malformado.csv");
        Files.writeString(csv, "codigo,nombre,ala,ocupantes\nA-1,Sala,Ala,no-numero\n");

        IllegalArgumentException error = assertThrows(IllegalArgumentException.class,
                () -> OccupancyCsvLoader.load(csv));

        assertEquals("Ocupantes inválidos en la línea 2", error.getMessage());
    }

    @Test
    void csvConColumnasIncorrectasSeRechaza() throws IOException {
        Path csv = temporaryDirectory.resolve("columnas.csv");
        Files.writeString(csv, "codigo,nombre\nA-1,Sala\n");

        IllegalArgumentException error = assertThrows(IllegalArgumentException.class,
                () -> OccupancyCsvLoader.load(csv));

        assertEquals("Encabezado CSV inválido", error.getMessage());
    }
}
