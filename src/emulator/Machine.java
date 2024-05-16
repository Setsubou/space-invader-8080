package emulator;

import intel.Chip;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.io.IOException;

import javax.swing.JFrame;

import intel.Chip;

public class Machine extends JFrame {
	//TODO Input and Output Port are still Hacky
	private InvaderGraphics Panel;
	static int Interrupt = 1;
	static boolean EnableInterrupt = false;

	private Chip cpu;
	static long window;
	
	int overflow;
	
	private InvaderGraphics frame;
	
	private static char PC;
	private static int cycle;
	
	char shiftLSB;
	char shiftMSB;
	char shift_offset;
	
	static char frameBuffer;
	
	public char Input(char port) {
		char a = 0;
		
		switch(port) {
			case 0:{
				return 1;
			}
			
			case 1:{
				return 0;
			}
	
			case 3:{
				a = (char)((shiftMSB << 8 | shiftLSB) >> (8 - shift_offset));
				return a;
			}
		}
		return a;
	}
	
	public void Output(char port) {
		switch(port) {
			case 0:{
				
			}
			
			case 1:{
				
			}
			
			case 2:{
				shift_offset = (char) (Chip.A & 0x7);
				break;
			}
			
			case 3:{
				break;
			}
			
			case 4:{
				shiftLSB = shiftMSB;
				shiftMSB = Chip.A;
				break;
			}
		}
	}
	
	public void runMachine() throws IOException {
		init();
		loop();
	}
	
	private void init() {
		setPreferredSize(new Dimension(256 * 2  , 224 * 2 ));
		setResizable(false);
		pack();
		setPreferredSize(new Dimension((224 * 2) + getInsets().left + getInsets().right, (256 * 2) + getInsets().top + getInsets().bottom));
		Panel = new InvaderGraphics(cpu);
		setLayout(new BorderLayout());
		add(Panel, BorderLayout.CENTER);
		pack();
		setLocationRelativeTo(null);
		setVisible(true);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setTitle("Space Invader");
	}
	
	private void loop() throws IOException {
		cpu = new Chip();
		cpu.init();
		cpu.loadProgram("invaders.rom");
		while(true) {
			long cycleLeft = 16667;
			int cycle = 0 + overflow;
			overflow = 0;	
			while(cycleLeft > cycle) {
				cpu.run();
				cycle = cycle + Chip.getCycle();
				if(cycle > cycleLeft) {
					overflow = (int) (cycle + cycleLeft);
					}
				}
			//Try to switch these two if something's broken
			interrupt();
			repaint();
			}
		}
	
	public void interrupt() {
		if(EnableInterrupt == true) {
			if(Interrupt == 1) {
				GenerateInterrupt(8);
				Interrupt = 2;
			}else if(Interrupt == 2) {
				GenerateInterrupt(16);
				Interrupt = 1;
			}
		}
	}
	
	public void GenerateInterrupt(int opcode) {
		//Save current PC
		char ret = Chip.PC;
		Chip.stack[Chip.SP - 1] = (char) ((ret >> 8));
		Chip.stack[Chip.SP - 2] = (char) (ret & 0xff);
		Chip.SP -= 2;
		Chip.PC = (char)opcode;
		
		EnableInterrupt = false;
	}
	
	public int getCycle() {
		return cycle;
	}

	public boolean getEnableInterrupt() {
		return EnableInterrupt;
	}

	public void setEnableInterrupt(boolean Enableinterrupt) {
		EnableInterrupt = Enableinterrupt;
	}
}

