// Chip8.java
import java.awt.Toolkit;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Random;

public class Chip8 {
    
    public byte[] memory  = new byte[40960];
    public byte[] v = new byte[16]; 	   // registers (V0 - VF)
    public byte[] gfx = new byte[64 * 32]; // video buffer
    public boolean[] key = new boolean[16];// keys
    public byte dt; // delay timer
    public byte st; // sound timer
    public char[] stack = new char[16]; // stack
    public char sp; //stack pointer
    public char i; // adress register
    public char pc; //program counter
    public boolean drawFlag = false; // draw flag
    public static final int SCREEN_WIDTH = 64;
    public static final int SCREEN_HEIGHT = 32;
    private Random rand = new Random(); // randem number generator

    // font pattern
    private final byte[] chip8Fontset = {
        (byte) 0xF0, (byte) 0x90, (byte) 0x90, (byte) 0x90, (byte) 0xF0, // 0
        (byte) 0x20, (byte) 0x60, (byte) 0x20, (byte) 0x20, (byte) 0x70, // 1
        (byte) 0xF0, (byte) 0x10, (byte) 0xF0, (byte) 0x80, (byte) 0xF0, // 2
        (byte) 0xF0, (byte) 0x10, (byte) 0xF0, (byte) 0x10, (byte) 0xF0, // 3
        (byte) 0x90, (byte) 0x90, (byte) 0xF0, (byte) 0x10, (byte) 0x10, // 4
        (byte) 0xF0, (byte) 0x80, (byte) 0xF0, (byte) 0x10, (byte) 0xF0, // 5
        (byte) 0xF0, (byte) 0x80, (byte) 0xF0, (byte) 0x90, (byte) 0xF0, // 6
        (byte) 0xF0, (byte) 0x10, (byte) 0x20, (byte) 0x40, (byte) 0x40, // 7
        (byte) 0xF0, (byte) 0x90, (byte) 0xF0, (byte) 0x90, (byte) 0xF0, // 8
        (byte) 0xF0, (byte) 0x90, (byte) 0xF0, (byte) 0x10, (byte) 0xF0, // 9
        (byte) 0xF0, (byte) 0x90, (byte) 0xF0, (byte) 0x90, (byte) 0x90, // A
        (byte) 0xE0, (byte) 0x90, (byte) 0xE0, (byte) 0x90, (byte) 0xE0, // B
        (byte) 0xF0, (byte) 0x80, (byte) 0x80, (byte) 0x80, (byte) 0xF0, // C
        (byte) 0xE0, (byte) 0x90, (byte) 0x90, (byte) 0x90, (byte) 0xE0, // D
        (byte) 0xF0, (byte) 0x80, (byte) 0xF0, (byte) 0x80, (byte) 0xF0, // E
        (byte) 0xF0, (byte) 0x80, (byte) 0xF0, (byte) 0x80, (byte) 0x80  // F
    };

    // initialize emulator state to pre sets
    public void init() {
        Arrays.fill(memory, (byte) 0);
        Arrays.fill(v, (byte) 0);
        Arrays.fill(gfx, (byte) 0);
        Arrays.fill(key, false);
        Arrays.fill(stack, (char) 0);
        dt = 0;
        st = 0;
        sp = 0;
        i = 0;

        pc = 0x200;

	// load font into memory
        System.arraycopy(chip8Fontset, 0, memory, 0x050, chip8Fontset.length);
    }

    // load rom into memory
    public void loadRom(String filename) throws IOException {
        File rom = new File(filename);
        if (rom.length() > (4096 - 0x200)) {
            throw new IOException("ROM muito grande! Tamanho máximo: 3584 bytes.");
        }

        System.out.println("Carregando ROM: " + filename + " (" + rom.length() + " bytes)");

        try (DataInputStream dis = new DataInputStream(new FileInputStream(rom))) {
            byte[] buffer = new byte[(int) rom.length()];
            dis.readFully(buffer);

            System.arraycopy(buffer, 0, memory, 0x200, buffer.length); // copies rom to system's memory
        }
    }

