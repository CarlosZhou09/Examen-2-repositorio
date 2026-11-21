package cr.ac.tec.examen2;

import java.io.IOException;
import java.nio.file.Path;

public class Main {
    public static void main(String[] args) throws IOException {
        BPlusTree tree = OccupancyCsvLoader.load(
                Path.of("../datos/version-b/ocupantes.csv"));
        Room[] rooms = tree.listRooms();
        String[] wings = new String[rooms.length];
        int wingCount = 0;

        System.out.println("Personas por ala:");
        for (Room room : rooms) {
            boolean alreadyPrinted = false;
            for (int i = 0; i < wingCount; i++) {
                if (wings[i].equals(room.wing())) {
                    alreadyPrinted = true;
                    break;
                }
            }
            if (!alreadyPrinted) {
                wings[wingCount++] = room.wing();
                System.out.println(room.wing() + ": " + tree.countOccupantsByWing(room.wing()));
            }
        }
        System.out.println("Personas en todo el edificio: " + tree.countBuildingOccupants());

        RoomQuickSort.sortByOccupantsDescending(rooms);
        System.out.println("Salas de mayor a menor ocupantes:");
        for (Room room : rooms) {
            System.out.println(room.code() + " - " + room.name() + " ("
                    + room.wing() + "): " + room.occupants());
        }

        EvacuationGraph graph = EvacuationGraph.load(
                Path.of("../datos/version-b/pasillos.csv"));
        System.out.println("Guías de evacuación:");
        for (Room room : rooms) {
            EvacuationGuide guide = graph.findGuide(room.code());
            if (guide == null) {
                System.out.println(room.code() + ": sin ruta disponible");
            } else {
                System.out.print(room.code() + ": ");
                for (int i = 0; i < guide.path().length; i++) {
                    if (i > 0) {
                        System.out.print(" -> ");
                    }
                    System.out.print(guide.path()[i]);
                }
                System.out.println(" (" + guide.seconds() + " segundos)");
            }
        }
    }
}
