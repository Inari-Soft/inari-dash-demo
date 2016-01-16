package com.inari.dash.game.cave.unit.wall;

import java.util.Map;

import com.inari.commons.geom.Direction;
import com.inari.commons.geom.Rectangle;
import com.inari.commons.lang.aspect.AspectSetBuilder;
import com.inari.dash.game.cave.CaveSystem;
import com.inari.dash.game.cave.CaveSystem.SoundChannel;
import com.inari.dash.game.cave.unit.EUnit;
import com.inari.dash.game.cave.unit.UnitAspect;
import com.inari.dash.game.cave.unit.UnitHandle;
import com.inari.dash.game.cave.unit.UnitType;
import com.inari.firefly.FFInitException;
import com.inari.firefly.asset.AnimatedSpriteData;
import com.inari.firefly.asset.AnimatedTileAsset;
import com.inari.firefly.audio.AudioEvent;
import com.inari.firefly.audio.Sound;
import com.inari.firefly.audio.SoundAsset;
import com.inari.firefly.audio.AudioEvent.Type;
import com.inari.firefly.entity.EEntity;
import com.inari.firefly.entity.ETransform;
import com.inari.firefly.entity.EntityController;
import com.inari.firefly.entity.prefab.EntityPrefab;
import com.inari.firefly.graphics.tile.ETile;
import com.inari.firefly.state.StateChange;
import com.inari.firefly.state.Workflow;
import com.inari.firefly.system.FFContext;

public final class MagicWall extends UnitHandle {
    
    public enum StateName {
        INACTIVE,
        ACTIVE
    }
    
    public enum StateChangeName {
        INACTIVE_TO_ACTIVE,
        ACTIVE_TO_INACTIVE
    }
    
    public static final String MAGIC_WALL_NAME = "magicWall";

    private int animationAssetId;
    private int prefabId;
    private int controllerId;
    private int soundId;
    

    @Override
    public final void init( FFContext context ) throws FFInitException {
        super.init( context );

        String soundName = MAGIC_WALL_NAME + "_sound";
        int soundAssetId = assetSystem.getAssetBuilder()
            .set( SoundAsset.NAME, soundName )
            .set( SoundAsset.RESOURCE_NAME, "original/sound/" + MAGIC_WALL_NAME + ".wav" )
            .set( SoundAsset.STREAMING, false )
        .activate( SoundAsset.class );
        
        soundId = soundSystem.getSoundBuilder()
            .set( Sound.NAME, soundName )
            .set( Sound.SOUND_ASSET_ID, soundAssetId )
            .set( Sound.CHANNEL, SoundChannel.MAGIC_WALL.ordinal() )
            .set( Sound.LOOPING, true )
        .build();
    }

    @Override
    public final void loadCaveData( FFContext context ) {
        super.loadCaveData( context );
        
        int workflowId = stateSystem.getWorkflowBuilder()
            .set( Workflow.NAME, MAGIC_WALL_NAME )
            .set( Workflow.START_STATE_NAME, StateName.INACTIVE.name() )
            .add( Workflow.STATES, StateName.INACTIVE.name() )
            .add( Workflow.STATES, StateName.ACTIVE.name() )
            .add( Workflow.STATE_CHANGES, new StateChange( StateChangeName.INACTIVE_TO_ACTIVE.name(), StateName.INACTIVE.name(), StateName.ACTIVE.name() ) )
            .add( Workflow.STATE_CHANGES, new StateChange( StateChangeName.ACTIVE_TO_INACTIVE.name(), StateName.ACTIVE.name(), StateName.INACTIVE.name() ) )
        .activate();
        
        float updateRate = caveService.getUpdateRate();
        AnimatedSpriteData[] animationDataInactive = AnimatedSpriteData.create( StateName.INACTIVE.name(), Integer.MAX_VALUE, new Rectangle( 3 * 32, 6 * 32, 32, 32 ), 1, Direction.EAST );
        AnimatedSpriteData[] animationDataActive = AnimatedSpriteData.create( StateName.ACTIVE.name(), 100 - (int) updateRate * 4, new Rectangle( 4 * 32, 6 * 32, 32, 32 ), 4, Direction.EAST );
        animationAssetId = assetSystem.getAssetBuilder()
            .set( AnimatedTileAsset.NAME, MAGIC_WALL_NAME )
            .set( AnimatedTileAsset.LOOPING, true )
            .set( AnimatedTileAsset.UPDATE_RESOLUTION, updateRate )
            .set( AnimatedTileAsset.TEXTURE_ASSET_ID, assetSystem.getAssetId( CaveSystem.GAME_UNIT_TEXTURE_NAME ) )
            .set( AnimatedTileAsset.WORKFLOW_ID, workflowId )
            .add( AnimatedTileAsset.ANIMATED_SPRITE_DATA, animationDataInactive )
            .add( AnimatedTileAsset.ANIMATED_SPRITE_DATA, animationDataActive )
        .activate( AnimatedTileAsset.class );
        
        controllerId = controllerSystem.getControllerBuilder()
            .set( EntityController.NAME, MAGIC_WALL_NAME )
            .set( EntityController.UPDATE_RESOLUTION, updateRate )
        .build( MagicWallController.class );
        
        prefabId = prefabSystem.getEntityPrefabBuilder()
            .set( EntityPrefab.NAME, MAGIC_WALL_NAME )
            .add( EEntity.CONTROLLER_IDS, controllerId )
            .add( EEntity.CONTROLLER_IDS, assetSystem.getAssetInstaceId( animationAssetId ) )
            .set( ETransform.VIEW_ID, viewSystem.getViewId( CaveSystem.CAVE_VIEW_NAME ) )
            .set( ETile.MULTI_POSITION, false )
            .set( EUnit.UNIT_TYPE, type() )
            .set( EUnit.ASPECTS, AspectSetBuilder.create( 
                UnitAspect.DESTRUCTIBLE
            ) )
        .build();
        prefabSystem.cacheComponents( prefabId, 50 );
    }
    
    @Override
    public final void disposeCaveData( FFContext context ) {
        super.disposeCaveData( context );

        controllerSystem.deleteController( controllerId );
        controllerId = -1;
        prefabSystem.deletePrefab( prefabId );
        assetSystem.deleteAsset( animationAssetId );
        stateSystem.deleteWorkflow( MAGIC_WALL_NAME );
        context.notify( new AudioEvent( soundId, Type.STOP_PLAYING ) );
    }

    @Override
    public final UnitType type() {
        return UnitType.MAGIC_WALL;
    }

    @Override
    public final void initBDCFFTypesMap( Map<String, UnitType> bdcffMap ) {
        bdcffMap.put( "M", type() );
    }

    @Override
    public final int getSoundId() {
        return soundId;
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
    public final void dispose( FFContext context ) {
        prefabSystem.deletePrefab( MAGIC_WALL_NAME );
        soundSystem.deleteSound( soundId );
        assetSystem.deleteAsset( MAGIC_WALL_NAME + "_sound" );
    }

}
