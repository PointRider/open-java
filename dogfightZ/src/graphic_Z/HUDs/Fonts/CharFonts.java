package graphic_Z.HUDs.Fonts;

import graphic_Z.HUDs.CharImage;

public class CharFonts extends CharImage {
    
    /*
      @    @ @    @ @ @  @ @    @ @ @  @ @ @  @ @ @  @   @  @ @ @  @ @ @  @   @  @
    @   @  @   @  @      @   @  @      @      @      @   @    @      @    @ @    @
    @ @ @  @ @ @  @      @   @  @ @ @  @ @ @  @   @  @ @ @    @      @    @      @
    @   @  @   @  @      @   @  @      @      @   @  @   @    @      @    @ @    @    
    @   @  @ @    @ @ @  @ @    @ @ @  @      @ @ @  @   @  @ @ @  @      @   @  @ @ @
                                                                                         
     A   A   A                                                                
    [_] /_\ /H\  A                                                            
    |_| | |  H  <o>                                                             
    \_/ |_|  H   U                                                             
    /|\ === /U\ /|\                                                           


0000 0011
0000 1100
     */
    /*
    public static final char zoom2[2][3] = {
            {'-=-'}
    };*/
    
    private static final String[] NUMBERSTEMPLATE = {
            "@@@  @  @@@ @@@  @@ @@@ @@@ @@@ @@@ @@@", 
            "@ @ @@    @   @ @ @ @   @     @ @ @ @ @",
            "@ @  @   @   @@ @@@ @@@ @@@  @  @@@ @@@",
            "@ @  @  @     @   @   @ @ @  @  @ @   @",
            "@@@ @@@ @@@ @@@   @ @@@ @@@  @  @@@ @@@"
    };
    
    private static final char[][][] ASCII;
    
    static {
        ASCII = new char[128][][];
        char[][] theNum;
        for(char n = 0; n < 9; ++n) {
            ASCII[n + '0'] = theNum = new char[5][3];
            for(int i = 0, ii = theNum.length; i < ii; ++i) {
                for(int j = 0, jj = theNum[0].length; j < jj; ++j) {
                    theNum[i][j] = NUMBERSTEMPLATE[i].charAt(j + (n << 2));
                }
            }
        }
    }
    
    public static CharFonts getNumber(int number, char[][] frapsBuffer, int locationX, int locationY, int HUDLayer, int[] scrResolution) {
        if(number > 9) return null;
        
        CharFonts ret = new CharFonts(frapsBuffer, locationX, locationY, HUDLayer, scrResolution);
        ret.HUDImg = ASCII[number + '0'];
        
        return ret;
    }
    
    private CharFonts(char[][] frapsBuffer, int locationX, int locationY, int HUDLayer, int[] scrResolution) {
        super(null, frapsBuffer, 3, 5, locationX, locationY, HUDLayer, scrResolution, false, false);
    }
    
    /*
    private static final CharFonts[] table;
    
    static {
        table = new CharFonts[128];
        for(int i = 0; i < 9; ++i) {
            table['0' + i] = getNumber(i);
        }
    }*/

}
