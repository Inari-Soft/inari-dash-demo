package com.inari.dash.game.cave.unit.misc;

import java.util.Map;

import com.inari.commons.geom.Direction;
import com.inari.commons.geom.Rectangle;
import com.inari.commons.lang.aspect.AspectSetBuilder;
import com.inari.dash.game.cave.CaveSystem;
import com.inari.dash.game.cave.unit.EUnit;
import com.inari.dash.game.cave.unit.UnitHandle;
import com.inari.dash.game.cave.unit.UnitType;
import com.inari.firefly.asset.AnimatedSpriteAsset;
import com.inari.firefly.asset.AnimatedSpriteData;
import com.inari.firefly.entity.EEntity;
import com.inari.firefly.entity.ETransform;
import com.inari.firefly.graphics.tile.ETile;
import com.inari.firefly.state.State;
import com.inari.firefly.state.StateChange;
import com.inari.firefly.state.StateSystem;
import com.inari.firefly.state.Workflow;
import com.inari.firefly.system.FFContext;

public final class Exit extends UnitHandle {
    
    public enum ExitState {
        EXIT_CLOSED,
        EXIT_OPEN
    }
    
    public static final String EXIT_NAME = "exit";
    
    private int exitEntityId;
    private int animationAssetId = -1;

    @Override
    public final void loadCaveData( FFContext context ) {
        super.loadCaveData( context );
        
        StateSystem stateSystem = context.getSystem( StateSystem.SYSTEM_KEY );
        int workflowId = stateSystem.getWorkflowBuilder()
            .set( Workflow.NAME, EXIT_NAME )
            .set( Workflow.START_STATE_NAME, ExitState.EXIT_CLOSED.name() )
        .build();
        int closedStateId = stateSystem.getStateBuilder()
            .set( State.NAME, ExitState.EXIT_CLOSED.name() )
            .set( State.WORKFLOW_ID, workflowId )
        .build();
        int openStateId = stateSystem.getStateBuilder()
            .set( State.NAME, ExitState.EXIT_OPEN.name() )
            .set( State.WORKFLOW_ID, workflowId )
        .build();
        stateSystem.getStateChangeBuilder()
            .set( StateChange.NAME, getStateChangeName() )
            .set( StateChange.FORM_STATE_ID, closedStateId )
            .set( StateChange.TO_STATE_ID, openStateId )
            .set( StateChange.WORKFLOW_ID, workflowId )
        .build();
        stateSystem.activateWorkflow( workflowId );
        
        float updateRate = caveService.getUpdateRate();
        AnimatedSpriteData[] animationDataClosed = AnimatedSpriteData.create( closedStateId, Integer.MAX_VALUE, new Rectangle( 32, 6 * 32, 32, 32 ), 1, Direction.EAST );
        AnimatedSpriteData[] animationDataOpen = AnimatedSpriteData.create( openStateId, 400 - (int) updateRate * 4, new Rectangle( 32, 6 * 32, 32, 32 ), 2, Direction.EAST );
        animationAssetId = assetSystem.getAssetBuilder()
            .set( AnimatedSpriteAsset.NAME, EXIT_NAME )
            .set( AnimatedSpriteAsset.LOOPING, true )
            .set( AnimatedSpriteAsset.UPDATE_RESOLUTION, updateRate )
            .set( AnimatedSpriteAsset.TEXTURE_ASSET_ID, assetSystem.getAssetId( CaveSystem.GAME_UNIT_TEXTURE_NAME ) )
            .set( AnimatedSpriteAsset.WORKFLOW_ID, workflowId )
            .add( AnimatedSpriteAsset.ANIMATED_SPRITE_DATA, animationDataClosed )
            .add( AnimatedSpriteAsset.ANIMATED_SPRITE_DATA, animationDataOpen )
        .activate( AnimatedSpriteAsset.class );
    }

    public static final String getStateChangeName() {
        return ExitState.EXIT_CLOSED.name() + ExitState.EXIT_OPEN.name();
    }

    @Override
    public void disposeCaveData( FFContext context ) {
        super.disposeCaveData( context );
        
        entitySystem.delete( exitEntityId );
        assetSystem.deleteAsset( animationAssetId );
    }

    @Override
    public final UnitType type() {
        return UnitType.EXIT;
    }

    @Override
    public final void initBDCFFTypesMap( Map<String, UnitType> bdcffMap ) {
        bdcffMap.put( "X", type() );
        bdcffMap.put( "H", type() );
    }

    @Override
    public final int createOne( int xGridPos, int yGridPos ) {
        int animatioControllerId = assetSystem.getAssetInstaceId( animationAssetId );
        exitEntityId = entitySystem.getEntityBuilder()
            .add( EEntity.CONTROLLER_IDS, animatioControllerId )
            .set( ETransform.VIEW_ID, viewSystem.getViewId( CaveSystem.CAVE_VIEW_NAME ) )
            .set( ETile.GRID_X_POSITION, xGridPos )
            .set( ETile.GRID_Y_POSITION, yGridPos )
            .set( EUnit.UNIT_TYPE, type() )
            .set( EUnit.ASPECTS, AspectSetBuilder.create() )
        .activate();
        return exitEntityId;
    }
    
    @Override
    public final int getEntityId() {
        return exitEntityId;
    }

    @Override
    public final void dispose( FFContext context ) {
        
    }
}
