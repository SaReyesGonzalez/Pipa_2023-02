public class Edge {

    private int u;
    private int v;
    private int osmid;
    private String nameStreet;

    public Edge(int u, int v, int osmid, String nameStreet) {
        this.u = u;
        this.v = v;

        this.osmid = osmid;
        this.nameStreet = nameStreet;
    }

    public int getOsmid() {
        return osmid;
    }

    public void setOsmid(int osmid) {
        this.osmid = osmid;
    }

    public int getU() {
        return u;
    }

    public void setU(int u) {
        this.u = u;
    }

    public int getV() {
        return v;
    }

    public void setV(int v) {
        this.v = v;
    }

    public String getNameStreet() {
        return nameStreet;
    }

    public void setNameStreet(String nameStreet) {
        this.nameStreet = nameStreet;
    }
}