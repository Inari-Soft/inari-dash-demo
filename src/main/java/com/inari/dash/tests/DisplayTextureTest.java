package com.inari.dash.tests;


import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.inari.commons.geom.Rectangle;
import com.inari.dash.Configuration;
import com.inari.dash.game.GameService;
import com.inari.firefly.asset.AssetSystem;
import com.inari.firefly.entity.ETransform;
import com.inari.firefly.entity.Entity;
import com.inari.firefly.entity.EntitySystem;
import com.inari.firefly.libgdx.GdxFFTestAdapter;
import com.inari.firefly.renderer.TextureAsset;
import com.inari.firefly.renderer.sprite.ESprite;
import com.inari.firefly.renderer.sprite.SpriteAsset;
import com.inari.firefly.system.FFContext;

public class DisplayTextureTest extends GdxFFTestAdapter {

    @Override
    public void initTest( FFContext context ) {
        Configuration globalAssetData = new Configuration();
        AssetSystem assetSystem = context.getComponent( AssetSystem.CONTEXT_KEY );
        EntitySystem entitySystem = context.getComponent( EntitySystem.CONTEXT_KEY );
        

        TextureAsset textureAsset = assetSystem
            .getAssetBuilder( TextureAsset.class )
                .set( TextureAsset.NAME, GameService.GAME_FONT_TEXTURE_KEY.name )
                .set( TextureAsset.ASSET_GROUP, GameService.GAME_FONT_TEXTURE_KEY.group )
                .set( TextureAsset.RESOURCE_NAME, globalAssetData.unitTextureResource )
                .set( TextureAsset.TEXTURE_WIDTH, globalAssetData.unitTextureWidth )
                .set( TextureAsset.TEXTURE_HEIGHT, globalAssetData.unitTextureHeight )
            .build();
        
        SpriteAsset spriteAsset = assetSystem
            .getAssetBuilder( SpriteAsset.class )
                .set( SpriteAsset.NAME, "TextureSprite" )
                .set( SpriteAsset.ASSET_GROUP, GameService.GAME_FONT_TEXTURE_KEY.group )
                .set( SpriteAsset.TEXTURE_ID, assetSystem.getAssetTypeKey( GameService.GAME_FONT_TEXTURE_KEY ).id )
                .set( SpriteAsset.TEXTURE_REGION, new Rectangle( 0, 0, globalAssetData.unitTextureWidth, globalAssetData.unitTextureHeight ) )
            .build();
        
        assetSystem.loadAssets( GameService.GAME_FONT_TEXTURE_KEY.group );
        
        Entity entity = entitySystem
              .getEntityBuilder()
                  .set( ETransform.VIEW_ID, 0 )
                  .set( ETransform.XPOSITION, 0 )
                  .set( ETransform.XPOSITION, 0 )
                  .set( ESprite.SPRITE_ID, spriteAsset.getId() )
              .build();
              
        entitySystem.activate( entity.getId() );

    }

    @Override
    public String getTitle() {
        return "TextureDisplayTest";
    }
    
    public static void main (String[] arg) {
        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.resizable = false;
        config.width = 800;
        config.height = 600;
        new LwjglApplication( new DisplayTextureTest(), config );
    }

}
