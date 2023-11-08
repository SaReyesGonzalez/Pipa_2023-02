import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.swing.filechooser.FileNameExtensionFilter;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.*;

public class FileSelectorApp {
    static Map<String, Nodo> nodosMap = new HashMap<>();
    private JFrame frame;
    private JButton openButton;
    private JButton Mostrar_Mapa;
    private JProgressBar progressBar;
    private File selectedFile1;
    private File selectedFile2;
    private ArrayList<Edge> listaEdge = new ArrayList<>();
    private ArrayList<Nodo> listaNodo = new ArrayList<>();
    private JPanel contentPane;
    private double xv = 0, xv2 = 0, yv = 0, yv2 = 0;

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

            }
        });
        Mostrar_Mapa = new JButton("Mostrar Mapa");
        Mostrar_Mapa.setEnabled(false);
        frame.add(Mostrar_Mapa);
        Mostrar_Mapa.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Map<String, Nodo> nodosMap = crearDiccionarioNodos(listaNodo);
                for (Edge edge : listaEdge) {
                    Nodo nodoFuente = nodosMap.get(edge.getU());
                    Nodo nodoDestino = nodosMap.get(edge.getV());
                    edge.setNodoFuente(nodoFuente);
                    edge.setNodoDestino(nodoDestino);
                }
                Graficar panel = new Graficar(listaNodo, listaEdge, xv, xv2, yv, yv2);

                // Crear un JScrollPane que contenga el panel App
                JScrollPane scrollPane = new JScrollPane(panel);

                // Configurar el comportamiento de las barras de desplazamiento
                scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
                scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

                // Crear el JFrame y configurarlo
                JFrame frame = new JFrame("Dibujar Mapa");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setSize(800, 600);

                // Agregar el JScrollPane al contenido del JFrame
                frame.getContentPane().add(scrollPane);
                frame.setVisible(true);

            }
        });

        progressBar = new JProgressBar(0, 100);
        progressBar.setStringPainted(true);
        frame.add(progressBar);

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
                loadXMLFilesInBackground();

            } else {
                JOptionPane.showMessageDialog(null, "Por favor, selecciona exactamente 2 archivos XML.");
            }
        }
    }

    private void loadXMLFilesInBackground() {
        SwingWorker<Void, Integer> worker = new SwingWorker<Void, Integer>() {
            @Override
            protected Void doInBackground() {
                try {
                    // Cargar archivos XML aquí y notificar el progreso
                    loadEdgesAndNodesFromXML(selectedFile1, selectedFile2);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                return null;
            }

            @Override
            protected void done() {
                // Actualizar la interfaz gráfica después de cargar los archivos
                Mostrar_Mapa.setEnabled(true);
                progressBar.setValue(100);
            }
        };

        worker.execute();
    }

    private void loadEdgesAndNodesFromXML(File file1, File file2) {
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
                        String u = (edgeElement.getElementsByTagName("u").item(0).getTextContent());
                        String v = (edgeElement.getElementsByTagName("v").item(0).getTextContent());
                        int k = Integer.parseInt(edgeElement.getElementsByTagName("k").item(0).getTextContent());
                        String osmid = (edgeElement.getElementsByTagName("osmid").item(0).getTextContent());
                        String name = edgeElement.getElementsByTagName("name").item(0).getTextContent();
                        String highway = edgeElement.getElementsByTagName("highway").item(0).getTextContent();
                        Nodo nodoFuente = nodosMap.get(u);
                        Nodo nodoDestino = nodosMap.get(v);
                        Edge e = new Edge(u, v, k, osmid, name, highway);

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
                        String osmid = (rowElement.getElementsByTagName("osmid").item(0).getTextContent());
                        double x = Double.parseDouble(rowElement.getElementsByTagName("x").item(0).getTextContent());
                        double y = Double.parseDouble(rowElement.getElementsByTagName("y").item(0).getTextContent());
                        int streetCount = Integer
                                .parseInt(rowElement.getElementsByTagName("street_count").item(0).getTextContent());
                        Nodo n = new Nodo(osmid, x, y, streetCount);
                        listaNodo.add(n);
                        if (xv == 0) {
                            xv = x;
                        } else if (xv > x) {
                            xv = x;
                        }
                        if (xv2 == 0) {
                            xv2 = x;
                        } else if (xv2 < x) {
                            xv2 = x;
                        }
                        if (yv == 0) {
                            yv = y;
                        } else if (yv > y) {
                            yv = y;
                        }
                        if (yv2 == 0) {
                            yv2 = y;
                        } else if (yv2 < y) {
                            yv2 = y;
                        }
                        for (Nodo nodo : listaNodo) {
                            nodo.setXv(xv);
                            nodo.setXv2(xv2);
                            nodo.setYv(yv);
                            nodo.setYv2(yv2);
                        }
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

    private static Map<String, Nodo> crearDiccionarioNodos(ArrayList<Nodo> nodos) {
        Map<String, Nodo> nodosMap = new HashMap<>();
        for (Nodo nodo : nodos) {
            nodosMap.put(nodo.getId(), nodo);
        }
        return nodosMap;
    }
}