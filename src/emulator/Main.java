package emulator;

import java.io.IOException;
import java.io.PrintStream;

public class Main{
	public static void main(String[] args) throws IOException {
		PrintStream fileOut = new PrintStream("./out2.txt");
		System.setOut(fileOut);
		new Machine().runMachine();
	}
}

