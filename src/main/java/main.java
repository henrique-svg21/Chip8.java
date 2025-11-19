// Main.java
import javax.swing.JFrame;
import javax.swing.JFileChooser;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.IOException;

public class Main implements Runnable {

    private Chip8 chip8;
    private GraphicsPanel panel;
    private JFrame frame;

    // Key mapping 
    private final int[] keyMap = {
        KeyEvent.VK_X, // 0
        KeyEvent.VK_1, // 1
        KeyEvent.VK_2, // 2
        KeyEvent.VK_3, // 3
        KeyEvent.VK_Q, // 4
        KeyEvent.VK_W, // 5
        KeyEvent.VK_E, // 6
        KeyEvent.VK_A, // 7
        KeyEvent.VK_S, // 8
        KeyEvent.VK_D, // 9
        KeyEvent.VK_Z, // A
        KeyEvent.VK_C, // B
        KeyEvent.VK_4, // C
        KeyEvent.VK_R, // D
        KeyEvent.VK_F, // E
        KeyEvent.VK_V  // F
    };

    public Main() {
        chip8 = new Chip8();
        chip8.init();

        String romPath = selectRom();
        if (romPath == null) {
            System.err.println("Nenhuma ROM selecionada. Encerrando.");
            System.exit(0);
        }
        
        try {
            chip8.loadRom(romPath);
        } catch (IOException e) {
            System.err.println("Erro ao carregar a ROM: " + e.getMessage());
            System.exit(1);
        }

        panel = new GraphicsPanel(chip8.gfx, 12); 
        frame = new JFrame("Emulador CHIP-8 em Java");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(panel);
        frame.pack(); 
        frame.setResizable(false);
        frame.setLocationRelativeTo(null); 

        frame.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                int keyCode = e.getKeyCode();
                for (int i = 0; i < keyMap.length; i++) {
                    if (keyMap[i] == keyCode) {
                        chip8.key[i] = true;
                    }
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                int keyCode = e.getKeyCode();
                for (int i = 0; i < keyMap.length; i++) {
                    if (keyMap[i] == keyCode) {
                        chip8.key[i] = false;
                    }
                }
            }
        });

        frame.setVisible(true);
    }

    // open file system
    private String selectRom() {
        JFileChooser fileChooser = new JFileChooser(".");
        fileChooser.setDialogTitle("Selecione uma ROM CHIP-8");
        int result = fileChooser.showOpenDialog(null);
        if (result == JFileChooser.APPROVE_OPTION) {
            return fileChooser.getSelectedFile().getAbsolutePath();
        }
        return null;
    }

    // main loop
    @Override
    public void run() {
        final double nsPerTick = 1_000_000_000.0 / 60.0;
        final int cyclesPerTick = 9; 

        long lastTime = System.nanoTime();
        double delta = 0;

        while (true) {
            long now = System.nanoTime();
            delta += (now - lastTime) / nsPerTick;
            lastTime = now;

            if (delta >= 1) {
                for (int i = 0; i < cyclesPerTick; i++) {
                    chip8.chip8Cycle();
                }

                chip8.updateTimers();

                if (chip8.drawFlag) {
                    panel.repaint();
                    chip8.drawFlag = false;
                }

                delta--;
            }
        }
    }

    public static void main(String[] args) {
        Main emulator = new Main();
        new Thread(emulator).start();
    }
}
