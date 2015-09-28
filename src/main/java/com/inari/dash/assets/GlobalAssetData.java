package com.inari.dash.assets;

import com.inari.commons.lang.TypedKey;
import com.inari.firefly.asset.AssetNameKey;

public final class GlobalAssetData {
    
    public static final TypedKey<GlobalAssetData> CONTEXT_KEY = TypedKey.create( "GlobalAssetData", GlobalAssetData.class );
    
    public static final String UNIT_ASSET_GROUP = "unitGroup";
    public static final String FONT_ASSET_GROUP = "fontGroup";
    public static final String FONT_NAME = "dashFont";
    
    
    public final String unitTextureResource;
    public final AssetNameKey unitTextureKey;
    public final int unitTextureWidth;
    public final int unitTextureHeight;
    public final int unitWidth;
    public final int unitHeight;
    
    public final String fontTextureResource;
    public final AssetNameKey fontTextureKey;
    public final int fontTextureWidth;
    public final int fontTextureHeight;
    public final int charWidth;
    public final int charHeight;
    
    
    public final char[][] fontChars;

    public GlobalAssetData() {
        unitTextureResource = "original/texture/tiles.png";
        fontTextureResource = "original/texture/font.png";
        unitTextureKey = new AssetNameKey( UNIT_ASSET_GROUP, "unitTexture" ) ;
        fontTextureKey = new AssetNameKey( FONT_ASSET_GROUP, "fontTexture" );
        
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
