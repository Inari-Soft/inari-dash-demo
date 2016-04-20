package com.inari.dash;

import com.inari.firefly.component.ContextComponent;

public final class Configuration extends ContextComponent {
    
    public static final String COMPONENT_NAME = "InariDashConfiguration";

    public String titleSongResource;
    
    public String unitTextureResource;
    public int unitTextureWidth;
    public int unitTextureHeight;
    public int unitWidth;
    public int unitHeight;
    
    public String fontTextureResource;
    public int fontTextureWidth;
    public int fontTextureHeight;
    public int charWidth;
    public int charHeight;
    
    public int cameraMoveVelocity = 3;
    
    public char[][] fontChars;

    public Configuration() {
        super( COMPONENT_NAME );
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
