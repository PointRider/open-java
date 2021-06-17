package startTheWorld;

import java.awt.Graphics;
import java.awt.Image;
import javax.swing.JPanel;

class BackgroundPanel extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1282182464133882226L;
	Image im;
	
	public BackgroundPanel(Image im) {
	   this.im=im;
	   this.setOpaque(true);
	}
	//Draw the back ground.
	public void paintComponent(Graphics g) {
	   super.paintComponents(g);
	   g.drawImage(im,0,0,this.getWidth(),this.getHeight(),this);
	
	}
}