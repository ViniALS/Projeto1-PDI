// Arquivo: ImageProcessor.java
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.util.ArrayList; // Novo
import java.util.List; // Novo

public class ImageProcessor {

    // ... (rotateImage e resizeImage permanecem os mesmos) ...
    public static BufferedImage rotateImage(BufferedImage originalImage, double degrees) {
        // ... (código existente) ...
        if (originalImage == null) {
            return null;
        }

        double angle = Math.toRadians(degrees);
        int width = originalImage.getWidth();
        int height = originalImage.getHeight();

        int newWidth = (int) Math.abs(width * Math.cos(angle)) + (int) Math.abs(height * Math.sin(angle));
        int newHeight = (int) Math.abs(height * Math.cos(angle)) + (int) Math.abs(width * Math.sin(angle));

        BufferedImage rotatedImage = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = rotatedImage.createGraphics();

        AffineTransform transform = new AffineTransform();
        transform.translate(newWidth / 2.0, newHeight / 2.0);
        transform.rotate(angle);
        transform.translate(-width / 2.0, -height / 2.0);

        g2d.drawImage(originalImage, transform, null);
        g2d.dispose();

        return rotatedImage;
    }

    public static BufferedImage resizeImage(BufferedImage originalImage, double scaleFactor) {
        // ... (código existente) ...
        if (originalImage == null || scaleFactor <= 0) {
            return originalImage;
        }

        int newWidth = (int) (originalImage.getWidth() * scaleFactor);
        int newHeight = (int) (originalImage.getHeight() * scaleFactor);
        
        if (newWidth < 1 || newHeight < 1) {
            return originalImage;
        }

        BufferedImage resizedImage = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = resizedImage.createGraphics();

        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.drawImage(originalImage, 0, 0, newWidth, newHeight, null);
        g2d.dispose();

        return resizedImage;
    }

    // --- MÉTODOS DE FILTRO ATUALIZADOS ---

    /**
     * Passa-Baixa Tipo 1: Box Blur (Média Simples)
     * RENOMEADO DE applyLowPassFilter
     */
    public static BufferedImage applyBoxBlur(BufferedImage originalImage) {
        if (originalImage == null) return null;

        // Kernel de blur (média 3x3)
        float[] blurKernel = {
            1/9f, 1/9f, 1/9f,
            1/9f, 1/9f, 1/9f,
            1/9f, 1/9f, 1/9f
        };
        
        return applyConvolution(originalImage, blurKernel);
    }

    /**
     * Passa-Alta Tipo 1: Sharpen (Realce)
     * RENOMEADO DE applyHighPassFilter
     */
    public static BufferedImage applySharpen(BufferedImage originalImage) {
        if (originalImage == null) return null;

        // Kernel de sharpen (realce)
        float[] sharpenKernel = {
             0f, -1f,  0f,
            -1f,  5f, -1f,
             0f, -1f,  0f
        };
        
        return applyConvolution(originalImage, sharpenKernel);
    }
    
    /**
     * NOVO - Passa-Baixa Tipo 2: Gaussian Blur (Suavização Ponderada)
     */
    public static BufferedImage applyGaussianBlur(BufferedImage originalImage) {
        if (originalImage == null) return null;
        
        // Kernel Gaussiano 3x3 (aproximação)
        float[] gaussianKernel = {
            1/16f, 2/16f, 1/16f,
            2/16f, 4/16f, 2/16f,
            1/16f, 2/16f, 1/16f
        };
        
        return applyConvolution(originalImage, gaussianKernel);
    }

    /**
     * NOVO - Passa-Alta Tipo 2: Detecção de Bordas (Laplacian)
     */
    public static BufferedImage applyEdgeDetection(BufferedImage originalImage) {
        if (originalImage == null) return null;

        // Kernel Laplacian (8 vizinhos)
        float[] edgeKernel = {
            -1f, -1f, -1f,
            -1f,  8f, -1f,
            -1f, -1f, -1f
        };
        
        return applyConvolution(originalImage, edgeKernel);
    }

