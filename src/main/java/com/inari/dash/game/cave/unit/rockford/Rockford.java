package com.inari.dash.game.cave.unit.rockford;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.inari.commons.geom.Direction;
import com.inari.commons.geom.Rectangle;
import com.inari.dash.game.cave.CaveSystem;
import com.inari.dash.game.cave.CaveSystem.SoundChannel;
import com.inari.dash.game.cave.unit.EUnit;
import com.inari.dash.game.cave.unit.UnitHandle;
import com.inari.dash.game.cave.unit.UnitType;
import com.inari.firefly.FFInitException;
import com.inari.firefly.asset.AnimatedSpriteData;
import com.inari.firefly.asset.AnimatedTileAsset;
import com.inari.firefly.audio.Sound;
import com.inari.firefly.audio.SoundAsset;
import com.inari.firefly.control.Controller;
import com.inari.firefly.entity.EEntity;
import com.inari.firefly.entity.ETransform;
import com.inari.firefly.entity.EntityController;
import com.inari.firefly.graphics.tile.ETile;
import com.inari.firefly.state.StateChange;
import com.inari.firefly.state.Workflow;
import com.inari.firefly.system.FFContext;

public final class Rockford extends UnitHandle {

    public enum StateEnum {
        ENTERING( 400, new Rectangle( 32, 6 * 32, 32, 32 ), 2 ),
        APPEARING( 300, new Rectangle( 32, 0, 32, 32 ), 3 ),                // Rockford appears in a short explosion where the blinking door was before
        IDLE( Integer.MAX_VALUE, new Rectangle( 0, 0, 32, 32 ), 1 ),        // Rockford idle state, no move, no animation
        BLINKING( 100, new Rectangle( 0, 32, 32, 32 ), 8 ),                 // Rockford eyes are blinking
        FRETFUL( 100, new Rectangle( 0, 2 * 32, 32, 32 ), 8 ),              // Rockford is fretful waiting for user interaction
        LEFT( 60, new Rectangle( 0, 4 * 32, 32, 32 ), 8 ),
        RIGHT( 60, new Rectangle( 0, 5 * 32, 32, 32 ), 8 );

        final int updateFactor;
        final Rectangle startRegion;
        final int frames;
        private StateEnum( int updateFactor, Rectangle startRegion, int frames ) {
            this.updateFactor = updateFactor;
            this.startRegion = startRegion;
            this.frames = frames;
        }
        
        public static String[] getStates() {
            String[] states = new String[ StateEnum.values().length ];
            int index = 0;
            for ( StateEnum state : StateEnum.values() ) {
                states[ index ] = state.name();
                index++;
            }
            return states;
        }
        
        public static AnimatedSpriteData[] getAnimatedSpriteData( int updateRate ) {
            List<AnimatedSpriteData> result = new ArrayList<AnimatedSpriteData>();
            for ( StateEnum state : StateEnum.values() ) {
                result.addAll( Arrays.asList( AnimatedSpriteData.create( state.name(), state.updateFactor - updateRate * 4, state.startRegion, state.frames, Direction.EAST ) ) );
            }
            return result.toArray( new AnimatedSpriteData[ result.size() ] );
        }

        public boolean is( String state ) {
            return name().equals( state );
        }
    }
    
