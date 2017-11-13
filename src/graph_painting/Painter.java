package graph_painting;

import crypto.Pair;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Painter {

    private List<Edge> edges;
    /**
     * id, vertex_data
     */
    private Map<Integer, Vertex> vertexes;
    private List<Colors> shuffledColors = new ArrayList<>(Arrays.asList(Colors.R, Colors.B, Colors.Y)),
    comparingColors = new ArrayList<>(shuffledColors);
    private final Random random = ThreadLocalRandom.current();
    private final String filename;
    private final GraphPurchaser purchaser;

    public Painter(String filename, GraphPurchaser purchaser){
        this.filename = filename;
        this.purchaser = purchaser;
    }

    public void paintGraph() throws IOException {
        LinkedList<String> lines = new LinkedList<>(Files.readAllLines(Paths.get(filename)));
//        this.NUMBER_OF_EDGES = lines.get(0)
        this.vertexes = parseVertexes(lines.get(lines.size() - 1));

        lines.remove(0);
        lines.remove(lines.size() - 1);
        Stream<String> lineStream = lines.stream();
        this.edges = lineStream.map(this::parseEdges)
                .collect(Collectors.toList());
        System.out.println("Рёбра: " + edges);
        System.out.println("Изначальные цвета: " + comparingColors);
        for (int i = 0; i < 4 * edges.size(); i++) {
            Collections.shuffle(shuffledColors);
            System.out.println("Перемешанные цвета:" + shuffledColors);
            vertexes.forEach(
                    (id, vertex) -> {
                        Colors color = vertex.getColor();
                        if (color.equals(comparingColors.get(0))) {
                            vertex.setColor(shuffledColors.get(0));
                        } else if (color.equals(comparingColors.get(1))) {
                            vertex.setColor(shuffledColors.get(1));
                        } else if (color.equals(comparingColors.get(2))) {
                            vertex.setColor(shuffledColors.get(2));
                        }
                        vertex.setR(injectColorInRValue(vertex.getColor(), new BigInteger(64, random)));
                        vertex.setZ(vertex.getR().modPow(vertex.getDPublicKey(), vertex.getNPublicKey()));
                    }
            );
            //send to purchaser...
            purchaser.startZeroKnowledge(this);
        }
        System.out.println("ПОБЕЕЕДА! ЭТОТ ГРАФ ДЕЙСТВИТЕЛЬНО ПРАВИЛЬНО РАСКРАШЕН!");
    }

    public Edge getRandomEdge() {
        return edges.get(random.nextInt(edges.size()));
    }

    private BigInteger injectColorInRValue(Colors color, BigInteger r) {
        switch (color) {
            case R: {
                r = r.clearBit(0);
                r = r.clearBit(1);
                break;
            }
            case B: {
                r = r.clearBit(1);
                r = r.setBit(0);
                break;
            }

            case Y: {
                r = r.setBit(1);
                r = r.clearBit(0);
                break;
            }
        }
        return r;
    }

    private Edge parseEdges(String line) {
        String numbers[] = line.split(" ");
        return new Edge(
                vertexes.get(new BigInteger(numbers[0])
                        .intValueExact()),
                vertexes.get(new BigInteger(numbers[1])
                        .intValueExact())
        );
    }

    private Map<Integer, Vertex> parseVertexes(String line) {
        final String[] vertex = line.split(" ");
        return IntStream.range(0, vertex.length)
                .mapToObj(
                        i -> new Vertex(i, vertex[i])
                ).collect(Collectors.toMap(Vertex::getOrdinal, Function.identity()));
    }

    public Pair<BigInteger, BigInteger> getPrivateKeyForEdge(Edge randomEdge) {
        return new Pair<>(randomEdge.getVertex1().getC(), randomEdge.getVertex2().getC());
    }
}
