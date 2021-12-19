package dogfight_Z;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class GameRun {
    
    private static Game newGame = null;
    
    private static final String RESOURCESLIST[] = {
        "Jet.dat",
        "Crosshair2.hud",
        "LoopingScrollBar1.hud",
        "LoopingScrollBar2.hud",
        "LoopingScrollBar3.hud",
        "HUD3.hud",
        "MyJetHUD_Friend.hud",
        "MyJetHUD_Enemy.hud",
        "MyJetHUD_Locking.hud",
        "MyJetHUD_Locked.hud",
        "MissileWarning.hud",
        "LockingWarning.hud",
        "RadarHUD.hud",
        "RaderPainter.hud",
        "ScoreHUD.hud",
        "GuiBackground.jpg"
    };
    
    private static final String DATAFILELIST[] = {
        "config_NPC.cfg",
        "config_OST.cfg",
    };
    
    public static void initEnviroment() {
        makeDir("resources");
        for(String resource : DATAFILELIST) {
            if(!fileExists("resources/" + resource)) extractResource(resource, "resources/");
        }
        for(String resource : RESOURCESLIST) {
            extractResource(resource, "resources/");
        }
    }
    
    public static void makeDir(String path) {
        File dir = new File(path);
        dir.mkdir();
    }
    
    public static boolean fileExists(String fileName) {
        File dir = new File(fileName);
        return dir.exists();
    }
    
    public static boolean deleteFile(String fileName) {
        File f = new File(fileName);
        if(f.exists()) {
            return f.delete();
        } else return false;
    }
    
    public static void extractResource(String resourceName, String path) {
       
        BufferedInputStream  inputBuffer  = null;
        BufferedOutputStream outputBuffer = null;
        FileOutputStream     output       = null;
        File                 newFile      = null;
        try {
            newFile = new File(path + resourceName);
            newFile.createNewFile();
            output       = new FileOutputStream(new File(path + resourceName), false);
            inputBuffer  = new BufferedInputStream(ClassLoader.getSystemResourceAsStream(resourceName));
            outputBuffer = new BufferedOutputStream(output);
            
            for(int readed = 0; (readed = inputBuffer.read()) != -1; outputBuffer.write(readed));
            
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if(inputBuffer  != null) inputBuffer.close();
                if(outputBuffer != null) outputBuffer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        
        if(newGame != null) newGame.exit();
        
        newGame = new Game(
            args[0] ,args[1] , args[2], args[3], args[4], args[5] ,
            args[6] , args[7], args[8], args[9], args[10], args[11], 
            args[12], args[13], args[14], args[15], args[16], args[17], 
            Integer.parseInt(args[18]), 
            Integer.parseInt(args[19]), 
            Integer.parseInt(args[20]),
            Integer.parseInt(args[21]),
            Integer.parseInt(args[22])
        );
        
        Thread gameThread = new Thread(newGame);
        gameThread.setPriority(Thread.MAX_PRIORITY);
        Thread.currentThread().setPriority(Thread.MIN_PRIORITY);
        newGame.getIntoGameWorld();
        gameThread.start();
    }

}
