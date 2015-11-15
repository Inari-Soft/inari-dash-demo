package com.inari.dash.tests;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.inari.commons.geom.Rectangle;
import com.inari.commons.lang.TypedKey;
import com.inari.dash.Configuration;
import com.inari.dash.game.GameService;
import com.inari.dash.game.cave.CaveData;
import com.inari.firefly.asset.AssetSystem;
import com.inari.firefly.component.dynattr.DynamicAttribueMapper;
import com.inari.firefly.entity.ETransform;
import com.inari.firefly.entity.Entity;
import com.inari.firefly.entity.EntitySystem;
import com.inari.firefly.filter.ColorReplaceMapFitler;
import com.inari.firefly.filter.IColorFilter;
import com.inari.firefly.libgdx.GdxConfiguration;
import com.inari.firefly.libgdx.GdxFFTestAdapter;
import com.inari.firefly.renderer.TextureAsset;
import com.inari.firefly.renderer.sprite.ESprite;
import com.inari.firefly.renderer.sprite.SpriteAsset;
import com.inari.firefly.system.FFContext;

public class DisplayColorFiteredTextureTest extends GdxFFTestAdapter {
    
    @Override
    public void initTest( FFContext context ) {
        DynamicAttribueMapper.addDynamicAttribute( GdxConfiguration.DynamicAttributes.TEXTURE_COLOR_FILTER_NAME );
        
        Configuration globalAssetData = new Configuration();
        AssetSystem assetSystem = context.getComponent( AssetSystem.CONTEXT_KEY );
        EntitySystem entitySystem = context.getComponent( EntitySystem.CONTEXT_KEY );
        
        TypedKey<IColorFilter> colorFilterKey = TypedKey.create( "colorFilterKey", IColorFilter.class );
        ColorReplaceMapFitler colorFilter = new ColorReplaceMapFitler( createColorReplaceMap() );
        context.putComponent( colorFilterKey, colorFilter );

        TextureAsset textureAsset = assetSystem
            .getAssetBuilder( TextureAsset.class )
                .set( TextureAsset.NAME, GameService.GAME_FONT_TEXTURE_KEY.name )
                .set( TextureAsset.ASSET_GROUP, GameService.GAME_FONT_TEXTURE_KEY.group )
                .set( TextureAsset.RESOURCE_NAME, globalAssetData.unitTextureResource )
                .set( TextureAsset.TEXTURE_WIDTH, globalAssetData.unitTextureWidth )
                .set( TextureAsset.TEXTURE_HEIGHT, globalAssetData.unitTextureHeight )
                .set( GdxConfiguration.DynamicAttributes.TEXTURE_COLOR_FILTER_NAME, colorFilterKey.id() )
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
                  .set( ETransform.XPOSITION, 10 )
                  .set( ETransform.YPOSITION, 10 )
                  .set( ESprite.SPRITE_ID, spriteAsset.getId() )
              .build();
              
        entitySystem.activate( entity.getId() );

    }

    private Map<Integer, Integer> createColorReplaceMap() {
        Map<Integer, Integer> result = new HashMap<Integer, Integer>();
        
        result.put( CaveData.CaveColors.BaseForegroundColor1.color, CaveData.CaveColors.Blue.color );
        result.put( CaveData.CaveColors.BaseForegroundColor2.color, CaveData.CaveColors.Gray1.color );
        result.put( CaveData.CaveColors.BaseForegroundColor3.color, CaveData.CaveColors.White.color );
        
        return result;
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
        new LwjglApplication( new DisplayColorFiteredTextureTest(), config );
    }

}
