package com.inari.dash.game.cave.unit.misc;

import java.util.Map;

import com.inari.commons.geom.Direction;
import com.inari.commons.geom.Rectangle;
import com.inari.commons.lang.aspect.AspectSetBuilder;
import com.inari.dash.game.cave.CaveSystem;
import com.inari.dash.game.cave.unit.EUnit;
import com.inari.dash.game.cave.unit.UnitHandle;
import com.inari.dash.game.cave.unit.UnitType;
import com.inari.firefly.asset.AnimatedSpriteData;
import com.inari.firefly.asset.AnimatedTileAsset;
import com.inari.firefly.entity.EEntity;
import com.inari.firefly.entity.ETransform;
import com.inari.firefly.graphics.tile.ETile;
import com.inari.firefly.state.StateChange;
import com.inari.firefly.state.Workflow;
import com.inari.firefly.system.FFContext;

public final class Exit extends UnitHandle {
    
    public enum ExitState {
        EXIT_CLOSED,
        EXIT_OPEN
    }
    
    public static final String EXIT_NAME = "exit";
    
    private int exitEntityId;
    private int animationAssetId;
    private int workflowId;

    @Override
    public final void loadCaveData( FFContext context ) {
        super.loadCaveData( context );
        
        workflowId = stateSystem.getWorkflowBuilder()
            .set( Workflow.NAME, EXIT_NAME )
            .set( Workflow.START_STATE_NAME, ExitState.EXIT_CLOSED.name() )
            .add( Workflow.STATES, ExitState.EXIT_CLOSED.name() )
            .add( Workflow.STATES, ExitState.EXIT_OPEN.name() )
            .add( Workflow.STATE_CHANGES, new StateChange( getStateChangeName(), ExitState.EXIT_CLOSED.name(), ExitState.EXIT_OPEN.name() ) )
        .activate();
        
        float updateRate = caveService.getUpdateRate();
        AnimatedSpriteData[] animationDataClosed = AnimatedSpriteData.create( ExitState.EXIT_CLOSED.name(), Integer.MAX_VALUE, new Rectangle( 32, 6 * 32, 32, 32 ), 1, Direction.EAST );
        AnimatedSpriteData[] animationDataOpen = AnimatedSpriteData.create( ExitState.EXIT_OPEN.name(), 400 - (int) updateRate * 4, new Rectangle( 32, 6 * 32, 32, 32 ), 2, Direction.EAST );
        animationAssetId = assetSystem.getAssetBuilder()
            .set( AnimatedTileAsset.NAME, EXIT_NAME )
            .set( AnimatedTileAsset.LOOPING, true )
            .set( AnimatedTileAsset.UPDATE_RESOLUTION, updateRate )
            .set( AnimatedTileAsset.TEXTURE_ASSET_ID, assetSystem.getAssetId( CaveSystem.GAME_UNIT_TEXTURE_NAME ) )
            .set( AnimatedTileAsset.WORKFLOW_ID, workflowId )
            .add( AnimatedTileAsset.ANIMATED_SPRITE_DATA, animationDataClosed )
            .add( AnimatedTileAsset.ANIMATED_SPRITE_DATA, animationDataOpen )
        .activate( AnimatedTileAsset.class );
    }

    @Override
    public void disposeCaveData( FFContext context ) {
        super.disposeCaveData( context );
        
        entitySystem.delete( exitEntityId );
        assetSystem.deleteAsset( animationAssetId );
        stateSystem.deleteWorkflow( workflowId );
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
        exitEntityId = entitySystem.getEntityBuilder()
            .add( EEntity.CONTROLLER_IDS, assetSystem.getAssetInstanceId( animationAssetId ) )
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
    
    public static final String getStateChangeName() {
        return ExitState.EXIT_CLOSED.name() + ExitState.EXIT_OPEN.name();
    }
}