    public enum StateChangeEnum {
        ENTERING_APPEARING( StateEnum.ENTERING, StateEnum.APPEARING ),
        APPEARING_IDLE( StateEnum.APPEARING, StateEnum.IDLE ),
        IDLE_BLINKING( StateEnum.IDLE, StateEnum.BLINKING ),
        IDLE_FRETFUL( StateEnum.IDLE, StateEnum.FRETFUL ),
        IDLE_LEFT( StateEnum.IDLE, StateEnum.LEFT ),
        IDLE_RIGHT( StateEnum.IDLE, StateEnum.RIGHT ),
        BLINKING_IDLE( StateEnum.BLINKING, StateEnum.IDLE ),
        FRETFUL_IDLE( StateEnum.FRETFUL, StateEnum.IDLE ),
        BLINKING_LEFT( StateEnum.BLINKING, StateEnum.LEFT ),
        FRETFUL_LEFT( StateEnum.FRETFUL, StateEnum.LEFT ),
        BLINKING_RIGHT( StateEnum.BLINKING, StateEnum.RIGHT ),
        FRETFUL_RIGHT( StateEnum.FRETFUL, StateEnum.RIGHT ),
        LEFT_IDLE( StateEnum.LEFT, StateEnum.IDLE ),
        RIGHT_IDLE( StateEnum.RIGHT, StateEnum.IDLE ),
        LEFT_RIGHT( StateEnum.LEFT, StateEnum.RIGHT ),
        RIGHT_LEFT( StateEnum.RIGHT, StateEnum.LEFT )
        ;
        
        final StateChange stateChange;
        private StateChangeEnum( StateEnum from, StateEnum to ) {
            stateChange = new StateChange( name(), from.name(), to.name() );
        }
        
        public final static StateChange[] getStateChanges() {
            StateChange[] stateChanges = new StateChange[ StateChangeEnum.values().length ];
            int index = 0;
            for ( StateChangeEnum stateChange : StateChangeEnum.values() ) {
                stateChanges[ index ] = stateChange.stateChange;
                index++;
            }
            return stateChanges;
        }
    }
    
    public static final String NAME = "rockford";
    public static final String ROCKFORD_SPRITE_ASSET_NAME = NAME + "_sprite";
    public static final String ROCKFORD_SPACE_SOUND_ASSEET_NAME = NAME + "_space";
    public static final String ROCKFORD_SAND_SOUND_ASSEET_NAME = NAME + "_sand";
    public static final String ROCKFORD_COLLECT_SOUND_ASSEET_NAME = NAME + "_collect";
    
    private int controllerId;
    private int rfEntityId;
    
    private int animationAssetId;
    int spaceSoundId;
    int sandSoundId;
    int inSoundId;
    int collectSoundId;
    int workflowId;
    
    @Override
    public final void init( FFContext context ) throws FFInitException {
        super.init( context );

        createSounds();
        
        initialized = true;
    }

    private void createSounds() {
        int soundAssetIdSpace = assetSystem.getAssetBuilder()
            .set( SoundAsset.NAME, ROCKFORD_SPACE_SOUND_ASSEET_NAME )
            .set( SoundAsset.RESOURCE_NAME, "original/sound/walkSpace.wav" )
            .set( SoundAsset.STREAMING, false )
        .activate( SoundAsset.class );
        int soundAssetIdSand = assetSystem.getAssetBuilder()
            .set( SoundAsset.NAME, ROCKFORD_SAND_SOUND_ASSEET_NAME )
            .set( SoundAsset.RESOURCE_NAME, "original/sound/walkSand.wav" )
            .set( SoundAsset.STREAMING, false )
        .activate( SoundAsset.class );
        int soundAssetIdCollect = assetSystem.getAssetBuilder()
            .set( SoundAsset.NAME, ROCKFORD_COLLECT_SOUND_ASSEET_NAME )
            .set( SoundAsset.RESOURCE_NAME, "original/sound/collectDiamond.wav" )
            .set( SoundAsset.STREAMING, false )
        .activate( SoundAsset.class );
        
        spaceSoundId = soundSystem.getSoundBuilder()
            .set( Sound.NAME, ROCKFORD_SPACE_SOUND_ASSEET_NAME )
            .set( Sound.SOUND_ASSET_ID, soundAssetIdSpace )
            .set( Sound.LOOPING, false )
        .build();
        sandSoundId = soundSystem.getSoundBuilder()
            .set( Sound.NAME, ROCKFORD_SAND_SOUND_ASSEET_NAME )
            .set( Sound.SOUND_ASSET_ID, soundAssetIdSand )
            .set( Sound.LOOPING, false )
        .build();
        collectSoundId = soundSystem.getSoundBuilder()
            .set( Sound.NAME, ROCKFORD_COLLECT_SOUND_ASSEET_NAME )
            .set( Sound.SOUND_ASSET_ID, soundAssetIdCollect )
            .set( Sound.LOOPING, false )
            .set( Sound.CHANNEL, SoundChannel.COLLECT.ordinal() )
        .build();
        inSoundId = soundSystem.getSoundId( CaveSystem.CaveSoundKey.CRACK.name() );
    }

