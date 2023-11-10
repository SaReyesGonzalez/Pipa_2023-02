import javax.swing.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.NodeList;

import java.io.File;
import java.util.ArrayList;


public class PruebaGUI {

    private FileSelectorApp fileSelectorApp;

    @Before
    public void setUp() throws InterruptedException {
        // Configura y muestra la interfaz gráfica
        fileSelectorApp = new FileSelectorApp();
        fileSelectorApp.selectedFile2 = new File("src/archivosXML/nodes.xml");
        fileSelectorApp.selectedFile1 = new File("src/archivosXML/edges.xml");

        SwingWorker<Void, Integer> worker = fileSelectorApp.loadXMLFilesInBackground();

        while (!worker.isDone()) {

        }

    }

    @After
    public void tearDown() {
        // Cierra la interfaz gráfica después de la prueba
    }

    @Test
    public void test() {

    }

    @Test
    public void testDataObserverNoteEdgeWithoutEdit() {
        Cliping.instalarObservers(fileSelectorApp);
        ArrayList<Edge> nodeList = Cliping.extractNodeEdge();
        assert nodeList == fileSelectorApp.listaEdge;


    }

    @Test
    public void testDataObserverNoteEdgeEditingSet() {

        Cliping.instalarObservers(fileSelectorApp);
        ArrayList<Edge> nodeList = Cliping.extractNodeEdge();

        Edge  edge = nodeList.get(0);
        edge.setOsmid("A");
        nodeList.set(0,edge);
        assert nodeList.get(0).getOsmid().equals(fileSelectorApp.listaEdge.get(0).getOsmid());


    }

    @Test
    public void testDataObserverNoteEdgeEditingOnlyGet() {

        Cliping.instalarObservers(fileSelectorApp);
        ArrayList<Edge> nodeList = Cliping.extractNodeEdge();

        nodeList.get(0).setOsmid("A");

        assert nodeList.get(0).getOsmid().equals(fileSelectorApp.listaEdge.get(0).getOsmid());


    }
}