    public void chip8Cycle() {
        int byte1 = memory[pc] & 0xFF;
        int byte2 = memory[pc + 1] & 0xFF;
        int opcode = (byte1 << 8) | byte2;

        pc += 2;

        int x = (opcode & 0x0F00) >> 8;
        int y = (opcode & 0x00F0) >> 4;
        int n = opcode & 0x000F;
        int nn = opcode & 0x00FF;
        int nnn = opcode & 0x0FFF;

        switch (opcode & 0xF000) {
            case 0x0000:
                switch (nn) {
                    case 0x00E0: // 00E0: CLS (clears screen)
                        Arrays.fill(gfx, (byte) 0);
                        drawFlag = true;
                        break;
                    case 0x00EE: // 00EE: RET (return sub routine)
                        sp--;
                        pc = stack[sp];
                        break;
                    default:
                        break;
                }
                break;

            case 0x1000: // 1NNN: JP addr (jump to NNN)
                pc = (char) nnn;
                break;

            case 0x2000: // 2NNN: CALL addr (call NNN)
                stack[sp] = pc;
                sp++;
                pc = (char) nnn;
                break;

            case 0x3000: // 3XNN: if Vx, byte (jump if Vx == NN)
                if ((v[x] & 0xFF) == nn) {
                    pc += 2;
                }
                break;

            case 0x4000: // 4XNN: SNE Vx, byte (jump if Vx != NN)
                if ((v[x] & 0xFF) != nn) {
                    pc += 2;
                }
                break;

            case 0x5000: // 5XY0: SE Vx, Vy (jump if Vx == Vy)
                if (v[x] == v[y]) {
                    pc += 2;
                }
                break;

            case 0x6000: // 6XNN: LD Vx, byte (Vx = NN)
                v[x] = (byte) nn;
                break;

            case 0x7000: // 7XNN: ADD Vx, byte (Vx = Vx + NN)
                v[x] += (byte) nn;
                break;

            case 0x8000: // bitwise operators 
                int vx = v[x] & 0xFF;
                int vy = v[y] & 0xFF;
                int result;

                switch (n) {
                    case 0x0: // 8XY0: LD Vx, Vy (Vx = Vy)
                        v[x] = v[y];
                        break;
                    case 0x1: // 8XY1: OR Vx, Vy (Vx = Vx | Vy)
                        v[x] = (byte) (vx | vy);
                        break;
                    case 0x2: // 8XY2: AND Vx, Vy (Vx = Vx & Vy)
                        v[x] = (byte) (vx & vy);
                        break;
                    case 0x3: // 8XY3: XOR Vx, Vy (Vx = Vx ^ Vy)
                        v[x] = (byte) (vx ^ vy);
                        break;
                    case 0x4: // 8XY4: ADD Vx, Vy (Vx = Vx + Vy, VF = carry)
                        result = vx + vy;
                        v[x] = (byte) result;
                        v[0xF] = (byte) (result > 255 ? 1 : 0); // define VF if there's carry
                        break;
                    case 0x5: // 8XY5: SUB Vx, Vy (Vx = Vx - Vy, VF = NOT borrow)
                        v[0xF] = (byte) (vx > vy ? 1 : 0); // define VF if there isn't "borrow"
                        v[x] = (byte) (vx - vy);
                        break;
                    case 0x6: // 8XY6: SHR Vx (Vx = Vx >> 1, VF = LSB)
                        v[0xF] = (byte) (vx & 0x1); // define VF to leat siginificative bit
                        v[x] = (byte) (vx >> 1);
                        break;
                    case 0x7: // 8XY7: SUBN Vx, Vy (Vx = Vy - Vx, VF = NOT borrow)
                        v[0xF] = (byte) (vy > vx ? 1 : 0); 
                        v[x] = (byte) (vy - vx);
                        break;
                    case 0xE: // 8XYE: SHL Vx (Vx = Vx << 1, VF = MSB)
                        v[0xF] = (byte) ((vx & 0x80) >> 7); // define VF to most significative bit
			v[x] = (byte) (vx << 1);
                        break;
                }
                break;

            case 0x9000: // 9XY0: SNE Vx, Vy (jump if Vx != Vy)
                if (v[x] != v[y]) {
                    pc += 2;
                }
                break;

            case 0xA000: // ANNN: LD I, addr (I = NNN)
                i = (char) nnn;
                break;

            case 0xB000: // BNNN: JP V0, addr (jump to NNN + V0)
                pc = (char) (nnn + (v[0] & 0xFF));
                break;

            case 0xC000: // CXNN: RND Vx, byte (Vx = random byte & NN)
                v[x] = (byte) (rand.nextInt(256) & nn);
                break;

            case 0xD000: // DXYN: DRW Vx, Vy, N (draw sprite)
                int coordX = v[x] & 0xFF;
                int coordY = v[y] & 0xFF;
                
                v[0xF] = 0; // resets colision flag 

                for (int line = 0; line < n; line++) {
                    int spriteByte = memory[i + line] & 0xFF;
                    int pixelY = (coordY + line);
                    
                    if (pixelY >= SCREEN_HEIGHT) continue; // cuts to go out of screen (vertical)

                    for (int bit = 0; bit < 8; bit++) {
                        int pixelX = (coordX + bit);
                        
                        if (pixelX >= SCREEN_WIDTH) continue;  // cuts to go out of screen (horizontal)
                        // verify if sprite's current bit is high 
                        if ((spriteByte & (0x80 >> bit)) != 0) {
                            int index = pixelY * SCREEN_WIDTH + pixelX;
                            
                            // if high = colision
                            if (gfx[index] == 1) {
                                v[0xF] = 1;
                            }
			    //xor pixel
                            gfx[index] ^= 1;
                        }
                    }
                }
                drawFlag = true; //sets draw flag to true 
                break;

            case 0xE000: // keyboard opcodes 
                int keyIndex = v[x] & 0xFF;
                switch (nn) {
                    case 0x9E: // EX9E: SKP Vx (jumps if VX got pressed)
                        if (key[keyIndex]) {
                            pc += 2;
                        }
                        break;
                    case 0xA1: // EXA1: SKNP Vx (jumps if VX isn't pressed)
                        if (!key[keyIndex]) {
                            pc += 2;
                        }
                        break;
                }
                break;

            case 0xF000: // Opcodes diversos (timers, memória, BCD)
                switch (nn) {
                    case 0x07: // FX07: LD Vx, DT (Vx = valor do delay timer)
                        v[x] = dt;
                        break;
                    case 0x0A: // FX0A: LD Vx, K (waits for input and keep's in VX)
                        boolean keyPressed = false;
                        for (int k = 0; k < key.length; k++) {
                            if (key[k]) {
                                v[x] = (byte) k;
                                keyPressed = true;
                                break;
                            }
                        }
                        if (!keyPressed) {
                            pc -= 2; 
                        }
                        break;
                    case 0x15: // FX15: LD DT, Vx (Delay timer = Vx)
                        dt = v[x];
                        break;
                    case 0x18: // FX18: LD ST, Vx (Sound timer = Vx)
                        st = v[x];
                        break;
                    case 0x1E: // FX1E: ADD I, Vx (I = I + Vx)
                        i += (v[x] & 0xFF);
                        break;
                    case 0x29: 
                        i = (char) (0x050 + ((v[x] & 0xFF) * 5));
                        break;
                    case 0x33:                        int val = v[x] & 0xFF;
                        memory[i] = (byte) (val / 100);        
                        memory[i + 1] = (byte) ((val / 10) % 10);                     
			memory[i + 2] = (byte) (val % 10);                                 
			break;
                    case 0x55: // FX55: LD [I], Vx (load V0 to Vx into memory after I)
                        System.arraycopy(v, 0, memory, i, x + 1);
                        break;
                    case 0x65: // FX65: LD Vx, [I] (load V0 to Vx from from memory after I)
                        System.arraycopy(memory, i, v, 0, x + 1);
                        break;
                }
                break;

            default:
        }
    }

    // reload timers
    public void updateTimers() {
        if ((dt & 0xFF) > 0) {
            dt--;
	    Toolkit.getDefaultToolkit().beep();
        }
        if ((st & 0xFF) > 0) {
            st--;
        }
    }
}
