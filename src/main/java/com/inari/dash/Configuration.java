package com.inari.dash;

import java.util.Set;

import com.inari.commons.lang.TypedKey;
import com.inari.firefly.component.DataComponent;
import com.inari.firefly.component.attr.AttributeKey;
import com.inari.firefly.component.attr.AttributeMap;

public final class Configuration extends DataComponent {
    
    public static final TypedKey<Configuration> CONTEXT_KEY = TypedKey.create( "InariDashConfiguration", Configuration.class );

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

    @Override
    public final Class<Configuration> componentType() {
        return Configuration.class;
    }

    @Override
    public final Set<AttributeKey<?>> attributeKeys() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public final void fromAttributes( AttributeMap attributes ) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public final void toAttributes( AttributeMap attributes ) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public final TypedKey<? extends DataComponent> componentKey() {
        return CONTEXT_KEY;
    }

}
