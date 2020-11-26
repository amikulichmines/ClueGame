package clueGame;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

public class ImagePanel extends JPanel{

    private BufferedImage image;
    private Image scaledImage;

    public ImagePanel(String fileName) {
       try {                
          image = ImageIO.read(new File(fileName));
          scaledImage = image.getScaledInstance(100, 100, Image.SCALE_SMOOTH);
       } catch (IOException ex) {
            // handle exception...
       }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(scaledImage, 0, 0, this); // see javadoc for more info on the parameters            
    }

}
