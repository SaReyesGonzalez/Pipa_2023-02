import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.filechooser.FileNameExtensionFilter;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.*;

public class FileSelectorApp {
    private JFrame frame;
    private JButton openButton;
    private File selectedFile1;
    private File selectedFile2;
    private ArrayList<Edge> listaEdge = new ArrayList<>();
    private ArrayList<Nodo> listaNodo = new ArrayList<>();

    public FileSelectorApp() {
        frame = new JFrame("Seleccionar 2 archivos XML");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 150);
        frame.setLayout(new FlowLayout());
        frame.setLocationRelativeTo(null);

        openButton = new JButton("Abrir archivos XML");
        frame.add(openButton);

        openButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                openXMLFiles();
                dibujar();
            }
        });

        frame.setVisible(true);
    }

    private void openXMLFiles() {
        JFileChooser fileChooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Archivos XML (*.xml)", "xml");
        fileChooser.setFileFilter(filter);
        fileChooser.setMultiSelectionEnabled(true);

        int returnValue = fileChooser.showOpenDialog(null);

        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File[] selectedFiles = fileChooser.getSelectedFiles();
            if (selectedFiles.length == 2) {
                selectedFile1 = selectedFiles[0];
                selectedFile2 = selectedFiles[1];
                openXMLViewerFrames();

            } else {
                JOptionPane.showMessageDialog(null, "Por favor, selecciona exactamente 2 archivos XML.");
            }
        }
    }

    private void openXMLViewerFrames() {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        Document D_edges;
        Document D_nodes;
        try {
            DocumentBuilder builder = factory.newDocumentBuilder();

            // Get Document
            D_edges = builder.parse(selectedFile1);
            D_nodes = builder.parse(selectedFile2);

            // Normalize the xml structure
            D_edges.getDocumentElement().normalize();
            D_nodes.getDocumentElement().normalize();
            // Get all the element by tag name
            NodeList edgeList = D_edges.getElementsByTagName("edge");
            for (int temp = 0; temp < edgeList.getLength(); temp++) {
                Node edgeNode = edgeList.item(temp);

                if (edgeNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element edgeElement = (Element) edgeNode;
                    try {
                        int u = Integer.parseInt(edgeElement.getElementsByTagName("u").item(0).getTextContent());
                        int v = Integer.parseInt(edgeElement.getElementsByTagName("v").item(0).getTextContent());
                        int osmid = Integer
                                .parseInt(edgeElement.getElementsByTagName("osmid").item(0).getTextContent());
                        String name = edgeElement.getElementsByTagName("name").item(0).getTextContent();
                        Edge e = new Edge(u, v, osmid, name);
                        listaEdge.add(e);
                    } catch (NumberFormatException e) {

                    }
                }

            }

            NodeList rowList = D_nodes.getElementsByTagName("row");
            for (int temp = 0; temp < rowList.getLength(); temp++) {
                Node rowNode = rowList.item(temp);

                if (rowNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element rowElement = (Element) rowNode;
                    try {
                        int osmid = Integer.parseInt(rowElement.getElementsByTagName("osmid").item(0).getTextContent());
                        double x = Double.parseDouble(rowElement.getElementsByTagName("x").item(0).getTextContent());
                        double y = Double.parseDouble(rowElement.getElementsByTagName("y").item(0).getTextContent());
                        int streetCount = Integer
                                .parseInt(rowElement.getElementsByTagName("street_count").item(0).getTextContent());
                        Nodo n = new Nodo(osmid, x, y, streetCount);
                        listaNodo.add(n);
                        // System.out.println("x: " + x);
                        // System.out.println("y: " + y);
                    } catch (NumberFormatException e) {

                    }
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void dibujar() {
        ArrayList<PointDouble> coordenadas = new ArrayList<>();
        try {
            for (int i = 0; i < listaNodo.size(); i++) {
                double x = listaNodo.get(i).getX();
                double y = listaNodo.get(i).getY();
                System.out.println(x);
                PointDouble p = new PointDouble(x, y);
                coordenadas.add(p);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Mapa");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.add(new JPanel() {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    PointDouble puntoAnterior = null;
                    g.setColor(Color.RED);

                    for (PointDouble punto : coordenadas) {
                        int x = (int) punto.getX(); // Convierte double a int
                        int y = (int) punto.getY(); // Convierte double a int

                        g.fillOval(-1 * x, -1 * y, 5, 5);
                        if (puntoAnterior != null) {
                            g.drawLine((int) puntoAnterior.getX(), (int) puntoAnterior.getY(), x, y);
                        }
                        puntoAnterior = punto;
                    }

                }
            });
            frame.setSize(800, 600);
            frame.setVisible(true);
        });

    }
}