    /**
     * Método utilitário para aplicar qualquer kernel de convolução 3x3
     */
    private static BufferedImage applyConvolution(BufferedImage originalImage, float[] kernelData) {
        Kernel kernel = new Kernel(3, 3, kernelData);
        ConvolveOp op = new ConvolveOp(kernel, ConvolveOp.EDGE_NO_OP, null);
        
        int type = (originalImage.getType() == 0) ? BufferedImage.TYPE_INT_ARGB : originalImage.getType();
        BufferedImage resultImage = new BufferedImage(originalImage.getWidth(), originalImage.getHeight(), type);
        
        op.filter(originalImage, resultImage);
        return resultImage;
    }


    // ... (Métodos applyErosion e applyDilation permanecem os mesmos) ...
    
    public static BufferedImage applyErosion(BufferedImage originalImage) {
        // ... (código existente) ...
        if (originalImage == null) return null;

        int width = originalImage.getWidth();
        int height = originalImage.getHeight();
        
        BufferedImage erodedImage = new BufferedImage(width, height, originalImage.getType());
        Graphics2D g = erodedImage.createGraphics();
        g.drawImage(originalImage, 0, 0, null);
        g.dispose();

        for (int y = 1; y < height - 1; y++) {
            for (int x = 1; x < width - 1; x++) {
                int minA = 255, minR = 255, minG = 255, minB = 255;
                for (int ky = -1; ky <= 1; ky++) {
                    for (int kx = -1; kx <= 1; kx++) {
                        int rgb = originalImage.getRGB(x + kx, y + ky);
                        int a = (rgb >> 24) & 0xFF;
                        int r = (rgb >> 16) & 0xFF;
                        int gr = (rgb >> 8) & 0xFF;
                        int b = rgb & 0xFF;

                        minA = Math.min(minA, a);
                        minR = Math.min(minR, r);
                        minG = Math.min(minG, gr);
                        minB = Math.min(minB, b);
                    }
                }
                int newRgb = (minA << 24) | (minR << 16) | (minG << 8) | minB;
                erodedImage.setRGB(x, y, newRgb);
            }
        }
        return erodedImage;
    }
    
    public static BufferedImage applyDilation(BufferedImage originalImage) {
        // ... (código existente) ...
        if (originalImage == null) return null;

        int width = originalImage.getWidth();
        int height = originalImage.getHeight();

        BufferedImage dilatedImage = new BufferedImage(width, height, originalImage.getType());
        Graphics2D g = dilatedImage.createGraphics();
        g.drawImage(originalImage, 0, 0, null);
        g.dispose();

        for (int y = 1; y < height - 1; y++) {
            for (int x = 1; x < width - 1; x++) {
                int maxA = 0, maxR = 0, maxG = 0, maxB = 0;
                for (int ky = -1; ky <= 1; ky++) {
                    for (int kx = -1; kx <= 1; kx++) {
                        int rgb = originalImage.getRGB(x + kx, y + ky);
                        int a = (rgb >> 24) & 0xFF;
                        int r = (rgb >> 16) & 0xFF;
                        int gr = (rgb >> 8) & 0xFF;
                        int b = rgb & 0xFF;

                        maxA = Math.max(maxA, a);
                        maxR = Math.max(maxR, r);
                        maxG = Math.max(maxG, gr);
                        maxB = Math.max(maxB, b);
                    }
                }
                int newRgb = (maxA << 24) | (maxR << 16) | (maxG << 8) | maxB;
                dilatedImage.setRGB(x, y, newRgb);
            }
        }
        return dilatedImage;
    }

