public class Nodo {
    private int osmid;
    private double y;
    private double x;
    private int street_Count;

    public Nodo(int osmid, double x, double y, int street_Count) {
        this.osmid = osmid;
        this.y = y;
        this.x = x;
        this.street_Count = street_Count;

    }

    public int getId() {
        return osmid;
    }

    public void setId(int id) {
        this.osmid = id;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public int getContCalle() {
        return street_Count;
    }

    public void setContCalle(int street_Count) {
        this.street_Count = street_Count;
    }

}