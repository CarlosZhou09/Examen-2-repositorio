package cr.ac.tec.examen2;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public final class EvacuationGraph {
    private static final String HEADER = "desde,hacia,segundos,estado";
    private static final String[] EXITS = {
        "Salida Norte", "Salida Este", "Salida Sur"
    };

    private String[] nodes = new String[8];
    private int[] firstEdge = new int[8];
    private int[] lastEdge = new int[8];
    private int nodeCount;
    private int[] edgeTargets = new int[16];
    private int[] edgeSeconds = new int[16];
    private int[] nextEdge = new int[16];
    private int edgeCount;

    private EvacuationGraph() {
        for (int i = 0; i < firstEdge.length; i++) {
            firstEdge[i] = -1;
            lastEdge[i] = -1;
        }
    }

    public static EvacuationGraph load(Path path) throws IOException {
        EvacuationGraph graph = new EvacuationGraph();
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
                if (fields.length != 4 || fields[0].isBlank() || fields[1].isBlank()) {
                    throw new IllegalArgumentException("Fila CSV inválida en la línea " + lineNumber);
                }

                int seconds;
                try {
                    seconds = Integer.parseInt(fields[2]);
                } catch (NumberFormatException exception) {
                    throw new IllegalArgumentException(
                            "Segundos inválidos en la línea " + lineNumber, exception);
                }
                if (seconds < 0) {
                    throw new IllegalArgumentException(
                            "Segundos inválidos en la línea " + lineNumber);
                }

                if (!fields[3].equals("Abierto") && !fields[3].equals("Bloqueado")) {
                    throw new IllegalArgumentException(
                            "Estado inválido en la línea " + lineNumber);
                }

                int from = graph.addNode(fields[0]);
                int to = graph.addNode(fields[1]);
                if (fields[3].equals("Abierto")) {
                    // Cada par del CSV representa una conexión de pasillo transitable en ambos sentidos.
                    graph.addConnection(from, to, seconds);
                    graph.addConnection(to, from, seconds);
                }
            }
        }
        return graph;
    }

    public EvacuationGuide findGuide(String origin) {
        int originIndex = findNode(origin);
        if (originIndex < 0) {
            throw new IllegalArgumentException("El origen no existe en el grafo: " + origin);
        }

        int[] queue = new int[nodeCount];
        int[] parents = new int[nodeCount];
        int[] secondsFromParent = new int[nodeCount];
        boolean[] visited = new boolean[nodeCount];
        for (int i = 0; i < nodeCount; i++) {
            parents[i] = -1;
        }

        int front = 0;
        int back = 0;
        queue[back++] = originIndex;
        visited[originIndex] = true;
        int exitIndex = -1;
        while (front < back) {
            int current = queue[front++];
            if (isExit(nodes[current])) {
                exitIndex = current;
                break;
            }

            // Se conservan los vecinos en orden de aparición del CSV para resolver empates de BFS.
            for (int edge = firstEdge[current]; edge != -1; edge = nextEdge[edge]) {
                int neighbor = edgeTargets[edge];
                if (!visited[neighbor]) {
                    visited[neighbor] = true;
                    parents[neighbor] = current;
                    secondsFromParent[neighbor] = edgeSeconds[edge];
                    queue[back++] = neighbor;
                }
            }
        }

        if (exitIndex < 0) {
            return null;
        }

        int pathLength = 0;
        int totalSeconds = 0;
        for (int node = exitIndex; node != -1; node = parents[node]) {
            pathLength++;
            if (parents[node] != -1) {
                totalSeconds += secondsFromParent[node];
            }
        }
        String[] path = new String[pathLength];
        int pathIndex = pathLength - 1;
        for (int node = exitIndex; node != -1; node = parents[node]) {
            path[pathIndex--] = nodes[node];
        }
        return new EvacuationGuide(nodes[exitIndex], path, totalSeconds);
    }

    private int addNode(String name) {
        int existing = findNode(name);
        if (existing >= 0) {
            return existing;
        }
        ensureNodeCapacity();
        nodes[nodeCount] = name;
        firstEdge[nodeCount] = -1;
        lastEdge[nodeCount] = -1;
        return nodeCount++;
    }

    private int findNode(String name) {
        if (name == null) {
            return -1;
        }
        for (int i = 0; i < nodeCount; i++) {
            if (nodes[i].equals(name)) {
                return i;
            }
        }
        return -1;
    }

    private void addConnection(int from, int to, int seconds) {
        ensureEdgeCapacity();
        edgeTargets[edgeCount] = to;
        edgeSeconds[edgeCount] = seconds;
        nextEdge[edgeCount] = -1;
        if (firstEdge[from] == -1) {
            firstEdge[from] = edgeCount;
        } else {
            nextEdge[lastEdge[from]] = edgeCount;
        }
        lastEdge[from] = edgeCount;
        edgeCount++;
    }

    private void ensureNodeCapacity() {
        if (nodeCount < nodes.length) {
            return;
        }
        int newCapacity = nodes.length * 2;
        String[] expandedNodes = new String[newCapacity];
        int[] expandedFirst = new int[newCapacity];
        int[] expandedLast = new int[newCapacity];
        System.arraycopy(nodes, 0, expandedNodes, 0, nodeCount);
        System.arraycopy(firstEdge, 0, expandedFirst, 0, nodeCount);
        System.arraycopy(lastEdge, 0, expandedLast, 0, nodeCount);
        for (int i = nodeCount; i < newCapacity; i++) {
            expandedFirst[i] = -1;
            expandedLast[i] = -1;
        }
        nodes = expandedNodes;
        firstEdge = expandedFirst;
        lastEdge = expandedLast;
    }

    private void ensureEdgeCapacity() {
        if (edgeCount < edgeTargets.length) {
            return;
        }
        int newCapacity = edgeTargets.length * 2;
        int[] expandedTargets = new int[newCapacity];
        int[] expandedSeconds = new int[newCapacity];
        int[] expandedNext = new int[newCapacity];
        System.arraycopy(edgeTargets, 0, expandedTargets, 0, edgeCount);
        System.arraycopy(edgeSeconds, 0, expandedSeconds, 0, edgeCount);
        System.arraycopy(nextEdge, 0, expandedNext, 0, edgeCount);
        edgeTargets = expandedTargets;
        edgeSeconds = expandedSeconds;
        nextEdge = expandedNext;
    }

    private static boolean isExit(String node) {
        for (String exit : EXITS) {
            if (exit.equals(node)) {
                return true;
            }
        }
        return false;
    }
}
