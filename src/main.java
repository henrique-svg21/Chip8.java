// Main.java
import javax.swing.JFrame;
import javax.swing.JFileChooser;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.IOException;

public class Main implements Runnable {

    public enum State {
        MENU,
        SETTINGS,
        CONTROLLERS,
        GAME
    }

    private Chip8 chip8;
    private GraphicsPanel panel;
    private JFrame frame;
    
    private State currentState = State.MENU;
    private boolean romLoaded = false;

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

                switch (currentState) {
                    case MENU:
                        if (keyCode == KeyEvent.VK_1) {
                            // 1. LOAD ROM
                            if (!romLoaded) {
                                String romPath = selectRom();
                                if (romPath != null) {
                                    try {
                                        chip8.loadRom(romPath);
                                        romLoaded = true;
                                        changeState(State.GAME);
                                    } catch (IOException ex) {
                                        System.err.println("Erro: " + ex.getMessage());
                                    }
                                }
                            } else {
                                changeState(State.GAME); 
                            }
                        } else if (keyCode == KeyEvent.VK_2) {
                            // 2. SETTINGS
                            changeState(State.SETTINGS);
                        } else if (keyCode == KeyEvent.VK_3) {
                            // 3. QUIT
                            System.exit(0);
                        }
                        break;

                    case SETTINGS:
                        if (keyCode == KeyEvent.VK_1) {
                            // 1. CONTROLLERS
                            changeState(State.CONTROLLERS);
                        } else if (keyCode == KeyEvent.VK_2) {
                            // 2. RETURN
                            changeState(State.MENU);
                        }
                        break;

                    case CONTROLLERS:
                        // Qualquer tecla de retorno ou a opção 2
                        if (keyCode == KeyEvent.VK_2 || keyCode == KeyEvent.VK_ESCAPE) {
                            changeState(State.SETTINGS);
                        }
                        break;

                    case GAME:
                        if (keyCode == KeyEvent.VK_ESCAPE) {
                            // ESCAPE para PAUSAR e voltar ao MENU
                            changeState(State.MENU);
                        } else {
                            // Inputs normais do Chip-8
                            for (int i = 0; i < keyMap.length; i++) {
                                if (keyMap[i] == keyCode) {
                                    chip8.key[i] = true;
                                }
                            }
                        }
                        break;
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                if (currentState == State.GAME) {
                    int keyCode = e.getKeyCode();
                    for (int i = 0; i < keyMap.length; i++) {
                        if (keyMap[i] == keyCode) {
                            chip8.key[i] = false;
                        }
                    }
                }
            }
        });

        frame.setVisible(true);
    }
    
    private void changeState(State newState) {
        this.currentState = newState;
        panel.setState(newState);
        panel.repaint();
    }

    // open file system
    private String selectRom() {
        JFileChooser fileChooser = new JFileChooser(".");
        fileChooser.setDialogTitle("Selecione uma ROM CHIP-8");
        int result = fileChooser.showOpenDialog(frame); // Use 'frame' como pai para modalidade
        if (result == JFileChooser.APPROVE_OPTION) {
            return fileChooser.getSelectedFile().getAbsolutePath();
        }
        return null;
    }

    // main loop
    @Override
    public void run() {
        final double nsPerTick = 1_000_000_000.0 / 60.0;
        final int cyclesPerTick = 9; // Velocidade da emulação

        long lastTime = System.nanoTime();
        double delta = 0;

        while (true) {
            long now = System.nanoTime();
            delta += (now - lastTime) / nsPerTick;
            lastTime = now;

            if (delta >= 1) {
                // SÓ executa o ciclo da CPU se estivermos no estado GAME
                if (currentState == State.GAME) {
                    for (int i = 0; i < cyclesPerTick; i++) {
                        chip8.chip8Cycle();
                    }
                    chip8.updateTimers();

                    if (chip8.drawFlag) {
                        panel.repaint();
                        chip8.drawFlag = false;
                    }
                } else {
                    panel.repaint(); 
                }
                delta--;
            } else {
                try { Thread.sleep(1); } catch (InterruptedException e) {}
            }
        }
    }

    public static void main(String[] args) {
        Main emulator = new Main();
        new Thread(emulator).start();
    }
}
