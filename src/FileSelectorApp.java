import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.*;
import org.xml.sax.InputSource;

public class FileSelectorApp {

    public CiudadesProvider ciudadesProvider;
    //parte ciudades provider
    static Map<String, Nodo> nodosMap = new HashMap<>();
    public JFrame frame;
    public JButton openButton;
    public JButton Mostrar_Mapa;
    public JButton Mostrar_Map_Provider;
    public JButton cargar_Provider;
    public JButton cancelar;
    public File selectedFile1;
    public File selectedFile2;
    public File selectedFile1Provider;
    public File selecetdFile2Provider;
    public ArrayList<Edge> listaEdge = new ArrayList<>();
    public ArrayList<Nodo> listaNodo = new ArrayList<>();
    public JPanel contentPane;
    public double xv = 0, xv2 = 0, yv = 0, yv2 = 0;
    public Graficar graficarPanel;
    private SwingWorker loadFilesWorker;

    public FileSelectorApp() {
        frame = new JFrame("Carga de mapas");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 200);
        frame.setLayout(new BorderLayout());
        frame.setLocationRelativeTo(null);

        // Crea un JTabbedPane para contener las pestañas
        JTabbedPane tabbedPane = new JTabbedPane();

        // Crea pestaña para la selección de archivos XML
        JPanel fileSelectionPanel = new JPanel();

        fileSelectionPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 20));
        
        openButton = new JButton("Abrir archivos XML");
        cancelar = new JButton("Cancelar Carga");
        Mostrar_Mapa = new JButton("Mostrar Mapa");
        Mostrar_Mapa.setEnabled(false);
        fileSelectionPanel.add(openButton);
        fileSelectionPanel.add(Mostrar_Mapa);
        fileSelectionPanel.add(cancelar);
        tabbedPane.addTab("Selección de Archivos XML", fileSelectionPanel);

        //parte ciudad provider
        CiudadesProvider provider = CiudadesProvider.instance();

        
        // Crea pestaña para mostrar el mapa

        JPanel mapDisplayPanel = new JPanel();
        mapDisplayPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 20));
        cargar_Provider = new JButton("Cargar Datos");
        Mostrar_Map_Provider = new JButton("Mostrar Mapa Provider");
        Mostrar_Map_Provider.setEnabled(false);
        mapDisplayPanel.add(cargar_Provider);
        tabbedPane.addTab("Ciudades Provider", mapDisplayPanel);
        List<String> ciudadesList;
        try{
            ciudadesList = provider.list();
        }catch(Exception e){
            ciudadesList = new ArrayList<>();
            e.printStackTrace();
        }
        JComboBox<String> ciudadDropdown = new JComboBox<>(ciudadesList.toArray(new String[0]));
        mapDisplayPanel.add(ciudadDropdown, BorderLayout.NORTH);
        tabbedPane.addTab("Ciudades Provider", mapDisplayPanel);
        mapDisplayPanel.add(Mostrar_Map_Provider);

        // Clipping 
        class SetGraficarCommand {

            private final FileSelectorApp fileSelector;

            public SetGraficarCommand(FileSelectorApp fileSelectorApp) {
                this.fileSelector = fileSelectorApp;
            }
            public Graficar execute() {
                Graficar graficar = new Graficar(listaNodo, listaEdge, xv, xv2, yv, yv2);
                fileSelector.graficarPanel = graficar;
                return graficar;
            }
        }
        SetGraficarCommand commandSetGraficar = new SetGraficarCommand(this);
        
        // Actioner de todos los botones //

        //Boton de cancelar
        cancelar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (loadFilesWorker != null && !loadFilesWorker.isDone()) {
                    loadFilesWorker.cancel(true); // Intenta cancelar el SwingWorker
                }
                // Acciones a realizar al hacer clic en Cancelar
                JOptionPane.showMessageDialog(null, "Carga cancelada");
        
                // Puedes reiniciar o limpiar las variables relacionadas con la carga de archivos
                selectedFile1 = null;
                selectedFile2 = null;
                listaEdge.clear();
                listaNodo.clear();
                Mostrar_Mapa.setEnabled(false);
            }
        });

        // Boton para abrir los archivos XML
        frame.add(tabbedPane, BorderLayout.CENTER);
        openButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                openXMLFiles();
            }
        });
        
        // Boton para mostrar el mapa con los archivos XML
        Mostrar_Mapa.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Map<String, Nodo> nodosMap = crearDiccionarioNodos(listaNodo);
                for (Edge edge : listaEdge) {
                    Nodo nodoFuente = nodosMap.get(edge.getU());
                    Nodo nodoDestino = nodosMap.get(edge.getV());
                    edge.setNodoFuente(nodoFuente);
                    edge.setNodoDestino(nodoDestino);
                }
                Graficar graficar = commandSetGraficar.execute();
                // Crear un JScrollPane que contenga el graficar App
                JScrollPane scrollPane = new JScrollPane(graficar);
                // Configurar el comportamiento de las barras de desplazamiento
                scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
                scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
                // Crear el JFrame y configurarlo
                JFrame frame = new JFrame("Dibujar Mapa");
                frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
                frame.setSize(800, 600);
                frame.setLocation(0, 0);
                // Agregar el JScrollPane al contenido del JFrame
                frame.getContentPane().add(scrollPane);
                frame.setVisible(true);

                // Agrega un WindowListener para reiniciar todo cuando se cierre la ventana de graficar
                frame.addWindowListener(new WindowAdapter() {
                    @Override
                    public void windowClosing(WindowEvent e) {
                        reiniciarTodo();
                    }
                });

            }
        });

        // Boton para cargar los datos de la ciudad seleccionada

        cargar_Provider.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String ciudadSeleccionada = (String) ciudadDropdown.getSelectedItem();
                try {
                    CiudadesProvider.Ciudad ciudad;
                    ciudad = provider.ciudad(ciudadSeleccionada);
                    // Obtener los datos XML de la ciudad
                    String xmlNodes = ciudad.getXmlNodes();
                    String xmlEdges = ciudad.getXmlEdges();
                    // Convertir el String a un XML
                    Document documentNodes = convertStringToDocument(xmlNodes);
                    Document documentEdges = convertStringToDocument(xmlEdges);

                    System.out.println("XML NODES tiene tamaño " + xmlNodes.length());
                    System.out.println("XML EDGES tiene tamaño " + xmlEdges.length());

                    loadEdgesAndNodesFromXML(documentEdges,documentNodes);
                    // Habilitar el botón "Mostrar Mapa Provider" después de cargar los datos
                    Mostrar_Map_Provider.setEnabled(true);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });

        // Boton para mostrar el mapa con la clase Ciudades Provider
        Mostrar_Map_Provider.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Map<String, Nodo> nodosMap = crearDiccionarioNodos(listaNodo);
                for (Edge edge : listaEdge) 
                {
                    Nodo nodoFuente = nodosMap.get(edge.getU());
                    Nodo nodoDestino = nodosMap.get(edge.getV());
                    edge.setNodoFuente(nodoFuente);
                    edge.setNodoDestino(nodoDestino);
                }
                Graficar graficar = commandSetGraficar.execute();

                // Crear un JScrollPane que contenga el graficar App
                JScrollPane scrollPane = new JScrollPane(graficar);

                // Configurar el comportamiento de las barras de desplazamiento
                scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
                scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

                // Crear el JFrame y configurarlo
                JFrame frame = new JFrame("Dibujar Mapa");
                frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
                frame.setSize(800, 600);
                frame.setLocation(0, 0);

                // Agregar el JScrollPane al contenido del JFrame
                frame.getContentPane().add(scrollPane);
                frame.setVisible(true);

            }
        });

        frame.setVisible(true);
    }
     // Añade un método para reiniciar todo
     private void reiniciarTodo() {
        // Limpiar las listas y variables
        listaEdge.clear();
        listaNodo.clear();
        selectedFile1 = null;
        selectedFile2 = null;
        
        // Deshabilitar el botón "Mostrar Mapa"
        Mostrar_Mapa.setEnabled(false);
    }
    // Función para convertir un String a un XML
    private static Document convertStringToDocument(String xmlString) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            InputSource inputSource = new InputSource(new StringReader(xmlString));
            return builder.parse(inputSource);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void openXMLFiles() {
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

    public SwingWorker<Void, Integer> loadXMLFilesInBackground() {
        loadFilesWorker = new SwingWorker<Void, Integer>() {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            Document D_edges;
            Document D_nodes;
            @Override
            protected Void doInBackground() {
                try {
                    // Cargar archivos XML aquí y notificar el progreso
                     DocumentBuilder builder = factory.newDocumentBuilder();
                    // Get Document
                    D_edges = builder.parse(selectedFile1);
                    D_nodes = builder.parse(selectedFile2);
                    // Normalize the xml structure
                    D_edges.getDocumentElement().normalize();
                    D_nodes.getDocumentElement().normalize();
                    loadEdgesAndNodesFromXML(D_edges, D_nodes);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                return null;
            }
            @Override
            protected void done() {
                // Actualizar la interfaz gráfica después de cargar los archivos
                Mostrar_Mapa.setEnabled(true);
            }
        };
        loadFilesWorker.execute();
        return loadFilesWorker;
    }

    public void loadEdgesAndNodesFromXML(Document d_edges, Document d_nodes) {
        try {
            // Get all the element by tag name
            NodeList edgeList = d_edges.getElementsByTagName("edge");
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
                        Edge e = new Edge(u, v, k, osmid, name, highway);
                        listaEdge.add(e);
                    } 
                    catch (NumberFormatException e) {
                    }
                }
            }

            NodeList rowList = d_nodes.getElementsByTagName("row");
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
                    } 
                    catch (NumberFormatException e) {
                    }
                }
            }
             for (Nodo nodo : listaNodo) {
                    nodo.setXv(xv);
                    nodo.setXv2(xv2);
                    nodo.setYv(yv);
                    nodo.setYv2(yv2);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Map<String, Nodo> crearDiccionarioNodos(ArrayList<Nodo> nodos) {
        Map<String, Nodo> nodosMap = new HashMap<>();
        for (Nodo nodo : nodos) {
            nodosMap.put(nodo.getId(), nodo);
        }
        return nodosMap;
    }
}