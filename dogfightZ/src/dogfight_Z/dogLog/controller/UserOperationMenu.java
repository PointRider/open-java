package dogfight_Z.dogLog.controller;

import javax.swing.JTextArea;

import dogfight_Z.dogLog.view.Menu;
import graphic_Z.Common.Operation;
import graphic_Z.HUDs.CharLabel;
import graphic_Z.HUDs.Widget;

/**
 * 开始战斗
 * -载人对局配置
 * -创建对局配置(玩家ID, 配置名, 对局时间, 采用的BGM列表, NPC设定字符串(text))
 * 
 * 管理对局配置
 * -创建对局配置
 * -编辑对局配置
 * -删除对局配置
 *
 * 管理播放列表
 * -创建
 * -删除
 * -编辑
 * --添加
 * --移除
 * --选中上移
 * --选中下移
 *
 * 查看游戏记录
 * -查看个人记录
 * -查看他人记录（列表）

 * 账户设置
 * -查看详细信息
 * -更改用户名
 * -更改密码
 * -更改昵称
 * -校准屏幕
 *
 * @author Zafkiel
 */
public class UserOperationMenu extends Menu {
    
    private CharLabel labels[];
    private Widget    widgets[];

    public UserOperationMenu(String[] args, JTextArea screen, int resolutionX, int resolutionY) {
        super(args, screen, 0, resolutionX, resolutionY);
        
        labels = new CharLabel[] {
            
        };
        
        widgets = new Widget[] {
            
        };
        
        setSelectableCount(widgets.length);
    }

    @Override
    public void getRefresh() {
    }

    @Override
    public Operation putKeyReleaseEvent(int keyCode) {
        return null;
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
