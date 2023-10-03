public class Edge {

    private String u;
    private String v;
    private int k;
    private String osmid;
    private String nameStreet;
    private Nodo nodoFuente;
    private Nodo nodoDestino;

    public Edge(String u, String v, int k, String osmid, String nameStreet, Nodo nodoFuente, Nodo nodoDestino) {
        this.u = u;
        this.v = v;
        this.k = k;
        this.osmid = osmid;
        this.nameStreet = nameStreet;
    }

    public String getOsmid() {
        return osmid;
    }

    public void setOsmid(String osmid) {
        this.osmid = osmid;
    }

    public String getU() {
        return u;
    }

    public void setU(String u) {
        this.u = u;
    }

    public String getV() {
        return v;
    }

    public void setV(String v) {
        this.v = v;
    }

    public String getNameStreet() {
        return nameStreet;
    }

    public void setNameStreet(String nameStreet) {
        this.nameStreet = nameStreet;
    }

    public int getK() {
        return k;
    }

    public void setK(int k) {
        this.k = k;
    }

    public Nodo getNodoFuente() {
        return nodoFuente;
    }

    public void setNodoFuente(Nodo nodoFuente) {
        this.nodoFuente = nodoFuente;
    }

    public Nodo getNodoDestino() {
        return nodoDestino;
    }

    public void setNodoDestino(Nodo nodoDestino) {
        this.nodoDestino = nodoDestino;
    }
}