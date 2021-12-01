package dogfight_Z.dogLog.controller;

import java.awt.event.KeyEvent;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javax.swing.JTextArea;

import dogfight_Z.dogLog.model.PlayerProfile;
import dogfight_Z.dogLog.view.Menu;
import graphic_Z.Common.Operation;
import graphic_Z.HUDs.CharLabel;
import graphic_Z.HUDs.Operable;
import graphic_Z.HUDs.Widget;

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
                newCharLabel("Username:", 8, 16)
            },
            new Widget[] {
                newCharSingleLineTextEdit(20, 16, 34),
                newCharButton("Confirm", 10, resolution[1] - 4, 18,
                    new Operable() {
                        @Override
                        public Operation call() {
                            return Operation.EXIT;
                        }
                    }
                ),
                newCharButton("Cancel", 34, resolution[1] - 4, 18,
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
                switch(keyCode) {
                case KeyEvent.VK_UP:
                    indexUp();
                    break;
                case KeyEvent.VK_DOWN:
                    indexDown();
                    break;
                case KeyEvent.VK_ENTER:{
                    int idx = getSelectedIndex();
                    if(idx == 0) {
                        indexDown();
                    } else {
                        widgets[idx].setSelected(true);
                        opt = widgets[idx].call();
                    }
                } break;
                case KeyEvent.VK_ESCAPE:
                    opt = new Operation(true, null, null, null, null, null);
                default:
                    widgets[getSelectedIndex()].setSelected(true);
                    widgets[getSelectedIndex()].getControl(keyCode);
                }
                return opt;
            }
        };
        return editMyProfile;
    }

    public ProfileSettingsMenu(String[] args, PlayerProfile player, JTextArea screen, int resolutionX, int resolutionY) {
        super(args, screen, 0, resolutionX, resolutionY);
        this.player        = player;
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
            newCharButton("Change My Username", 10, 20, 40,
                new Operable() {
                    @Override
                    public Operation call() {
                        return Operation.getIntoMenu(newEditMyProfile());
                    }
                }
            ), 
            newCharButton("Change My Password", 10, 24, 40,
                new Operable() {
                    @Override
                    public Operation call() {
                        return null;
                    }
                }
            ), 
            newCharButton("Change My Nickname", 10, 28, 40,
                new Operable() {
                    @Override
                    public Operation call() {
                        return null;
                    }
                }
            ), 
            newCharButton("Setup Screen Size", 10, 32, 40,
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
            widgets[4].setText("My Screen Size: " + setScreenSize[0] + "," + setScreenSize[1] + "," + setScreenSize[2]);
        }
        return null;
    }

    @Override
    public Operation afterRefreshNotification() {
        return null;
    }
}
