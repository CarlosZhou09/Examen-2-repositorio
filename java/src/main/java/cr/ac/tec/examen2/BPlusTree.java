package cr.ac.tec.examen2;

public final class BPlusTree {
    private static final int MAX_KEYS = 3;

    private Node root = new Node(true);

    public void insert(Room room) {
        validateRoom(room);
        if (findByCode(room.code()) != null) {
            throw new IllegalArgumentException("La sala ya existe: " + room.code());
        }

        Split split = insert(root, room);
        if (split != null) {
            Node newRoot = new Node(false);
            newRoot.keys[0] = split.separator;
            newRoot.children[0] = root;
            newRoot.children[1] = split.right;
            newRoot.keyCount = 1;
            root = newRoot;
        }
    }

    public void updateOccupants(String roomCode, int occupants) {
        if (roomCode == null || roomCode.isBlank()) {
            throw new IllegalArgumentException("El código de sala es obligatorio");
        }
        if (occupants < 0) {
            throw new IllegalArgumentException("La cantidad de ocupantes no puede ser negativa");
        }

        Node leaf = findLeaf(new Room(roomCode, "", "", 0));
        while (leaf != null) {
            for (int i = 0; i < leaf.keyCount; i++) {
                Room room = leaf.keys[i];
                int codeComparison = room.code().compareTo(roomCode);
                if (codeComparison == 0) {
                    leaf.keys[i] = new Room(room.code(), room.name(), room.wing(), occupants);
                    return;
                }
                if (codeComparison > 0) {
                    throw new IllegalArgumentException("La sala no existe: " + roomCode);
                }
            }
            leaf = leaf.next;
        }
        throw new IllegalArgumentException("La sala no existe: " + roomCode);
    }

    public Room[] listRooms() {
        // El CSV guarda cantidades por sala, no identidades; se enumeran esos registros en orden.
        int size = 0;
        Node leaf = firstLeaf();
        while (leaf != null) {
            size += leaf.keyCount;
            leaf = leaf.next;
        }

        Room[] rooms = new Room[size];
        int index = 0;
        leaf = firstLeaf();
        while (leaf != null) {
            for (int i = 0; i < leaf.keyCount; i++) {
                rooms[index++] = leaf.keys[i];
            }
            leaf = leaf.next;
        }
        return rooms;
    }

    public int countOccupantsByWing(String wing) {
        if (wing == null) {
            throw new IllegalArgumentException("El ala es obligatoria");
        }
        int total = 0;
        Node leaf = firstLeaf();
        while (leaf != null) {
            for (int i = 0; i < leaf.keyCount; i++) {
                if (wing.equals(leaf.keys[i].wing())) {
                    total += leaf.keys[i].occupants();
                }
            }
            leaf = leaf.next;
        }
        return total;
    }

    public int countBuildingOccupants() {
        int total = 0;
        Node leaf = firstLeaf();
        while (leaf != null) {
            for (int i = 0; i < leaf.keyCount; i++) {
                total += leaf.keys[i].occupants();
            }
            leaf = leaf.next;
        }
        return total;
    }

    private static void validateRoom(Room room) {
        if (room == null || room.code() == null || room.code().isBlank()
                || room.name() == null || room.name().isBlank()
                || room.wing() == null || room.wing().isBlank()) {
            throw new IllegalArgumentException("Los datos de sala, ala y nombre son obligatorios");
        }
        if (room.occupants() < 0) {
            throw new IllegalArgumentException("La cantidad de ocupantes no puede ser negativa");
        }
    }

    private Room findByCode(String roomCode) {
        Node leaf = findLeaf(new Room(roomCode, "", "", 0));
        while (leaf != null) {
            for (int i = 0; i < leaf.keyCount; i++) {
                Room room = leaf.keys[i];
                int comparison = room.code().compareTo(roomCode);
                if (comparison == 0) {
                    return room;
                }
                if (comparison > 0) {
                    return null;
                }
            }
            leaf = leaf.next;
        }
        return null;
    }

    private Node findLeaf(Room key) {
        Node node = root;
        while (!node.leaf) {
            int childIndex = 0;
            while (childIndex < node.keyCount
                    && compare(key, node.keys[childIndex]) >= 0) {
                childIndex++;
            }
            node = node.children[childIndex];
        }
        return node;
    }

    private Node firstLeaf() {
        Node node = root;
        while (!node.leaf) {
            node = node.children[0];
        }
        return node;
    }

