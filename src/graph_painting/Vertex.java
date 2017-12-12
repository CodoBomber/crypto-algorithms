package graph_painting;

import rsa.RSACore;

import java.math.BigInteger;

public class Vertex extends RSACore {

    private Colors color;
    private int ordinal;
    private BigInteger r;
    private BigInteger Z;

    Vertex(int ordinal, String color) {
        super(127, 127);
        this.ordinal = ordinal;
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

    //別々のパッケージーに別けること package-public
    public BigInteger getC() {
        BigInteger oldC = c;
        regeneratePrivateKeys();
        return oldC;
    }

    public int getOrdinal() {
        return ordinal;
    }

    public BigInteger getR() {
        return r;
    }

    public BigInteger getZ() {
        return Z;
    }
}
