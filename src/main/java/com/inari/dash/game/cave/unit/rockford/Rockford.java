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
import com.inari.dash.game.cave.unit.Unit;
import com.inari.dash.game.cave.unit.UnitType;
import com.inari.firefly.asset.Asset;
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
import com.inari.firefly.graphics.tile.ETile;
import com.inari.firefly.system.Disposable;
import com.inari.firefly.system.FFContext;

public final class Rockford extends Unit {

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
    
    public static final String NAME = UnitType.ROCKFORD.name();
    public static final String ROCKFORD_SPRITE_ASSET_NAME = NAME + "_sprite";
    public static final String ROCKFORD_SPACE_SOUND_ASSET_NAME = NAME + "_space";
    public static final String ROCKFORD_SAND_SOUND_ASSEET_NAME = NAME + "_sand";
    public static final String ROCKFORD_COLLECT_SOUND_ASSEET_NAME = NAME + "_collect";
    
    private int controllerId;
    private int rfEntityId;
    private int animatioControllerId;
    int inSoundId;
    int spaceSoundId;
    int sandSoundId;
    int collectSoundId;
    
    protected Rockford( int id ) {
        super( id );
    }

    @Override
    public final int getEntityId() {
        return rfEntityId;
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
    public final Disposable load( FFContext context ) {
        createSounds();
        
        float updateRate = getUpdateRate();
        int workflowId = context.getComponentBuilder( Workflow.TYPE_KEY )
            .set( Workflow.NAME, NAME )
            .set( Workflow.START_STATE_NAME, StateEnum.ENTERING.name() )
            .add( Workflow.STATES, StateEnum.getStates() )
            .add( Workflow.STATE_CHANGES, StateChangeEnum.getStateChanges() )
        .activate();
        
        controllerId = context.getComponentBuilder( Controller.TYPE_KEY )
            .set( EntityController.NAME, NAME )
            .set( Controller.UPDATE_RESOLUTION, updateRate )
        .build( RFController.class );

        int animationAssetId = context.getComponentBuilder( Asset.TYPE_KEY )
            .set( AnimatedTile.NAME, NAME )
            .set( AnimatedTile.LOOPING, true )
            .set( AnimatedTile.UPDATE_RESOLUTION, updateRate )
            .set( AnimatedTile.TEXTURE_ASSET_NAME, CaveSystem.GAME_UNIT_TEXTURE_NAME )
            .set( AnimatedTile.WORKFLOW_ID, workflowId )
            .add( AnimatedTile.ANIMATED_SPRITE_DATA, StateEnum.getAnimatedSpriteData( (int) updateRate ) )
        .activate( AnimatedTile.class );
        animatioControllerId = context
            .getSystemComponent( Asset.TYPE_KEY, animationAssetId, AnimatedTile.class )
            .getAnimationControllerId();
        
        return this;
    }

    @Override
    public final void dispose( FFContext context ) {

        context.deleteSystemComponent( Controller.TYPE_KEY, NAME );
        context.deleteSystemComponent( Asset.TYPE_KEY, NAME );
        context.deleteSystemComponent( Workflow.TYPE_KEY, NAME );
        context.deleteEntity( rfEntityId );
        rfEntityId = -1;
        
        context.deleteSystemComponent( Sound.TYPE_KEY, ROCKFORD_SPACE_SOUND_ASSET_NAME );
        context.deleteSystemComponent( Sound.TYPE_KEY, ROCKFORD_SAND_SOUND_ASSEET_NAME );
        context.deleteSystemComponent( Sound.TYPE_KEY, ROCKFORD_COLLECT_SOUND_ASSEET_NAME );
        context.deleteSystemComponent( Asset.TYPE_KEY, ROCKFORD_SPACE_SOUND_ASSET_NAME );
        context.deleteSystemComponent( Asset.TYPE_KEY, ROCKFORD_SAND_SOUND_ASSEET_NAME );
        context.deleteSystemComponent( Asset.TYPE_KEY, ROCKFORD_COLLECT_SOUND_ASSEET_NAME );
    }
    
    @Override
    public final int createOne( int xGridPos, int yGridPos, String type ) {
        
        rfEntityId = context.getEntityBuilder()
            .add( EEntity.CONTROLLER_IDS, controllerId )
            .add( EEntity.CONTROLLER_IDS, animatioControllerId )
            .set( ETransform.VIEW_NAME, CaveSystem.CAVE_VIEW_NAME )
            .set( ETile.GRID_X_POSITION, xGridPos )
            .set( ETile.GRID_Y_POSITION, yGridPos )
            .set( EUnit.UNIT_TYPE, type() )
        .activate();
        return rfEntityId;
    }

    private void createSounds() {
        context.getComponentBuilder( Asset.TYPE_KEY )
            .set( SoundAsset.NAME, ROCKFORD_SPACE_SOUND_ASSET_NAME )
            .set( SoundAsset.RESOURCE_NAME, "original/sound/walkSpace.wav" )
            .set( SoundAsset.STREAMING, false )
        .activate( SoundAsset.class );
        context.getComponentBuilder( Asset.TYPE_KEY )
            .set( SoundAsset.NAME, ROCKFORD_SAND_SOUND_ASSEET_NAME )
            .set( SoundAsset.RESOURCE_NAME, "original/sound/walkSand.wav" )
            .set( SoundAsset.STREAMING, false )
        .activate( SoundAsset.class );
        context.getComponentBuilder( Asset.TYPE_KEY )
            .set( SoundAsset.NAME, ROCKFORD_COLLECT_SOUND_ASSEET_NAME )
            .set( SoundAsset.RESOURCE_NAME, "original/sound/collectDiamond.wav" )
            .set( SoundAsset.STREAMING, false )
        .activate( SoundAsset.class );
        
        spaceSoundId = context.getComponentBuilder( Sound.TYPE_KEY )
            .set( Sound.NAME, ROCKFORD_SPACE_SOUND_ASSET_NAME )
            .set( Sound.SOUND_ASSET_NAME, ROCKFORD_SPACE_SOUND_ASSET_NAME )
            .set( Sound.LOOPING, false )
        .build();
        sandSoundId = context.getComponentBuilder( Sound.TYPE_KEY )
            .set( Sound.NAME, ROCKFORD_SAND_SOUND_ASSEET_NAME )
            .set( Sound.SOUND_ASSET_NAME, ROCKFORD_SAND_SOUND_ASSEET_NAME )
            .set( Sound.LOOPING, false )
        .build();
        collectSoundId = context.getComponentBuilder( Sound.TYPE_KEY )
            .set( Sound.NAME, ROCKFORD_COLLECT_SOUND_ASSEET_NAME )
            .set( Sound.SOUND_ASSET_NAME, ROCKFORD_COLLECT_SOUND_ASSEET_NAME )
            .set( Sound.LOOPING, false )
            .set( Sound.CHANNEL, SoundChannel.COLLECT.ordinal() )
        .build();
        
        inSoundId = context.getSystemComponentId( Sound.TYPE_KEY, CaveSystem.CaveSoundKey.CRACK.name() );
    }

}
