package dogfight_Z.dogLog.controller;

import java.awt.event.KeyEvent;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javax.swing.JTextArea;

import dogfight_Z.dogLog.model.PlayerProfile;
import dogfight_Z.dogLog.view.Menu;
import graphic_Z.Common.Operation;
import graphic_Z.HUDs.CharButton;
import graphic_Z.HUDs.CharLabel;
import graphic_Z.HUDs.CharSingleLineTextEdit;
import graphic_Z.HUDs.Operable;
import graphic_Z.HUDs.Widget;

/**
 * -查看详细信息
 * -更改用户名
 * -更改密码
 * -更改昵称
 * -校准屏幕
 * @author Zafkiel
 */
public class ProfileSettingsMenu extends Menu {
    
    private CharLabel     labels[];
    //private PlayerProfile player;
    private Widget        widgets[];
    private int           setScreenSize[];
    private PlayerProfile player;

    public ProfileSettingsMenu(String[] args, PlayerProfile player, JTextArea screen, int resolutionX, int resolutionY) {
        super(args, screen, 0, resolutionX, resolutionY);
        this.player = player;
        setScreenSize = null;
        labels = new CharLabel[] {
            new CharLabel(
                screenBuffer, 
                0, 
                resolution, 
                logoString, 
                (resolution[0] >> 1) - 31, 
                2,
                true
            ),
            new CharLabel(
                screenBuffer, 
                0, 
                resolution, 
                "Hello, " + player.getUserNick() + "! Now is " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), 
                1, 
                11,
                true
            )
        };
        
        widgets = new Widget[] {
            new CharButton(
                screenBuffer, 
                resolution, 
                "Check My Profile", 
                10, 
                16,
                40,
                new Operable() {
                    @Override
                    public Operation call() {
                        return Operation.getIntoMenu(new SimpleMenu(
                            args, screen, 
                            new CharLabel[] {
                                new CharLabel(
                                    null, 
                                    0, 
                                    resolution, 
                                    logoString, 
                                    (resolution[0] >> 1) - 31, 
                                    2,
                                    true
                                ), 
                                new CharLabel(
                                    null, 
                                    0, 
                                    resolution, 
                                    "Pilot No: " + player.getUserID(), 
                                    16, 
                                    16,
                                    true
                                ),
                                new CharLabel(
                                    null, 
                                    0, 
                                    resolution, 
                                    "Username: " + player.getUserName(), 
                                    16, 
                                    18,
                                    true
                                ),
                                new CharLabel(
                                    null, 
                                    0, 
                                    resolution, 
                                    "Password: [Hidden]", 
                                    16, 
                                    20,
                                    true
                                ),
                                new CharLabel(
                                    null, 
                                    0, 
                                    resolution, 
                                    "Nickname: " + player.getUserNick(), 
                                    16, 
                                    22,
                                    true
                                ),
                                new CharLabel(
                                    null, 
                                    0, 
                                    resolution, 
                                    "Bank    : " + player.getUserBank().toString(), 
                                    16, 
                                    24,
                                    true
                                ),
                                new CharLabel(
                                    null, 
                                    0, 
                                    resolution, 
                                    "Registed on " + player.getCreateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), 
                                    16, 
                                    26,
                                    true
                                )
                            },
                            new Widget[] {
                                new CharButton(
                                    null, 
                                    resolution, 
                                    "<- Go Back", 
                                    10, 
                                    resolution[1] - 4,
                                    40,
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
                        ));
                    }
                }
            ), 
            //------------------------------------------------------------------------------
            new CharButton(
                screenBuffer, 
                resolution, 
                "Change My Username", 
                10, 
                20,
                40,
                new Operable() {
                    @Override
                    public Operation call() {
                        return Operation.getIntoMenu(new SimpleMenu(
                            args, screen, 
                            new CharLabel[] {
                                new CharLabel(
                                    null, 
                                    0, 
                                    resolution, 
                                    logoString, 
                                    (resolution[0] >> 1) - 31, 
                                    2,
                                    true
                                ),
                                new CharLabel(
                                    null, 
                                    2, 
                                    resolution, 
                                    "Username:", 
                                    8, 
                                    16,
                                    false
                                )
                            },
                            new Widget[] {
                                new CharSingleLineTextEdit(
                                    screenBuffer, 
                                    resolution, 
                                    20, 
                                    16, 
                                    34
                                ),
                                new CharButton(
                                    null, 
                                    resolution, 
                                    "Confirm", 
                                    10, 
                                    resolution[1] - 4,
                                    18,
                                    new Operable() {
                                        @Override
                                        public Operation call() {
                                            return Operation.EXIT;
                                        }
                                    }
                                ),
                                new CharButton(
                                    null, 
                                    resolution, 
                                    "Cancel", 
                                    34, 
                                    resolution[1] - 4,
                                    18,
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
                        });
                    }
                }
            ), 
            new CharButton(
                screenBuffer, 
                resolution, 
                "Change My Password", 
                10, 
                24,
                40,
                new Operable() {

                    @Override
                    public Operation call() {
                        return null;
                    }
                }
            ), 
            new CharButton(
                screenBuffer, 
                resolution, 
                "Change My Nickname", 
                10, 
                28,
                40,
                new Operable() {

                    @Override
                    public Operation call() {
                        return null;
                    }
                }
            ), 
            new CharButton(
                screenBuffer, 
                resolution, 
                "Setup Screen Size", 
                10, 
                32,
                40,
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
