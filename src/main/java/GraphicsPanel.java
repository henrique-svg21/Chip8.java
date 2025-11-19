// GraphicsPanel.java
import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Dimension;

public class GraphicsPanel extends JPanel {

    private final byte[] gfx;
    private final int pixelSize;

    private final Color offColor = Color.BLACK;
    private final Color onColor = Color.WHITE;

    public GraphicsPanel(byte[] gfx, int pixelSize) {
        this.gfx = gfx;
        this.pixelSize = pixelSize;
        
        int width = Chip8.SCREEN_WIDTH * pixelSize;
        int height = Chip8.SCREEN_HEIGHT * pixelSize;
        Dimension dim = new Dimension(width, height);
        setPreferredSize(dim);
        setBackground(offColor);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g); 

        g.setColor(onColor); 

	// prints video buffer
        for (int y = 0; y < Chip8.SCREEN_HEIGHT; y++) {
            for (int x = 0; x < Chip8.SCREEN_WIDTH; x++) {
                
                if (gfx[y * Chip8.SCREEN_WIDTH + x] == 1) {
                    
                    g.fillRect(x * pixelSize, y * pixelSize, pixelSize, pixelSize);
                }
            }
        }
    }
}
