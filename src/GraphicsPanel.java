// GraphicsPanel.java
import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Dimension;
import java.awt.Image; 
import java.awt.image.BufferedImage; 
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO; 
public class GraphicsPanel extends JPanel {

    private final byte[] gfx;
    private final int pixelSize;

    private final Color offColor = Color.BLACK;
    private final Color onColor = Color.WHITE;
    
    private BufferedImage menuImg;
    private BufferedImage settingsImg;
    private BufferedImage controllersImg;
    
    private Main.State currentState = Main.State.MENU;

    public GraphicsPanel(byte[] gfx, int pixelSize) {
        this.gfx = gfx;
        this.pixelSize = pixelSize;
        
        int width = Chip8.SCREEN_WIDTH * pixelSize;
        int height = Chip8.SCREEN_HEIGHT * pixelSize;
        Dimension dim = new Dimension(width, height);
        setPreferredSize(dim);
        setBackground(offColor);

        // load images 
	try {
            menuImg = ImageIO.read(new File("src/1.png"));
            settingsImg = ImageIO.read(new File("src/2.png"));
            controllersImg = ImageIO.read(new File("src/3.png"));
        } catch (IOException e) {
            System.err.println("Erro ao carregar imagens do menu: " + e.getMessage());
        }
    }
    
    public void setState(Main.State state) {
        this.currentState = state;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g); 

        int width = getWidth();
        int height = getHeight();

        // state machines for renderization
        switch (currentState) {
            case MENU:
                if (menuImg != null) g.drawImage(menuImg, 0, 0, width, height, null);
                break;
            case SETTINGS:
                if (settingsImg != null) g.drawImage(settingsImg, 0, 0, width, height, null);
                break;
            case CONTROLLERS:
                if (controllersImg != null) g.drawImage(controllersImg, 0, 0, width, height, null);
                break;
            case GAME:
                g.setColor(onColor); 
                for (int y = 0; y < Chip8.SCREEN_HEIGHT; y++) {
                    for (int x = 0; x < Chip8.SCREEN_WIDTH; x++) {
                        if (gfx[y * Chip8.SCREEN_WIDTH + x] == 1) {
                            g.fillRect(x * pixelSize, y * pixelSize, pixelSize, pixelSize);
                        }
                    }
                }
                break;
        }
    }
}
