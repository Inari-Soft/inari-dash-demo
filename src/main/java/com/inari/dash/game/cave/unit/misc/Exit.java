package com.inari.dash.game.cave.unit.misc;

import java.util.Map;

import com.inari.commons.geom.Direction;
import com.inari.commons.geom.Rectangle;
import com.inari.commons.lang.aspect.AspectSetBuilder;
import com.inari.dash.game.cave.CaveSystem;
import com.inari.dash.game.cave.unit.EUnit;
import com.inari.dash.game.cave.unit.Unit;
import com.inari.dash.game.cave.unit.UnitType;
import com.inari.firefly.asset.Asset;
import com.inari.firefly.composite.sprite.AnimatedSpriteData;
import com.inari.firefly.composite.sprite.AnimatedTile;
import com.inari.firefly.control.state.StateChange;
import com.inari.firefly.control.state.Workflow;
import com.inari.firefly.entity.EEntity;
import com.inari.firefly.entity.ETransform;
import com.inari.firefly.graphics.tile.ETile;
import com.inari.firefly.system.Disposable;
import com.inari.firefly.system.FFContext;

public final class Exit extends Unit {

    public enum ExitState {
        EXIT_CLOSED,
        EXIT_OPEN
    }

    private int exitEntityId;
    private int animationAssetId;
    private int animatioControllerId;
    
    protected Exit( int id ) {
        super( id );
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
    public final Disposable load( FFContext context ) {
        
        int workflowId = context.getComponentBuilder( Workflow.TYPE_KEY )
            .set( Workflow.NAME, UnitType.EXIT.name() )
            .set( Workflow.START_STATE_NAME, ExitState.EXIT_CLOSED.name() )
            .add( Workflow.STATES, ExitState.EXIT_CLOSED.name() )
            .add( Workflow.STATES, ExitState.EXIT_OPEN.name() )
            .add( Workflow.STATE_CHANGES, new StateChange( getStateChangeName(), ExitState.EXIT_CLOSED.name(), ExitState.EXIT_OPEN.name() ) )
        .activate();
        
        float updateRate = getUpdateRate();
        AnimatedSpriteData[] animationDataClosed = AnimatedSpriteData.create( 
            ExitState.EXIT_CLOSED.name(), 
            Integer.MAX_VALUE, 
            new Rectangle( 32, 6 * 32, 32, 32 ), 
            1, Direction.EAST 
        );
        AnimatedSpriteData[] animationDataOpen = AnimatedSpriteData.create( 
            ExitState.EXIT_OPEN.name(), 
            400 - (int) updateRate * 4, 
            new Rectangle( 32, 6 * 32, 32, 32 ), 
            2, Direction.EAST 
        );
        animationAssetId = context.getComponentBuilder( Asset.TYPE_KEY )
            .set( AnimatedTile.NAME, UnitType.EXIT.name() )
            .set( AnimatedTile.LOOPING, true )
            .set( AnimatedTile.UPDATE_RESOLUTION, updateRate )
            .set( AnimatedTile.TEXTURE_ASSET_NAME, CaveSystem.GAME_UNIT_TEXTURE_NAME )
            .set( AnimatedTile.WORKFLOW_ID, workflowId )
            .add( AnimatedTile.ANIMATED_SPRITE_DATA, animationDataClosed )
            .add( AnimatedTile.ANIMATED_SPRITE_DATA, animationDataOpen )
        .activate( AnimatedTile.class );
        animatioControllerId = context
            .getSystemComponent( Asset.TYPE_KEY, animationAssetId, AnimatedTile.class )
            .getAnimationControllerId();
        
        return this;
    }

    @Override
    public final void dispose( FFContext context ) {
        
        context.deleteEntity( exitEntityId );
        context.deleteSystemComponent( Asset.TYPE_KEY, animationAssetId );
        context.deleteSystemComponent( Workflow.TYPE_KEY, UnitType.EXIT.name() );
    }

    @Override
    public final int createOne( int xGridPos, int yGridPos, String type ) {
        exitEntityId = context.getEntityBuilder()
            .add( EEntity.CONTROLLER_IDS, animatioControllerId )
            .set( ETransform.VIEW_NAME, CaveSystem.CAVE_VIEW_NAME )
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

    
    public static final String getStateChangeName() {
        return ExitState.EXIT_CLOSED.name() + ExitState.EXIT_OPEN.name();
    }
}
