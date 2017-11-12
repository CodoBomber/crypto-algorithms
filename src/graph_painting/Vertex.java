package graph_painting;

import rsa.RSACore;

import java.math.BigInteger;

public class Vertex extends RSACore {

    private Colors color;
    private BigInteger r;
    private BigInteger Z;

    Vertex(String color) {
        super(127, 127);
        this.color = Colors.valueOf(color);
    }

    public Colors getColor() {
        return color;
    }

    public void setColor(Colors color) {
        this.color = color;
    }

    public void setR(BigInteger r) {
        this.r = r;
    }

    public void setZ(BigInteger z) {
        Z = z;
    }

    public BigInteger getR() {
        return r;
    }
}