    private static Split insert(Node node, Room room) {
        if (node.leaf) {
            return insertIntoLeaf(node, room);
        }

        int childIndex = 0;
        while (childIndex < node.keyCount && compare(room, node.keys[childIndex]) >= 0) {
            childIndex++;
        }
        Split childSplit = insert(node.children[childIndex], room);
        if (childSplit == null) {
            return null;
        }
        return insertIntoInternal(node, childIndex, childSplit);
    }

    private static Split insertIntoLeaf(Node leaf, Room room) {
        Room[] expanded = new Room[MAX_KEYS + 1];
        int insertionIndex = 0;
        while (insertionIndex < leaf.keyCount
                && compare(leaf.keys[insertionIndex], room) < 0) {
            expanded[insertionIndex] = leaf.keys[insertionIndex];
            insertionIndex++;
        }
        expanded[insertionIndex] = room;
        for (int i = insertionIndex; i < leaf.keyCount; i++) {
            expanded[i + 1] = leaf.keys[i];
        }

        if (leaf.keyCount < MAX_KEYS) {
            System.arraycopy(expanded, 0, leaf.keys, 0, leaf.keyCount + 1);
            leaf.keyCount++;
            return null;
        }

        Node right = new Node(true);
        int leftCount = (MAX_KEYS + 1) / 2;
        leaf.keyCount = leftCount;
        for (int i = 0; i < leftCount; i++) {
            leaf.keys[i] = expanded[i];
        }
        right.keyCount = MAX_KEYS + 1 - leftCount;
        for (int i = 0; i < right.keyCount; i++) {
            right.keys[i] = expanded[leftCount + i];
        }
        right.next = leaf.next;
        leaf.next = right;
        return new Split(right.keys[0], right);
    }

    private static Split insertIntoInternal(Node node, int childIndex, Split split) {
        Room[] expandedKeys = new Room[MAX_KEYS + 1];
        Node[] expandedChildren = new Node[MAX_KEYS + 2];
        for (int i = 0; i < childIndex; i++) {
            expandedKeys[i] = node.keys[i];
        }
        expandedKeys[childIndex] = split.separator;
        for (int i = childIndex; i < node.keyCount; i++) {
            expandedKeys[i + 1] = node.keys[i];
        }
        for (int i = 0; i <= childIndex; i++) {
            expandedChildren[i] = node.children[i];
        }
        expandedChildren[childIndex + 1] = split.right;
        for (int i = childIndex + 1; i <= node.keyCount; i++) {
            expandedChildren[i + 1] = node.children[i];
        }

        if (node.keyCount < MAX_KEYS) {
            System.arraycopy(expandedKeys, 0, node.keys, 0, node.keyCount + 1);
            System.arraycopy(expandedChildren, 0, node.children, 0, node.keyCount + 2);
            node.keyCount++;
            return null;
        }

        int promotedIndex = (MAX_KEYS + 1) / 2;
        Node right = new Node(false);
        Room promoted = expandedKeys[promotedIndex];
        node.keyCount = promotedIndex;
        for (int i = 0; i < node.keyCount; i++) {
            node.keys[i] = expandedKeys[i];
        }
        for (int i = 0; i <= node.keyCount; i++) {
            node.children[i] = expandedChildren[i];
        }

        right.keyCount = MAX_KEYS - promotedIndex;
        for (int i = 0; i < right.keyCount; i++) {
            right.keys[i] = expandedKeys[promotedIndex + 1 + i];
        }
        for (int i = 0; i <= right.keyCount; i++) {
            right.children[i] = expandedChildren[promotedIndex + 1 + i];
        }
        return new Split(promoted, right);
    }

    private static int compare(Room first, Room second) {
        // El CSV describe un único edificio y no tiene columna de edificio, así que queda implícito
        // y la clave disponible ordena primero por sala y luego por ala.
        int roomComparison = first.code().compareTo(second.code());
        if (roomComparison != 0) {
            return roomComparison;
        }
        return first.wing().compareTo(second.wing());
    }

    private static final class Node {
        private final boolean leaf;
        private final Room[] keys = new Room[MAX_KEYS];
        private final Node[] children = new Node[MAX_KEYS + 1];
        private int keyCount;
        private Node next;

        private Node(boolean leaf) {
            this.leaf = leaf;
        }
    }

    private record Split(Room separator, Node right) {
    }
}
