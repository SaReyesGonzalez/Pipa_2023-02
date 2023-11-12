import java.awt.*;
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
        int[] dataCenterMouser = (new ObserverDataMouse()).suscribe();

        ventanaWidthHeightCenterXY[0] = fileSelectorApp.graficarPanel.getWidth();
        ventanaWidthHeightCenterXY[1] = fileSelectorApp.graficarPanel.getHeight();

        // testeando si son los valores correctos
        ventanaWidthHeightCenterXY[2] = dataCenterMouser[0];
        ventanaWidthHeightCenterXY[3] = dataCenterMouser[1];
    }

    public int[] suscribe() {
        return ventanaWidthHeightCenterXY;
    }
}

class ObserverDataMouse {

    public static FileSelectorApp fileSelectorApp;
    private static int x = 20;
    private static int y = 20;

    private boolean firstSuscribe = true;


    public int[] suscribe() {


        return new int[]{x, y};
    }

    public void trigger(int deltaX, int deltaY) {
        x -= deltaX;
        y -= deltaY;
    }
}
public abstract  class  Cliping {


    public static  void cliping(Graphics g) {
        int[] WidtHEightXY = extractDataVentana();

        // TEST PORPOUSE
        g.setColor(Color.GREEN);
        g.drawRect(WidtHEightXY[2] , WidtHEightXY[3] , WidtHEightXY[0] , WidtHEightXY[1] );
        g.setColor(Color.GREEN);
        g.setClip(WidtHEightXY[2] , WidtHEightXY[3] , WidtHEightXY[0] , WidtHEightXY[1] );
    }



















    public static void instalarObservers(FileSelectorApp fileSelectorApp) {
        ObserverListaEdge.fileSelectorApp = fileSelectorApp;
        ObserverVentana.fileSelectorApp = fileSelectorApp;
        ObserverDataMouse.fileSelectorApp = fileSelectorApp;
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
