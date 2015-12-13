package com.inari.dash.game.cave.unit.wall;

import java.util.Map;

import com.inari.commons.geom.Position;
import com.inari.commons.lang.aspect.AspectSetBuilder;
import com.inari.dash.game.cave.CaveSystem;
import com.inari.dash.game.cave.unit.EUnit;
import com.inari.dash.game.cave.unit.UnitAspect;
import com.inari.dash.game.cave.unit.UnitHandle;
import com.inari.dash.game.cave.unit.UnitType;
import com.inari.firefly.control.Controller;
import com.inari.firefly.entity.EEntity;
import com.inari.firefly.entity.ETransform;
import com.inari.firefly.entity.EntityController;
import com.inari.firefly.renderer.sprite.ESprite;
import com.inari.firefly.renderer.tile.ETile;
import com.inari.firefly.system.FFContext;

public final class ExpandingWall extends UnitHandle {
    
    private int expandingWallEntityId;
    private int controllerId;

    @Override
    public final void loadCaveData( FFContext context ) {
        super.loadCaveData( context );
        
        Controller controller = controllerSystem.getController( 
            controllerSystem.getControllerBuilder()
                .set( EntityController.NAME, "ExpandingWallController" )
            .build( ExpandingWallController.class )
        );
        controllerId = controller.getId();
        float updateRate = caveService.getUpdateRate();
        controller.setUpdateResolution( updateRate );
        
        expandingWallEntityId = entitySystem.getEntityBuilder()
            .set( ETransform.VIEW_ID, viewSystem.getViewId( CaveSystem.CAVE_VIEW_NAME ) )
            .set( EEntity.CONTROLLER_IDS, new int[] { controllerId } )
            .set( ETile.MULTI_POSITION, true )
            .set( ESprite.SPRITE_ID, assetSystem.getAssetId( BrickWall.BRICK_WALL_SPRITE_ASSET_KEY ) )
            .set( EUnit.UNIT_TYPE, type() )
            .set( EUnit.ASPECTS, AspectSetBuilder.create( 
                UnitAspect.ASLOPE, 
                UnitAspect.DESTRUCTIBLE
            ) )
        .activate();
    }

    @Override
    public final void disposeCaveData( FFContext context ) {
        super.disposeCaveData( context );
        entitySystem.delete( expandingWallEntityId );
        expandingWallEntityId = -1;
        controllerSystem.deleteController( controllerId );
        controllerId = -1;
    }

    @Override
    public final UnitType type() {
        return UnitType.EXPANDING_WALL;
    }

    @Override
    public final int getSoundId() {
        return UnitType.ROCK.handler.getSoundId();
    }

    @Override
    public final void initBDCFFTypesMap( Map<String, UnitType> bdcffMap ) {
        bdcffMap.put( "x", type() );
    }

    @Override
    public final int createOne( int xGridPos, int yGridPos ) {
        ETile tile = entitySystem.getComponent( expandingWallEntityId, ETile.TYPE_KEY );
        tile.getGridPositions().add( new Position( xGridPos, yGridPos ) );
        caveService.setEntityId( expandingWallEntityId, xGridPos, yGridPos );
        return expandingWallEntityId;
    }
    
    @Override
    public final void dispose( FFContext context ) {
        
    }

}
