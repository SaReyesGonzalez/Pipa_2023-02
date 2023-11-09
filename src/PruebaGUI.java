import javax.swing.JButton;
import javax.swing.JFrame;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;

public class PruebaGUI {

    private FileSelectorApp fileSelectorApp;
    private JButton button;

    @Before
    public void setUp() {
        // Configura y muestra la interfaz gráfica
        fileSelectorApp = new FileSelectorApp();
        fileSelectorApp.selectedFile2 = new File("src/archivosXML/nodes.xml");
        fileSelectorApp.selectedFile1 = new File("src/archivosXML/edges.xml");
        fileSelectorApp.loadXMLFilesInBackground();
    }

    @After
    public void tearDown() {
        // Cierra la interfaz gráfica después de la prueba
    }

    @Test
    public void test() {

    }
}
