package graph_painting;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Painter {

    private final List<Edge> edges;
    private final List<Vertex> vertexes;
    private List<Colors> shuffledColors = new ArrayList<>(Arrays.asList(Colors.R, Colors.B, Colors.Y)),
    comparingColors = new ArrayList<>(shuffledColors);
    private final Random random = ThreadLocalRandom.current();

    public Painter(String filename, GraphPurchaser purchaser) throws IOException {
        LinkedList<String> lines = new LinkedList<>(Files.readAllLines(Paths.get(filename)));
//        this.NUMBER_OF_EDGES = lines.get(0)
        this.vertexes = parseVertexes(lines.get(lines.size() - 1));
        lines.remove(0);
        lines.remove(lines.size() - 1);
        Stream<String> lineStream = lines.stream();
        this.edges = lineStream.map(this::parseLine)
                .collect(Collectors.toList());
        Collections.shuffle(shuffledColors);
        vertexes.forEach(
                vertex -> {
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

    private Edge parseLine(String line) {
        String numbers[] = line.split(" ");
        return new Edge(new BigInteger(numbers[0]), new BigInteger(numbers[1]));
    }

    private List<Vertex> parseVertexes(String line) {
        return Arrays.stream(line.split(" "))
                .map(Vertex::new)
                .collect(Collectors.toList());
    }
}
