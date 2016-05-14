package com.inari.dash.game.cave.unit.wall;

import java.util.Map;

import com.inari.commons.geom.Position;
import com.inari.commons.geom.Rectangle;
import com.inari.dash.game.cave.CaveSystem;
import com.inari.dash.game.cave.unit.EUnit;
import com.inari.dash.game.cave.unit.Unit;
import com.inari.dash.game.cave.unit.UnitAspect;
import com.inari.dash.game.cave.unit.UnitType;
import com.inari.firefly.asset.Asset;
import com.inari.firefly.entity.EEntity;
import com.inari.firefly.entity.ETransform;
import com.inari.firefly.graphics.sprite.SpriteAsset;
import com.inari.firefly.graphics.tile.ETile;
import com.inari.firefly.system.FFContext;
import com.inari.firefly.system.utils.Disposable;

public class BrickWall extends Unit {

    private int brickWallEntityId;
    
    protected BrickWall( int id ) {
        super( id );
    }
    
    @Override
    public UnitType type() {
        return UnitType.BRICK_WALL;
    }

    @Override
    public void initBDCFFTypesMap( Map<String, UnitType> bdcffMap ) {
        bdcffMap.put( "w", type() );
    }
    @Override
    public final Disposable load( FFContext context ) {
        
        context.getComponentBuilder( Asset.TYPE_KEY )
            .set( SpriteAsset.NAME, UnitType.BRICK_WALL.name() )
            .set( SpriteAsset.TEXTURE_ASSET_NAME, CaveSystem.GAME_UNIT_TEXTURE_NAME )
            .set( SpriteAsset.TEXTURE_REGION, new Rectangle( 3 * 32, 6 * 32, 32, 32 ) )
        .activate( SpriteAsset.class );
        
        brickWallEntityId = context.getEntityBuilder()
            .set( ETransform.VIEW_NAME, CaveSystem.CAVE_VIEW_NAME )
            .set( ETile.MULTI_POSITION, true )
            .set( ETile.SPRITE_ASSET_NAME, UnitType.BRICK_WALL.name() )
            .set( EUnit.UNIT_TYPE, type() )
            .add( EEntity.ASPECTS, UnitAspect.DESTRUCTIBLE )
            .add( EEntity.ASPECTS, UnitAspect.ASLOPE )
        .activate();
        
        return this;
    }

    @Override
    public final void dispose( FFContext context ) {
        context.deleteEntity( brickWallEntityId );
        brickWallEntityId = -1;
        context.deleteSystemComponent( Asset.TYPE_KEY, UnitType.BRICK_WALL.name() );
    }

    @Override
    public final int createOne( int xGridPos, int yGridPos, String type ) {
        ETile tile = context.getEntityComponent( brickWallEntityId, ETile.TYPE_KEY );
        tile.getGridPositions().add( new Position( xGridPos, yGridPos ) );
        context.getSystem( CaveSystem.SYSTEM_KEY )
            .setEntityId( brickWallEntityId, xGridPos, yGridPos );
        return brickWallEntityId;
    }

}
