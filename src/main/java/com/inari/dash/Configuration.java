package com.inari.dash;


public final class Configuration {

    public final String titleSongResource;

    public final String unitTextureResource;
    public final int unitTextureWidth;
    public final int unitTextureHeight;
    public final int unitWidth;
    public final int unitHeight;
    
    public final String fontTextureResource;
    public final int fontTextureWidth;
    public final int fontTextureHeight;
    public final int charWidth;
    public final int charHeight;
    
    public final int cameraMoveVelocity = 3;
    
    
    public final char[][] fontChars;

    public Configuration() {
        titleSongResource = "original/sound/intro.wav";
        unitTextureResource = "original/texture/tiles.png";
        fontTextureResource = "original/texture/font.png";
        
        unitTextureWidth = 8 * 32;
        unitTextureHeight = 12 * 32;
        unitWidth = 32;
        unitHeight = 32;
        
        fontTextureWidth = 128;
        fontTextureHeight = 192;
        charWidth = 32;
        charHeight = 16;
        
        fontChars = new char[][] {
            { '%', '(', '*', ':' },
            { ' ', ')', ' ', ';' },
            { '0', '&', 'J', 'T' },
            { '1', 'A', 'K', 'U' },
            { '2', 'B', 'L', 'V' },
            { '3', 'C', 'M', 'W' },
            { '4', 'D', 'N', 'X' },
            { '5', 'E', 'O', 'Y' },
            { '6', 'F', 'P', 'Z' },
            { '7', 'G', 'Q' },
            { '8', 'H', 'R' },
            { '9', 'I', 'S' }
        };
    }

    

}
