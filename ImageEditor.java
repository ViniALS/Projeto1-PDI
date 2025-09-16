// Arquivo: ImageEditor.java
import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.color.ColorSpace;
import java.awt.event.ActionListener;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import java.awt.image.RescaleOp;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;


public class ImageEditor extends JFrame {

    // Variáveis de estado da imagem e transformações
    private BufferedImage image;
    private BufferedImage grayImage; // Cache para a versão em escala de cinza

    private double rotation = 0;
    private double scale = 1.0;
    private boolean flippedHorizontally = false;
    private boolean flippedVertically = false;
    private int grayscaleIntensity = 0;
    private int brightnessValue = 0;
    private int contrastValue = 100; // 100 é o valor neutro (fator 1.0)

    // Componentes da UI
    private final JTextField rotationField;
    private final JTextField scaleField;
    private final JPanel imagePanel;
    private final JSlider grayscaleSlider;
    private final JSlider brightnessSlider;
    private final JSlider contrastSlider;

    public ImageEditor() {
        super("Editor de Imagem Completo");

        // Painel principal onde a imagem é desenhada
        imagePanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (image != null) {
                    Graphics2D g2d = (Graphics2D) g.create();
                    g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

                    // Operação combinada de Brilho e Contraste
                    float contrastFactor = contrastValue / 100.0f;
                    RescaleOp combinedOp = new RescaleOp(contrastFactor, brightnessValue, null);

                    // Transformação geométrica (girar, escalar, espelhar)
                    AffineTransform tx = new AffineTransform();
                    tx.translate(getWidth() / 2.0, getHeight() / 2.0);
                    tx.rotate(Math.toRadians(rotation));
                    double scaleX = flippedHorizontally ? -1 : 1;
                    double scaleY = flippedVertically ? -1 : 1;
                    tx.scale(scale * scaleX, scale * scaleY);
                    tx.translate(-image.getWidth() / 2.0, -image.getHeight() / 2.0);
                    
                    g2d.transform(tx);
                    
                    // Desenha a imagem base aplicando brilho e contraste
                    g2d.drawImage(image, combinedOp, 0, 0);

                    // Aplica o efeito de escala de cinza por cima, se necessário
                    if (grayscaleIntensity > 0) {
                        if (grayImage == null) {
                            grayImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);
                            ColorConvertOp op = new ColorConvertOp(ColorSpace.getInstance(ColorSpace.CS_GRAY), null);
                            op.filter(image, grayImage);
                        }
                        
                        float alpha = grayscaleIntensity / 100.0f;
                        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
                        
                        // Desenha a imagem cinza por cima, também com brilho e contraste
                        g2d.drawImage(grayImage, combinedOp, 0, 0);
                        
                        g2d.setComposite(AlphaComposite.SrcOver);
                    }
                    g2d.dispose();
                }
            }
        };

        setLayout(new BorderLayout());
        add(imagePanel, BorderLayout.CENTER);
        
        // --- Barra de Menu ---
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

        // --- Painel de Controle ---
        JPanel controlPanel = new JPanel(new BorderLayout());
        controlPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        
        JPanel topControls = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        JPanel bottomControls = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));

        // Grupo de Rotação
        JPanel rotationPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        rotationField = new JTextField("0", 4);
        JButton rotateButton = new JButton("Rotacionar");
        rotationPanel.add(new JLabel("Rotação:"));
        rotationPanel.add(rotationField);
        rotationPanel.add(rotateButton);
        
        // Grupo de Tamanho
        JPanel scalePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        scaleField = new JTextField("100", 4);
        JButton scaleButton = new JButton("Aplicar Tamanho");
        scalePanel.add(new JLabel("Tamanho:"));
        scalePanel.add(scaleField);
        scalePanel.add(scaleButton);

        // Grupo de Espelhamento
        JPanel flipPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        JButton flipHorizontalButton = new JButton("Espelhar H");
        JButton flipVerticalButton = new JButton("Espelhar V");
        flipPanel.add(flipHorizontalButton);
        flipPanel.add(flipVerticalButton);

        // Grupo de Contraste
        JPanel contrastPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        contrastPanel.add(new JLabel("Contraste:"));
        contrastSlider = new JSlider(0, 200, 100);
        contrastPanel.add(contrastSlider);

        // Grupo de Brilho
        JPanel brightnessPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        brightnessPanel.add(new JLabel("Brilho:"));
        brightnessSlider = new JSlider(-100, 100, 0);
        brightnessPanel.add(brightnessSlider);

        // Grupo da Escala de Cinza
        JPanel grayscalePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        grayscalePanel.add(new JLabel("Escala de Cinza:"));
        grayscaleSlider = new JSlider(0, 100, 0);
        grayscalePanel.add(grayscaleSlider);
        
        // Adicionando os grupos aos painéis
        topControls.add(rotationPanel);
        topControls.add(scalePanel);
        topControls.add(flipPanel);
        bottomControls.add(contrastPanel);
        bottomControls.add(brightnessPanel);
        bottomControls.add(grayscalePanel);
        
        controlPanel.add(topControls, BorderLayout.NORTH);
        controlPanel.add(bottomControls, BorderLayout.CENTER);
        add(controlPanel, BorderLayout.SOUTH);
        
        // --- Listeners (Ouvintes de Ações) ---
        ActionListener rotateAction = e -> updateRotationFromTextField();
        rotationField.addActionListener(rotateAction);
        rotateButton.addActionListener(rotateAction);
        
        ActionListener scaleAction = e -> updateScaleFromTextField();
        scaleField.addActionListener(scaleAction);
        scaleButton.addActionListener(scaleAction);
        
        flipHorizontalButton.addActionListener(e -> { flippedHorizontally = !flippedHorizontally; imagePanel.repaint(); });
        flipVerticalButton.addActionListener(e -> { flippedVertically = !flippedVertically; imagePanel.repaint(); });

        contrastSlider.addChangeListener(e -> {
            contrastValue = contrastSlider.getValue();
            imagePanel.repaint();
        });

        brightnessSlider.addChangeListener(e -> {
            brightnessValue = brightnessSlider.getValue();
            imagePanel.repaint();
        });

        grayscaleSlider.addChangeListener(e -> {
            grayscaleIntensity = grayscaleSlider.getValue();
            imagePanel.repaint();
        });
        
        // --- Configuração final da Janela ---
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        pack();
        setMinimumSize(getSize());
        setSize(900, 700);
        setLocationRelativeTo(null);
    }
    
    /**
     * Carrega uma nova imagem, reseta todos os controles e atualiza a tela.
     */
    private void openImage() {
        BufferedImage loadedImage = ImageFileManager.loadImage(this);
        if (loadedImage != null) {
            this.image = loadedImage;
            this.grayImage = null; // Limpa o cache da imagem cinza
            
            // Reseta todos os valores de estado para o padrão
            this.rotation = 0;
            this.scale = 1.0;
            this.flippedHorizontally = false;
            this.flippedVertically = false;
            this.grayscaleIntensity = 0;
            this.brightnessValue = 0;
            this.contrastValue = 100;
            
            // Reseta todos os controles da UI para o padrão
            rotationField.setText("0");
            scaleField.setText("100");
            grayscaleSlider.setValue(0);
            brightnessSlider.setValue(0);
            contrastSlider.setValue(100);
            
            imagePanel.repaint();
        }
    }

    /**
     * Aplica todas as transformações atuais na imagem e a salva.
     */
    private void saveImage() {
        if (image == null) {
            JOptionPane.showMessageDialog(this, "Nenhuma imagem para salvar!", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Começa com a imagem original
        BufferedImage processedImage = image;

        // Etapa 1: Espelhamento
        if (flippedHorizontally || flippedVertically) {
            int w = processedImage.getWidth();
            int h = processedImage.getHeight();
            BufferedImage flipped = new BufferedImage(w, h, image.getType());
            Graphics2D g2 = flipped.createGraphics();
            AffineTransform at = new AffineTransform();
            double scaleX = flippedHorizontally ? -1 : 1;
            double scaleY = flippedVertically ? -1 : 1;
            if (flippedHorizontally) at.translate(w, 0);
            if (flippedVertically) at.translate(0, h);
            at.scale(scaleX, scaleY);
            g2.setTransform(at);
            g2.drawImage(processedImage, 0, 0, null);
            g2.dispose();
            processedImage = flipped;
        }

        // Etapa 2: Brilho e Contraste
        if (brightnessValue != 0 || contrastValue != 100) {
            float contrastFactor = contrastValue / 100.0f;
            RescaleOp op = new RescaleOp(contrastFactor, brightnessValue, null);
            BufferedImage adjustedImage = new BufferedImage(
                processedImage.getWidth(), processedImage.getHeight(), processedImage.getType());
            op.filter(processedImage, adjustedImage);
            processedImage = adjustedImage;
        }

        // Etapa 3: Escala de Cinza
        if (grayscaleIntensity > 0) {
            BufferedImage tempGray = new BufferedImage(
                processedImage.getWidth(), processedImage.getHeight(), BufferedImage.TYPE_INT_ARGB);
            new ColorConvertOp(ColorSpace.getInstance(ColorSpace.CS_GRAY), null).filter(processedImage, tempGray);
            
            BufferedImage blendedImage = new BufferedImage(
                processedImage.getWidth(), processedImage.getHeight(), BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2 = blendedImage.createGraphics();
            g2.drawImage(processedImage, 0, 0, null);
            float alpha = grayscaleIntensity / 100.0f;
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
            g2.drawImage(tempGray, 0, 0, null);
            g2.dispose();
            processedImage = blendedImage;
        }

        // Etapas Finais: Redimensionar e Rotacionar (usando suas classes externas)
        BufferedImage resizedImage = ImageProcessor.resizeImage(processedImage, scale);
        BufferedImage finalImage = ImageProcessor.rotateImage(resizedImage, rotation);
         ImageFileManager.saveImage(this, finalImage);
       
    }

    /**
     * Lê, valida e aplica o valor de rotação do campo de texto.
     */
    private void updateRotationFromTextField() {
        if (image == null) return;
        try {
            double newRotation = Double.parseDouble(rotationField.getText());
            if (newRotation >= 0 && newRotation <= 360) {
                this.rotation = newRotation;
                imagePanel.repaint();
            } else {
                JOptionPane.showMessageDialog(this, "Por favor, insira um ângulo entre 0 e 360.", "Valor Inválido", JOptionPane.ERROR_MESSAGE);
                rotationField.setText(String.valueOf(Math.round(this.rotation)));
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Por favor, insira um número válido para o ângulo.", "Entrada Inválida", JOptionPane.ERROR_MESSAGE);
            rotationField.setText(String.valueOf(Math.round(this.rotation)));
        }
    }

    /**
     * Lê, valida e aplica o valor de tamanho do campo de texto.
     */
    private void updateScaleFromTextField() {
        if (image == null) return;
        try {
            double newScalePercent = Double.parseDouble(scaleField.getText());
            if (newScalePercent >= 50 && newScalePercent <= 500) {
                this.scale = newScalePercent / 100.0;
                imagePanel.repaint();
            } else {
                JOptionPane.showMessageDialog(this, "Por favor, insira um tamanho entre 50 e 500.", "Valor Inválido", JOptionPane.ERROR_MESSAGE);
                scaleField.setText(String.valueOf(Math.round(this.scale * 100)));
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Por favor, insira um número válido para o tamanho.", "Entrada Inválida", JOptionPane.ERROR_MESSAGE);
            scaleField.setText(String.valueOf(Math.round(this.scale * 100)));
        }
    }


}
