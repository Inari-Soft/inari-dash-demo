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
import com.inari.firefly.asset.AssetNameKey;
import com.inari.firefly.control.EController;
import com.inari.firefly.entity.ETransform;
import com.inari.firefly.entity.Entity;
import com.inari.firefly.entity.EntityController;
import com.inari.firefly.entity.EntityPrefab;
import com.inari.firefly.renderer.sprite.ESprite;
import com.inari.firefly.renderer.sprite.SpriteAsset;
import com.inari.firefly.renderer.tile.ETile;
import com.inari.firefly.sound.Sound;
import com.inari.firefly.sound.SoundAsset;
import com.inari.firefly.system.FFContext;
import com.inari.firefly.system.FFInitException;

public final class Rock extends UnitHandle {
    
    public static final String ROCK_NAME = "rock";
    public static final AssetNameKey ROCK_SPRITE_ASSET_KEY = new AssetNameKey( CaveSystem.GAME_UNIT_TEXTURE_KEY.group, ROCK_NAME );
    public static final AssetNameKey ROCK_SOUND_ASSEET_KEY = new AssetNameKey( CaveSystem.CAVE_SOUND_GROUP_NAME, ROCK_NAME );
    
    private int prefabId;
    private int controllerId;
    private int soundId;

    @Override
    public final void init( FFContext context ) throws FFInitException {
        super.init( context );
        
        assetSystem.getAssetBuilder()
            .set( SpriteAsset.NAME, ROCK_SPRITE_ASSET_KEY.name )
            .set( SpriteAsset.ASSET_GROUP, ROCK_SPRITE_ASSET_KEY.group )
            .set( SpriteAsset.TEXTURE_ID, assetSystem.getAssetId( CaveSystem.GAME_UNIT_TEXTURE_KEY ) )
            .set( SpriteAsset.TEXTURE_REGION, new Rectangle( 0, 7 * 32, 32, 32 ) )
        .build( SpriteAsset.class );
        super.caveAssetsToReload.add( assetSystem.getAssetTypeKey( ROCK_SPRITE_ASSET_KEY ) );
        
        assetSystem.getAssetBuilder()
            .set( SoundAsset.NAME, ROCK_SOUND_ASSEET_KEY.name )
            .set( SoundAsset.ASSET_GROUP, ROCK_SOUND_ASSEET_KEY.group )
            .set( SoundAsset.RESOURCE_NAME, "original/sound/stone.wav" )
            .set( SoundAsset.STREAMING, false )
        .activate( SoundAsset.class );
        
        soundId = soundSystem.getSoundBuilder()
            .set( Sound.NAME, ROCK_SOUND_ASSEET_KEY.name )
            .set( Sound.ASSET_ID, assetSystem.getAssetId( ROCK_SOUND_ASSEET_KEY ) )
            .set( Sound.LOOPING, false )
            .set( Sound.CHANNEL, SoundChannel.ROCK.ordinal() )
        .build();

        initialized = true;
    }

    @Override
    public final void loadCaveData( FFContext context ) {
        super.loadCaveData( context );
        controllerId = controllerSystem.getControllerBuilder()
            .set( EntityController.NAME, "RockController" )
        .build( RockController.class );
        
        prefabId = prefabSystem.getEntityPrefabBuilder()
            .set( EController.CONTROLLER_IDS, new int[] { controllerId } )
            .set( EntityPrefab.NAME, ROCK_NAME )
            .set( ETransform.VIEW_ID, viewSystem.getViewId( CaveSystem.CAVE_VIEW_NAME ) )
            .set( ESprite.SPRITE_ID, assetSystem.getAssetId( ROCK_SPRITE_ASSET_KEY ) )
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
    }

    @Override
    public final void dispose( FFContext context ) {
        prefabSystem.deletePrefab( ROCK_NAME );
        assetSystem.disposeAsset( ROCK_SPRITE_ASSET_KEY );
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
        Entity entity = prefabSystem.buildOne( prefabId );
        int entityId = entity.getId();
        ETile tile = entitySystem.getComponent( entityId , ETile.class );
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
