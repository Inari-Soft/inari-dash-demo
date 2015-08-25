
package com.inari.dash;

import java.util.Collection;
import java.util.Iterator;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.graphics.FPSLogger;
import com.inari.commons.geom.Rectangle;
import com.inari.commons.graphics.RGBColor;
import com.inari.commons.lang.ImmutablePair;
import com.inari.commons.lang.IntIterator;
import com.inari.firefly.animation.Animation;
import com.inari.firefly.animation.AnimationSystem;
import com.inari.firefly.animation.IntTimelineAnimation;
import com.inari.firefly.asset.AssetNameKey;
import com.inari.firefly.asset.AssetSystem;
import com.inari.firefly.asset.AssetTypeKey;
import com.inari.firefly.asset.SpriteAssetBatch;
import com.inari.firefly.control.Controller;
import com.inari.firefly.control.ControllerSystem;
import com.inari.firefly.control.EController;
import com.inari.firefly.controller.SpriteIdAnimationController;
import com.inari.firefly.entity.ETransform;
import com.inari.firefly.entity.Entity;
import com.inari.firefly.entity.EntitySystem;
import com.inari.firefly.libgdx.GDXInputImpl;
import com.inari.firefly.libgdx.GDXLowerSystemImpl;
import com.inari.firefly.renderer.TextureAsset;
import com.inari.firefly.renderer.sprite.ESprite;
import com.inari.firefly.system.FFContext;
import com.inari.firefly.system.FFContext.Systems;
import com.inari.firefly.system.FireFly;
import com.inari.firefly.system.view.ViewSystem;

public class InariDash extends ApplicationAdapter {
    
    private FPSLogger fpsLogger = new FPSLogger();
    private FireFly firefly;
    private static final AssetNameKey TEXTURE_ASSET_KEY = new AssetNameKey( "origTilesResource", "boulderDashTextureAsset" );
    private static final AssetNameKey SPRITE_ASSET_KEY = new AssetNameKey( "origTilesResource", "spriteAsset" );
    
    @Override
    public void create () {
        Gdx.graphics.setTitle( "Inari Dash" );
        
        firefly = new FireFly( GDXLowerSystemImpl.class, GDXInputImpl.class );
        FFContext context = firefly.getContext();
        AssetSystem assetSystem = context.getComponent( Systems.ASSET_SYSTEM );
        EntitySystem entitySystem = context.getComponent( Systems.ENTITY_SYSTEM );
        ViewSystem viewSystem = context.getComponent( Systems.VIEW_SYSTEM );
        AnimationSystem animationSystem = context.getComponent( Systems.ANIMATION_SYSTEM );
        ControllerSystem controllerSystem = context.getComponent( Systems.ENTITY_CONTROLLER_SYSTEM );
        
        assetSystem
            .getAssetBuilder( TextureAsset.class )
                .setAttribute( TextureAsset.NAME, TEXTURE_ASSET_KEY.name )
                .setAttribute( TextureAsset.ASSET_GROUP, TEXTURE_ASSET_KEY.group )
                .setAttribute( TextureAsset.RESOURCE_NAME, "origTiles.png" )
                .setAttribute( TextureAsset.TEXTURE_WIDTH, 8 * 32 )
                .setAttribute( TextureAsset.TEXTURE_HEIGHT, 12 * 32 )
//            .buildAndNext( SpriteAsset.class )
//                .setAttribute( SpriteAsset.NAME, SPRITE_ASSET_KEY.name )
//                .setAttribute( SpriteAsset.ASSET_GROUP, SPRITE_ASSET_KEY.group )
//                .setAttribute( SpriteAsset.TEXTURE_ID, assetSystem.getAssetTypeKey( TEXTURE_ASSET_KEY ).id )
//                .setAttribute( SpriteAsset.TEXTURE_REGION, new Rectangle( 0, 0, 32, 32 ) )
            .build()
            ;
        
        SpriteAssetBatch inariDashSpriteBatch = new SpriteAssetBatch( context, TEXTURE_ASSET_KEY );
        Collection<ImmutablePair<AssetNameKey, AssetTypeKey>> rockfordBlinkSpriteData = inariDashSpriteBatch.createSprites( 
            new Rectangle( 0, 32, 32, 32 ), 8, 1, "origTilesResource", "rockfordBlink" 
        );
        
        IntTimelineAnimation rockfordBlinkAnimation = animationSystem.getAnimationBuilder( IntTimelineAnimation.class )
            .setAttribute( Animation.NAME, "rockfordBlinkAnimation" )
            .setAttribute( Animation.LOOPING, true )
            .build()
            .createTimelineData( createSpriteIterator( rockfordBlinkSpriteData ), 100 );
        ;
        
        SpriteIdAnimationController rockfordSpriteController = controllerSystem.getControllerBuilder( SpriteIdAnimationController.class )
            .setAttribute( Controller.NAME, "rockfordSpriteController" )
            .setAttribute( SpriteIdAnimationController.SPRITE_ID_ANIMATION_ID, rockfordBlinkAnimation.getId() )
            .build();
            
        Entity entity = entitySystem
            .getEntityBuilder()
                .setAttribute( ETransform.VIEW_ID, 0 )
                .setAttribute( ETransform.XPOSITION, 0 )
                .setAttribute( ETransform.XPOSITION, 0 )
//                .setAttribute( ETransform.PIVOT_X, 16 )
//                .setAttribute( ETransform.PIVOT_Y, 16 )
//                .setAttribute( ETransform.SCALE_X, 2 )
//                .setAttribute( ETransform.SCALE_Y, 2 )
//                .setAttribute( ETransform.ROTATION, 45 )
                .setAttribute( ESprite.SPRITE_ID, rockfordBlinkSpriteData.iterator().next().second.id )
                .setAttribute( ESprite.TINT_COLOR, new RGBColor( 1f, 1f, 1, 1 ) )
                .setAttribute( EController.CONTROLLER_IDS, new int[] { rockfordSpriteController.getId() } )
            .build();
            ;
            
        
        assetSystem.loadAsset( TEXTURE_ASSET_KEY );
        //assetSystem.loadAsset( SPRITE_ASSET_KEY );
        inariDashSpriteBatch.load();
        entitySystem.activate( entity.getId() );
    }

    private IntIterator createSpriteIterator( final Collection<ImmutablePair<AssetNameKey, AssetTypeKey>> spriteData ) {
        return new IntIterator() {

            private Iterator<ImmutablePair<AssetNameKey, AssetTypeKey>> iterator = spriteData.iterator();
            
            @Override
            public boolean hasNext() {
                return iterator.hasNext();
            }

            @Override
            public int next() {
                return iterator.next().second.id;
            }
            
        };
    }

    @Override
    public void render () {
        firefly.update();
        firefly.render();
    }
    
    public static void main (String[] arg) {
        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.resizable = false;
        config.width = 800;
        config.height = 600;
        new LwjglApplication(new InariDash(), config);
    }
}
