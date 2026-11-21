package cr.ac.tec.examen2;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class F3Test {
    @TempDir
    Path temporaryDirectory;

    @Test
    void obtieneRutaDeMenorCantidadDeAristasDesdeLosDatos() throws IOException {
        EvacuationGraph graph = EvacuationGraph.load(Path.of("../datos/version-b/pasillos.csv"));

        EvacuationGuide guide = graph.findGuide("LAB-101");

        assertEquals("Salida Norte", guide.exit());
        assertArrayEquals(new String[] {"LAB-101", "Pasillo A", "Salida Norte"}, guide.path());
        assertEquals(30, guide.seconds());
    }

    @Test
    void encuentraSalidaConUnaConexionDirecta() throws IOException {
        EvacuationGraph graph = loadGraph(
                "Sala 1,Salida Norte,7,Abierto\n");

        EvacuationGuide guide = graph.findGuide("Sala 1");

        assertEquals("Salida Norte", guide.exit());
        assertArrayEquals(new String[] {"Sala 1", "Salida Norte"}, guide.path());
        assertEquals(7, guide.seconds());
    }

    @Test
    void origenIgualASalidaTieneRutaDeCeroSegundos() throws IOException {
        EvacuationGraph graph = loadGraph(
                "Sala 1,Salida Norte,7,Abierto\n");

        EvacuationGuide guide = graph.findGuide("Salida Norte");

        assertEquals("Salida Norte", guide.exit());
        assertArrayEquals(new String[] {"Salida Norte"}, guide.path());
        assertEquals(0, guide.seconds());
    }

    @Test
    void noExisteRutaCuandoLosCaminosEstanBloqueados() throws IOException {
        EvacuationGraph graph = loadGraph(
                "Sala 1,Pasillo A,5,Bloqueado\n");

        EvacuationGuide guide = graph.findGuide("Sala 1");

        assertEquals(null, guide);
    }

    @Test
    void empateDeAristasSeResuelvePorElOrdenDeVecinosDelCsv() throws IOException {
        EvacuationGraph graph = loadGraph(
                "Sala 1,Pasillo A,3,Abierto\n"
                + "Sala 1,Pasillo B,4,Abierto\n"
                + "Pasillo A,Salida Norte,5,Abierto\n"
                + "Pasillo B,Salida Este,6,Abierto\n");

        EvacuationGuide guide = graph.findGuide("Sala 1");

        assertEquals("Salida Norte", guide.exit());
        assertArrayEquals(new String[] {"Sala 1", "Pasillo A", "Salida Norte"}, guide.path());
        assertEquals(8, guide.seconds());
    }

    @Test
    void ningunaRutaDelCsvPasaPorElPasilloBloqueado() throws IOException {
        EvacuationGraph graph = EvacuationGraph.load(Path.of("../datos/version-b/pasillos.csv"));

        EvacuationGuide guide = graph.findGuide("Pasillo B");

        assertEquals("Salida Sur", guide.exit());
        assertArrayEquals(new String[] {"Pasillo B", "Pasillo C", "Salida Sur"}, guide.path());
        assertEquals(32, guide.seconds());
    }

    @Test
    void salaInexistenteSeRechaza() throws IOException {
        EvacuationGraph graph = EvacuationGraph.load(Path.of("../datos/version-b/pasillos.csv"));

        IllegalArgumentException error = assertThrows(IllegalArgumentException.class,
                () -> graph.findGuide("Sala inexistente"));

        assertEquals("El origen no existe en el grafo: Sala inexistente", error.getMessage());
    }

    @Test
    void segundosNoNumericosSeRechazan() throws IOException {
        Path csv = writeCsv("segundos.csv",
                "desde,hacia,segundos,estado\nSala 1,Salida Norte,no-numero,Abierto\n");

        IllegalArgumentException error = assertThrows(IllegalArgumentException.class,
                () -> EvacuationGraph.load(csv));

        assertEquals("Segundos inválidos en la línea 2", error.getMessage());
    }

    @Test
    void estadoDesconocidoSeRechaza() throws IOException {
        Path csv = writeCsv("estado.csv",
                "desde,hacia,segundos,estado\nSala 1,Salida Norte,7,Desconocido\n");

        IllegalArgumentException error = assertThrows(IllegalArgumentException.class,
                () -> EvacuationGraph.load(csv));

        assertEquals("Estado inválido en la línea 2", error.getMessage());
    }

    private EvacuationGraph loadGraph(String rows) throws IOException {
        Path csv = writeCsv("grafo.csv", "desde,hacia,segundos,estado\n" + rows);
        return EvacuationGraph.load(csv);
    }

    private Path writeCsv(String name, String content) throws IOException {
        Path csv = temporaryDirectory.resolve(name);
        Files.writeString(csv, content);
        return csv;
    }
}
