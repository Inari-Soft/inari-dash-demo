package com.inari.dash.game.cave.unit.wall;

import java.util.Map;

import com.inari.commons.geom.Direction;
import com.inari.commons.geom.Rectangle;
import com.inari.dash.game.cave.CaveSystem;
import com.inari.dash.game.cave.CaveSystem.SoundChannel;
import com.inari.dash.game.cave.unit.EUnit;
import com.inari.dash.game.cave.unit.Unit;
import com.inari.dash.game.cave.unit.UnitAspect;
import com.inari.dash.game.cave.unit.UnitType;
import com.inari.firefly.asset.Asset;
import com.inari.firefly.audio.AudioSystemEvent;
import com.inari.firefly.audio.AudioSystemEvent.Type;
import com.inari.firefly.audio.Sound;
import com.inari.firefly.audio.SoundAsset;
import com.inari.firefly.composite.sprite.AnimatedSpriteData;
import com.inari.firefly.composite.sprite.AnimatedTile;
import com.inari.firefly.control.Controller;
import com.inari.firefly.control.state.StateChange;
import com.inari.firefly.control.state.Workflow;
import com.inari.firefly.entity.EEntity;
import com.inari.firefly.entity.ETransform;
import com.inari.firefly.entity.EntityController;
import com.inari.firefly.entity.prefab.EntityPrefab;
import com.inari.firefly.entity.prefab.EntityPrefabSystem;
import com.inari.firefly.graphics.tile.ETile;
import com.inari.firefly.system.FFContext;
import com.inari.firefly.system.utils.Disposable;

public final class MagicWall extends Unit {

    public enum StateName {
        INACTIVE,
        ACTIVE
    }
    
    public enum StateChangeName {
        INACTIVE_TO_ACTIVE,
        ACTIVE_TO_INACTIVE
    }

    private int prefabId;
    private int soundId;
    
    protected MagicWall( int id ) {
        super( id );
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
    public final Disposable load( FFContext context ) {
        String soundName = UnitType.MAGIC_WALL.name() + "_sound";
        context.getComponentBuilder( Asset.TYPE_KEY )
            .set( SoundAsset.NAME, soundName )
            .set( SoundAsset.RESOURCE_NAME, "original/sound/magicWall.wav" )
            .set( SoundAsset.STREAMING, false )
        .activate( SoundAsset.class );
        
        soundId = context.getComponentBuilder( Sound.TYPE_KEY )
            .set( Sound.NAME, soundName )
            .set( Sound.SOUND_ASSET_NAME, soundName )
            .set( Sound.CHANNEL, SoundChannel.MAGIC_WALL.ordinal() )
            .set( Sound.LOOPING, true )
        .build();
        
        int workflowId = context.getComponentBuilder( Workflow.TYPE_KEY )
            .set( Workflow.NAME, UnitType.MAGIC_WALL.name() )
            .set( Workflow.START_STATE_NAME, StateName.INACTIVE.name() )
            .add( Workflow.STATES, StateName.INACTIVE.name() )
            .add( Workflow.STATES, StateName.ACTIVE.name() )
            .add( Workflow.STATE_CHANGES, new StateChange( StateChangeName.INACTIVE_TO_ACTIVE.name(), StateName.INACTIVE.name(), StateName.ACTIVE.name() ) )
            .add( Workflow.STATE_CHANGES, new StateChange( StateChangeName.ACTIVE_TO_INACTIVE.name(), StateName.ACTIVE.name(), StateName.INACTIVE.name() ) )
        .activate();
        
        float updateRate = getUpdateRate();
        AnimatedSpriteData[] animationDataInactive = AnimatedSpriteData.create( 
            StateName.INACTIVE.name(), 
            Integer.MAX_VALUE, 
            new Rectangle( 3 * 32, 6 * 32, 32, 32 ), 
            1, Direction.EAST 
        );
        AnimatedSpriteData[] animationDataActive = AnimatedSpriteData.create( 
            StateName.ACTIVE.name(), 
            100 - (int) updateRate * 4, 
            new Rectangle( 4 * 32, 6 * 32, 32, 32 ), 
            4, Direction.EAST 
        );
        int animationAssetId = context.getComponentBuilder( Asset.TYPE_KEY )
            .set( AnimatedTile.NAME, UnitType.MAGIC_WALL.name() )
            .set( AnimatedTile.LOOPING, true )
            .set( AnimatedTile.UPDATE_RESOLUTION, updateRate )
            .set( AnimatedTile.TEXTURE_ASSET_NAME, CaveSystem.GAME_UNIT_TEXTURE_NAME )
            .set( AnimatedTile.WORKFLOW_ID, workflowId )
            .add( AnimatedTile.ANIMATED_SPRITE_DATA, animationDataInactive )
            .add( AnimatedTile.ANIMATED_SPRITE_DATA, animationDataActive )
        .activate( AnimatedTile.class );
        int animatioControllerId = context
            .getSystemComponent( Asset.TYPE_KEY, animationAssetId, AnimatedTile.class )
            .getAnimationControllerId();
        
        int controllerId = context.getComponentBuilder( Controller.TYPE_KEY )
            .set( EntityController.NAME, UnitType.MAGIC_WALL.name() )
            .set( EntityController.UPDATE_RESOLUTION, updateRate )
        .build( MagicWallController.class );
        
        prefabId = context.getComponentBuilder( EntityPrefab.TYPE_KEY )
            .set( EntityPrefab.NAME, UnitType.MAGIC_WALL.name() )
            .set( EntityPrefab.INITIAL_CREATE_NUMBER, 50 )
            .add( EEntity.CONTROLLER_IDS, controllerId )
            .add( EEntity.CONTROLLER_IDS, animatioControllerId )
            .set( ETransform.VIEW_NAME, CaveSystem.CAVE_VIEW_NAME )
            .set( ETile.MULTI_POSITION, false )
            .set( EUnit.UNIT_TYPE, type() )
            .add( EUnit.ASPECTS, UnitAspect.DESTRUCTIBLE )
        .build();
        
        return this;
    }
    
    @Override
    public final void dispose( FFContext context ) {
        context.deleteSystemComponent( Controller.TYPE_KEY, UnitType.MAGIC_WALL.name() );
        context.deleteSystemComponent( EntityPrefab.TYPE_KEY, UnitType.MAGIC_WALL.name() );
        context.deleteSystemComponent( Asset.TYPE_KEY, UnitType.MAGIC_WALL.name() );
        context.deleteSystemComponent( Workflow.TYPE_KEY, UnitType.MAGIC_WALL.name() );

        context.notify( new AudioSystemEvent( soundId, Type.STOP_PLAYING ) );
        context.deleteSystemComponent( Workflow.TYPE_KEY, soundId );
        context.deleteSystemComponent( Asset.TYPE_KEY, UnitType.MAGIC_WALL.name() + "_sound" );
    }

    @Override
    public final int createOne( int xGridPos, int yGridPos, String type ) {
        int entityId = context.getSystem( EntityPrefabSystem.SYSTEM_KEY ).buildOne( prefabId );
        ETile tile = context.getEntityComponent( entityId, ETile.TYPE_KEY );
        tile.setGridXPos( xGridPos );
        tile.setGridYPos( yGridPos );
        context.activateEntity( entityId );
        return entityId;
    }
    

}
