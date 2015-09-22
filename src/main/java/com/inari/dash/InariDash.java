
package com.inari.dash;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.inari.firefly.libgdx.FireflyApplicationAdapter;
import com.inari.firefly.state.State;
import com.inari.firefly.system.FFContext;

public class InariDash extends FireflyApplicationAdapter {
    
//    private FPSLogger fpsLogger = new FPSLogger();
//    private FireFly firefly;
//    private static final AssetNameKey TEXTURE_ASSET_KEY = new AssetNameKey( "origTilesResource", "boulderDashTextureAsset" );
//    private static final AssetNameKey SPRITE_ASSET_KEY = new AssetNameKey( "origTilesResource", "spriteAsset" );
    
//    @Override
//    public void create () {
//        Gdx.graphics.setTitle( "Inari Dash" );
//        
//        firefly = new FireFly( GDXLowerSystemImpl.class, GDXInputImpl.class );
//        FFContext context = firefly.getContext();
//        AssetSystem assetSystem = context.getComponent( AssetSystem.CONTEXT_KEY );
//        EntitySystem entitySystem = context.getComponent( EntitySystem.CONTEXT_KEY );
//        ViewSystem viewSystem = context.getComponent( ViewSystem.CONTEXT_KEY );
//        AnimationSystem animationSystem = context.getComponent( AnimationSystem.CONTEXT_KEY );
//        ComponentControllerSystem controllerSystem = context.getComponent( ComponentControllerSystem.CONTEXT_KEY );
//        
//        assetSystem
//            .getAssetBuilder( TextureAsset.class )
//                .setAttribute( TextureAsset.NAME, TEXTURE_ASSET_KEY.name )
//                .setAttribute( TextureAsset.ASSET_GROUP, TEXTURE_ASSET_KEY.group )
//                .setAttribute( TextureAsset.RESOURCE_NAME, "original/tiles.png" )
//                .setAttribute( TextureAsset.TEXTURE_WIDTH, 8 * 32 )
//                .setAttribute( TextureAsset.TEXTURE_HEIGHT, 12 * 32 )
////            .buildAndNext( SpriteAsset.class )
////                .setAttribute( SpriteAsset.NAME, SPRITE_ASSET_KEY.name )
////                .setAttribute( SpriteAsset.ASSET_GROUP, SPRITE_ASSET_KEY.group )
////                .setAttribute( SpriteAsset.TEXTURE_ID, assetSystem.getAssetTypeKey( TEXTURE_ASSET_KEY ).id )
////                .setAttribute( SpriteAsset.TEXTURE_REGION, new Rectangle( 0, 0, 32, 32 ) )
//            .build()
//            ;
//        
//        SpriteAssetBatch inariDashSpriteBatch = new SpriteAssetBatch( context, TEXTURE_ASSET_KEY );
//        Collection<ImmutablePair<AssetNameKey, AssetTypeKey>> rockfordBlinkSpriteData = inariDashSpriteBatch.createSprites( 
//            new Rectangle( 0, 32, 32, 32 ), 8, 1, "origTilesResource", "rockfordBlink" 
//        );
//        
//        IntTimelineAnimation rockfordBlinkAnimation = animationSystem.getAnimationBuilder( IntTimelineAnimation.class )
//            .setAttribute( Animation.NAME, "rockfordBlinkAnimation" )
//            .setAttribute( Animation.LOOPING, true )
//            .build()
//            .createTimelineData( createSpriteIterator( rockfordBlinkSpriteData ), 100 );
//        ;
//        
//        SpriteIdAnimationController rockfordSpriteController = controllerSystem.getControllerBuilder( SpriteIdAnimationController.class )
//            .setAttribute( Controller.NAME, "rockfordSpriteController" )
//            .setAttribute( SpriteIdAnimationController.SPRITE_ID_ANIMATION_ID, rockfordBlinkAnimation.getId() )
//            .build();
//            
//        Entity entity = entitySystem
//            .getEntityBuilder()
//                .setAttribute( ETransform.VIEW_ID, 0 )
//                .setAttribute( ETransform.XPOSITION, 0 )
//                .setAttribute( ETransform.XPOSITION, 0 )
////                .setAttribute( ETransform.PIVOT_X, 16 )
////                .setAttribute( ETransform.PIVOT_Y, 16 )
////                .setAttribute( ETransform.SCALE_X, 2 )
////                .setAttribute( ETransform.SCALE_Y, 2 )
////                .setAttribute( ETransform.ROTATION, 45 )
//                .setAttribute( ESprite.SPRITE_ID, rockfordBlinkSpriteData.iterator().next().second.id )
//                .setAttribute( ESprite.TINT_COLOR, new RGBColor( 1f, 1f, 1, 1 ) )
//                .setAttribute( EController.CONTROLLER_IDS, new int[] { rockfordSpriteController.getId() } )
//            .build();
//            ;
//            
//        
//        assetSystem.loadAsset( TEXTURE_ASSET_KEY );
//        //assetSystem.loadAsset( SPRITE_ASSET_KEY );
//        inariDashSpriteBatch.load();
//        entitySystem.activate( entity.getId() );
//    }

//    private IntIterator createSpriteIterator( final Collection<ImmutablePair<AssetNameKey, AssetTypeKey>> spriteData ) {
//        return new IntIterator() {
//
//            private Iterator<ImmutablePair<AssetNameKey, AssetTypeKey>> iterator = spriteData.iterator();
//            
//            @Override
//            public boolean hasNext() {
//                return iterator.hasNext();
//            }
//
//            @Override
//            public int next() {
//                return iterator.next().second.id;
//            }
//            
//        };
//    }

//    @Override
//    public void render () {
//        firefly.update();
//        firefly.render();
//    }
    
    public static void main (String[] arg) {
        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.resizable = false;
        config.width = 800;
        config.height = 600;
        new LwjglApplication(new InariDash(), config);
    }

    @Override
    public String getTitle() {
        return "InariDash";
    }

    @Override
    public void initContext( FFContext context, State gameInitState ) {
        // TODO Auto-generated method stub
        
    }
}
