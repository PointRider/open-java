package dogfight_Z.dogLog.controller;

import java.awt.Color;
import java.awt.event.KeyEvent;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javax.swing.JTextArea;

import dogfight_Z.dogLog.model.PlayerProfile;
import dogfight_Z.dogLog.service.PlayerProfileServiceImp;
import dogfight_Z.dogLog.utils.Common;
import dogfight_Z.dogLog.view.Menu;
import graphic_Z.Common.Operation;
import graphic_Z.HUDs.CharLabel;
import graphic_Z.HUDs.Operable;
import graphic_Z.HUDs.Widget;

import graphic_Z.HUDs.CharSingleLineTextEdit;
import graphic_Z.HUDs.CharPasswordEdit;

/**
 * -查看详细信息
 * -编辑个人资料
 * -校准屏幕
 * @author Zafkiel
 */
public class ProfileSettingsMenu extends Menu {
    
    private CharLabel     labels[];
    //private PlayerProfile player;
    private Widget        widgets[];
    private int           setScreenSize[];
    private PlayerProfile player;
    private CharLabel     childsHelloTip;
    
    private SimpleMenu    checkMyProfileMenu;
    private SimpleMenu    editMyProfile;
    
    private int shareMyRecord;
    
    private SimpleMenu newCheckMyProfileMenu() {
        checkMyProfileMenu = new SimpleMenu(
            args, screen, 
            new CharLabel[] {
                childsHelloTip = 
                    newCharLabel("Hello, " + player.getUserNick() + "! Now is " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), 1, 11),
                newCharLabel(logoString, (resolution[0] >> 1) - 31, 2), 
                newCharLabel("Pilot No: " + player.getUserID(), 16, 16),
                newCharLabel("Username: " + player.getUserName(), 16, 18),
                newCharLabel("Password: [Hidden]", 16, 20),
                newCharLabel("Nickname: " + player.getUserNick(), 16, 22),
                newCharLabel("Bank    : " + player.getUserBank().toString(), 16, 24),
                newCharLabel("Registed on " + player.getCreateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), 16, 26)
            },
            new Widget[] {
                newCharButton("<- Go Back", 10, resolution[1] - 4, 40,
                    new Operable() {
                        @Override
                        public Operation call() {
                            return Operation.EXIT;
                        }
                    }
                ), 
            },
            resolution[0],
            resolution[1]
        );
        
        checkMyProfileMenu.setOverridedBeforeRefreshEvent(new Runnable() {
            @Override
            public void run() {
                if(childsHelloTip != null) childsHelloTip.setText("Hello, " + player.getUserNick() + "! Now is " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                checkMyProfileMenu.widgets[getSelectedIndex()].setSelected(true);
            }
        });
        return checkMyProfileMenu;
    }
    
    private SimpleMenu newEditMyProfile() {
        editMyProfile = new SimpleMenu(
            args, screen, 
            new CharLabel[] {
                newCharLabel(logoString, (resolution[0] >> 1) - 31, 2),
                newCharLabel("Username:", 8, 12),
                newCharLabel("Password:", 8, 15),
                newCharLabel(" Confirm:", 8, 18),
                newCharLabel("Nickname:", 8, 21)
            },
            new Widget[] {
                newCharSingleLineTextEdit(20, 12, 34, player.getUserName()),
                newCharPasswordEdit(20, 15, 34, "[HIDDEN]"),
                newCharPasswordEdit(20, 18, 34, "Repeat again."),
                newCharSingleLineTextEdit(20, 21, 34, player.getUserNick()),

                newCharButton(
                    "Share my game record: " + (shareMyRecord == 1? "YES" : "No."), 10, 26, 44,
                    new Operable() {
                        @Override
                        public Operation call() {
                            editMyProfile.showConfirmDialog("你想要将自己的游戏记录设置为所有用户可见吗？" ,"YES!", "NO.", 
                                new Runnable() {
                                    @Override
                                    public void run() {
                                        shareMyRecord = 1;
                                        editMyProfile.widgets[4].setText("Share my game record: " + (shareMyRecord == 1? "YES" : "No."));
                                        editMyProfile.closeDialog();
                                    }
                                }, 
                                new Runnable() {
                                    @Override
                                    public void run() {
                                        shareMyRecord = 0;
                                        editMyProfile.widgets[4].setText("Share my game record: " + (shareMyRecord == 1? "YES" : "No."));
                                        editMyProfile.closeDialog();
                                    }
                                }
                            );
                            return null;
                        }
                    }
                ),
                newCharButton("Confirm", 10, resolution[1] - 3, 18,
                    new Operable() {
                        @Override
                        public Operation call() {
                            
                            String newUserName = ((CharSingleLineTextEdit)(editMyProfile.widgets[0])).getText();
                            if(Common.isStringEmpty(newUserName)) newUserName = null;
                            
                            String newUserPass = ((CharPasswordEdit)(editMyProfile.widgets[1])).getText();
                            if(Common.isStringEmpty(newUserPass)) newUserPass = null;
                            
                            String newUserPassConfirm = ((CharPasswordEdit)(editMyProfile.widgets[2])).getText();
                            if(Common.isStringEmpty(newUserPassConfirm)) newUserPassConfirm = null;
                            
                            String newUserNick = ((CharSingleLineTextEdit)(editMyProfile.widgets[3])).getText();
                            if(Common.isStringEmpty(newUserNick)) newUserNick = null;
                            
                            if(newUserPass != null) {
                                if(newUserPassConfirm == null) {
                                    ((CharPasswordEdit)editMyProfile.widgets[1]).clear();
                                    ((CharPasswordEdit)editMyProfile.widgets[2]).clear();
                                    editMyProfile.setFocus(1);
                                    editMyProfile.showTipsDialog("若需更改登录口令，请再次输入确认。");
                                    
                                    return null;
                                } else if(!newUserPass.equals(newUserPassConfirm)) {
                                    ((CharPasswordEdit)editMyProfile.widgets[1]).clear();
                                    ((CharPasswordEdit)editMyProfile.widgets[2]).clear();
                                    editMyProfile.setFocus(1);
                                    editMyProfile.showTipsDialog("两次输入口令不一致，请检查后重新输入。");
                                    return null;
                                }
                            }
                            
                            PlayerProfile newProfileInfo = new PlayerProfile(
                                player.getUserID(), newUserName, newUserPass, newUserNick, null, null, shareMyRecord, null, null, null, null, null
                            );
                            
                            System.out.println(newProfileInfo.toString());
                            
                            if(PlayerProfileServiceImp.getPlayerProfileService().editProfile(newProfileInfo)) {
                                synchronized(player) {
                                    if(newProfileInfo.getUserName() != null) player.setUserName(newProfileInfo.getUserName());
                                    if(newProfileInfo.getUserPass() != null) player.setUserPass(newProfileInfo.getUserPass());
                                    if(newProfileInfo.getUserNick() != null) player.setUserNick(newProfileInfo.getUserNick());
                                    player.setGameRecordShare(newProfileInfo.getGameRecordShare());
                                }
                                showTipsDialog("已保存。");
                                return new Operation(true, null, new Color(48, 64, 48), null, null, null);
                            } else {
                                ((CharSingleLineTextEdit) editMyProfile.widgets[0]).clear();
                                editMyProfile.setFocus(0);
                                editMyProfile.showTipsDialog("信息更新失败，请检查登录名，可能已经存在同名的飞行员了。");
                                return new Operation(false, null, new Color(128, 96, 64), null, null, null);
                            }
                        }
                    }
                ),
                newCharButton("Cancel", 34, resolution[1] - 3, 18,
                    new Operable() {
                        @Override
                        public Operation call() {
                            return Operation.EXIT;
                        }
                    }
                )
            },
            resolution[0],
            resolution[1]
        ) {
            @Override
            public Operation putKeyReleaseEvent(int keyCode) {
                Operation opt = null;
                int idx = getSelectedIndex();
                switch(keyCode) {
                case KeyEvent.VK_UP:
                    indexUp();
                    break;
                case KeyEvent.VK_DOWN:
                    indexDown();
                    break;
                case KeyEvent.VK_ENTER:{
                    if(idx < 4) {
                        indexDown();
                    } else {
                        widgets[idx].setSelected(true);
                        opt = widgets[idx].call();
                    }
                } break;
                case KeyEvent.VK_LEFT:
                    if(getSelectedIndex() < 4) {
                        widgets[idx].setSelected(true);
                        widgets[idx].getControl(keyCode);
                    } else indexUp();
                    break;
                case KeyEvent.VK_RIGHT:
                    if(getSelectedIndex() < 4) {
                        widgets[idx].setSelected(true);
                        widgets[idx].getControl(keyCode);
                    } else indexDown();
                    break;
                case KeyEvent.VK_ESCAPE:
                    opt = new Operation(true, null, null, null, null, null);
                default:
                    widgets[idx].setSelected(true);
                    widgets[idx].getControl(keyCode);
                }
                return opt;
            }
        };
        return editMyProfile;
    }

    public ProfileSettingsMenu(String[] args, PlayerProfile player, JTextArea screen, int resolutionX, int resolutionY) {
        super(args, screen, 0, resolutionX, resolutionY);
        this.player        = player;
        shareMyRecord      = player.getGameRecordShare();
        checkMyProfileMenu = null;
        editMyProfile      = null;
        setScreenSize      = null;
        childsHelloTip     = null;
        
        labels = new CharLabel[] {
            newCharLabel(logoString, (resolution[0] >> 1) - 31, 2, true),
            newCharLabel("Hello, " + player.getUserNick() + "! Now is " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), 1, 11)
        };
        
        widgets = new Widget[] {
            newCharButton("Check My Profile", 10, 16, 40,
                new Operable() {
                    @Override
                    public Operation call() {
                        return Operation.getIntoMenu(newCheckMyProfileMenu());
                    }
                }
            ), 
            //------------------------------------------------------------------------------
            newCharButton("Edit My Profile", 10, 20, 40,
                new Operable() {
                    @Override
                    public Operation call() {
                        return Operation.getIntoMenu(newEditMyProfile());
                    }
                }
            ), 
            newCharButton("Set Key Map", 10, 24, 40,
                new Operable() {
                    @Override
                    public Operation call() {
                        return null;
                    }
                }
            ), 
            newCharButton("Setup Screen Size", 10, 28, 40,
                new Operable() {
                    @Override
                    public Operation call() {
                        return new Operation(false, new ScreenResize(args, screen, resolution[0], resolution[1], setScreenSize), null, null, null, null);
                    }
                }
            ), 
        };
            
        setSelectableCount(widgets.length);
    }

    @Override
    public void getRefresh() {
        for(CharLabel l : labels) {
            l.printNew();
        }
        for(Widget w : widgets) {
            w.printNew();
        }
    }

    @Override
    protected void beforeRefreshEvent() {
        labels[1].setText("Hello, " + player.getUserNick() + "! Now is " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        widgets[getSelectedIndex()].setSelected(true);
    }

    @Override
    protected void afterRefreshEvent() {
    }

    @Override
    public Operation putKeyReleaseEvent(int keyCode) {
        Operation opt = null;
        switch(keyCode) {
        case KeyEvent.VK_UP:
            indexUp();
            break;
        case KeyEvent.VK_DOWN:
            indexDown();
            break;
        case KeyEvent.VK_ENTER:
            widgets[getSelectedIndex()].setSelected(true);
            opt = widgets[getSelectedIndex()].call();
            break;
        case KeyEvent.VK_ESCAPE:
            opt = new Operation(true, null, null, null, null, null);
        }
        return opt;
    }

    @Override
    public Operation putKeyTypeEvent(int keyChar) {
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

            PlayerProfile newProfileInfo = new PlayerProfile(
                player.getUserID(), null, null, null, null, null, null, null, 
                setScreenSize[0], setScreenSize[1], setScreenSize[2], setScreenSize[3]
            );
            PlayerProfileServiceImp.getPlayerProfileService().editProfile(newProfileInfo);
            
            widgets[4].setText("My Screen Size: " + setScreenSize[0] + "," + setScreenSize[1] + "," + setScreenSize[2] + "," + setScreenSize[3]);
        }
        return null;
    }

    @Override
    public Operation afterRefreshNotification() {
        return null;
    }
}
