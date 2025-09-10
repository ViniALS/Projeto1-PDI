// Arquivo: ImageEditor.java
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import javax.swing.*;

public class ImageEditor extends JFrame {

    private BufferedImage image;
    private double rotation = 0;
    private double scale = 1.0; // NOVO: Variável para controlar a escala

    private Timer rotateTimer;
    private final JPanel imagePanel;
    private final JLabel angleLabel;
    private final JLabel scaleLabel; // NOVO: Label para o tamanho

    public ImageEditor() {
        super("Editor de Imagem");

        // Painel para exibir a imagem (lógica de pintura ALTERADA)
        imagePanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (image != null) {
                    Graphics2D g2d = (Graphics2D) g.create();
                    
                    // Melhora a qualidade da renderização no painel
                    g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                    
                    AffineTransform transform = new AffineTransform();
                    transform.translate(getWidth() / 2.0, getHeight() / 2.0);
                    transform.rotate(Math.toRadians(rotation));
                    transform.scale(scale, scale); // ALTERADO: Aplica a escala
                    transform.translate(-image.getWidth() / 2.0, -image.getHeight() / 2.0);
                    
                    g2d.drawImage(image, transform, null);
                    g2d.dispose();
                }
            }
        };

        setLayout(new BorderLayout());
        add(imagePanel, BorderLayout.CENTER);
        
        // ... (código da barra de menu continua o mesmo) ...
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("Arquivo");
        JMenuItem openItem = new JMenuItem("Abrir");
        JMenuItem saveItem = new JMenuItem("Salvar");
        openItem.addActionListener(e -> openImage());
        saveItem.addActionListener(e -> saveImage());
        fileMenu.add(openItem);
        fileMenu.add(saveItem);
        menuBar.add(fileMenu);
        setJMenuBar(menuBar);


        // Painel de controle (ALTERADO para incluir botões de zoom)
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 5));
        
        // Controles de Rotação
        JButton rotateLeft = new JButton("↺");
        JButton rotateRight = new JButton("↻");
        angleLabel = new JLabel("Ângulo: 0°");
        setupRotationButton(rotateLeft, -1);
        setupRotationButton(rotateRight, 1);
        
        // NOVOS Controles de Tamanho
        JButton zoomOutButton = new JButton("-");
        JButton zoomInButton = new JButton("+");
        scaleLabel = new JLabel("Tamanho: 100%");
        zoomOutButton.addActionListener(e -> updateScale(0.9)); // Fator de 90%
        zoomInButton.addActionListener(e -> updateScale(1.1)); // Fator de 110%

        // Adiciona os componentes ao painel de controle
        controlPanel.add(new JLabel("Rotação:"));
        controlPanel.add(rotateLeft);
        controlPanel.add(rotateRight);
        controlPanel.add(angleLabel);

        controlPanel.add(new JSeparator(SwingConstants.VERTICAL));

        controlPanel.add(new JLabel("Tamanho:"));
        controlPanel.add(zoomOutButton);
        controlPanel.add(zoomInButton);
        controlPanel.add(scaleLabel);

        add(controlPanel, BorderLayout.SOUTH);

        setSize(800, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
    }
    
    // Método openImage (ALTERADO para resetar a escala)
    private void openImage() {
        BufferedImage loadedImage = ImageFileManager.loadImage(this);
        if (loadedImage != null) {
            this.image = loadedImage;
            this.rotation = 0;
            this.scale = 1.0; // ALTERADO: Reseta a escala ao abrir nova imagem
            updateAngleLabel();
            updateScaleLabel(); // ALTERADO: Atualiza o novo label
            imagePanel.repaint();
        }
    }

    // Método saveImage (ALTERADO para aplicar escala ANTES da rotação)
    private void saveImage() {
        if (image != null) {
            // 1. Primeiro, cria uma imagem com o tamanho correto (escala)
            BufferedImage resizedImage = ImageProcessor.resizeImage(image, scale);
            // 2. Depois, rotaciona a imagem já redimensionada
            BufferedImage finalImage = ImageProcessor.rotateImage(resizedImage, rotation);
            // 3. Pede para o ImageFileManager salvar o resultado final
            ImageFileManager.saveImage(this, finalImage);
        } else {
            JOptionPane.showMessageDialog(this, "Nenhuma imagem para salvar!", "Aviso", JOptionPane.WARNING_MESSAGE);
        }
    }
    
    // NOVO método para atualizar a escala e a UI
    private void updateScale(double factor) {
        if (image == null) return;
        
        // Define um limite mínimo e máximo para o zoom para evitar problemas
        double newScale = scale * factor;
        if (newScale < 0.1 || newScale > 10.0) {
            return; // Impede zoom muito pequeno ou muito grande
        }

        scale = newScale;
        updateScaleLabel();
        imagePanel.repaint();
    }

    // NOVO método para atualizar o label da escala
    private void updateScaleLabel() {
        scaleLabel.setText(Math.round(scale * 100) + "%");
    }

    // ... (os outros métodos: setupRotationButton, startRotationTimer, stopRotationTimer, updateAngleLabel continuam aqui sem alterações) ...
    private void setupRotationButton(JButton button, int direction) {
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) { startRotationTimer(direction); }
            @Override
            public void mouseReleased(MouseEvent e) { stopRotationTimer(); }
        });
    }

    private void startRotationTimer(int direction) {
        stopRotationTimer();
        rotateTimer = new Timer(20, e -> {
            rotation += direction;
            updateAngleLabel();
            imagePanel.repaint();
        });
        rotateTimer.start();
    }

    private void stopRotationTimer() {
        if (rotateTimer != null && rotateTimer.isRunning()) {
            rotateTimer.stop();
        }
    }

    private void updateAngleLabel() {
        long normalizedAngle = Math.round(rotation) % 360;
        if (normalizedAngle < 0) normalizedAngle += 360;
        angleLabel.setText(normalizedAngle + "°");
    }
}
