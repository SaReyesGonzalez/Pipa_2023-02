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
    public class VentanaData {
    };
    public void trigger() {
    }

    public VentanaData suscribe() {
        return new VentanaData();
    }
}
public abstract  class  Cliping {


    public static void instalarObservers(FileSelectorApp fileSelectorApp) {
        ObserverListaEdge.fileSelectorApp = fileSelectorApp;
    }

    public static ArrayList<Edge> extractNodeEdge() {
        ObserverListaEdge observerListaEdge = new ObserverListaEdge();
        observerListaEdge.trigger();
        return observerListaEdge.suscribe();
    }
}
