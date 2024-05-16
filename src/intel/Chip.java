package intel;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import emulator.Machine;

public class Chip {
	
	//TODO Clear Screen bugged, Starts at #9d6 to #9ee, Might be related to POP PSW

	public static char A;
	private char B;
	private char C;
	private char D;
	private char E;
	private char H;
	private char L;
	
	private char Z;
	private char S;
	private char P;
	private char CY;
	private char AC;
	
	public static char [] memory;
	
	public static char [] stack;
	public static char SP;
	public static char PC;
	
	static int cycle;
	
	Machine Machine = new Machine();
	
	public void init() {
		A = 0;
		B = 0;
		C = 0;
		D = 0;
		E = 0;
		H = 0;
		L = 0;
		
		Z = 0;
		S = 0;
		P = 0;
		CY = 0;
		
		stack = new char[20000];
		memory = new char[20000];
		PC = 0x0;
	}

	public void run() {
			cycle = Machine.getCycle();
		//Fetch Opcode
			char opcode = (memory[PC]);
			Registerstatus();
			Flagstatus();
			System.out.print("#"+ Integer.toHexString(PC) + " ");
			System.out.print(" " + Integer.toHexString(opcode) +": ");
		//Decode Opcode
			switch(opcode) {
			
			case 0x00: {
				System.out.println("NOP");
				cycle += 4;
				PC += 1;
				break;
			}
			
			case 0x01:{
				System.out.println("LXI BC #" + Integer.toHexString(memory[PC+2]) + Integer.toHexString(memory[PC+1]));
				B = memory[PC + 2];
				C = memory[PC + 1];
				cycle += 4;
				PC += 3;
				break;
			}
			
			case 0x02:{
				System.out.println("STAX B");
				char location = (char)(B << 8 | C);
				memory[location] = A;
				cycle += 7;
				PC += 1;
				break;
			}
			
			case 0x03:{
				System.out.println("INX B");
				int x = ((B << 8) | C);
				x += 1;
				B = (char) (x >> 8);
				C = (char) (x & 0x00FF);
				cycle += 5;
				PC += 1;
				break;
			}
			
			case 0x04:{
				System.out.println("INR B");
				B += 1;
				Z = Zcheck(B);
				S = Scheck(B);
				Pcheck(B);
				cycle += 5;
				PC += 1;
				break;
			}
			
			case 0x05:{
				System.out.println("DCR B");
				B -= 1;
				
				B = Overflowcheck(B);
				Z = Zcheck(B);
				S = Scheck(B);
				Pcheck(B);
				cycle += 5;
				PC += 1;
				break;
			}
			
			case 0x06:{
				System.out.println("MVI B #" + Integer.toHexString(memory[PC+1]));
				B = memory[PC+1];
				cycle += 7;
				PC += 2;
				break;
			}
			
			case 0x07:{ //TODO Might be Broken
				System.out.println("RLC");
				char x = (char)(A << 1);
				CY = (char) (x & 0x100);
				A = x;
				A = Overflowcheck(A);
				cycle += 4;
				PC += 1;
				break;
			}
			
			case 0x08:{
				System.out.println("NOP");
				PC += 1;
				break;
			}
			
			case 0x09:{
				System.out.println("DAD B");
				char HL = (char)((H << 8) | L);
				char BC = (char)((B << 8) | C);
				HL = (char)(HL + BC);
				H = (char) (HL >> 8);
				L = (char) (HL & 0x00FF);
				cycle += 10;
				PC += 1;
				break;
			}
			
			case 0x0a:{
				System.out.println("LDAX B");
				char location = (char)(B << 8 | C);
				A = memory[location];
				cycle += 7;
				PC += 1;
				break;
			}
			
			/*case 0x0b:{
				System.out.println("DCX B");
				int x = ((B << 8) | C);
				x -= 1;
				B = (char) (x >> 8);
				C = (char) (x & 0x00FF);
				cycle += 5;
				PC += 1;
				break;
			}*/
			
			case 0x0c:{
				System.out.println("INR C");
				C += 1;
				Z = Zcheck(C);
				S = Scheck(C);
				Pcheck(C);
				cycle += 5;
				PC += 1;
				break;
			}
			
			case 0x0d:{
				System.out.println("DCR C");
				C -= 1;
				Z = Zcheck(C);
				S = Scheck(C);
				Pcheck(C);
				cycle += 5;
				PC += 1;
				break;
			}
			
			case 0x0e:{
				System.out.println("MVI C #" + Integer.toHexString(memory[PC+1]));
				C = memory[PC+1];
				cycle += 7;
				PC += 2;
				break;
			}
			
			case 0x0f:{ //TODO Broke
				System.out.println("RRC");
				char x = A;
				CY = (char) (A & 0x1);
				A = (char) (x >> 1);
				cycle += 4;
				PC += 1;
				break;
			}
			
			case 0x11:{
				System.out.println("LXI DE, #" + Integer.toHexString(memory[PC+1]) + Integer.toHexString(memory[PC+2]));
				D = memory[PC + 2];
				E = memory[PC + 1];
				cycle += 10;
				PC += 3;
				break;
			}
			
/*			case 0x12:{
				System.out.println("STAX D");
				char location = (char)(D << 8 | E);
				memory[location] = A;
				cycle += 7;
				PC += 1;
				break;
			}*/
			
			case 0x13:{
				System.out.println("INX D");
				int x = ((D << 8) | E);
				x += 1;
				D = (char) (x >> 8);
				E = (char) (x & 0x00FF);
				cycle += 5;
				PC += 1;
				break;
			}
			
			case 0x14:{
				System.out.println("INR D");
				D += 1;
				Z = Zcheck(D);
				S = Scheck(D);
				Pcheck(D);
				cycle += 5;
				PC += 1;
				break;
			}
			
			case 0x15:{
				System.out.println("DCR D");
				D -= 1;
				Z = Zcheck(D);
				S = Scheck(D);
				Pcheck(D);
				cycle += 5;
				PC += 1;
				break;
			}
			
			case 0x16:{
				System.out.println("MVI D #" + Integer.toHexString(memory[PC+1]));
				D = memory[PC+1];
				cycle += 7;
				PC += 2;
				break;
			}
			
			case 0x19:{
				System.out.println("DAD D");
				char HL = (char)((H << 8) | L);
				char DE = (char)((D << 8) | E);
				HL = (char)(HL + DE);
				H = (char) (HL >> 8);
				L = (char) (HL & 0x00FF);
				cycle += 10;
				PC += 1;
				break;
			}
			
			case 0x1a:{
				System.out.println("LDAX D");
				A = (char) memory[(D << 8) | E];
				cycle += 7;
				PC += 1;
				break;
			}
			
			/*case 0x1b:{
				System.out.println("DCX D");
				int x = ((D << 8) | E);
				x -= 1;
				D = (char) (x >> 8);
				E = (char) (x & 0x00FF);
				cycle += 5;
				PC += 1;
				break;
			}

			case 0x1c:{
				System.out.println("INR E");
				E += 1;
				Z = Zcheck(E);
				S = Scheck(E);
				Pcheck(E);
				cycle += 5;
				PC += 1;
				break;
			}
			
			case 0x1d:{
				System.out.println("DCR E");
				E -= 1;
				Z = Zcheck(E);
				S = Scheck(E);
				Pcheck(E);
				cycle += 5;
				PC += 1;
				break;
			}
			
			case 0x1e:{
				System.out.println("MVI E #" + Integer.toHexString(memory[PC+1]));
				E = memory[PC+1];
				cycle += 7;
				PC += 2;
				break;
			}*/
			
			case 0x1f:{ // TODO Should be Good Now
				System.out.println("RAR");
				char Newbyte = A;
				
				A = (char) ((CY << 7) | (Newbyte >> 1));
				CY = (char) (Newbyte & 0x1);
				PC += 1;
				cycle +=4 ;
				break;
			}
			
			case 0x21:{
				System.out.println("LXI HL #" + Integer.toHexString(memory[PC+2]) + Integer.toHexString(memory[PC+1]));
				H = memory[PC + 2];
				L = memory[PC + 1];
				cycle += 10;
				PC += 3;
				break;
			}
			
			case 0x22:{
				System.out.println("SHLD #" + Integer.toHexString(memory[PC+2]) + Integer.toHexString(memory[PC+1]));
				char x = (char)((memory[PC + 2] << 8) | memory[PC +1]);
				memory[x] = L;
				memory[x+1] = H;
				cycle += 16;
				PC += 3;
				break;
			}
			
			case 0x23:{
				System.out.println("INX H");
				int x = ((H << 8) | L);
				x += 1;
				H = (char) (x >> 8);
				L = (char) (x & 0x00FF);
				cycle += 5;
				PC += 1;
				break;
			}
			
/*			case 0x24:{
				System.out.println("INR H");
				H += 1;
				Z = Zcheck(H);
				S = Scheck(H);
				Pcheck(H);
				cycle += 5;
				PC += 1;
				break;
			}
			
			case 0x25:{
				System.out.println("DCR H");
				H -= 1;
				Z = Zcheck(H);
				S = Scheck(H);
				Pcheck(H);
				cycle += 5;
				PC += 1;
				break;
			}*/
			
			case 0x26:{
				System.out.println("MVI H #" + Integer.toHexString(memory[PC+1]));
				H = memory[PC+1];
				cycle += 7;
				PC += 2;
				break;
			}
			
			case 0x29:{
				System.out.println("DAD H");
				char x = (char)((H << 8) | L);
				x = (char)(x + x);
				H = (char) (x >> 8);
				L = (char) (x & 0x00FF);
				cycle += 10;
				PC += 1;
				break;
			}
			
			case 0x2a:{
				System.out.println("LHLD #" + Integer.toHexString(memory[PC+2]) + Integer.toHexString(memory[PC+1]));
				int x = (memory[PC+2] << 8) | memory[PC+1];
				L = memory[x];
				H = memory[x+1];
				cycle += 16;
				PC += 3;
				break;
			}
			
			case 0x2b:{
				System.out.println("DCX H");
				char x = (char)((H << 8) | L);
				x -= 1;
				H = (char) (x >> 8);
				L = (char) (x & 0x00FF);
				cycle += 5;
				PC += 1;
				break;
			}
			
			/*case 0x2c:{
				System.out.println("INR L");
				L += 1;
				Z = Zcheck(L);
				S = Scheck(L);
				Pcheck(L);
				cycle += 5;
				PC += 1;
				break;
			}
			
			case 0x2d:{
				System.out.println("DCR L");
				L -= 1;
				Z = Zcheck(L);
				S = Scheck(L);
				Pcheck(L);
				cycle += 5;
				PC += 1;
				break;
			}*/
			
			case 0x2e:{
				System.out.println("MVI L #" + Integer.toHexString(memory[PC+1]));
				L = memory[PC+1];
				cycle += 7;
				PC += 2;
				break;
			}
			
/*			case 0x2f:{
				System.out.println("CMA");
				A = (char) ~A;
				A = Overflowcheck(A);
				cycle += 4;
				PC += 1;
				break;
			}*/
			
			case 0x31:{
				System.out.println("LXI SP, #" + Integer.toHexString(memory[PC+2]) + Integer.toHexString(memory[PC+1]));
				SP = (char)((memory[PC+2] << 8) | memory[PC+1]);
				cycle += 10;
				PC += 3;
				break;
			}
			
			case 0x32:{
				System.out.println("STA #" + Integer.toHexString(memory[PC+2]) + Integer.toHexString(memory[PC+1]));
				char location = (char) ((memory[PC+2] << 8) | memory[PC+1]);
				memory[location] = A;
				cycle += 13;
				PC += 3;
				break;
			}
			
		case 0x34:{
				System.out.println("INR M");
				System.out.println("INR M Before :" + Integer.toHexString(memory[H << 8 | L]));
				memory[H << 8 | L] += 1;
				System.out.println("INR M After :" + Integer.toHexString(memory[H << 8 | L]));
				cycle += 10;
				PC += 1;
				break;
			}
			
			case 0x35:{
				System.out.println("DCR M");
				memory[H << 8 | L] -= 1;
				cycle += 10;
				PC += 1;
				break;
			}
			
			case 0x36:{
				System.out.println("MVI M, #" + Integer.toHexString(memory[PC+1]));
				memory[H << 8 | L] = memory[PC+1];
				cycle += 10;
				PC += 2;
				break;
			}
			
		case 0x37:{
				System.out.println("STC");
				CY = 1;
				cycle += 4;
				PC += 1;
				break;
			}
			
			case 0x3a:{
				System.out.println("LDA #" + Integer.toHexString(memory[PC+2]) + Integer.toHexString(memory[PC+1]));
				char location = (char)((memory[PC+2] << 8) | memory[PC+1]);
				A = memory[location];
				cycle += 13;
				PC += 3;
				break;
			}
			
			case 0x3c:{
				System.out.println("INR A");
				A += 1;
				
				Z = Zcheck(A);
				S = Scheck(A);
				Pcheck(A);
				cycle += 5;
				PC += 1;
				break;
			}
			
			case 0x3d:{
				System.out.println("DCR A");
				A -= 1;
				
				A = Overflowcheck(A);
				Z = Zcheck(A);
				S = Scheck(A);
				Pcheck(A);
				cycle += 5;
				PC += 1;
				break;
			}
			
			case 0x3e:{
				System.out.println("MVI, A #" + Integer.toHexString(memory[PC+1]));
				A = memory[PC+1];
				cycle += 7;
				PC += 2;
				break;
			}
			
			case 0x3f:{
				System.out.println("CMC");
				if(CY == 0) {
					CY = 1;
				}else {
					CY = 0;
				}
				cycle += 4;
				PC += 1;
				break;
			}
			
			case 0x41:{
				System.out.println("MOV B C");
				B = C;
				cycle += 5;
				PC += 1;
				break;
			}
			
			case 0x42:{
				System.out.println("MOV B D");
				B = D;
				cycle += 5;
				PC += 1;
				break;
			}
			
			case 0x43:{
				System.out.println("MOV B E");
				B = E;
				cycle += 5;
				PC += 1;
				break;
			}
			
			case 0x44:{
				System.out.println("MOV B H");
				B = H;
				cycle += 5;
				PC += 1;
				break;
			}
			
			case 0x45:{
				System.out.println("MOV B L");
				B = L;
				cycle += 5;
				PC += 1;
				break;
			}
			
			case 0x46:{
				System.out.println("MOV B M");
				int location = (H << 8) | L;
				B = memory[location];
				cycle += 7;
				PC += 1;
				break;
			}
			
			case 0x47:{
				System.out.println("MOV B A");
				B = A;
				cycle += 5;
				PC += 1;
				break;
			}
			
			case 0x48:{
				System.out.println("MOV C B");
				C = B;
				cycle += 5;
				PC += 1;
				break;
			}
			
			case 0x4a:{
				System.out.println("MOV C D");
				C = D;
				cycle += 5;
				PC += 1;
				break;
			}
			
			case 0x4b:{
				System.out.println("MOV C E");
				C = E;
				cycle += 5;
				PC += 1;
				break;
			}
			
			case 0x4c:{
				System.out.println("MOV C H");
				C = H;
				cycle += 5;
				PC += 1;
				break;
			}
			
			case 0x4d:{
				System.out.println("MOV C L");
				C = L;
				cycle += 5;
				PC += 1;
				break;
			}
			
			case 0x4e:{
				System.out.println("MOV C M");
				int location = (H << 8) | L;
				C = memory[location];
				cycle += 7;
				PC += 1;
				break;
			}
			
			case 0x4f:{
				System.out.println("MOV C A");
				C = A;
				cycle += 5;
				PC += 1;
				break;
			}
			
			case 0x50:{
				System.out.println("MOV D B");
				D = B;
				cycle += 5;
				PC += 1;
				break;
			}
			
			case 0x51:{
				System.out.println("MOV D C");
				D = C;
				cycle += 5;
				PC += 1;
				break;
			}
			
			case 0x53:{
				System.out.println("MOV D E");
				D = E;
				cycle += 5;
				PC += 1;
				break;
			}
			
			case 0x54:{
				System.out.println("MOV D H");
				D = H;
				cycle += 5;
				PC += 1;
				break;
			}
			
			case 0x55:{
				System.out.println("MOV D L");
				D = L;
				cycle += 5;
				PC += 1;
				break;
			}
			
			case 0x56:{
				System.out.println("MOV D M");
				int location = (H << 8) | L;
				D = memory[location];
				cycle += 7;
				PC += 1;
				break;
			}
			
			case 0x57:{
				System.out.println("MOV D A");
				D = A;
				cycle += 5;
				PC += 1;
				break;
			}
			/*case 0x58:{
				System.out.println("MOV E B");
				E = B;
				cycle += 5;
				PC += 1;
				break;
			}
			
			case 0x59:{
				System.out.println("MOV E C");
				E = C;
				cycle += 5;
				PC += 1;
				break;
			}
			
			case 0x5a:{
				System.out.println("MOV E D");
				E = D;
				cycle += 5;
				PC += 1;
				break;
			}
			
			case 0x5c:{
				System.out.println("MOV E H");
				E = H;
				cycle += 5;
				PC += 1;
				break;
			}
			
			case 0x5d:{
				System.out.println("MOV E L");
				E = L;
				cycle += 5;
				PC += 1;
				break;
			}*/
			
			case 0x5e:{
				System.out.println("MOV E M");
				int location = (H << 8) | L;
				E = memory[location];
				cycle += 7;
				PC += 1;
				break;
			}
			
			case 0x5f:{
				System.out.println("MOV E A");
				E = A;
				cycle += 5;
				PC += 1;
				break;
			}
			
			case 0x60:{
				System.out.println("MOV H B");
				H = B;
				cycle += 5;
				PC += 1;
				break;
			}
			
			case 0x61:{
				System.out.println("MOV H C");
				H = C;
				cycle += 5;
				PC += 1;
				break;
			}
			
			case 0x62:{
				System.out.println("MOV H D");
				H = D;
				cycle += 5;
				PC += 1;
				break;
			}
			
			case 0x63:{
				System.out.println("MOV H E");
				H = E;
				cycle += 5;
				PC += 1;
				break;
			}
			
			case 0x65:{
				System.out.println("MOV H L");
				H = L;
				cycle += 5;
				PC += 1;
				break;
			}
			
			case 0x66:{
				System.out.println("MOV H M");
				int location = (H << 8) | L;
				H = memory[location];
				cycle += 7;
				PC += 1;
				break;
			}
			
			case 0x67:{
				System.out.println("MOV H A");
				H = A;
				cycle += 5;
				PC += 1;
				break;
			}
			
			case 0x68:{
				System.out.println("MOV L B");
				L = B;
				cycle += 5;
				PC += 1;
				break;
			}
			
			case 0x69:{
				System.out.println("MOV L C");
				L = C;
				cycle += 5;
				PC += 1;
				break;
			}
			
			/*case 0x6a:{
				System.out.println("MOV L D");
				L = D;
				cycle += 5;
				PC += 1;
				break;
			}
			
			case 0x6b:{
				System.out.println("MOV L E");
				L = E;
				cycle += 5;
				PC += 1;
				break;
			}
			
			case 0x6c:{
				System.out.println("MOV L H");
				L = H;
				cycle += 5;
				PC += 1;
				break;
			}
			
			case 0x6e:{
				System.out.println("MOV L M");
				int location = (H << 8) | L;
				memory[location] = (char) L;
				cycle += 7;
				PC += 1;
				break;
			}*/
			
			case 0x6f:{
				System.out.println("MOV L A");
				L = A;
				cycle += 5;
				PC += 1;
				break;
			}
			
			case 0x70:{
				System.out.println("MOV M B");
				int location = (H << 8) | L;
				memory[location] = (char) B;
				cycle += 7;
				PC += 1;
				break;
			}
			/*
			case 0x72:{
				System.out.println("MOV M D");
				int location = (H << 8) | L;
				memory[location] = (char) D;
				cycle += 7;
				PC += 1;
				break;
			}
			
			case 0x73:{
				System.out.println("MOV M E");
				int location = (H << 8) | L;
				memory[location] = (char) E;
				cycle += 7;
				PC += 1;
				break;
			}
			
			case 0x74:{
				System.out.println("MOV M H");
				int location = (H << 8) | L;
				memory[location] = (char) H;
				cycle += 7;
				PC += 1;
				break;
			}
			
			case 0x75:{
				System.out.println("MOV M L");
				int location = (H << 8) | L;
				memory[location] = (char) L;
				cycle += 7;
				PC += 1;
				break;
			}*/
			
			case 0x77:{
				System.out.println("MOV M A");
				int location = (H << 8) | L;
				memory[location] = (char) A;
				cycle += 7;
				PC += 1;
				break;
			}
			
			case 0x78:{
				System.out.println("MOV A B");
				A = B;
				cycle += 5;
				PC += 1;
				break;
			}
			
			case 0x79:{
				System.out.println("MOV A C");
				A = C;
				cycle += 5;
				PC += 1;
				break;
			}
			
			case 0x7a:{
				System.out.println("MOV A D");
				A = D;
				cycle += 5;
				PC += 1;
				break;
			}
			
			case 0x7b:{
				System.out.println("MOV A E");
				A = E;
				cycle += 5;
				PC += 1;
				break;
			}
			
			case 0x7c:{
				System.out.println("MOV A H");
				A = H;
				cycle += 5;
				PC += 1;
				break;
			}
			
			case 0x7d:{
				System.out.println("MOV A L");
				A = L;
				cycle += 5;
				PC += 1;
				break;
			}
			
			case 0x7e:{
				System.out.println("MOV A M");
				A = memory[(H << 8) | L];
				cycle += 7;
				PC += 1;
				break;
			}
			
			case 0x7f:{
				System.out.println("MOV A A");
				A = A;
				cycle += 5;
				PC += 1;
				break;
			}
			
			/*case 0x80:{
				System.out.println("ADD B");
				A = (char)(A + B);
				
				CYcheck(A);
				A = Overflowcheck(A);
				Z = Zcheck(A);
				S = Scheck(A);
				Pcheck(A);
				cycle += 4;
				PC += 1;
				break;
			}
			
			case 0x81:{
				System.out.println("ADD C");
				A = (char)(A + C);
				
				A = Overflowcheck(A);
				Z = Zcheck(A);
				S = Scheck(A);
				CYcheck(A);
				Pcheck(A);
				cycle += 4;
				PC += 1;
				break;
			}
			
			case 0x82:{
				System.out.println("ADD D");
				A = (char)(A + D);
				
				A = Overflowcheck(A);
				Z = Zcheck(A);
				S = Scheck(A);
				CYcheck(A);
				Pcheck(A);
				cycle += 4;
				PC += 1;
				break;
			}
			
			case 0x83:{
				System.out.println("ADD E");
				A = (char)(A + E);
				
				A = Overflowcheck(A);
				Z = Zcheck(A);
				S = Scheck(A);
				CYcheck(A);
				Pcheck(A);
				cycle += 4;
				PC += 1;
				break;
			}
			
			case 0x84:{
				System.out.println("ADD H");
				A = (char)(A + H);
				
				A = Overflowcheck(A);
				Z = Zcheck(A);
				S = Scheck(A);
				CYcheck(A);
				Pcheck(A);
				cycle += 4;
				PC += 1;
				break;
			}
			
			case 0x85:{
				System.out.println("ADD L");
				A = (char)(A + L);
				
				A = Overflowcheck(A);
				Z = Zcheck(A);
				S = Scheck(A);
				CYcheck(A);
				Pcheck(A);
				cycle += 4;
				PC += 1;
				break;
			}*/
			
			case 0x86:{
				System.out.println("ADD M");
				A = (char)(A + memory[(H << 8) | L]);
				A = Overflowcheck(A);
				cycle += 7;
				PC += 1;
				break;
			}
			
			case 0x87:{
				System.out.println("ADD A");
				A = (char)(A + A);
				
				A = Overflowcheck(A);
				Z = Zcheck(A);
				S = Scheck(A);
				CYcheck(A);
				Pcheck(A);
				cycle += 4;
				PC += 1;
				break;
			}
			
			case 0x88:{
				System.out.println("ADC B");
				A = (char)(A + B + CY);
				Z = Zcheck(A);
				S = Scheck(A);
				CYcheck(A);
				Pcheck(A);
				cycle += 4;
				PC += 1;
				break;
			}
			
			case 0x89:{
				System.out.println("ADC C");
				A = (char)(A + C + CY);
				Z = Zcheck(A);
				S = Scheck(A);
				CYcheck(A);
				Pcheck(A);
				cycle += 4;
				PC += 1;
				break;
			}
			
			case 0x8a:{
				System.out.println("ADC D");
				A = (char)(A + D + CY);
				Z = Zcheck(A);
				S = Scheck(A);
				CYcheck(A);
				Pcheck(A);
				cycle += 4;
				PC += 1;
				break;
			}
			
			case 0x8b:{
				System.out.println("ADC E");
				A = (char)(A + E + CY);
				Z = Zcheck(A);
				S = Scheck(A);
				CYcheck(A);
				Pcheck(A);
				cycle += 4;
				PC += 1;
				break;
			}
			
			case 0x8c:{
				System.out.println("ADC H");
				A = (char)(A + H + CY);
				Z = Zcheck(A);
				S = Scheck(A);
				CYcheck(A);
				Pcheck(A);
				cycle += 4;
				PC += 1;
				break;
			}
			
			case 0x8d:{
				System.out.println("ADC L");
				A = (char)(A + L + CY);
				Z = Zcheck(A);
				S = Scheck(A);
				CYcheck(A);
				Pcheck(A);
				cycle += 4;
				PC += 1;
				break;
			}
			
			case 0x8e:{
				System.out.println("ADC M");
				A = (char)(A + memory[(H << 8) | L] + CY);
				Z = Zcheck(A);
				S = Scheck(A);
				CYcheck(A);
				Pcheck(A);
				cycle += 7;
				PC += 1;
				break;
			}
			
			case 0x8f:{
				System.out.println("ADC A");
				A = (char)(A + A + CY);
				Z = Zcheck(A);
				S = Scheck(A);
				CYcheck(A);
				Pcheck(A);
				cycle += 4;
				PC += 1;
				break;
			}
			
			case 0x90:{
				System.out.println("SUB B");
				A = (char)(A - B);
				Z = Zcheck(A);
				S = Scheck(A);
				Pcheck(A);
				CYcheck(A);
				cycle += 4;
				PC += 1;
				break;
			}
			
			case 0x91:{
				System.out.println("SUB C");
				A = (char)(A - C);
				Z = Zcheck(A);
				S = Scheck(A);
				Pcheck(A);
				CYcheck(A);
				cycle += 4;
				PC += 1;
				break;
			}
			
			case 0x92:{
				System.out.println("SUB D");
				A = (char)(A - D);
				Z = Zcheck(A);
				S = Scheck(A);
				Pcheck(A);
				CYcheck(A);
				cycle += 4;
				PC += 1;
				break;
			}
			
			case 0x93:{
				System.out.println("SUB E");
				A = (char)(A - E);
				Z = Zcheck(A);
				S = Scheck(A);
				Pcheck(A);
				CYcheck(A);
				cycle += 4;
				PC += 1;
				break;
			}
			
			case 0x94:{
				System.out.println("SUB H");
				A = (char)(A - H);
				Z = Zcheck(A);
				S = Scheck(A);
				Pcheck(A);
				CYcheck(A);
				cycle += 4;
				PC += 1;
				break;
			}
			
			case 0x95:{
				System.out.println("SUB L");
				A = (char)(A - L);
				Z = Zcheck(A);
				S = Scheck(A);
				Pcheck(A);
				CYcheck(A);
				cycle += 4;
				PC += 1;
				break;
			}
			
			case 0x96:{
				System.out.println("SUB M");
				A = (char)(A - memory[(H << 8) | L]);
				Z = Zcheck(A);
				S = Scheck(A);
				Pcheck(A);
				CYcheck(A);
				cycle += 7;
				PC += 1;
				break;
			}
			
			case 0x97:{
				System.out.println("SUB A");
				A = (char)(A - A);
				Z = Zcheck(A);
				S = Scheck(A);
				Pcheck(A);
				CYcheck(A);
				cycle += 4;
				PC += 1;
				break;
			}
			
			case 0x98:{
				System.out.println("SBB B");
				A = (char)(A - B - CY);
				
				CYcheck(A);
				A = Overflowcheck(A);
				Z = Zcheck(A);
				S = Scheck(A);
				Pcheck(A);
				cycle += 4;
				PC += 1;
				break;
			}
			
			case 0x99:{
				System.out.println("SBB C");
				A = (char)(A - C - CY);
				
				CYcheck(A);
				A = Overflowcheck(A);
				Z = Zcheck(A);
				S = Scheck(A);
				Pcheck(A);
				cycle += 4;
				PC += 1;
				break;
			}
			
			case 0x9a:{
				System.out.println("SBB D");
				A = (char)(A - D - CY);
				
				CYcheck(A);
				A = Overflowcheck(A);
				Z = Zcheck(A);
				S = Scheck(A);
				Pcheck(A);
				cycle += 4;
				PC += 1;
				break;
			}
			
			case 0x9b:{
				System.out.println("SBB E");
				A = (char)(A - E - CY);
				
				CYcheck(A);
				A = Overflowcheck(A);
				Z = Zcheck(A);
				S = Scheck(A);
				Pcheck(A);
				cycle += 4;
				PC += 1;
				break;
			}
			
			case 0x9c:{
				System.out.println("SBB H");
				A = (char)(A - H - CY);
				
				CYcheck(A);
				A = Overflowcheck(A);
				Z = Zcheck(A);
				S = Scheck(A);
				Pcheck(A);
				cycle += 4;
				PC += 1;
				break;
			}
			
			case 0x9d:{
				System.out.println("SBB L");
				A = (char)(A - L - CY);
				
				CYcheck(A);
				A = Overflowcheck(A);
				Z = Zcheck(A);
				S = Scheck(A);
				Pcheck(A);
				cycle += 4;
				PC += 1;
				break;
			}
			
			case 0x9e:{
				System.out.println("SBB M");
				A = (char)(A - memory[H << 8 | L] - CY);
				
				CYcheck(A);
				A = Overflowcheck(A);
				Z = Zcheck(A);
				S = Scheck(A);
				Pcheck(A);
				cycle += 7;
				PC += 1;
				break;
			}
			
			case 0x9f:{
				System.out.println("SBB A");
				A = (char)(A - A - CY);
				
				CYcheck(A);
				A = Overflowcheck(A);
				Z = Zcheck(A);
				S = Scheck(A);
				Pcheck(A);
				cycle += 4;
				PC += 1;
				break;
			}
			
			case 0xa1:{
				System.out.println("ANA C");
				A = (char)(A & C);
				Z = Zcheck(A);
				S = Scheck(A);
				Pcheck(A);
				CYcheck(A);
				cycle += 4;
				PC += 1;
				break;
			}
			
			case 0xa2:{
				System.out.println("ANA D");
				A = (char)(A & D);
				Z = Zcheck(A);
				S = Scheck(A);
				Pcheck(A);
				CYcheck(A);
				cycle += 4;
				PC += 1;
				break;
			}
			
			case 0xa3:{
				System.out.println("ANA E");
				A = (char)(A & E);
				Z = Zcheck(A);
				S = Scheck(A);
				Pcheck(A);
				CYcheck(A);
				cycle += 4;
				PC += 1;
				break;
			}
			
			case 0xa4:{
				System.out.println("ANA H");
				A = (char)(A & H);
				Z = Zcheck(A);
				S = Scheck(A);
				Pcheck(A);
				CYcheck(A);
				cycle += 4;
				PC += 1;
				break;
			}
			
			case 0xa5:{
				System.out.println("ANA L");
				A = (char)(A & L);
				Z = Zcheck(A);
				S = Scheck(A);
				Pcheck(A);
				CYcheck(A);
				cycle += 4;
				PC += 1;
				break;
			}
			
			case 0xa6:{
				System.out.println("ANA L");
				A = (char)(A & memory[H << 8 | L]);
				Z = Zcheck(A);
				S = Scheck(A);
				Pcheck(A);
				CYcheck(A);
				cycle += 7;
				PC += 1;
				break;
			}
	
			case 0xa7:{
				System.out.println("ANA A");
				A = (char)(A & A);
				Z = Zcheck(A);
				S = Scheck(A);
				Pcheck(A);
				CYcheck(A);
				cycle += 4;
				PC += 1;
				break;
			}

			case 0xa8:{
				System.out.println("XRA B");
				A = (char)(A ^ B);
				Z = Zcheck(A);
				S = Scheck(A);
				Pcheck(A);
				CYcheck(A);
				cycle += 4;
				PC += 1;
				break;
			}
			/*		
			case 0xa9:{
				System.out.println("XRA C");
				A = (char)(A ^ C);
				Z = Zcheck(A);
				S = Scheck(A);
				Pcheck(A);
				CYcheck(A);
				cycle += 4;
				PC += 1;
				break;
			}
			
			case 0xaa:{
				System.out.println("XRA D");
				A = (char)(A ^ D);
				Z = Zcheck(A);
				S = Scheck(A);
				Pcheck(A);
				CYcheck(A);
				cycle += 4;
				PC += 1;
				break;
			}
			
			case 0xab:{
				System.out.println("XRA E");
				A = (char)(A ^ E);
				Z = Zcheck(A);
				S = Scheck(A);
				Pcheck(A);
				CYcheck(A);
				cycle += 4;
				PC += 1;
				break;
			}
			
			case 0xac:{
				System.out.println("XRA H");
				A = (char)(A ^ H);
				Z = Zcheck(A);
				S = Scheck(A);
				Pcheck(A);
				CYcheck(A);
				cycle += 4;
				PC += 1;
				break;
			}*/
			
			case 0xad:{
				System.out.println("XRA L");
				A = (char)(A ^ L);
				Z = Zcheck(A);
				S = Scheck(A);
				Pcheck(A);
				CYcheck(A);
				cycle += 4;
				PC += 1;
				break;
			}
			
			case 0xae:{
				System.out.println("XRA M");
				A = (char)(A ^ memory[(H << 8) | L]);
				Z = Zcheck(A);
				S = Scheck(A);
				Pcheck(A);
				CYcheck(A);
				cycle += 7;
				PC += 1;
				break;
			}
			
			case 0xaf:{
				System.out.println("XRA A");
				A = (char)(A ^ A);
				Z = Zcheck(A);
				S = Scheck(A);
				Pcheck(A);
				CYcheck(A);
				cycle += 4;
				PC += 1;
				break;
			}
			
			case 0xb0:{
				System.out.println("ORA B");
				A = (char)(A | B);
				Z = Zcheck(A);
				S = Scheck(A);
				Pcheck(A);
				CYcheck(A);
				cycle += 4;
				PC += 1;
				break;
			}
			/*
			case 0xb1:{
				System.out.println("ORA C");
				A = (char)(A | C);
				Z = Zcheck(A);
				S = Scheck(A);
				Pcheck(A);
				CYcheck(A);
				cycle += 4;
				PC += 1;
				break;
			}
			
			case 0xb2:{
				System.out.println("ORA D");
				A = (char)(A | D);
				Z = Zcheck(A);
				S = Scheck(A);
				Pcheck(A);
				CYcheck(A);
				cycle += 4;
				PC += 1;
				break;
			}
			
			case 0xb3:{
				System.out.println("ORA E");
				A = (char)(A | E);
				Z = Zcheck(A);
				S = Scheck(A);
				Pcheck(A);
				CYcheck(A);
				cycle += 4;
				PC += 1;
				break;
			}*/
			
			case 0xb4:{
				System.out.println("ORA H");
				A = (char)(A | H);
				Z = Zcheck(A);
				S = Scheck(A);
				Pcheck(A);
				CYcheck(A);
				cycle += 4;
				PC += 1;
				break;
			}
			
			/*case 0xb5:{
				System.out.println("ORA L");
				A = (char)(A | L);
				Z = Zcheck(A);
				S = Scheck(A);
				Pcheck(A);
				CYcheck(A);
				cycle += 4;
				PC += 1;
				break;
			}*/
			
			case 0xb6:{
				System.out.println("ORA M");
				A = (char)(A | memory[(H << 8) | L]);
				Z = Zcheck(A);
				S = Scheck(A);
				Pcheck(A);
				CYcheck(A);
				cycle += 7;
				PC += 1;
				break;
			}
			
			/*case 0xb7:{
				System.out.println("ORA A");
				A = (char)(A | A);
				Z = Zcheck(A);
				S = Scheck(A);
				Pcheck(A);
				CYcheck(A);
				cycle += 4;
				PC += 1;
				break;
			}*/
			
			case 0xb8:{
				System.out.println("CMP B");
				char x = (char)(B - A);
				Z = Zcheck(x);
				S = Scheck(x);
				CYcheck(x);
				Pcheck(x);
				cycle += 4;
				PC += 1;
				break;
			}
			
			case 0xb9:{
				System.out.println("CMP C");
				char x = (char)(C - A);
				Z = Zcheck(x);
				S = Scheck(x);
				CYcheck(x);
				Pcheck(x);
				cycle += 4;
				PC += 1;
				break;
			}
			
			case 0xba:{
				System.out.println("CMP D");
				char x = (char)(D - A);
				Z = Zcheck(x);
				S = Scheck(x);
				CYcheck(x);
				Pcheck(x);
				cycle += 4;
				PC += 1;
				break;
			}
			
			case 0xbb:{
				System.out.println("CMP E");
				char x = (char)(E - A);
				Z = Zcheck(x);
				S = Scheck(x);
				CYcheck(x);
				Pcheck(x);
				cycle += 4;
				PC += 1;
				break;
			}
			
			case 0xbc:{
				System.out.println("CMP H");
				char x = (char)(H - A);
				Z = Zcheck(x);
				S = Scheck(x);
				CYcheck(x);
				Pcheck(x);
				cycle += 4;
				PC += 1;
				break;
			}
			
			/*case 0xbd:{
				System.out.println("CMP L");
				char x = (char)(L - A);
				Z = Zcheck(x);
				S = Scheck(x);
				CYcheck(x);
				Pcheck(x);
				cycle += 4;
				PC += 1;
				break;
			}
			
			case 0xbe:{
				System.out.println("CMP M");
				char x = (char)(memory[(H << 8) | L] - A);
				Z = Zcheck(x);
				S = Scheck(x);
				CYcheck(x);
				Pcheck(x);
				cycle += 7;
				PC += 1;
				break;
			}*/
			
			case 0xc0:{
				System.out.println("RZ");
				if(Z == 0) {
					PC = (char)(stack[SP] | (stack[SP + 1] << 8));
					cycle += 11;
					SP += 2;
					break;
				}else {
					cycle += 5;
					PC += 1;
				}break;
			}
			
			case 0xc1:{
				System.out.println("POP B");
				C = stack[SP];
				B = stack [SP+1];
				SP += 2;
				cycle += 10;
				PC += 1;
				break;
			}
			
			case 0xc2:{
				System.out.println("JNZ #" + Integer.toHexString(memory[PC+2]) + Integer.toHexString(memory[PC+1]));
				if(Z == 0) {
					cycle += 10;
					PC = (char)((memory[PC+2] << 8) | memory[PC+1]);
				}else {
					cycle += 10;
					PC += 3;
				}
				break;
			}
			
			case 0xc3:{
				System.out.println("JMP #" + Integer.toHexString(memory[PC+2]) + Integer.toHexString(memory[PC+1]));
				PC = (char)((memory[PC+2] << 8) | memory[PC+1]);
				cycle += 10;
				break;
			}
			
			case 0xc4:{
				System.out.println("CNZ #" + Integer.toHexString(memory[PC+2]) + Integer.toHexString(memory[PC+1]));
				if(Z == 0) {
					char ret = (char)(PC + 3);
					stack[SP - 1] = (char) ((ret >> 8) & 0xff);
					stack[SP - 2] = (char) (ret & 0xff);
					SP -= 2;
					cycle += 17;
					PC = (char)((memory[PC+2] << 8) | memory[PC+1]);
				}else {
					cycle += 11;
					PC += 3;
				}
				break;
			}
			
			case 0xc5:{
				System.out.println("PUSH B");
				stack[SP - 1] = B;
				stack[SP - 2] = C;
				SP -= 2;
				cycle += 11;
				PC += 1;
				break;
			}
			
			case 0xc6:{
				System.out.println("ADI #" + Integer.toHexString(memory[PC+1]));
				A = (char) (A + memory[PC+1]);
				
				A = Overflowcheck(A);
				Z = Zcheck(A);
				S = Scheck(A);
				CYcheck(A);
				Pcheck(A);
				cycle += 7;
				PC += 2;
				break;
			}
			
			case 0xc8:{
				System.out.println("RZ");
				if(Z == 1) {
					PC = (char)(stack[SP] | (stack[SP + 1] << 8));
					cycle += 11;
					SP += 2;
					break;
				}else {
					cycle += 5;
					PC += 1;
				}break;
			}

			case 0xc9:{
				System.out.println("RET");
				PC = (char)((stack[SP + 1] << 8) | stack[SP]);
				cycle += 10;
				SP += 2;
				break;
			}
			
		case 0xca:{
				System.out.println("JZ #" + Integer.toHexString(memory[PC+2]) + Integer.toHexString(memory[PC+1]));
				if(Z == 1) {
					cycle += 10;
					PC = (char)((memory[PC+2] << 8) | memory[PC+1]);
				}else {
					cycle += 10;
					PC += 3;
				}
				break;
			}
			
		case 0xcc:{
				System.out.println("CZ #" + Integer.toHexString(memory[PC+2]) + Integer.toHexString(memory[PC+1]));
				if(Z == 1) {
					char ret = (char)(PC + 3);
					stack[SP -1] = (char) ((ret >> 8) & 0xff);
					stack[SP -2] = (char) (ret & 0xff);
					SP -= 2;
					cycle += 17;
					PC = (char)((memory[PC+2] << 8) | memory[PC+1]);
				}else {
					cycle += 11;
					PC += 3;
				}
				break;
			}
			
			case 0xcd:{
				System.out.println("CALL #" + Integer.toHexString(memory[PC+2]) + Integer.toHexString(memory[PC+1]));
				char ret = (char)(PC + 3);
				stack[SP - 1] = (char) ((ret >> 8));
				stack[SP - 2] = (char) (ret & 0xff);
				SP -= 2;
				cycle += 17;
				PC = (char)((memory[PC+2] << 8) | memory[PC+1]);
				break;
			}
			
			/*case 0xce:{
				System.out.println("ACI #" + Integer.toHexString(memory[PC+1]));
				A = (char)(A + memory[PC+1] + CY);
				
				A = Overflowcheck(A);
				Z = Zcheck(A);
				S = Scheck(A);
				CYcheck(A);
				Pcheck(A);
				cycle += 7;
				PC += 2;
				break;
			}*/
			
			case 0xd0:{
				System.out.println("RNC");
				if(CY == 0) {
					PC = (char)(stack[SP] | (stack[SP + 1] << 8));
					cycle += 11;
					SP += 2;
					break;
				}else {
					cycle += 5;
					PC += 1;
				}break;
			}
			
			case 0xd1:{
				System.out.println("POP D");
				E = stack[SP];
				D = stack [SP+1];
				SP += 2;
				cycle += 10;
				PC += 1;
				break;
			}
			
			case 0xd2:{
				System.out.println("JNC #" + Integer.toHexString(memory[PC+2]) + Integer.toHexString(memory[PC+1]));
				if(CY == 0) {
					PC = (char)((memory[PC+2] << 8) | memory[PC+1]);
					cycle += 10;
				}else {
					cycle += 10;
					PC += 3;
				}
				break;
			}
			
			case 0xd3:{
				System.out.println("OUT #" + Integer.toHexString(memory[PC+1]));
				char port = memory[PC+1];
				Machine.Output(port);
				cycle += 3;
				PC += 2;
				break;
			}
			
			case 0xd4:{
				System.out.println("CNC #" + Integer.toHexString(memory[PC+2]) + Integer.toHexString(memory[PC+1]));
				if(CY == 0) {
					char ret = (char)(PC + 3);
					stack[SP -1] = (char) ((ret >> 8) & 0xff);
					stack[SP -2] = (char) (ret & 0xff);
					SP -= 2;
					cycle += 17;
					PC = (char)((memory[PC+2] << 8) | memory[PC+1]);
				}else {
					cycle += 11;
					PC += 3;
				}
				break;
			}
			
			case 0xd5:{
				System.out.println("PUSH D");
				stack[SP - 1] = D;
				stack[SP - 2] = E;
				SP -= 2;
				cycle += 11;
				PC += 1;
				break;
			}
			
			case 0xd6:{
				System.out.println("SUI #" + Integer.toHexString(memory[PC+1]));
				A = (char)(A - memory[PC+1]);
				
				CYcheck(A);
				A = Overflowcheck(A);
				Z = Zcheck(A);
				S = Scheck(A);
				Pcheck(A);
				cycle += 7;
				PC += 2;
				break;
			}
			
			case 0xd8:{
				System.out.println("RC");
				if(CY == 1) {
					PC = (char)(stack[SP] | (stack[SP + 1] << 8));
					cycle += 11;
					SP += 2;
					break;
				}else {
					cycle += 5;
					PC += 1;
				}break;
			}
			
			case 0xda:{
				System.out.println("JC #" + Integer.toHexString(memory[PC+2]) + Integer.toHexString(memory[PC+1]));
				if(CY == 1) {
					cycle += 10;
					PC = (char)((memory[PC+2] << 8) | memory[PC+1]);
				}else {
					cycle += 10;
					PC += 3;
				}
				break;
			}
			
			case 0xdb:{
				System.out.println("IN #" + Integer.toHexString(memory[PC+1]));
				char port = memory[PC+1];
				A = Machine.Input(port);
				cycle += 10;
				PC += 2;
				break;	
			}
			
			case 0xdc:{
				System.out.println("CC #" + Integer.toHexString(memory[PC+2]) + Integer.toHexString(memory[PC+1]));
				if(CY == 1) {
					char ret = (char)(PC + 3);
					stack[SP -1] = (char) ((ret >> 8) & 0xff);
					stack[SP -2] = (char) (ret & 0xff);
					SP -= 2;
					cycle += 17;
					PC = (char)((memory[PC+2] << 8) | memory[PC+1]);
				}else {
					cycle += 11;
					PC += 3;
				}
				break;
			}
			
			case 0xde:{
				System.out.println("SBI #" + Integer.toHexString(memory[PC+1]));
				A = (char)(A - memory[PC+1] - CY);
				
				A = Overflowcheck(A);
				
				if(A == 0xff) {
					CY = 1;
				}else {
					CY = 0;
				}
				
				Z = Zcheck(A);
				S = Scheck(A);
				Pcheck(A);
				cycle += 7;
				PC += 2;
				break;
			}
			
			case 0xe0:{
				System.out.println("RPO");
				if(P == 0) {
					PC = (char)(stack[SP] | (stack[SP + 1] << 8));
					cycle += 11;
					SP += 2;
					break;
				}else {
					cycle += 5;
					PC += 1;
				}break;
			}
			
			case 0xe1:{
				System.out.println("POP H");
				L = stack[SP];
				H = stack [SP+1];
				SP += 2;
				cycle += 10; 
				PC += 1;
				break;
			}
			
/*			case 0xe2:{
				System.out.println("JPO #" + Integer.toHexString(memory[PC+2]) + Integer.toHexString(memory[PC+1]));
				if(P == 0) {
					cycle += 10;
					PC = (char)((memory[PC+2] << 8) | memory[PC+1]);
				}else {
					cycle += 10;
					PC += 3;	
				}
				break;
			}*/
			
			case 0xe3:{
				System.out.println("XTHL");
				char Lcontent = L;
				char Hcontent = H;
				char FirstSP = stack[SP];
				char SecondSP = stack[SP+1];
				
				L = FirstSP;
				H = SecondSP;
				stack[SP] = Lcontent;
				stack[SP+1] = Hcontent;
				PC += 1;
				cycle += 18;
				break;
			}
			
/*			case 0xe4:{
				System.out.println("CPO #" + Integer.toHexString(memory[PC+2]) + Integer.toHexString(memory[PC+1]));
				if(P == 0) {
					char ret = (char)(PC + 3);
					stack[SP -1] = (char) ((ret >> 8) & 0xff);
					stack[SP -2] = (char) (ret & 0xff);
					SP -= 2;
					cycle += 17;
					PC = (char)((memory[PC+2] << 8) | memory[PC+1]);
				}else {
					cycle += 11;
					PC += 3;
				}
				break;
			}*/
			
			case 0xe5:{
				System.out.println("Push H");
				stack[SP - 1] = H;
				stack[SP - 2] = L;
				SP -= 2;
				cycle += 11;
				PC += 1;
				break;
			}
			
			case 0xe6:{
				System.out.println("ANI #" + Integer.toHexString(memory[PC+1]));
				A = (char)(A & memory[PC+1]);
				Z = Zcheck(A);
				S = Scheck(A);
				CYcheck(A);
				Pcheck(A);
				cycle += 7;
				PC += 2;
				break;
			}
			
			/*case 0xe8:{
				System.out.println("RPE");
				if(P == 1) {
					PC = (char)(stack[SP] | (stack[SP + 1] << 8));
					cycle += 11;
					SP += 2;
					break;
				}else {
					cycle += 5;
					PC += 1;
				}break;
			}*/
			
			case 0xe9:{
				System.out.println("PCHL");
				char address = (char)(H << 8 | L);
				PC = address;
				cycle += 5;
				break;
			}
			
			/*case 0xea:{
				System.out.println("JPE #" + Integer.toHexString(memory[PC+2]) + Integer.toHexString(memory[PC+1]));
				if(P == 1) {
					cycle += 10;
					PC = (char)((memory[PC+2] << 8) | memory[PC+1]);
				}else {
					cycle += 10;
					PC += 3;	
				}
				break;
			}*/
			
			case 0xeb:{
				System.out.println("XCHG");
				char HTemp = H;
				char LTemp = L;
				H = D;
				L = E;
				D = HTemp;
				E = LTemp;
				cycle += 5;
				PC += 1;
				break;
			}
			
			/*case 0xec:{
				System.out.println("CPE #" + Integer.toHexString(memory[PC+2]) + Integer.toHexString(memory[PC+1]));
				if(P == 1) {
					char ret = (char)(PC + 3);
					stack[SP -1] = (char) ((ret >> 8) & 0xff);
					stack[SP -2] = (char) (ret & 0xff);
					SP -= 2;
					cycle += 17;
					PC = (char)((memory[PC+2] << 8) | memory[PC+1]);
				}else {
					cycle += 11;
					PC += 3;
				}
				break;
			}
			
			case 0xee:{
				System.out.println("XRI #" + Integer.toHexString(memory[PC+1]));
				A = (char)(A ^ memory[PC+1]);
				Z = Zcheck(A);
				S = Scheck(A);
				CYcheck(A);
				Pcheck(A);
				cycle += 7;
				PC += 2;
				break;
			}
			
			case 0xf0:{
				System.out.println("RP");
				if(P == 1) {
					PC = (char)(stack[SP] | (stack[SP + 1] << 8));
					cycle += 11;
					SP += 2;
					break;
				}else {
					cycle += 5;
					PC += 1;
				}break;
			}*/
			
			case 0xf1:{//TODO FIX THIS Z still error
				System.out.println("POP PSW");
				A = stack[SP+1];
				S = (char) ((stack[SP] & 0x80) >> 7);
				//Z = (char) ((stack[SP] & 0x40) >> 6);
				AC = (char) ((stack[SP] & 0x10) >> 4);
				P = (char) ((stack[SP] & 0x4) >> 2);
				CY = (char) (stack[SP] & 0x1);
		
				SP += 2;
				cycle += 11;
				PC += 1;
				break;
			}
			
			/*case 0xf2:{
				System.out.println("JP #" + Integer.toHexString(memory[PC+2]) + Integer.toHexString(memory[PC+1]));
				if(P == 1) {
					cycle += 10;
					PC = (char)((memory[PC+2] << 8) | memory[PC+1]);
				}else {
					cycle += 10;
					PC += 3;	
				}
				break;
			}
			
			case 0xf4:{
				System.out.println("CP #" + Integer.toHexString(memory[PC+2]) + Integer.toHexString(memory[PC+1]));
				if(P == 1) {
					char ret = (char)(PC + 3);
					stack[SP -1] = (char) ((ret >> 8) & 0xff);
					stack[SP -2] = (char) (ret & 0xff);
					SP -= 2;
					cycle += 17;
					PC = (char)((memory[PC+2] << 8) | memory[PC+1]);
				}else {
					cycle += 11;
					PC += 3;
				}
				break;
			}*/
			
			case 0xf5:{
				System.out.println("PUSH PSW");
				char psw = (char) ((S << 7)| (Z << 6) | (AC << 4) | (P << 2) | (CY) );
				stack[SP-1] = A;
				stack[SP-2] = psw;
				SP -= 2;
				cycle += 11;
				PC += 1;
				break;
			}
			
			case 0xf6:{
				System.out.println("ORI #" + Integer.toHexString(memory[PC+1]));
				A = (char)(A | memory[PC+1]);
				Z = Zcheck(A);
				S = Scheck(A);
				CYcheck(A);
				Pcheck(A);
				cycle += 7;
				PC += 2;
				break;
			}
			/*
			case 0xf8:{
				System.out.println("RM");
				if(S == 1) {
					PC = (char)(stack[SP] | (stack[SP + 1] << 8));
					cycle += 11;
					SP += 2;
					break;
				}else {
					cycle += 5;
					PC += 1;
				}break;
			}*/
			
			case 0xfa:{
				System.out.println("JM #" + Integer.toHexString(memory[PC+2]) + Integer.toHexString(memory[PC+1]));
				if(S == 1) {
					cycle += 10;
					PC = (char)((memory[PC+2] << 8) | memory[PC+1]);
				}else {
					cycle += 10;
					PC += 3;	
				}
				break;
			}
			
			
			case 0xfb:{
				System.out.println("EI");
				Machine.setEnableInterrupt(true);
				PC += 1;
				cycle += 4;
				break;
			}
			
			/*
			case 0xfc:{
				System.out.println("CM #" + Integer.toHexString(memory[PC+2]) + Integer.toHexString(memory[PC+1]));
				if(S == 1) {
					char ret = (char)(PC + 3);
					stack[SP -1] = (char) ((ret >> 8) & 0xff);
					stack[SP -2] = (char) (ret & 0xff);
					SP -= 2;
					cycle += 17;
					PC = (char)((memory[PC+2] << 8) | memory[PC+1]);
				}else {
					cycle += 11;
					PC += 3;
				}
				break;
			}*/
			
			case 0xfe:{//TODO Clean up, hacky
				System.out.println("CPI #" + Integer.toHexString(memory[PC+1]));
				char x = (char)(memory[PC+1]);
				char p = (char) (A - memory[PC+1]);
				p = Overflowcheck(p);
				if(A == x) {
					Z = 1;
				}else {
					Z = 0;
				}
				
				if(A > x) {
					CY = 0;
				}
				
				if(A < x) {
					CY = 1;
				}
				S = Scheck(p);
				Pcheck(x);
				cycle += 7;
				PC +=2;
				break;
			}
			
			default:{
				System.out.println("Unimplemented");
				System.exit(0);
				break;
			}
		}
	}

public static int getCycle() {
	return cycle;
}

public static char getPC() {
	return PC;
}

void Flagstatus() {
		if(Z == 1) {
			System.out.print("Z");
		}else {
			System.out.print("-");
		}
		
		if(S == 1) {
			System.out.print("S");
		}else {
			System.out.print("-");
		}
		
		if(P == 1) {
			System.out.print("P");
		}else {
			System.out.print("-");
		}
		
		if(CY == 1) {
			System.out.print("CY ");
		}else {
			System.out.print("- ");
		}
	}
	
void Registerstatus() {
	System.out.print("A(" + Integer.toHexString(A) + ") ");
	System.out.print("BC(" + Integer.toHexString(B) + ")" + "(" + Integer.toHexString(C) + ") ");
	System.out.print("DE(" + Integer.toHexString(D) + ")" + "(" + Integer.toHexString(E) + ") ");
	System.out.print("HL(" + Integer.toHexString(H) + ")" + "(" + Integer.toHexString(L) + ") ");
	System.out.println("SP(" + Integer.toHexString(SP) + ")");
}

char Zcheck(char x) {
	if(x == 0){
		return 1;
	}else {
		return 0;
	}
}

char Scheck(char x) {
	if((x & 0x80) == 0x80) {
		return 1;
	}else {
		return 0;
	}
}

void CYcheck(char x) {
	if(x > 0xff){
		CY = 1;
	}else {
		CY = 0;
	}
}

void Pcheck(int x) {
	int i;
	int p = 0;
    for(i = 0; i < 8; i++){
        if((x &(1 << i)) >> i == 1){
        	p++;
        }
    }
	if((p % 2) == 0 | p == 0) {
		P = 1;
	}else {
		P = 0;
	}
}

char Overflowcheck(char x) {
	x = (char) (x & 0xff);
	return x;
}

public void loadProgram(String file) throws IOException {
		DataInputStream input = null;
		try {
			input = new DataInputStream(new FileInputStream(new File(file))); //Read data from file
			
			int offset = 0;
			while(input.available() > 0) { //Move data to memory
				memory[0x0 + offset] = (char)(input.readByte() & 0xFF); //Prevent Negative Values
				offset++;
			}
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			System.exit(0);
		} finally {
			if (input != null) {
				try { input.close();} catch (IOException ex) {}
			}
		}
	}
}
