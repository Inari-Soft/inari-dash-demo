package com.inari.dash.game.cave.unit.wall;

import java.util.Map;

import com.inari.commons.geom.Position;
import com.inari.commons.geom.Rectangle;
import com.inari.dash.game.cave.CaveSystem;
import com.inari.dash.game.cave.unit.EUnit;
import com.inari.dash.game.cave.unit.Unit;
import com.inari.dash.game.cave.unit.UnitType;
import com.inari.firefly.asset.Asset;
import com.inari.firefly.entity.ETransform;
import com.inari.firefly.graphics.sprite.SpriteAsset;
import com.inari.firefly.graphics.tile.ETile;
import com.inari.firefly.system.FFContext;
import com.inari.firefly.system.utils.Disposable;

public final class SolidWall extends Unit {

    private int solidWallEntityId;
    
    protected SolidWall( int id ) {
        super( id );
    }

    @Override
    public final UnitType type() {
        return UnitType.SOLID_WALL;
    }

    @Override
    public final void initBDCFFTypesMap( Map<String, UnitType> bdcffMap ) {
        bdcffMap.put( "W", type() );
    }

    @Override
    public Disposable load( FFContext context ) {
        
        context.getComponentBuilder( Asset.TYPE_KEY )
            .set( SpriteAsset.NAME, UnitType.SOLID_WALL.name() )
            .set( SpriteAsset.TEXTURE_ASSET_NAME, CaveSystem.GAME_UNIT_TEXTURE_NAME )
            .set( SpriteAsset.TEXTURE_REGION, new Rectangle( 32, 6 * 32, 32, 32 ) )
        .activate( SpriteAsset.class );
        
        solidWallEntityId = context.getEntityBuilder()
            .set( ETransform.VIEW_NAME, CaveSystem.CAVE_VIEW_NAME )
            .set( ETile.MULTI_POSITION, true )
            .set( ETile.SPRITE_ASSET_NAME , UnitType.SOLID_WALL.name() )
            .set( EUnit.UNIT_TYPE, type() )
        .activate();
        
        return this;
    }

    @Override
    public final void dispose( FFContext context ) {
        
        context.deactivateEntity( solidWallEntityId );
        solidWallEntityId = -1;
        context.deleteSystemComponent( Asset.TYPE_KEY, UnitType.SOLID_WALL.name() );
    }
    
    @Override
    public final int createOne( int xGridPos, int yGridPos, String type ) {
        ETile tile = context.getEntityComponent( solidWallEntityId, ETile.TYPE_KEY );
        tile.getGridPositions().add( new Position( xGridPos, yGridPos ) );
        context.getSystem( CaveSystem.SYSTEM_KEY ).setEntityId( solidWallEntityId, xGridPos, yGridPos );
        return solidWallEntityId;
    }
    

}
