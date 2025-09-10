// Arquivo: ImageProcessor.java
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;


public class ImageProcessor {

    public static BufferedImage rotateImage(BufferedImage originalImage, double degrees) {
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
        if (originalImage == null || scaleFactor <= 0) {
            return originalImage;
        }

        int newWidth = (int) (originalImage.getWidth() * scaleFactor);
        int newHeight = (int) (originalImage.getHeight() * scaleFactor);
        
        // Garante que a imagem não desapareça (tamanho mínimo de 1x1)
        if (newWidth < 1 || newHeight < 1) {
            return originalImage;
        }

        BufferedImage resizedImage = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = resizedImage.createGraphics();

        // Usa interpolação bilinear para um redimensionamento de melhor qualidade
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.drawImage(originalImage, 0, 0, newWidth, newHeight, null);
        g2d.dispose();

        return resizedImage;
    }
    }