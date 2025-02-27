import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        // Crear una ventana
        JFrame frame = new JFrame("Mi Primera Interfaz Gráfica");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 300);

        // Crear un botón
        JButton button = new JButton("Haz clic aquí");
        frame.getContentPane().add(button); // Añadir el botón a la ventana

        // Mostrar la ventana
        frame.setVisible(true);
    }
}
