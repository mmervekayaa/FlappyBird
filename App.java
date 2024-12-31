import javax.swing.*;

public class App {
    public static void main(String[] args) throws Exception {
        int tahtaGenisligi = 360;
        int tahtaYuksekligi = 640;

        JFrame cerceve = new JFrame("Flappy Bird");
        // cerceve.setVisible(true);
        cerceve.setSize(tahtaGenisligi, tahtaYuksekligi);
        cerceve.setLocationRelativeTo(null);
        cerceve.setResizable(false);
        cerceve.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        FlappyBird flappyBird = new FlappyBird();
        cerceve.add(flappyBird);
        cerceve.pack();
        flappyBird.requestFocus();
        cerceve.setVisible(true);
    }
}
