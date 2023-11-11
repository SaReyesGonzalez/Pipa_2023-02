import javax.swing.*;
import java.util.ArrayList;

class ObserverListaEdge {


    public static FileSelectorApp fileSelectorApp;
    public ArrayList<Edge> edgeList;
    public void trigger() {
        edgeList = fileSelectorApp.listaEdge;
    }

    public ArrayList<Edge> suscribe() {
        return edgeList;
    }
}

class ObserverVentana {


    public static FileSelectorApp fileSelectorApp;
    public int[] ventanaWidthHeightCenterXY = new int[4];
    public void trigger() {
        ventanaWidthHeightCenterXY[0] = fileSelectorApp.graficarPanel.getWidth();
        ventanaWidthHeightCenterXY[1] = fileSelectorApp.graficarPanel.getHeight();

        // testeando si son los valores correctos
        ventanaWidthHeightCenterXY[2] = fileSelectorApp.graficarPanel.getX();
        ventanaWidthHeightCenterXY[3] = fileSelectorApp.graficarPanel.getY();
    }

    public int[] suscribe() {
        return ventanaWidthHeightCenterXY;
    }
}
public abstract  class  Cliping {


    public static void instalarObservers(FileSelectorApp fileSelectorApp) {
        ObserverListaEdge.fileSelectorApp = fileSelectorApp;
        ObserverVentana.fileSelectorApp = fileSelectorApp;
    }

    public static ArrayList<Edge> extractNodeEdge() {
        ObserverListaEdge observerListaEdge = new ObserverListaEdge();
        observerListaEdge.trigger();
        return observerListaEdge.suscribe();
    }

    public static int[] extractDataVentana() {
        ObserverVentana observerVentana = new ObserverVentana();
        observerVentana.trigger();
        return observerVentana.suscribe();
    }
}
