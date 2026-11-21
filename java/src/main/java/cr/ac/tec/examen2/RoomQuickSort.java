package cr.ac.tec.examen2;

public final class RoomQuickSort {
    private RoomQuickSort() {
    }

    public static void sortByOccupantsDescending(Room[] rooms) {
        if (rooms == null) {
            throw new IllegalArgumentException("El arreglo de salas no puede ser nulo");
        }
        for (Room room : rooms) {
            if (room == null) {
                throw new IllegalArgumentException("El arreglo no puede contener salas nulas");
            }
            if (room.occupants() < 0) {
                throw new IllegalArgumentException(
                        "La cantidad de ocupantes no puede ser negativa");
            }
        }
        quickSort(rooms, 0, rooms.length - 1);
    }

    private static void quickSort(Room[] rooms, int low, int high) {
        if (low >= high) {
            return;
        }
        int left = low;
        int right = high;
        // El pivote central es suficiente para el arreglo de salas y no establece desempate.
        int pivot = rooms[low + (high - low) / 2].occupants();

        while (left <= right) {
            while (rooms[left].occupants() > pivot) {
                left++;
            }
            while (rooms[right].occupants() < pivot) {
                right--;
            }
            if (left <= right) {
                Room temporary = rooms[left];
                rooms[left] = rooms[right];
                rooms[right] = temporary;
                left++;
                right--;
            }
        }

        if (low < right) {
            quickSort(rooms, low, right);
        }
        if (left < high) {
            quickSort(rooms, left, high);
        }
    }
}
