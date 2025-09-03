import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

public class ImageEditor extends JFrame {

    private BufferedImage image;
    private double rotation = 0;
    private Timer rotateTimer;

    public ImageEditor() {
        super("Editor de Imagem");

        // Painel para exibir a imagem
        JPanel imagePanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (image != null) {
                    Graphics2D g2d = (Graphics2D) g;
                    AffineTransform transform = new AffineTransform();
                    transform.translate(getWidth() / 2, getHeight() / 2);
                    transform.rotate(Math.toRadians(rotation));
                    transform.translate(-image.getWidth() / 2, -image.getHeight() / 2);
                    g2d.drawImage(image, transform, null);
                }
            }
        };

        setLayout(new BorderLayout());
        add(imagePanel, BorderLayout.CENTER);

        // Barra de menu
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("Arquivo");
        JMenuItem openItem = new JMenuItem("Abrir");
        JMenuItem saveItem = new JMenuItem("Salvar");

        openItem.addActionListener(e -> abrirImagem(imagePanel));
        saveItem.addActionListener(e -> salvarImagem());

        fileMenu.add(openItem);
        fileMenu.add(saveItem);
        menuBar.add(fileMenu);
        setJMenuBar(menuBar);

        // Painel de controle
        JPanel controlPanel = new JPanel();
        JButton rotateLeft = new JButton("↺ -1°");
        JButton rotateRight = new JButton("↻ +1°");

        JLabel angleLabel = new JLabel("Ângulo: 0°");
        Runnable updateAngleLabel = () -> {
            int anguloAtual = ((int) rotation % 360 + 360) % 360;
            angleLabel.setText("Ângulo: " + anguloAtual + "°");
        };

        // Rotação para a esquerda
        rotateLeft.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                startRotationTimer(-1, updateAngleLabel);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                stopRotationTimer();
            }
        });

        // Rotação para a direita
        rotateRight.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                startRotationTimer(1, updateAngleLabel);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                stopRotationTimer();
            }
        });

        controlPanel.add(rotateLeft);
        controlPanel.add(rotateRight);
        controlPanel.add(angleLabel);
        add(controlPanel, BorderLayout.SOUTH);

        setSize(800, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    // Inicia o timer de rotação contínua
    private void startRotationTimer(int direction, Runnable updateLabel) {
        if (rotateTimer != null && rotateTimer.isRunning()) {
            rotateTimer.stop();
        }

        rotateTimer = new Timer(20, e -> {
            rotation += direction;
            repaint();
        
            int anguloAtual = ((int) rotation % 360 + 360) % 360; // normaliza
            updateLabel.run(); // ainda mantém a chamada original
        });

        rotateTimer.start();
    }

    private void stopRotationTimer() {
        if (rotateTimer != null && rotateTimer.isRunning()) {
            rotateTimer.stop();
        }
    }

    private void abrirImagem(JPanel imagePanel) {
        JFileChooser fileChooser = new JFileChooser();
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                image = ImageIO.read(fileChooser.getSelectedFile());
                rotation = 0;
                imagePanel.repaint();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Erro ao abrir imagem!");
            }
        }
    }

    private void salvarImagem() {
        if (image != null) {
            JFileChooser fileChooser = new JFileChooser();
            if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                try {
                    double angle = Math.toRadians(rotation);
                    int width = image.getWidth();
                    int height = image.getHeight();

                    // Calcula o tamanho da imagem após rotação
                    int newWidth = (int) Math.abs(width * Math.cos(angle)) + (int) Math.abs(height * Math.sin(angle));
                    int newHeight = (int) Math.abs(height * Math.cos(angle)) + (int) Math.abs(width * Math.sin(angle));

                    BufferedImage novaImagem = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_ARGB);
                    Graphics2D g2d = novaImagem.createGraphics();

                    AffineTransform transform = new AffineTransform();
                    transform.translate(newWidth / 2, newHeight / 2);
                    transform.rotate(angle);
                    transform.translate(-width / 2, -height / 2);

                    g2d.drawImage(image, transform, null);
                    g2d.dispose();

                    File output = fileChooser.getSelectedFile();
                    if (!output.getName().toLowerCase().endsWith(".png")) {
                        output = new File(output.getAbsolutePath() + ".png");
                    }

                    ImageIO.write(novaImagem, "png", output);
                    JOptionPane.showMessageDialog(this, "Imagem salva com sucesso!");
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Erro ao salvar imagem!");
                }
            }
        }
    }

    public static void main(String[] args) {
        try {
            // Look and Feel do Windows
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(ImageEditor::new);
    }
}
