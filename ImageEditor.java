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
import javax.swing.JTabbedPane; 
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;


public class ImageEditor extends JFrame {

    // ... (Variáveis de estado e componentes da UI permanecem os mesmos) ...
    private BufferedImage image;
    private BufferedImage grayImage; 

    private double rotation = 0;
    private double scale = 1.0;         
    private double viewZoom = 1.0;      
    private boolean flippedHorizontally = false;
    private boolean flippedVertically = false;
    private int grayscaleIntensity = 0;
    private int brightnessValue = 0;
    private int contrastValue = 100; 

    private final JTextField rotationField;
    private final JTextField scaleField;
    private final JPanel imagePanel;
    private final JSlider grayscaleSlider;
    private final JSlider brightnessSlider;
    private final JSlider contrastSlider;

    public ImageEditor() {
        super("Editor de Imagem Completo");

        // ... (O painel imagePanel permanece o mesmo) ...
        imagePanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (image != null) {
                    Graphics2D g2d = (Graphics2D) g.create();
                    g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

                    float contrastFactor = contrastValue / 100.0f;
                    RescaleOp combinedOp = new RescaleOp(contrastFactor, brightnessValue, null);

                    AffineTransform tx = new AffineTransform();
                    tx.translate(getWidth() / 2.0, getHeight() / 2.0);
                    tx.rotate(Math.toRadians(rotation));
                    
                    double scaleX = flippedHorizontally ? -1 : 1;
                    double scaleY = flippedVertically ? -1 : 1;

                    double totalViewScale = scale * viewZoom; 
                    tx.scale(totalViewScale * scaleX, totalViewScale * scaleY);
                    
                    tx.translate(-image.getWidth() / 2.0, -image.getHeight() / 2.0);
                    
                    g2d.transform(tx);
                    
                    g2d.drawImage(image, combinedOp, 0, 0);

                    if (grayscaleIntensity > 0) {
                        if (grayImage == null) {
                            grayImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);
                            ColorConvertOp op = new ColorConvertOp(ColorSpace.getInstance(ColorSpace.CS_GRAY), null);
                            op.filter(image, grayImage);
                        }
                        
                        float alpha = grayscaleIntensity / 100.0f;
                        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
                        g2d.drawImage(grayImage, combinedOp, 0, 0);
                        g2d.setComposite(AlphaComposite.SrcOver);
                    }
                    g2d.dispose();
                }
            }
        };

        setLayout(new BorderLayout());
        add(imagePanel, BorderLayout.CENTER);
        
        // ... (A Barra de Menu permanece a mesma) ...
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

        // --- Painel de Controle (Com Abas) ---
        JTabbedPane tabbedControlPanel = new JTabbedPane();
        
        // --- CRIAÇÃO DOS GRUPOS DE CONTROLE ---
        
        // ... (Grupos rotation, scale, flip, contrast, brightness, grayscale, zoom permanecem os mesmos) ...

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
        
        // *** MUDANÇA AQUI: Grupo de Filtros atualizado ***
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        JButton boxBlurButton = new JButton("Suavizar (Box)");      
        JButton gaussianBlurButton = new JButton("Suavizar (Gauss)"); 
        JButton sharpenButton = new JButton("Realçar (Sharpen)");   
        JButton edgeDetectButton = new JButton("Detectar Bordas");  
        JButton erosionButton = new JButton("Erosão");      
        JButton dilationButton = new JButton("Dilatação"); 
        JButton zhangSuenButton = new JButton("Afinar (Zhang-Suen)"); // NOVO
        JButton stentifordButton = new JButton("Afinar (Stentiford)");// NOVO
        
       filterPanel.add(boxBlurButton);      
        filterPanel.add(gaussianBlurButton); 
        filterPanel.add(sharpenButton);      
        filterPanel.add(edgeDetectButton);   
        filterPanel.add(erosionButton);   
        filterPanel.add(dilationButton); 
        filterPanel.add(zhangSuenButton); // ADICIONADO
        filterPanel.add(stentifordButton); // ADICIONADO
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
        
        // Grupo de Zoom da View
        JPanel zoomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        JButton zoomInButton = new JButton("Zoom +");
        JButton zoomOutButton = new JButton("Zoom -");
        JButton zoomResetButton = new JButton("Zoom 100%");
        zoomPanel.add(new JLabel("Zoom (View):"));
        zoomPanel.add(zoomOutButton);
        zoomPanel.add(zoomInButton);
        zoomPanel.add(zoomResetButton);
        

        // --- Criação dos painéis para cada Aba ---

        // --- Aba 1: Transformação ---
        JPanel transformTab = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 5));
        transformTab.add(rotationPanel);
        transformTab.add(scalePanel);
        transformTab.add(flipPanel);
        
        // --- Aba 2: Ajustes de Cor ---
        JPanel colorTab = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        colorTab.add(contrastPanel);
        colorTab.add(brightnessPanel);
        colorTab.add(grayscalePanel);
        
        // --- Aba 3: Filtros ---
       // --- Aba 3: Filtros ---
        JPanel filterTab = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        filterTab.add(filterPanel); // Painel de filtros já foi atualizado
        
       // ... (Adicionar abas e o 'mainControlPanel' permanece o mesmo) ...
        tabbedControlPanel.addTab("Transformação", transformTab);
        tabbedControlPanel.addTab("Ajustes de Cor", colorTab);
        tabbedControlPanel.addTab("Filtros", filterTab);
        
        JPanel mainControlPanel = new JPanel(new BorderLayout());
        mainControlPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        mainControlPanel.add(zoomPanel, BorderLayout.NORTH);
        mainControlPanel.add(tabbedControlPanel, BorderLayout.CENTER);
        add(mainControlPanel, BorderLayout.SOUTH);
        
        

        // --- Listeners (Ouvintes de Ações) ---
        
        // ... (Listeners de Rotação, Scale, Flip, Zoom e Sliders permanecem os mesmos) ...
        ActionListener rotateAction = e -> updateRotationFromTextField();
        rotationField.addActionListener(rotateAction);
        rotateButton.addActionListener(rotateAction);
        
        ActionListener scaleAction = e -> updateScaleFromTextField();
        scaleField.addActionListener(scaleAction);
        scaleButton.addActionListener(scaleAction);
        
        flipHorizontalButton.addActionListener(e -> { flippedHorizontally = !flippedHorizontally; imagePanel.repaint(); });
        flipVerticalButton.addActionListener(e -> { flippedVertically = !flippedVertically; imagePanel.repaint(); });
        
        zoomInButton.addActionListener(e -> updateViewZoom(1.25)); 
        zoomOutButton.addActionListener(e -> updateViewZoom(0.8)); 
        zoomResetButton.addActionListener(e -> updateViewZoom(0)); 

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

        // *** MUDANÇA AQUI: Listeners dos Filtros atualizados ***
        boxBlurButton.addActionListener(e -> applyFilter("boxblur"));         
        gaussianBlurButton.addActionListener(e -> applyFilter("gaussianblur")); 
        sharpenButton.addActionListener(e -> applyFilter("sharpen"));       
        edgeDetectButton.addActionListener(e -> applyFilter("edgedetect"));   
        
        erosionButton.addActionListener(e -> applyFilter("erosion"));
        dilationButton.addActionListener(e -> applyFilter("dilation"));
        
        zhangSuenButton.addActionListener(e -> applyFilter("zhangsuen")); // NOVO
        stentifordButton.addActionListener(e -> applyFilter("stentiford")); // NOVO
        
        // --- Configuração final da Janela ---
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        pack();
        setMinimumSize(getSize());
        setSize(900, 700);
        setLocationRelativeTo(null);
    }
    
    // ... (O resto da classe: openImage, saveImage, updateRotation, updateScale, updateViewZoom) ...
    // ... (NÃO HÁ MUDANÇAS NESSES MÉTODOS) ...
    private void openImage() {
        // ... (código existente) ...
        BufferedImage loadedImage = ImageFileManager.loadImage(this);
        if (loadedImage != null) {
            this.image = loadedImage;
            this.grayImage = null; 
            
            this.rotation = 0;
            this.scale = 1.0;
            this.viewZoom = 1.0; 
            this.flippedHorizontally = false;
            this.flippedVertically = false;
            this.grayscaleIntensity = 0;
            this.brightnessValue = 0;
            this.contrastValue = 100;
            
            rotationField.setText("0");
            scaleField.setText("100");
            grayscaleSlider.setValue(0);
            brightnessSlider.setValue(0);
            contrastSlider.setValue(100);
            
            imagePanel.repaint();
        }
    }

    private void saveImage() {
        // ... (código existente) ...
        if (image == null) {
            JOptionPane.showMessageDialog(this, "Nenhuma imagem para salvar!", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        BufferedImage processedImage = image;

        // Etapa 1: Espelhamento
        if (flippedHorizontally || flippedVertically) {
            int w = processedImage.getWidth();
            int h = processedImage.getHeight();
            int type = (processedImage.getType() == 0) ? BufferedImage.TYPE_INT_ARGB : processedImage.getType();
            BufferedImage flipped = new BufferedImage(w, h, type);
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

        // Etapas Finais: Redimensionar e Rotacionar
        BufferedImage resizedImage = ImageProcessor.resizeImage(processedImage, scale);
        BufferedImage finalImage = ImageProcessor.rotateImage(resizedImage, rotation);
         ImageFileManager.saveImage(this, finalImage);
       
    }

    private void updateRotationFromTextField() {
        // ... (código existente) ...
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

    private void updateScaleFromTextField() {
        // ... (código existente) ...
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
    
    /**
     * Aplica um filtro (convolução ou morfológico) 
     * diretamente na imagem principal.
     */
    private void applyFilter(String filterType) {
        if (image == null) {
            JOptionPane.showMessageDialog(this, "Carregue uma imagem primeiro!", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // *** MUDANÇA AQUI: Bloco if/else if atualizado ***
        if ("boxblur".equals(filterType)) {
            this.image = ImageProcessor.applyBoxBlur(this.image);
        } else if ("gaussianblur".equals(filterType)) {
            this.image = ImageProcessor.applyGaussianBlur(this.image);
        } else if ("sharpen".equals(filterType)) {
            this.image = ImageProcessor.applySharpen(this.image);
        } else if ("edgedetect".equals(filterType)) {
            this.image = ImageProcessor.applyEdgeDetection(this.image);
        } else if ("erosion".equals(filterType)) { 
            this.image = ImageProcessor.applyErosion(this.image);
        } else if ("dilation".equals(filterType)) { 
            this.image = ImageProcessor.applyDilation(this.image);
        } else if ("zhangsuen".equals(filterType)) { // NOVO
            this.image = ImageProcessor.applyZhangSuen(this.image);
        } else if ("stentiford".equals(filterType)) { // NOVO
            this.image = ImageProcessor.applyStentiford(this.image);
        }
        
        this.grayImage = null; // A imagem base mudou, invalida o cache de cinza
        imagePanel.repaint();
    }

    private void updateViewZoom(double factor) {
        // ... (código existente) ...
        if (image == null) return;

        if (factor == 0) {
            this.viewZoom = 1.0;
        } else {
            this.viewZoom *= factor;
        }
        
        if (this.viewZoom > 20.0) this.viewZoom = 20.0;     
        if (this.viewZoom < 0.05) this.viewZoom = 0.05;   

        imagePanel.repaint();
    }


    
}