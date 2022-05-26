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

public class PilotRegist extends Menu {
    
    private CharLabel lblTiltle;

    private CharLabel lblUsername;
    private CharSingleLineTextEdit tbUsername;
    
    private CharLabel lblUserPass;
    private CharPasswordEdit pasUserPass;

    private CharLabel lblUserPassConfirm;
    private CharPasswordEdit pasUserPassConfirm;
    
    private CharLabel lblUserNick;
    private CharSingleLineTextEdit tbUserNick;
    
    private CharButton btnUserScreenSetup;
    private CharButton btnUserRecordShareSetup;
    
    private CharButton btnConfirm;
    private CharButton btnCancel;
    
    private Widget widget[];
    
    private int setScreenSize[];
    private int shareMyRecord = 1;
    
    public PilotRegist(String args[], JTextArea screen, int resolutionX, int resolutionY) {
        super(args, screen, 0, resolutionX, resolutionY);
        
        setScreenSize = null;
        
        lblTiltle = new CharLabel(
            screenBuffer, 1, resolution, 
            "DOGFIGHT Z - PILOT REGIST", 
            (resolution[0] >> 1) - 12, 6, false
        );
        
        lblUsername = new CharLabel(screenBuffer, 2,  resolution, "Username:", 8, 12, false);
        tbUsername = new CharSingleLineTextEdit(screenBuffer, resolution, 20, 12, 36, "Use this and password to login.");
        lblUserPass = new CharLabel(screenBuffer, 3, resolution, "Password:", 8, 15, false);
        pasUserPass = new CharPasswordEdit(screenBuffer, resolution, 20, 15, 36, "This can be reset after login.");
        lblUserPassConfirm = new CharLabel(screenBuffer, 4, resolution, " Confirm:", 8, 18, false);
        pasUserPassConfirm = new CharPasswordEdit(screenBuffer, resolution, 20, 18, 36, "Repeat again.");
        lblUserNick = new CharLabel(screenBuffer, 5, resolution, "Nickname:", 8, 21, false);
        tbUserNick = new CharSingleLineTextEdit(screenBuffer, resolution, 20, 21, 36);
        
        btnUserRecordShareSetup = new CharButton(
            screenBuffer, resolution, "Share my game record: YES", 10, 26, 44,
            new Operable() {
                @Override
                public Operation call() {
                    showConfirmDialog("你想要将自己的游戏记录设置为所有用户可见吗？","YES!", "NO.", 
                        new Runnable() {
                            @Override
                            public void run() {
                                shareMyRecord = 1;
                                closeDialog();
                            }
                        }, 
                        new Runnable() {
                            @Override
                            public void run() {
                                shareMyRecord = 0;
                                closeDialog();
                            }
                        }
                    );
                    return new Operation(false, null, null, null, null, null);
                }
            }
        );

        btnUserScreenSetup = new CharButton(
            screenBuffer, resolution, "Setup Screen Size", 10, 29, 44,
            new Operable() {
                @Override
                public Operation call() {
                    return new Operation(false, new ScreenResize(args, screen, resolution[0], resolution[1], setScreenSize), null, null, null, null);
                }
            }
        );
        
        btnConfirm = new CharButton(
            screenBuffer, resolution, "O K", 10, 32, 20,
            new Operable() {
                @Override
                public Operation call() {
                    String uname = tbUsername.getText();
                    String pass = pasUserPass.getText();
                    String passConfirm = pasUserPassConfirm.getText();
                    String unick = tbUserNick.getText();
                    
                    if(uname.isEmpty()) {
                        tbUsername.setSelected(true);
                        setFocus(0);
                        showTipsDialog("用户名不能为空。");
                        return null;
                    }

                    if(pass.isEmpty()) {
                        pasUserPass.setSelected(true);
                        setFocus(1);
                        showTipsDialog("登录口令不能为空。");
                        return null;
                    }

                    if(!pass.equals(passConfirm)) {
                        pasUserPass.clear();
                        pasUserPassConfirm.clear();
                        pasUserPass.setSelected(true);
                        setFocus(1);
                        showTipsDialog("两次输入口令不一致，请重试。");
                        return null;
                    }
                    
                    if(setScreenSize == null) {
                        showTipsDialog("请先校准屏幕尺寸。");
                        return null;
                    }
                    
                    PlayerProfile player = new PlayerProfile(-1, uname, pass, unick, null, null, shareMyRecord,
                            null, setScreenSize[0], setScreenSize[1], setScreenSize[2], setScreenSize[3]);
                    
                    int pilotNo = -1;
                    
                    pilotNo = PlayerProfileServiceImp.getPlayerProfileService().regist(player);
                    
                    if(pilotNo != -1) {
                        showTipsDialog("飞行战斗员注册成功，您的编号为: " + pilotNo + "。");
                        return new Operation(true, null, new Color(48, 64, 48), null, null, null);
                    } else {
                        tbUsername.clear();
                        tbUsername.setSelected(true);
                        setFocus(0);
                        showTipsDialog("注册失败，可能已经存在同名的飞行员了。");
                        return new Operation(false, null, new Color(128, 96, 64), null, null, null);
                    }
                }
            }
        );
        
        btnCancel = new CharButton(
            screenBuffer, resolution, "Cancel", 34, 32, 20,
            new Operable() {
                @Override
                public Operation call() {
                    return new Operation(true, null, null, null, null, null);
                }
            }
        );
        
        //--------------------------------------------
        widget = new Widget[] {
            tbUsername,
            pasUserPass,
            pasUserPassConfirm,
            tbUserNick,
            btnUserRecordShareSetup,
            btnUserScreenSetup,
            btnConfirm,
            btnCancel
        };
        
        setSelectableCount(widget.length);
    }

    @Override
    public void getRefresh() {
        for(int i = 0, j = widget.length; i < j; ++i) {
            if(i == getSelectedIndex()) widget[i].setSelected(true);
            widget[i].printNew();
        }
        
        lblTiltle.printNew();
        lblUsername.printNew();
        lblUserPass.printNew();
        lblUserPassConfirm.printNew();
        lblUserNick.printNew();
    }

    @Override
    public Operation putKeyReleaseEvent(int keyCode) {
        
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
            if(selected >= 4) indexDown();
            else {
                widget[selected].setSelected(true);
                widget[selected].getControl(keyCode);
            }
            break;
        case KeyEvent.VK_LEFT:
            if(selected >= 4) indexUp();
            else {
                widget[selected].setSelected(true);
                widget[selected].getControl(keyCode);
            }
            break;
        case KeyEvent.VK_ENTER:
            if(selected >= 0  &&  selected < 4) {
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
            widget[selected].setSelected(true);
            widget[selected].getControl(keyCode);
            
            break;
        }
        
        return opt;
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
        Object tmp = pollMail();
        if(tmp != null) {
            setScreenSize = (int[]) tmp;
            btnUserScreenSetup.setText("My Screen Size: " + setScreenSize[0] + "," + setScreenSize[1] + "," + setScreenSize[2] + "," + setScreenSize[3]);
        }
        return null;
    }

    @Override
    public Operation afterRefreshNotification() {
        return null;
    }

    @Override
    protected void beforeRefreshEvent() {
        if(shareMyRecord == 1) {
            btnUserRecordShareSetup.setText("Share my game record: YES");
        } else {
            btnUserRecordShareSetup.setText("Share my game record: NO");
        }
    }

    @Override
    protected void afterRefreshEvent() {
    }

}
