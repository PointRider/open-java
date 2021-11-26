package dogfight_Z.dogLog.controller;

import java.awt.Color;
import java.awt.event.KeyEvent;

import javax.swing.JTextArea;

import dogfight_Z.dogLog.model.PlayerProfile;
import dogfight_Z.dogLog.service.PlayerProfileServiceImp;
import dogfight_Z.dogLog.view.Menu;
import graphic_Z.Common.Operation;
import graphic_Z.HUDs.CharButton;
import graphic_Z.HUDs.CharLabel;
import graphic_Z.HUDs.CharPasswordEdit;
import graphic_Z.HUDs.CharSingleLineTextEdit;
import graphic_Z.HUDs.Operable;
import graphic_Z.HUDs.Widget;

public class PilotLogin extends Menu {

    private CharLabel lblTiltle;
    
    private CharLabel lblUsername;
    private CharSingleLineTextEdit tbUsername;
    
    private CharLabel lblUserPass;
    private CharPasswordEdit pasUserPass;
    
    private CharButton btnLogin;
    private CharButton btnCancel;
    
    private Widget widget[];
    
    private Menu currentDialog;

    public PilotLogin(String[] args, JTextArea screen, int resolutionX, int resolutionY) {
        super(args, screen, 4, resolutionX, resolutionY);
        currentDialog = null;
        lblTiltle = new CharLabel(
            screenBuffer, 
            1, 
            resolution, 
            "DOGFIGHT Z - LOGIN", 
            (resolution[0] >> 1) - 12, 
            6,
            false
        );
        
        lblUsername = new CharLabel(
            screenBuffer, 
            2, 
            resolution, 
            "Username:", 
            8, 
            12,
            false
        );
        
        tbUsername = new CharSingleLineTextEdit(
            screenBuffer, 
            resolution, 
            20, 
            12, 
            36
        );
        
        lblUserPass = new CharLabel(
            screenBuffer, 
            3, 
            resolution, 
            "Password:", 
            8, 
            18,
            false
        );
        
        pasUserPass = new CharPasswordEdit(
            screenBuffer, 
            resolution, 
            20, 
            18, 
            36
        );
        
        btnLogin = new CharButton(
            screenBuffer, 
            resolution, 
            "LOGIN", 
            10, 
            29,
            20,
            new Operable() {
                @Override
                public Operation call() {
                    PlayerProfile player = new PlayerProfile();
                    player.setUserName(tbUsername.getText());
                    player.setUserPass(pasUserPass.getText());
                    player = PlayerProfileServiceImp.getPlayerProfileService().login(player);
                    if(player == null) {
                        currentDialog = new TipsConfirmMenu(
                            args, 
                            screenBuffer,
                            "登录失败，请检查登录名和口令是否正确", 
                            "YES!", 
                            "NO.", 
                            screen, 
                            resolution[0], 
                            resolution[1], 
                            new Runnable() {
                                @Override
                                public void run() {
                                    currentDialog = null;
                                }
                            }, 
                            new Runnable() {
                                @Override
                                public void run() {
                                    currentDialog = null;
                                }
                            }
                        );
                        return new Operation(false, null, null, new Color(86, 32, 32), null, null);
                    }
                    return new Operation(true, new UserOperationMenu(args, player, screen, resolution[0], resolution[1]), null, new Color(32, 86, 32), null, null);
                }
                
            }
        );
        
        btnCancel = new CharButton(
            screenBuffer, 
            resolution, 
            "Cancel", 
            34, 
            29,
            20,
            new Operable() {
                @Override
                public Operation call() {
                    return new Operation(true, null, null, null, null, null);
                }
            }
        );
        
        widget = new Widget[] {
            tbUsername,
            pasUserPass,
            btnLogin,
            btnCancel
        };
    }

    @Override
    public void getRefresh() {
        
        if(currentDialog == null) {
            for(int i = 0, j = widget.length; i < j; ++i) {
                if(i == getSelectedIndex()) widget[i].setSelected(true);
                widget[i].printNew();
            }
            
            lblTiltle.printNew();
            lblUsername.printNew();
            lblUserPass.printNew();
        } else currentDialog.getRefresh();
        
    }

    @Override
    public Operation putKeyReleaseEvent(int keyCode) {
        if(currentDialog == null) {
            int selected = getSelectedIndex();
            
            Operation opt = null;
            switch(keyCode) {
            case KeyEvent.VK_UP: 
                indexUp();
                break;
            case KeyEvent.VK_DOWN: 
                indexDown();
                break;
            case KeyEvent.VK_RIGHT:
                if(selected >= 2) indexDown();
                else {
                    widget[selected].setSelected(true);
                    widget[selected].getControl(keyCode);
                }
                break;
            case KeyEvent.VK_LEFT:
                if(selected >= 2) indexUp();
                else {
                    widget[selected].setSelected(true);
                    widget[selected].getControl(keyCode);
                }
                break;
            case KeyEvent.VK_ENTER:
                if(selected >= 0  &&  selected < 2) {
                    //文本框
                    indexDown();
                } else {
                    //按钮
                    widget[selected].setSelected(true);
                    opt = widget[selected].call();
                }
                break;
            case KeyEvent.VK_ESCAPE:
                opt = new Operation(true, null, null, null, null, null);
                break;
            default:
                if(selected >= 0  &&  selected < widget.length) {
                    widget[selected].setSelected(true);
                    widget[selected].getControl(keyCode);
                }
                break;
            }
            
            return opt;
        } else return currentDialog.putKeyReleaseEvent(keyCode);
    }

    @Override
    public Operation putKeyTypeEvent(int keyChar) {
        if(keyChar == '\n'  ||  keyChar == '\r') return null;
        int selected = getSelectedIndex();

        widget[selected].setSelected(true);
        widget[selected].getInput(keyChar);
        
        return null;
    }

    @Override
    public Operation putKeyPressEvent(int keyCode) {
        return null;
    }

    @Override
    public Operation beforeRefreshNotification() {
        return null;
    }

    @Override
    public Operation afterRefreshNotification() {
        return null;
    }

    @Override
    protected void beforeRefreshEvent() {
        
    }

    @Override
    protected void afterRefreshEvent() {
        
    }

}
