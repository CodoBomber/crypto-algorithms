package graph_painting;

public class Edge {

    private final Vertex vertex1;
    private final Vertex vertex2;

    Edge(Vertex vertex1, Vertex vertex2) {
        this.vertex1 = vertex1;
        this.vertex2 = vertex2;
    }

    public Vertex getVertex1() {
        return vertex1;
    }

    public Vertex getVertex2() {
        return vertex2;
    }

    @Override
    public String toString() {
        return vertex1.getOrdinal() + " " + vertex2.getOrdinal();
    }
}
