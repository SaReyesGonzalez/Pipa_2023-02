import java.awt.Color;
import java.awt.Graphics;

public class Nodo {
    private String osmid;
    private double y;
    private double x;
    private int street_Count;
    private Color color = Color.RED;
    private int tamañoOriginal = 1;
    private boolean marcado = false;
    private double xv, xv2, yv, yv2;

    public Nodo(String osmid, double x, double y, int street_Count) {
        this.osmid = osmid;
        this.y = y;
        this.x = x;
        this.street_Count = street_Count;

    }

    public String getId() {
        return osmid;
    }

    public void setId(String id) {
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

    public double getYv() {
        return yv;
    }

    public void setYv(double yv) {
        this.yv = yv;
    }

    public double getYv2() {
        return yv2;
    }

    public void setYv2(double yv2) {
        this.yv2 = yv2;
    }

    public double getXv() {
        return xv;
    }

    public void setXv(double xv) {
        this.xv = xv;
    }

    public double getXv2() {
        return xv2;
    }

    public void setXv2(double xv2) {
        this.xv2 = xv2;
    }

    public int getContCalle() {
        return street_Count;
    }

    public void setContCalle(int street_Count) {
        this.street_Count = street_Count;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public boolean isMarcado() {
        return marcado;
    }

    public void setMarcado(boolean marcado) {
        this.marcado = marcado;

        // Cambiar el tamaño del nodo cuando se marca
        if (marcado) {
            tamañoOriginal += 10; // Aumentar el tamaño original (por ejemplo, x3)
        }
    }

    public int getTamañoOriginal() {
        return tamañoOriginal;
    }

    public void setTamañoOriginal(int tamañoOriginal) {
        this.tamañoOriginal = tamañoOriginal;
    }

    public void dibujar(Graphics g, int panelWidth, int panelHeight, double zoom) {
        g.setColor(color);
        int scaledX = escalarCoordenada(x, xv, xv2, 0, panelWidth);
        int scaledY = escalarCoordenada(y, yv, yv2, 0, panelHeight);
        scaledX = (int) (scaledX * zoom);
        scaledY = (int) (scaledY * zoom);
        g.fillOval(scaledX - 1, scaledY - 1, tamañoOriginal, tamañoOriginal);
        g.setColor(Color.BLACK);
    }

    private int escalarCoordenada(double valor, double rangoMinEntrada, double rangoMaxEntrada, int rangoMinSalida,
            int rangoMaxSalida) {
        return (int) (((valor - rangoMinEntrada) * (rangoMaxSalida - rangoMinSalida))
                / (rangoMaxEntrada - rangoMinEntrada) + rangoMinSalida);
    }

}