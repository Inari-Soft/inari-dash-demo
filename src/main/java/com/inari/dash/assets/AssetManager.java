package com.inari.dash.assets;

import com.inari.commons.geom.Rectangle;
import com.inari.commons.lang.TypedKey;
import com.inari.firefly.asset.AssetSystem;
import com.inari.firefly.renderer.TextureAsset;
import com.inari.firefly.renderer.sprite.SpriteAsset;
import com.inari.firefly.system.FFContext;
import com.inari.firefly.text.Font;
import com.inari.firefly.text.TextSystem;

public class AssetManager {
    
    public static final TypedKey<AssetManager> CONTEXT_KEY = TypedKey.create( "AssetManager", AssetManager.class );
    
    private final static GlobalAssetData DEFAULT_GLOBAL_ASSET_DATA = new GlobalAssetData();
    
    private final GlobalAssetData globalAssetData;
    private final FFContext context;
    
    public AssetManager( FFContext context, GlobalAssetData globalAssetData ) {
        this.globalAssetData = globalAssetData;
        this.context = context;
        
        context.putComponent( CONTEXT_KEY, this );
    }
    
    public void initAllAssets() {
        AssetSystem assetSystem = context.getComponent( AssetSystem.CONTEXT_KEY );
        assetSystem
            .getAssetBuilder( TextureAsset.class )
                .set( TextureAsset.NAME, globalAssetData.unitTextureKey.name )
                .set( TextureAsset.ASSET_GROUP, globalAssetData.unitTextureKey.group )
                .set( TextureAsset.RESOURCE_NAME, globalAssetData.unitTextureResource )
                .set( TextureAsset.TEXTURE_WIDTH, globalAssetData.unitTextureWidth )
                .set( TextureAsset.TEXTURE_HEIGHT, globalAssetData.unitTextureHeight )
            .buildAndNext()
                .set( TextureAsset.NAME, globalAssetData.fontTextureKey.name )
                .set( TextureAsset.ASSET_GROUP, globalAssetData.fontTextureKey.group )
                .set( TextureAsset.RESOURCE_NAME, globalAssetData.fontTextureResource )
                .set( TextureAsset.TEXTURE_WIDTH, globalAssetData.fontTextureWidth )
                .set( TextureAsset.TEXTURE_HEIGHT, globalAssetData.fontTextureHeight )
            .build();

        TextSystem textSystem = context.getComponent( TextSystem.CONTEXT_KEY );
        Font font = textSystem
            .getFontBuilder()
                .set( Font.NAME, GlobalAssetData.FONT_NAME )
                .set( Font.CHAR_WIDTH, globalAssetData.charWidth )
                .set( Font.CHAR_HEIGHT, globalAssetData.charHeight )
                .set( Font.CHAR_SPACE, 5 )
                .set( Font.LINE_SPACE, 5 )
            .build();
        
        createFontSprites( assetSystem, font );
    }
    
    public void loadTextures() {
        AssetSystem assetSystem = context.getComponent( AssetSystem.CONTEXT_KEY );
        
        assetSystem.loadAssets( globalAssetData.fontTextureKey.group );
        assetSystem.loadAssets( globalAssetData.unitTextureKey.group );
    }
    
    public void unloadTextures() {
        AssetSystem assetSystem = context.getComponent( AssetSystem.CONTEXT_KEY );
        
        assetSystem.disposeAsset( globalAssetData.fontTextureKey );
        assetSystem.disposeAsset( globalAssetData.unitTextureKey );
    }
    
    // TODO sounds

    private void createFontSprites( AssetSystem assetSystem, Font font ) {
        int textureAssetId = assetSystem.getAssetTypeKey( globalAssetData.fontTextureKey ).id;
        Rectangle textureRegion = new Rectangle( 0, 0, globalAssetData.charWidth, globalAssetData.charHeight );
        for ( int y = 0; y < globalAssetData.fontChars.length; y++ ) {
            for ( int x = 0; x < globalAssetData.fontChars[ y ].length; x++ ) {
                textureRegion.x = x * globalAssetData.charWidth;
                textureRegion.y = y * globalAssetData.charHeight;
                
                SpriteAsset charSpriteAsset = assetSystem
                    .getAssetBuilder( SpriteAsset.class )
                        .set( SpriteAsset.TEXTURE_ID, textureAssetId )
                        .set( SpriteAsset.TEXTURE_REGION, textureRegion )
                        .set( SpriteAsset.ASSET_GROUP, globalAssetData.fontTextureKey.group )
                        .set( SpriteAsset.NAME, "char_" + globalAssetData.fontChars[ y ][ x ] )
                    .build();
                
                font.setCharSpriteMapping( globalAssetData.fontChars[ y ][ x ], charSpriteAsset.getId() );
            }
        }
    }

}
