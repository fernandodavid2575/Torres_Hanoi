import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.Stack;

public class TorresDeHanoi extends JFrame {
    private JTextField discosField, torresField;
    private JButton resolverButton, reiniciarButton;
    private JLabel movimientosLabel, mensajeLabel;
    private int movimientos = 0;
    private int cantidadDiscos = 0;
    private int cantidadTorres = 3;
    private Stack<Integer>[] torres;
    private JPanel torresPanel;
    private volatile boolean algoritmoEnEjecucion = false;

    public TorresDeHanoi() {
        setTitle("Torres de Hanói");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        torres = new Stack[cantidadTorres];
        for (int i = 0; i < cantidadTorres; i++) {
            torres[i] = new Stack<>();
        }

        JPanel topPanel = new JPanel();
        topPanel.add(new JLabel("Discos:"));
        discosField = new JTextField(5);
        topPanel.add(discosField);

        topPanel.add(new JLabel("Torres:"));
        torresField = new JTextField(5);
        topPanel.add(torresField);

        resolverButton = new JButton("Resolver");
        reiniciarButton = new JButton("Reiniciar");
        topPanel.add(resolverButton);
        topPanel.add(reiniciarButton);
        add(topPanel, BorderLayout.NORTH);

        torresPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                dibujarTorres(g);
            }
        };
        torresPanel.setBackground(Color.WHITE);
        add(torresPanel, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel();
        movimientosLabel = new JLabel("Movimientos: 0");
        mensajeLabel = new JLabel("Ingresa la cantidad de discos y torres.");
        bottomPanel.add(movimientosLabel);
        bottomPanel.add(mensajeLabel);
        add(bottomPanel, BorderLayout.SOUTH);

        resolverButton.addActionListener(e -> resolverJuego());
        reiniciarButton.addActionListener(e -> reiniciarJuego());

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                torresPanel.repaint();
            }
        });

        setVisible(true);
    }

    private void inicializarTorres() {
        for (int i = 0; i < cantidadTorres; i++) {
            torres[i].clear();
        }
        for (int i = cantidadDiscos; i > 0; i--) {
            torres[0].push(i);
        }
        repaint();
    }

    private void dibujarTorres(Graphics g) {
        int anchoPanel = torresPanel.getWidth();
        int altoPanel = torresPanel.getHeight();
        int anchoTorre = anchoPanel / (cantidadTorres + 1);
        int espacioEntreTorres = anchoTorre / (cantidadTorres + 1);

        for (int i = 0; i < cantidadTorres; i++) {
            int xTorre = (i + 1) * espacioEntreTorres + i * anchoTorre;

            g.setColor(Color.BLACK);
            g.fillRect(xTorre + anchoTorre / 2 - 10, 50, 20, altoPanel - 100);

            Stack<Integer> torre = torres[i];
            int yDisco = altoPanel - 80;
            for (int j = 0; j < torre.size(); j++) {
                int disco = torre.get(j);
                int anchoDisco = 20 + (disco * 10);
                int xDisco = xTorre + (anchoTorre - anchoDisco) / 2;

                g.setColor(Color.ORANGE);
                g.fillRect(xDisco, yDisco, anchoDisco, 20);
                g.setColor(Color.BLACK);
                g.drawRect(xDisco, yDisco, anchoDisco, 20);

                yDisco -= 25;
            }
        }
    }

    private void resolverJuego() {
        try {
            cantidadDiscos = Integer.parseInt(discosField.getText());
            cantidadTorres = Integer.parseInt(torresField.getText());

            if (cantidadDiscos < 1 || cantidadTorres < 3) {
                mensajeLabel.setText("Mínimo 1 disco y 3 torres.");
                return;
            }

            torres = new Stack[cantidadTorres];
            for (int i = 0; i < cantidadTorres; i++) {
                torres[i] = new Stack<>();
            }

            inicializarTorres();
            movimientos = 0;
            movimientosLabel.setText("Movimientos: " + movimientos);

            int movimientosMinimos = (int) (Math.pow(2, cantidadDiscos) - 1);
            mensajeLabel.setText("Movimientos para que termine: " + movimientosMinimos);

            algoritmoEnEjecucion = true;
            new Thread(() -> resolverHanoi(cantidadDiscos, 0, cantidadTorres - 1, 1)).start();

        } catch (NumberFormatException e) {
            mensajeLabel.setText("Entrada inválida. Ingresa números válidos.");
        }
    }

    private void resolverHanoi(int discos, int origen, int destino, int auxiliar) {
        if (!algoritmoEnEjecucion) return;

        if (discos == 1) {
            moverDisco(origen, destino);
        } else {
            resolverHanoi(discos - 1, origen, auxiliar, destino);
            moverDisco(origen, destino);
            resolverHanoi(discos - 1, auxiliar, destino, origen);
        }

        if (discos == cantidadDiscos) {
            mensajeLabel.setText("¡Juego completado!");
        }
    }

    private void moverDisco(int origen, int destino) {
        try {
            Thread.sleep(500);
            SwingUtilities.invokeLater(() -> {
                if (!algoritmoEnEjecucion) {
                    return;
                }

                if (torres[origen].isEmpty()) {
                    return;
                }

                int disco = torres[origen].pop();
                if (!torres[destino].isEmpty() && torres[destino].peek() < disco) {
                    torres[origen].push(disco);
                    mensajeLabel.setText("Error: No se puede colocar un disco mayor sobre uno menor.");
                } else {
                    torres[destino].push(disco);
                    movimientos++;
                    movimientosLabel.setText("Movimientos: " + movimientos);
                }
                repaint();
            });
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void reiniciarJuego() {
        algoritmoEnEjecucion = false;

        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        for (int i = 0; i < cantidadTorres; i++) {
            torres[i].clear();
        }

        movimientos = 0;
        movimientosLabel.setText("Movimientos: 0");

        repaint();

        mensajeLabel.setText("Juego reiniciado. Ingresa nuevos valores para discos y torres.");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new TorresDeHanoi());
    }
}