    @Override
    public final void loadCaveData( FFContext context ) {
        super.loadCaveData( context );
        
        float updateRate = caveService.getUpdateRate();
        
        workflowId = stateSystem.getWorkflowBuilder()
            .set( Workflow.NAME, NAME )
            .set( Workflow.START_STATE_NAME, StateEnum.ENTERING.name() )
            .add( Workflow.STATES, StateEnum.getStates() )
            .add( Workflow.STATE_CHANGES, StateChangeEnum.getStateChanges() )
        .activate();
        
        controllerId = controllerSystem.getControllerBuilder()
            .set( EntityController.NAME, NAME )
            .set( Controller.UPDATE_RESOLUTION, updateRate )
        .build( RFController.class );

        animationAssetId = assetSystem.getAssetBuilder()
            .set( AnimatedTileAsset.NAME, NAME )
            .set( AnimatedTileAsset.LOOPING, true )
            .set( AnimatedTileAsset.UPDATE_RESOLUTION, updateRate )
            .set( AnimatedTileAsset.TEXTURE_ASSET_ID, assetSystem.getAssetId( CaveSystem.GAME_UNIT_TEXTURE_NAME ) )
            .set( AnimatedTileAsset.WORKFLOW_ID, workflowId )
            .add( AnimatedTileAsset.ANIMATED_SPRITE_DATA, StateEnum.getAnimatedSpriteData( (int) updateRate ) )
        .activate( AnimatedTileAsset.class );

        
    }
    
    

    @Override
    public void disposeCaveData( FFContext context ) {
        super.disposeCaveData( context );
        controllerSystem.deleteController( controllerId );
        assetSystem.deleteAsset( animationAssetId );
        stateSystem.deleteWorkflow( workflowId );
        rfEntityId = -1;
    }

    @Override
    public final UnitType type() {
        return UnitType.ROCKFORD;
    }

    @Override
    public final void initBDCFFTypesMap( Map<String, UnitType> bdcffMap ) {
        bdcffMap.put( "P", type() );
        bdcffMap.put( "P1", type() );
    }

    @Override
    public final int createOne( int xGridPos, int yGridPos ) {
        rfEntityId = entitySystem.getEntityBuilder()
            .add( EEntity.CONTROLLER_IDS, controllerId )
            .add( EEntity.CONTROLLER_IDS, assetSystem.getAssetInstanceId( animationAssetId ) )
            .set( ETransform.VIEW_ID, viewSystem.getViewId( CaveSystem.CAVE_VIEW_NAME ) )
            .set( ETile.GRID_X_POSITION, xGridPos )
            .set( ETile.GRID_Y_POSITION, yGridPos )
            .set( EUnit.UNIT_TYPE, type() )
        .activate();
        return rfEntityId;
    }

    @Override
    public final int getEntityId() {
        return rfEntityId;
    }

    @Override
    public final void dispose( FFContext context ) {
        
        RFController rfController = (RFController) controllerSystem.getController( controllerId );
        if ( rfController != null ) {
            soundSystem.deleteSound( spaceSoundId );
            soundSystem.deleteSound( sandSoundId );
            soundSystem.deleteSound( collectSoundId );
        }
        controllerSystem.deleteController( controllerId );
        entitySystem.delete( rfEntityId );
        assetSystem.deleteAsset( animationAssetId );
        stateSystem.deleteWorkflow( NAME );
    }

}
