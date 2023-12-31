import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.geom.AffineTransform;
import java.text.DecimalFormat;
import java.util.List;

public class Graficar extends JPanel {
    private JFrame distanciaFrame;
    private JLabel distanciaLabel;
    private JLabel labelNodoUno;
    private JLabel labelNodoDos;
    private JLabel xNodoUno;
    private JLabel yNodoUno;
    private JLabel xNodoDos;
    private JLabel yNodoDos;
    private List<Nodo> nodos;
    private List<Edge> edges;
    private double zoom = 1.0;
    private int prevMouseX;
    private int prevMouseY;
    public int panX;
    public int panY;
    private boolean panning = false;
    private Nodo nodoMarcado = null;
    private Nodo nodoSeleccionado1 = null;
    private Nodo nodoSeleccionado2 = null;
    private double xv, xv2, yv, yv2;

    public Graficar(List<Nodo> nodos, List<Edge> edges, double xv, double xv2, double yv, double yv2) {
        
        distanciaFrame = new JFrame("Distancia entre Nodos");
        
        distanciaFrame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        distanciaFrame.setSize(225, 200);
        distanciaFrame.setLayout(new FlowLayout());
        distanciaFrame.setLocationRelativeTo(null);
        
        labelNodoUno = new JLabel("Osmid Nodo Uno: ");
        labelNodoDos = new JLabel("Osmid Nodo Dos: ");
        xNodoUno = new JLabel("Nodo Uno x:");
        xNodoDos = new JLabel("Nodo Dos x:");
        yNodoUno = new JLabel("Nodo Uno y:");
        yNodoDos = new JLabel("Nodo Dos y:");
        distanciaLabel = new JLabel("Distancia entre nodos: ");

        distanciaFrame.add(labelNodoUno);
        distanciaFrame.add(xNodoUno);
        distanciaFrame.add(yNodoUno);
        distanciaFrame.add(labelNodoDos);
        distanciaFrame.add(xNodoDos);
        distanciaFrame.add(yNodoDos);
        distanciaFrame.add(distanciaLabel);

        distanciaFrame.setVisible(false);

        this.nodos = nodos;
        this.edges = edges;
        this.xv = xv;
        this.xv2 = xv2;
        this.yv = yv;
        this.yv2 = yv2;

        addMouseListener(new MouseAdapter() {

            @Override
            public void mousePressed(MouseEvent e) {

                if (SwingUtilities.isLeftMouseButton(e)) {
                    prevMouseX = e.getX();
                    prevMouseY = e.getY();
                    panning = true;

                    // Buscar el nodo más cercano al punto de clic
                    nodoMarcado = encontrarNodoMasCercano(e.getX(), e.getY());
                    
                    if (nodoSeleccionado1 == null || nodoSeleccionado2 == null) 
                    {
                        if (nodoMarcado != null) 
                        {
                            // Dibuja el nodo
                            nodoMarcado.setMarcado(true);

                            if (nodoSeleccionado1 == null) {
                                nodoSeleccionado1 = nodoMarcado;

                            } else if (nodoSeleccionado2 == null) {
                                nodoSeleccionado2 = nodoMarcado;

                                // Llama al método para actualizar la distancia en el JLabel
                                actualizarDistanciaLabel();
                            }
                        }

                    } else { // Si ambos nodos no son null.

                        if (!distanciaFrame.isVisible()) { // Y el frame fue cerrado

                            // Los datos y dibujo de cada nodo desapareceran
                            nodoMarcado = nodoSeleccionado1;
                            nodoMarcado.setOriginalSize(0);
                            nodoSeleccionado1 = null;

                            nodoMarcado = nodoSeleccionado2;
                            nodoMarcado.setOriginalSize(0);
                            nodoSeleccionado2 = null;
                        }
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
                    (new ObserverDataMouse()).trigger(deltaX, deltaY);
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

    public void setZoom(double newZoom, int zoomCenterX, int zoomCenterY) 
    {
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

    public double getZoom() 
    {
        return zoom;
    }

    private int escalarCoordenada(double valor, double rangoMinEntrada, double rangoMaxEntrada, int rangoMinSalida, int rangoMaxSalida) 
    {
        return (int) (((valor - rangoMinEntrada) * (rangoMaxSalida - rangoMinSalida))
                / (rangoMaxEntrada - rangoMinEntrada) + rangoMinSalida);
    }

    @Override
    protected void paintComponent(Graphics g) {
        
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        
        g2d.setColor(Color.BLACK);
        g2d.fillRect(0, 0, getWidth(), getHeight());
        g2d.setTransform(new AffineTransform());
        g2d.translate(panX, panY);
        
        Cliping.cliping(g);
        
        for (Edge edge : edges) {
            
            String highwayType = edge.getHighway();
            Nodo nodoFuente = edge.getNodoFuente();
            Nodo nodoDestino = edge.getNodoDestino();
            
            if (nodoFuente != null && nodoDestino != null) {
                
                int x1 = escalarCoordenada(nodoFuente.getX(), xv, xv2, 0, getWidth());
                int y1 = escalarCoordenada(nodoFuente.getY(), yv, yv2, 0, getHeight());
                int x2 = escalarCoordenada(nodoDestino.getX(), xv, xv2, 0, getWidth());
                int y2 = escalarCoordenada(nodoDestino.getY(), yv, yv2, 0, getHeight());
                
                x1 = (int) (x1 * zoom);
                y1 = (int) (y1 * zoom);
                x2 = (int) (x2 * zoom);
                y2 = (int) (y2 * zoom);
                
                switch (highwayType) {
                    
                    case "motorway":
                    case "motorway_link":
                        g2d.setColor(Color.RED);
                        break;

                    case "trunk":
                    case "trunk_link":
                        g2d.setColor(Color.RED);
                        break;

                    case "primary":
                    case "primary_link":
                        g2d.setColor(Color.BLUE);
                        break;

                    case "secondary":
                    case "secondary_link":
                        g2d.setColor(Color.GREEN);
                        break;

                    case "tertiary":
                    case "tertiary_link":
                        g2d.setColor(Color.MAGENTA);
                        break;

                    case "unclassified":
                        g2d.setColor(Color.CYAN);
                        break;

                    case "residential":
                        g2d.setColor(Color.YELLOW);
                        break;

                    default:
                        g2d.setColor(Color.WHITE);
                        break;
                }
                
                g2d.drawLine(x1, y1, x2, y2);
                
            }
        }
        
        for (Nodo nodo : nodos) {
            nodo.dibujar(g2d, getWidth(), getHeight(), zoom);
        }

        // Dibujar la línea entre los nodos seleccionados
        if (nodoSeleccionado1 != null && nodoSeleccionado2 != null) {
            int x1 = escalar(nodoSeleccionado1.getX(), xv, xv2, 0, getWidth(), zoom);
            int y1 = escalar(nodoSeleccionado1.getY(), yv, yv2, 0, getHeight(), zoom);
            int x2 = escalar(nodoSeleccionado2.getX(), xv, xv2, 0, getWidth(), zoom);
            int y2 = escalar(nodoSeleccionado2.getY(), yv, yv2, 0, getHeight(), zoom);
            
            g2d.setColor(Color.RED); // Color de la línea
            g2d.drawLine(x1, y1, x2, y2);
            g2d.setColor(Color.RED); // Restablecer el color

        } else {

        }

    }

    // Agregar un método para encontrar el nodo más cercano
    public Nodo encontrarNodoMasCercano(double mouseX, double mouseY) {
        Nodo nodoMasCercano = null;

        for (Nodo nodo : nodos) {
            if (escalarCoordenada(nodo.getX(), xv, xv2, 0, getWidth()) - mouseX < 10
                    && escalarCoordenada(nodo.getY(), yv, yv2, 0, getHeight()) - mouseY < 10
                    && escalarCoordenada(nodo.getX(), xv, xv2, 0, getWidth()) - mouseX > 0
                    && escalarCoordenada(nodo.getY(), yv, yv2, 0, getHeight()) - mouseY > 0) {
                nodoMasCercano = nodo;
                return nodoMasCercano;
            }
        }

        return null;
    }

    private int escalar(double valor, double rangoMinEntrada, double rangoMaxEntrada, int rangoMinSalida,
            int rangoMaxSalida, double zoom) {
        return (int) ((((valor - rangoMinEntrada) * (rangoMaxSalida - rangoMinSalida))
                / (rangoMaxEntrada - rangoMinEntrada) + rangoMinSalida) * zoom);
    }

    public static double calcularDistanciaHaversine(double latitudNodo1, double longitudNodo1,
            double latitudNodo2, double longitudNodo2) {
        
        double radioTierra = 6371; // Radio promedio de la Tierra en kilómetros
        
        // Convierte las coordenadas de grados a radianes
        double latitud1 = Math.toRadians(latitudNodo1);
        double longitud1 = Math.toRadians(longitudNodo1);
        double latitud2 = Math.toRadians(latitudNodo2);
        double longitud2 = Math.toRadians(longitudNodo2);
        
        // Calcula las diferencias en latitud y longitud
        double dLatitud = latitud2 - latitud1;
        double dLongitud = longitud2 - longitud1;
        
        // Aplica la fórmula haversine
        double a = Math.sin(dLatitud / 2) * Math.sin(dLatitud / 2) + 
                    Math.cos(latitud1) * Math.cos(latitud2) *
                    Math.sin(dLongitud / 2) * Math.sin(dLongitud / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        
        // Calcula la distancia
        double distancia = radioTierra * c;
        return distancia;
    }

    public void actualizarDistanciaLabel() {
        if (nodoSeleccionado1 != null && nodoSeleccionado2 != null) {

            double latitudNodo1 = nodoSeleccionado1.getY();
            double longitudNodo1 = nodoSeleccionado1.getX();
            double latitudNodo2 = nodoSeleccionado2.getY();
            double longitudNodo2 = nodoSeleccionado2.getX();

            // Calcula la distancia en kilómetros
            double distancia = calcularDistanciaHaversine(latitudNodo1, longitudNodo1, latitudNodo2, longitudNodo2);
            
            // Formatea los valores con aproximadamente 3 decimales
            DecimalFormat df = new DecimalFormat("#.###");

            // Actualiza el texto del JLabel con la distancia y las coordenadas redondeadas
            xNodoUno.setText("Nodo Uno x: " + df.format(nodoSeleccionado1.getX()));
            yNodoUno.setText("Nodo Uno y: " + df.format(nodoSeleccionado1.getY()));
            xNodoDos.setText("Nodo Dos x: " + df.format(nodoSeleccionado2.getX()));
            yNodoDos.setText("Nodo Dos y: " + df.format(nodoSeleccionado2.getY()));
            labelNodoUno.setText("Osmid Nodo Uno: " + nodoSeleccionado1.getId());
            labelNodoDos.setText("Osmid Nodo Dos: " + nodoSeleccionado2.getId());
            distanciaLabel.setText("Distancia entre nodos: " + df.format(distancia) + " km");

            // Muestra el JFrame con el JLabel actualizado
            distanciaFrame.setVisible(true);
        
        }
    }

}
