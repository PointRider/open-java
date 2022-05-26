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
import graphic_Z.HUDs.Operable;
import graphic_Z.HUDs.Widget;

/**
 * 开始战斗 4
 * -载人对局配置
 * -创建对局配置(玩家ID, 配置名, 对局时间, 采用的BGM列表, NPC设定字符串(text))
 * 
 * 管理对局配置  3
 * -创建对局配置
 * -编辑对局配置
 * -删除对局配置
 *
 * 管理播放列表 2
 * -创建
 * -删除
 * -编辑
 * --添加
 * --移除
 * --选中上移
 * --选中下移
 *
 * 查看游戏记录 5
 * -查看个人记录
 * -查看他人记录（列表）

 * 账户设置 1
 * -查看详细信息
 * -更改用户名
 * -更改密码
 * -更改昵称
 * -校准屏幕
 *
 * @author Zafkiel
 */
public class UserOperationMenu extends Menu {
    
    private CharLabel     labels[];
    //private PlayerProfile player;
    private Widget        widgets[];
    private PlayerProfile player;

    public UserOperationMenu(String[] args, PlayerProfile player, JTextArea screen, int resolutionX, int resolutionY) {
        super(args, screen, 0, resolutionX, resolutionY);
        this.player = player;
        
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
        
        //this.player = player;
        
        widgets = new Widget[] {
            new CharButton(
                screenBuffer, 
                resolution, 
                "START FIGHT", 
                10, 
                16,
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
                "Battleground Configurations", 
                10, 
                20,
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
                "Back Ground Music Lists", 
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
                "Fighting Records", 
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
                "Profile Settings", 
                10, 
                32,
                40,
                new Operable() {

                    @Override
                    public Operation call() {
                        return Operation.getIntoMenu(
                            new ProfileSettingsMenu(args, player, screen, resolution[0], resolution[1])
                        );
                    }
                }
            ), 
        };
        
        setSelectableCount(widgets.length);
        /*
        makeSureMenu = new TipsConfirmMenu(
            args, null,
            "确定要登出吗？", 
            "YES", 
            "CANCEL", 
            screen, 
            resolution[0], 
            resolution[1], 
            new Runnable() {
                @Override
                public void run() {
                    sendMail(new Object());
                }
            }, 
            null
        );*/
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
            showConfirmDialog("确定要登出吗？", "YES", "CANCEL", 
                new Runnable() {
                    @Override
                    public void run() {
                        sendMail(new Object());
                    }
                }
            );
            opt = new Operation(false, null, null, null, null, null);
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
        if(tmp != null) return new Operation(true, null, null, null, null, null);
        return null;
    }

    @Override
    public Operation afterRefreshNotification() {
        return null;
    }

    @Override
    protected void beforeRefreshEvent() {
        labels[1].setText("Hello, " + player.getUserNick() + "! Now is " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        widgets[getSelectedIndex()].setSelected(true);
    }

    @Override
    protected void afterRefreshEvent() {
        
    }

}
