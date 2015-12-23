package com.inari.dash.game.cave.unit.stone;

import java.util.Map;

import com.inari.commons.geom.Rectangle;
import com.inari.commons.lang.aspect.AspectSetBuilder;
import com.inari.dash.game.cave.CaveSystem;
import com.inari.dash.game.cave.CaveSystem.SoundChannel;
import com.inari.dash.game.cave.unit.EUnit;
import com.inari.dash.game.cave.unit.UnitAspect;
import com.inari.dash.game.cave.unit.UnitHandle;
import com.inari.dash.game.cave.unit.UnitType;
import com.inari.firefly.FFInitException;
import com.inari.firefly.animation.sprite.SpriteAnimationBuilder;
import com.inari.firefly.animation.sprite.SpriteAnimationBuilder.SpriteAnimationHandler;
import com.inari.firefly.entity.EEntity;
import com.inari.firefly.entity.ETransform;
import com.inari.firefly.entity.EntityController;
import com.inari.firefly.entity.EntityPrefab;
import com.inari.firefly.renderer.sprite.ESprite;
import com.inari.firefly.renderer.tile.ETile;
import com.inari.firefly.sound.Sound;
import com.inari.firefly.sound.SoundAsset;
import com.inari.firefly.system.FFContext;

public final class Diamond extends UnitHandle {
    
    public static final String DIAMOND_NAME = "diamond";
    
    private int prefabId;
    private int controllerId;
    private SpriteAnimationHandler spriteAnimationHandler;
    private int[] soundIds;
    private int soundIndex = 0;
    
    @Override
    public final void init( FFContext context ) throws FFInitException {
        super.init( context );
        
        soundIds = new int[ 8 ];
        for ( int i = 0; i < 8; i++ ) {
            String name = DIAMOND_NAME + ( i + 1 );
            int soundAssetId = assetSystem.getAssetBuilder()
                .set( SoundAsset.NAME, name )
                .set( SoundAsset.RESOURCE_NAME, "original/sound/" + name + ".wav" )
                .set( SoundAsset.STREAMING, false )
            .activate( SoundAsset.class );

            soundIds[ i ] = soundSystem.getSoundBuilder()
                .set( Sound.NAME, name )
                .set( Sound.SOUND_ASSET_ID, soundAssetId )
                .set( Sound.LOOPING, false )
                .set( Sound.CHANNEL, SoundChannel.DIAMOND.ordinal() )
            .build();
        }

        initialized = true;
    }

    @Override
    public final void loadCaveData( FFContext context ) {
        super.loadCaveData( context );
        
        spriteAnimationHandler = new SpriteAnimationBuilder( context )
            .setLooping( true )
            .setNamePrefix( DIAMOND_NAME )
            .setTextureAssetName( CaveSystem.GAME_UNIT_TEXTURE_NAME )
            .addSpritesToAnimation( 0, new Rectangle( 0, 10 * 32, 32, 32 ), 8, true )
        .build();
        
        controllerId = controllerSystem.getControllerBuilder()
            .set( EntityController.NAME, DIAMOND_NAME )
        .build( DiamondController.class );
        
        prefabId = prefabSystem.getEntityPrefabBuilder()
            .add( EEntity.CONTROLLER_IDS, controllerId )
            .add( EEntity.CONTROLLER_IDS, spriteAnimationHandler.getControllerId() )
            .set( EntityPrefab.NAME, DIAMOND_NAME )
            .set( ETransform.VIEW_ID, viewSystem.getViewId( CaveSystem.CAVE_VIEW_NAME ) )
            .set( ESprite.SPRITE_ID, spriteAnimationHandler.getStartSpriteId() )
            .set( ETile.MULTI_POSITION, false )
            .set( EUnit.UNIT_TYPE, type() )
            .set( EUnit.CHANGE_TO, UnitType.ROCK )
            .set( EUnit.ASPECTS, AspectSetBuilder.create( 
                UnitAspect.DESTRUCTIBLE, 
                UnitAspect.STONE, 
                UnitAspect.ASLOPE, 
                UnitAspect.WALKABLE
            ) )
        .build();
        prefabSystem.cacheComponents( prefabId, 200 );
            
        float updateRate = caveService.getUpdateRate();
        controllerSystem.getController( controllerId ).setUpdateResolution( updateRate );
        spriteAnimationHandler.setFrameTime( 80 - (int) updateRate * 4 );
    }

    @Override
    public void disposeCaveData( FFContext context ) {
        super.disposeCaveData( context );
        
        controllerSystem.deleteController( controllerId );
        prefabSystem.deletePrefab( prefabId );
        spriteAnimationHandler.dispose( context );
    }

    @Override
    public final UnitType type() {
        return UnitType.DIAMOND;
    }

    @Override
    public final void initBDCFFTypesMap( Map<String, UnitType> bdcffMap ) {
        bdcffMap.put( "d", type() );
    }
    
    @Override
    public final void dispose( FFContext context ) {
        prefabSystem.deletePrefab( DIAMOND_NAME );
        controllerSystem.deleteController( controllerId );
    }

    @Override
    public final int createOne( int xGridPos, int yGridPos ) {
        int entityId = prefabSystem.buildOne( prefabId );
        ETile tile = entitySystem.getComponent( entityId , ETile.TYPE_KEY );
        tile.setGridXPos( xGridPos );
        tile.setGridYPos( yGridPos );
        entitySystem.activateEntity( entityId );
        return entityId;
    }

    @Override
    public final int getSoundId() {
        soundIndex++;
        if ( soundIndex >= soundIds.length ) {
            soundIndex = 0;
        }
        return soundIds[ soundIndex ];
    }

}
