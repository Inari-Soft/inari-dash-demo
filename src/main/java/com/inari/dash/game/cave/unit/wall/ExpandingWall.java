package com.inari.dash.game.cave.unit.wall;

import java.util.Map;

import com.inari.commons.geom.Position;
import com.inari.dash.game.cave.CaveSystem;
import com.inari.dash.game.cave.unit.EUnit;
import com.inari.dash.game.cave.unit.Unit;
import com.inari.dash.game.cave.unit.UnitAspect;
import com.inari.dash.game.cave.unit.UnitType;
import com.inari.firefly.audio.Sound;
import com.inari.firefly.control.Controller;
import com.inari.firefly.entity.EEntity;
import com.inari.firefly.entity.ETransform;
import com.inari.firefly.entity.EntityController;
import com.inari.firefly.graphics.tile.ETile;
import com.inari.firefly.system.FFContext;
import com.inari.firefly.system.utils.Disposable;

public final class ExpandingWall extends Unit {

    private int expandingWallEntityId;
    
    protected ExpandingWall( int id ) {
        super( id );
    }
    
    @Override
    public final UnitType type() {
        return UnitType.EXPANDING_WALL;
    }

    @Override
    public final int getSoundId() {
        return context.getSystemComponentId( Sound.TYPE_KEY, UnitType.ROCK.name() + "_sound" );
    }

    @Override
    public final void initBDCFFTypesMap( Map<String, UnitType> bdcffMap ) {
        bdcffMap.put( "x", type() );
    }

    @Override
    public final Disposable load( FFContext context ) {
        context.getComponentBuilder( Controller.TYPE_KEY )
            .set( EntityController.NAME, UnitType.EXPANDING_WALL.name() )
            .set( EntityController.UPDATE_RESOLUTION, getUpdateRate() )
        .build( ExpandingWallController.class );

        
        expandingWallEntityId = context.getEntityBuilder()
            .set( ETransform.VIEW_NAME, CaveSystem.CAVE_VIEW_NAME )
            .add( EEntity.CONTROLLER_NAMES, UnitType.EXPANDING_WALL.name() )
            .set( ETile.MULTI_POSITION, true )
            .set( ETile.SPRITE_ASSET_NAME, UnitType.BRICK_WALL.name() )
            .set( EUnit.UNIT_TYPE, type() )
            .add( EEntity.ASPECTS, UnitAspect.ASLOPE )
            .add( EEntity.ASPECTS, UnitAspect.DESTRUCTIBLE )
        .activate();
        
        return this;
    }

    @Override
    public final void dispose( FFContext context ) {
        context.deleteSystemComponent( Controller.TYPE_KEY, UnitType.EXPANDING_WALL.name() );
        context.deleteEntity( expandingWallEntityId );
        expandingWallEntityId = -1;
    }

    @Override
    public final int createOne( int xGridPos, int yGridPos, String type ) {
        ETile tile = context.getEntityComponent( expandingWallEntityId, ETile.TYPE_KEY );
        tile.getGridPositions().add( new Position( xGridPos, yGridPos ) );
        context.getSystem( CaveSystem.SYSTEM_KEY ).setEntityId( expandingWallEntityId, xGridPos, yGridPos );
        return expandingWallEntityId;
    }
    
}
