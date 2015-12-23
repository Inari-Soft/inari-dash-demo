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
import com.inari.firefly.entity.EEntity;
import com.inari.firefly.entity.ETransform;
import com.inari.firefly.entity.EntityController;
import com.inari.firefly.entity.EntityPrefab;
import com.inari.firefly.renderer.sprite.ESprite;
import com.inari.firefly.renderer.sprite.SpriteAsset;
import com.inari.firefly.renderer.tile.ETile;
import com.inari.firefly.sound.Sound;
import com.inari.firefly.sound.SoundAsset;
import com.inari.firefly.system.FFContext;

public final class Rock extends UnitHandle {
    
    public static final String ROCK_NAME = "rock";
    public static final String ROCK_SPRITE_ASSET_NAME = ROCK_NAME + "_sprite";
    public static final String ROCK_SOUND_ASSEET_NAME = ROCK_NAME + "_sound";
    
    private int prefabId;
    private int controllerId;
    private int soundId;

    @Override
    public final void init( FFContext context ) throws FFInitException {
        super.init( context );
        
        int soundAssetId = assetSystem.getAssetBuilder()
            .set( SoundAsset.NAME, ROCK_SOUND_ASSEET_NAME )
            .set( SoundAsset.RESOURCE_NAME, "original/sound/stone.wav" )
            .set( SoundAsset.STREAMING, false )
        .activate( SoundAsset.class );
        
        soundId = soundSystem.getSoundBuilder()
            .set( Sound.NAME, ROCK_SOUND_ASSEET_NAME )
            .set( Sound.SOUND_ASSET_ID, soundAssetId )
            .set( Sound.LOOPING, false )
            .set( Sound.CHANNEL, SoundChannel.ROCK.ordinal() )
        .build();

        initialized = true;
    }

    @Override
    public final void loadCaveData( FFContext context ) {
        super.loadCaveData( context );
        
        assetSystem.getAssetBuilder()
            .set( SpriteAsset.NAME, ROCK_SPRITE_ASSET_NAME )
            .set( SpriteAsset.TEXTURE_ASSET_ID, assetSystem.getAssetId( CaveSystem.GAME_UNIT_TEXTURE_NAME ) )
            .set( SpriteAsset.TEXTURE_REGION, new Rectangle( 0, 7 * 32, 32, 32 ) )
        .activate( SpriteAsset.class );
        
        
        controllerId = controllerSystem.getControllerBuilder()
            .set( EntityController.NAME, "RockController" )
        .build( RockController.class );
        
        prefabId = prefabSystem.getEntityPrefabBuilder()
            .add( EEntity.CONTROLLER_IDS, controllerId )
            .set( EntityPrefab.NAME, ROCK_NAME )
            .set( ETransform.VIEW_ID, viewSystem.getViewId( CaveSystem.CAVE_VIEW_NAME ) )
            .set( ESprite.SPRITE_ID, assetSystem.getAssetInstanceId( ROCK_SPRITE_ASSET_NAME ) )
            .set( ETile.MULTI_POSITION, false )
            .set( EUnit.UNIT_TYPE, type() )
            .set( EUnit.CHANGE_TO, UnitType.DIAMOND )
            .set( EUnit.ASPECTS, AspectSetBuilder.create( 
                UnitAspect.DESTRUCTIBLE, 
                UnitAspect.STONE, 
                UnitAspect.ASLOPE
            ) )
        .build();
        prefabSystem.cacheComponents( prefabId, 200 );
        
        float updateRate = caveService.getUpdateRate();
        controllerSystem.getController( controllerId ).setUpdateResolution( updateRate );
    }

    @Override
    public void disposeCaveData( FFContext context ) {
        super.disposeCaveData( context );
        
        controllerSystem.deleteController( controllerId );
        prefabSystem.deletePrefab( prefabId );
        assetSystem.deleteAsset( ROCK_SPRITE_ASSET_NAME );
    }

    @Override
    public final void dispose( FFContext context ) {
        prefabSystem.deletePrefab( ROCK_NAME );
        assetSystem.disposeAsset( ROCK_SPRITE_ASSET_NAME );
        controllerSystem.deleteController( controllerId );
    }

    @Override
    public final UnitType type() {
        return UnitType.ROCK;
    }

    @Override
    public final void initBDCFFTypesMap( Map<String, UnitType> bdcffMap ) {
        bdcffMap.put( "r", type() );
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
        return soundId;
    }

}
