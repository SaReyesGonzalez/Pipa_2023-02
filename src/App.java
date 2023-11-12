import javax.swing.SwingUtilities;

public class App {
    public static void main(String[] args) throws Exception {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                Cliping.instalarObservers(new FileSelectorApp());
            }
        });
    }
}
