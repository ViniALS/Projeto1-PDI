// Arquivo: ImageFileManager.java
import java.awt.Component;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

/**
 * Classe utilitária para carregar e salvar arquivos de imagem.
 */
public class ImageFileManager {

  
    private static JFileChooser createConfiguredFileChooser() {
        // Cria a instância do seletor de arquivos
        JFileChooser fileChooser = new JFileChooser();
        
        // Pega o caminho do diretório de trabalho atual como uma String
        String userDir = System.getProperty("user.dir");
        
        File projectDir = new File(userDir);
        fileChooser.setCurrentDirectory(projectDir);
        
        return fileChooser;
    }

    /**
     * Abre um JFileChooser para o usuário selecionar uma imagem e a carrega.
     * @param parent O componente pai para o diálogo (geralmente a janela principal).
     * @return A BufferedImage carregada ou null se a operação for cancelada ou falhar.
     */
    public static BufferedImage loadImage(Component parent) {
        JFileChooser fileChooser = createConfiguredFileChooser();
        
        if (fileChooser.showOpenDialog(parent) == JFileChooser.APPROVE_OPTION) {
            try {
                return ImageIO.read(fileChooser.getSelectedFile());
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(parent, "Erro ao abrir imagem!", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
        return null;
    }

    /**
     * Abre um JFileChooser para o usuário salvar uma imagem.
     * @param parent O componente pai para o diálogo.
     * @param imageToSave A imagem que será salva.
     */
    public static void saveImage(Component parent, BufferedImage imageToSave) {
        if (imageToSave == null) return;

        JFileChooser fileChooser = createConfiguredFileChooser();

        if (fileChooser.showSaveDialog(parent) == JFileChooser.APPROVE_OPTION) {
            try {
                File output = fileChooser.getSelectedFile();
                if (!output.getName().toLowerCase().endsWith(".png")) {
                    output = new File(output.getAbsolutePath() + ".png");
                }
                ImageIO.write(imageToSave, "png", output);
                JOptionPane.showMessageDialog(parent, "Imagem salva com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(parent, "Erro ao salvar imagem!", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        } 
            
    
    
    
    }
}