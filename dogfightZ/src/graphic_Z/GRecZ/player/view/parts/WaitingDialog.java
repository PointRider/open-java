package graphic_Z.GRecZ.player.view.parts;

import java.awt.Container;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.WindowConstants;

public class WaitingDialog extends JDialog {

    /**
     * 
     */
    private static final long serialVersionUID = -6161816853139039967L;

    public JLabel  messageShow;
    
    public WaitingDialog(String msg) {
        super();
        //jf = new JFrame();
        setUndecorated(true);
        setResizable(false);
        setBounds(
            (java.awt.Toolkit.getDefaultToolkit().getScreenSize().width  >> 1) - 150, 
            (java.awt.Toolkit.getDefaultToolkit().getScreenSize().height >> 1) - 75, 
            300, 150
        );
        setTitle("a");
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        
        Container container = getContentPane();
        container.setLayout(null);
        
        messageShow = new JLabel(msg);
        messageShow.setBounds(20, 15, 280, 130);
        container.add(messageShow);
    }
}