    /**
     * Pré-processamento: Converte a imagem para um grid binário (0 ou 1).
     * Usa um limiar de 128 na escala de cinza.
     * 1 = Objeto (Preto), 0 = Fundo (Branco)
     */
    private static int[][] binarize(BufferedImage originalImage) {
        int width = originalImage.getWidth();
        int height = originalImage.getHeight();
        int[][] binaryGrid = new int[width][height];
        
        // Limiar simples
        final int THRESHOLD = 128;

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int rgb = originalImage.getRGB(x, y);
                int r = (rgb >> 16) & 0xFF;
                int g = (rgb >> 8) & 0xFF;
                int b = rgb & 0xFF;
                
                // Média simples para escala de cinza
                int gray = (r + g + b) / 3;
                
                // Se for mais escuro que o limiar, é objeto (1)
                if (gray < THRESHOLD) {
                    binaryGrid[x][y] = 1;
                } else {
                    binaryGrid[x][y] = 0;
                }
            }
        }
        return binaryGrid;
    }

    /**
     * Pós-processamento: Converte o grid binário de volta para uma BufferedImage
     */
    private static BufferedImage arrayToImage(int[][] grid, int width, int height) {
        
        // *** MUDANÇA AQUI ***
        // O tipo deve ser ARGB para ser compatível com as outras operações
        // do paintComponent (como brilho/contraste), que não rodam em TYPE_BYTE_BINARY.
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (grid[x][y] == 1) {
                    image.setRGB(x, y, Color.BLACK.getRGB());
                } else {
                    image.setRGB(x, y, Color.WHITE.getRGB());
                }
            }
        }
        return image;
    }

    /**
     * Aplica o algoritmo de afinamento Zhang-Suen
     */
    public static BufferedImage applyZhangSuen(BufferedImage originalImage) {
        int width = originalImage.getWidth();
        int height = originalImage.getHeight();
        int[][] grid = binarize(originalImage);
        
        List<Point> pixelsToDelete = new ArrayList<>();
        boolean hasChanged;

        do {
            hasChanged = false;
            
            // --- PASSO 1 ---
            for (int y = 1; y < height - 1; y++) {
                for (int x = 1; x < width - 1; x++) {
                    if (grid[x][y] == 0) continue;

                    int[] p = {
                        grid[x][y-1],   // P2
                        grid[x+1][y-1], // P3
                        grid[x+1][y],   // P4
                        grid[x+1][y+1], // P5
                        grid[x][y+1],   // P6
                        grid[x-1][y+1], // P7
                        grid[x-1][y],   // P8
                        grid[x-1][y-1]  // P9
                    };

                    int transitions = 0; // A(P1) - Transições 0 -> 1
                    int neighbors = 0;   // B(P1) - Vizinhos '1'

                    for (int i = 0; i < p.length; i++) {
                        neighbors += p[i];
                        if (i < 7) {
                            if (p[i] == 0 && p[i+1] == 1) transitions++;
                        } else {
                            if (p[7] == 0 && p[0] == 1) transitions++;
                        }
                    }

                    // Condições do Zhang-Suen (Passo 1)
                    if (grid[x][y] == 1 &&
                        (neighbors >= 2 && neighbors <= 6) &&
                        (transitions == 1) &&
                        (p[0] * p[2] * p[4] == 0) && // P2 * P4 * P6 == 0
                        (p[2] * p[4] * p[6] == 0)) { // P4 * P6 * P8 == 0
                        
                        pixelsToDelete.add(new Point(x, y));
                    }
                }
            }
            
            for (Point p : pixelsToDelete) {
                grid[p.x][p.y] = 0;
                hasChanged = true;
            }
            pixelsToDelete.clear();

            // --- PASSO 2 ---
            for (int y = 1; y < height - 1; y++) {
                for (int x = 1; x < width - 1; x++) {
                    if (grid[x][y] == 0) continue;
                    
                    int[] p = {
                        grid[x][y-1],   // P2
                        grid[x+1][y-1], // P3
                        grid[x+1][y],   // P4
                        grid[x+1][y+1], // P5
                        grid[x][y+1],   // P6
                        grid[x-1][y+1], // P7
                        grid[x-1][y],   // P8
                        grid[x-1][y-1]  // P9
                    };

                    int transitions = 0;
                    int neighbors = 0;

                    for (int i = 0; i < p.length; i++) {
                        neighbors += p[i];
                        if (i < 7) {
                            if (p[i] == 0 && p[i+1] == 1) transitions++;
                        } else {
                            if (p[7] == 0 && p[0] == 1) transitions++;
                        }
                    }
                    
                    // Condições do Zhang-Suen (Passo 2)
                    if (grid[x][y] == 1 &&
                        (neighbors >= 2 && neighbors <= 6) &&
                        (transitions == 1) &&
                        (p[0] * p[2] * p[6] == 0) && // P2 * P4 * P8 == 0
                        (p[0] * p[4] * p[6] == 0)) { // P2 * P6 * P8 == 0
                        
                        pixelsToDelete.add(new Point(x, y));
                    }
                }
            }
            
            for (Point p : pixelsToDelete) {
                grid[p.x][p.y] = 0;
                hasChanged = true;
            }
            pixelsToDelete.clear();

        } while (hasChanged);

        return arrayToImage(grid, width, height);
    }
    
    /**
     * Aplica o algoritmo de afinamento Stentiford
     */
    public static BufferedImage applyStentiford(BufferedImage originalImage) {
        int width = originalImage.getWidth();
        int height = originalImage.getHeight();
        int[][] grid = binarize(originalImage);
        
        boolean hasChanged = true;

        while (hasChanged) {
            hasChanged = false;
            // Itera sobre 4 templates (Norte, Sul, Leste, Oeste)
            for (int templateIndex = 0; templateIndex < 4; templateIndex++) {
                List<Point> pixelsToDelete = new ArrayList<>();
                
                for (int y = 1; y < height - 1; y++) {
                    for (int x = 1; x < width - 1; x++) {
                        if (grid[x][y] == 0) continue;
                        
                        // Checa se é um ponto final (não deve ser deletado)
                        if (isEndpoint(grid, x, y)) continue;

                        // Checa conectividade (se deletar, não quebra a imagem)
                        if (!preservesConnectivity(grid, x, y)) continue;

                        if (matchesTemplate(grid, x, y, templateIndex)) {
                            pixelsToDelete.add(new Point(x, y));
                        }
                    }
                }
                
                for (Point p : pixelsToDelete) {
                    grid[p.x][p.y] = 0;
                    hasChanged = true;
                }
            }
        }
        
        return arrayToImage(grid, width, height);
    }

    // --- Helpers para Stentiford ---

    private static boolean isEndpoint(int[][] grid, int x, int y) {
        int neighbors = 
            grid[x][y-1] + grid[x+1][y-1] + grid[x+1][y] + grid[x+1][y+1] + 
            grid[x][y+1] + grid[x-1][y+1] + grid[x-1][y] + grid[x-1][y-1];
        return neighbors <= 1;
    }
    
    private static int getTransitions(int[][] grid, int x, int y) {
        int[] p = {
            grid[x][y-1], grid[x+1][y-1], grid[x+1][y], grid[x+1][y+1], 
            grid[x][y+1], grid[x-1][y+1], grid[x-1][y], grid[x-1][y-1]
        };
        int transitions = 0;
        for (int i = 0; i < p.length; i++) {
            if (p[i] == 0 && p[(i+1) % 8] == 1) transitions++;
        }
        return transitions;
    }
    
    private static boolean preservesConnectivity(int[][] grid, int x, int y) {
        // Se A(P1) == 1, deletar quebra a conectividade
        return getTransitions(grid, x, y) != 1;
    }

    private static boolean matchesTemplate(int[][] grid, int x, int y, int templateIndex) {
        // p2, p3, p4, p5, p6, p7, p8, p9 (em ordem)
        int p2 = grid[x][y-1];
        int p3 = grid[x+1][y-1];
        int p4 = grid[x+1][y];
        int p5 = grid[x+1][y+1];
        int p6 = grid[x][y+1];
        int p7 = grid[x-1][y+1];
        int p8 = grid[x-1][y];
        int p9 = grid[x-1][y-1];

        switch (templateIndex) {
            case 0: // Norte
                return (p2 == 0 && p6 == 1 && (p4 == 1 || p5 == 1 || p8 == 1 || p7 == 1) && (p4 == 1 || p2 == 1 || p8 == 1 || p9 == 1));
            case 1: // Sul
                return (p6 == 0 && p2 == 1 && (p8 == 1 || p9 == 1 || p4 == 1 || p3 == 1) && (p8 == 1 || p6 == 1 || p4 == 1 || p5 == 1));
            case 2: // Leste
                return (p4 == 0 && p8 == 1 && (p2 == 1 || p3 == 1 || p6 == 1 || p5 == 1) && (p2 == 1 || p9 == 1 || p6 == 1 || p7 == 1));
            case 3: // Oeste
                return (p8 == 0 && p4 == 1 && (p6 == 1 || p7 == 1 || p2 == 1 || p9 == 1) && (p6 == 1 || p5 == 1 || p2 == 1 || p3 == 1));
        }
        return false;
    }

}