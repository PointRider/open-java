package graphic_Z.HUDs;

import java.awt.event.KeyEvent;

import graphic_Z.Common.Operation;
import graphic_Z.utils.Common;
import graphic_Z.utils.LinkedListZ;

public class CharSingleLineTextEdit extends CharLabel implements KeyInputGetter, Widget {

    private   CharLabel                       outerBoxNotSelected;
    private   CharLabel                       outerBoxSelected;
    
    private   int                             size;
    protected boolean                         selected;
    private   LinkedListZ<Character>          text;
    private   LinkedListZ<Character>.Iterator inputItr;
    private   StringBuilder                   stringBuilder;
    
    private String                          outerBoxNotSelectedText;
    private String                          outerBoxSelectedText;
    
    private static int id = 65536;
    
    public CharSingleLineTextEdit(
        char[][] frapsBuffer, 
        int[] scrResolution, 
        int locationX, 
        int locationY, 
        int sizeX
    ) {
        super(frapsBuffer, id++, scrResolution, null, locationX, locationY, false);
        // TODO 自动生成的构造函数存根
        outerBoxNotSelectedText = " " + Common.loopChar('-', sizeX) + " ";
        outerBoxSelectedText = outerBoxNotSelectedText + "\n<" + Common.loopChar(' ', sizeX) + ">\n" + outerBoxNotSelectedText;
        
        outerBoxNotSelected = new CharLabel(frapsBuffer, layer, scrResolution, outerBoxNotSelectedText, locationX - 1, locationY + 1);
        outerBoxSelected    = new CharLabel(frapsBuffer, layer, scrResolution, outerBoxSelectedText, locationX - 1, locationY - 1);
        selected = false;
        
        text = new LinkedListZ<>();
        inputItr = text.begin();
        stringBuilder = new StringBuilder(sizeX + 2);
        size = sizeX;
    }
    
    @Override
    public void printNew() {
        this.printNew(this.selected);
        super.printNew();
    }
    
    @Override
    public void resetScreenBuffer(char fraps_buffer[][]) {
        super.resetScreenBuffer(fraps_buffer);
        outerBoxSelected.resetScreenBuffer(fraps_buffer);
        outerBoxNotSelected.resetScreenBuffer(fraps_buffer);
    }
    
    /**
     * 将输入的字符链表缓冲到 CharLabel 的 String text 中
     * @return 当前输入光标的位置下标
     */
    private int buff() {
        stringBuilder.delete(0, stringBuilder.length());
        int i = 0, at = 0;
        
        for(
            LinkedListZ<Character>.Iterator itr = text.begin();
            itr.hasNext();
            itr.next()
        ) {
            
            stringBuilder.append(itr.get());
            if(itr.at(inputItr)) at = i;
            
            ++i;
        }
        
        super.text = stringBuilder.toString();
        
        if(inputItr.isEnd()) at = i;
        return at;
    }
    
    public void printNew(boolean selected) {
        StringBuilder sb2 = new StringBuilder(outerBoxNotSelectedText);
        sb2.setCharAt(buff() + 1, '|');
        String tmpNotSelectedText = sb2.toString();
        String tmpSelectedText    = tmpNotSelectedText + "\n<" + Common.loopChar(' ', size) + ">\n" + tmpNotSelectedText;
        
        if(selected) {
            outerBoxNotSelected.setText(tmpNotSelectedText);
            outerBoxSelected.setText(tmpSelectedText);
            outerBoxSelected.printNew();
        } else {
            outerBoxNotSelected.setText(outerBoxNotSelectedText);
            outerBoxSelected.setText(outerBoxSelectedText);
            outerBoxNotSelected.printNew();
        }
        setSelected(false);
    }

    @Override
    public boolean isSelected() {
        return selected;
    }

    @Override
    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public boolean getInput(int keyChar) {
        if(!selected) return false;

        if(text.size() < size && keyChar >= 32 && keyChar <= 126) {
            inputItr.add((char)keyChar);
            return true;
        } else if(keyChar == KeyEvent.VK_BACK_SPACE  &&  !inputItr.isBegin()) {
            inputItr.previous();
            inputItr.remove();
            return true;
        }
        
        return false;
    }

    @Override
    public boolean getControl(int keyCode) {
        if(!selected) return false;

        switch(keyCode) {
        case KeyEvent.VK_UP: case KeyEvent.VK_DOWN: case KeyEvent.VK_ENTER:
            break;
        case KeyEvent.VK_LEFT: if(!inputItr.isBegin()) inputItr.previous();
            return true;
        case KeyEvent.VK_RIGHT: if(!inputItr.isEnd()) inputItr.next();
            return true;
        }
        return false;
    }
    
    public String getText() {
        buff();
        return new String(super.text);
    }

    @Override
    public Operation call() {
        
        return null;
    }
}
