package emulator;

import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JPanel;

import intel.Chip;

public class InvaderGraphics extends JPanel{
	
	private Chip chip;

	public InvaderGraphics(Chip c) {
		this.chip = chip;
	}
	
	public void paint(Graphics g) {
		int i;
		int j;
		int offsetx = 256;
		int offsety = 0;
		char[] framebuffer = Chip.memory;
	    for (i=0; i< 224; i++){
	        for (j = 0; j < 256; j+= 8){
	        	int p;
	        	char pix = framebuffer[9216 + ((i*(256/8)) + j/8)];
       		 	char pixArray[] = new char[8];
       		 	pixArray[0] = (char) (pix & 0x1);
       		 	pixArray[1] = (char) ((pix & 0x2) >> 1);
       		 	pixArray[2] = (char) ((pix & 0x4) >> 2);
       		 	pixArray[3] = (char) ((pix & 0x8) >> 3);
       		 	pixArray[4] = (char) ((pix & 0x10) >> 4);
       		 	pixArray[5] = (char) ((pix & 0x20) >> 5);
       		 	pixArray[6] = (char) ((pix & 0x40) >> 6);
       		 	pixArray[7] = (char) ((pix & 0x80) >> 7);
	        	 for (p=0; p<8; p++){ 
	                 if (pixArray[p] == 1){
	                	 g.setColor(Color.white);
	                 }else{
	                     g.setColor(Color.black);
	                 }
	                 offsetx --;
	                 g.fillRect(offsety * 2, offsetx * 2, 1 * 2, 1 * 2);
	             }
	        	if(offsetx == 0) {
	        		offsetx = 256;
	             	offsety += 1;
	              }
	        }
	    }
	}
}
