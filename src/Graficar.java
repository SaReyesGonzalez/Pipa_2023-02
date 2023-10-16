import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.geom.AffineTransform;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;

public class Graficar extends JPanel {
    private List<Nodo> nodos;
    private List<Edge> edges;
    private double zoom = 1.0;
    private int prevMouseX;
    private int prevMouseY;
    private int panX;
    private int panY;
    private boolean panning = false;
    private Nodo nodoMarcado = null;

    public Graficar(List<Nodo> nodos, List<Edge> edges) {
        this.nodos = nodos;
        this.edges = edges;
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (SwingUtilities.isLeftMouseButton(e)) {
                    prevMouseX = e.getX();
                    prevMouseY = e.getY();
                    panning = true;
                    // Buscar el nodo más cercano al punto de clic
                    nodoMarcado = encontrarNodoMasCercano(e.getX(), e.getY());

                    // Marcar el nodo si se encontró uno
                    if (nodoMarcado != null) {
                        nodoMarcado.setMarcado(true);
                    }
                    repaint();
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (SwingUtilities.isLeftMouseButton(e)) {
                    panning = false;
                }
            }
        });

        addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (panning) {
                    int deltaX = e.getX() - prevMouseX;
                    int deltaY = e.getY() - prevMouseY;

                    panX += deltaX;
                    panY += deltaY;

                    prevMouseX = e.getX();
                    prevMouseY = e.getY();

                    repaint();
                }
            }
        });

        // Agregar un MouseWheelListener para el zoom con la rueda del mouse
        addMouseWheelListener(new MouseAdapter() {
            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                int notches = e.getWheelRotation();
                int zoomCenterX = e.getX(); // Obtenemos la posición X del ratón
                int zoomCenterY = e.getY(); // Obtenemos la posición Y del ratón

                if (notches < 0) {
                    // Zoom in
                    setZoom(getZoom() * 1.1, zoomCenterX, zoomCenterY);
                } else {
                    // Zoom out
                    setZoom(getZoom() / 1.1, zoomCenterX, zoomCenterY);
                }

                repaint();
            }
        });

    }

    public void setZoom(double newZoom, int zoomCenterX, int zoomCenterY) {
        // Calcular el desplazamiento de la posición del ratón después del zoom
        double offsetX = (zoomCenterX - panX) / zoom;
        double offsetY = (zoomCenterY - panY) / zoom;

        // Actualizar el nivel de zoom
        this.zoom = newZoom;

        // Ajustar el desplazamiento según el nuevo nivel de zoom
        panX = zoomCenterX - (int) (offsetX * newZoom);
        panY = zoomCenterY - (int) (offsetY * newZoom);

        repaint();
    }

    public double getZoom() {
        return zoom;
    }

    private int escalarCoordenada(double valor, double rangoMinEntrada, double rangoMaxEntrada, int rangoMinSalida,
            int rangoMaxSalida) {
        return (int) (((valor - rangoMinEntrada) * (rangoMaxSalida - rangoMinSalida))
                / (rangoMaxEntrada - rangoMinEntrada) + rangoMinSalida);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2d = (Graphics2D) g;

        g2d.setTransform(new AffineTransform());

        g2d.translate(panX, panY);

        for (Edge edge : edges) {
            Nodo nodoFuente = edge.getNodoFuente();
            Nodo nodoDestino = edge.getNodoDestino();
            if (nodoFuente != null && nodoDestino != null) {
                int x1 = escalarCoordenada(nodoFuente.getX(), -71.7, -71.1, 0, getWidth());
                int y1 = escalarCoordenada(nodoFuente.getY(), -30.5, -29.9, 0, getHeight());
                int x2 = escalarCoordenada(nodoDestino.getX(), -71.7, -71.1, 0, getWidth());
                int y2 = escalarCoordenada(nodoDestino.getY(), -30.5, -29.9, 0, getHeight());

                x1 = (int) (x1 * zoom);
                y1 = (int) (y1 * zoom);
                x2 = (int) (x2 * zoom);
                y2 = (int) (y2 * zoom);

                g2d.setColor(Color.red);
                g2d.drawLine(x1, y1, x2, y2);
                g2d.setColor(Color.black);
            }
        }

        for (Nodo nodo : nodos) {
            nodo.dibujar(g2d, getWidth(), getHeight(), zoom);
        }
    }

    // Agregar un método para encontrar el nodo más cercano
    public Nodo encontrarNodoMasCercano(double mouseX, double mouseY) {
        Nodo nodoMasCercano = null;
        // double distanciaMasCercana = Double.MAX_VALUE;

        for (Nodo nodo : nodos) {
            // double distancia = Math.sqrt(Math.pow(nodo.getX() - mouseX, 2) +
            // Math.pow(nodo.getY() - mouseY, 2));
            // if (distancia < distanciaMasCercana) {
            // distanciaMasCercana = distancia;
            if (escalarCoordenada(nodo.getX(), -71.7, -71.1, 0, getWidth()) - mouseX < 10
                    && escalarCoordenada(nodo.getY(), -30.5, -29.9, 0, getHeight()) - mouseY < 10
                    && escalarCoordenada(nodo.getX(), -71.7, -71.1, 0, getWidth()) - mouseX > 0
                    && escalarCoordenada(nodo.getY(), -30.5, -29.9, 0, getHeight()) - mouseY > 0) {
                System.out.println(escalarCoordenada(nodo.getX(), -71.7, -71.1, 0, getWidth()) - mouseX);
                System.out.println(escalarCoordenada(nodo.getY(), -30.5, -29.9, 0, getHeight()) - mouseY);
                nodoMasCercano = nodo;
                System.out.println(nodoMasCercano);
                return nodoMasCercano;
            }
        }
        // System.out.println(nodoMasCercano);

        return null;
    }

}
