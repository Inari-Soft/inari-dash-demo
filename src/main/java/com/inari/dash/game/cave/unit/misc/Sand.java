package com.inari.dash.game.cave.unit.misc;

import java.util.Map;

import com.inari.commons.geom.Position;
import com.inari.commons.geom.Rectangle;
import com.inari.dash.game.cave.CaveSystem;
import com.inari.dash.game.cave.unit.EUnit;
import com.inari.dash.game.cave.unit.Unit;
import com.inari.dash.game.cave.unit.UnitAspect;
import com.inari.dash.game.cave.unit.UnitType;
import com.inari.firefly.asset.Asset;
import com.inari.firefly.entity.ETransform;
import com.inari.firefly.graphics.sprite.SpriteAsset;
import com.inari.firefly.graphics.tile.ETile;
import com.inari.firefly.graphics.tile.TileGrid;
import com.inari.firefly.graphics.tile.TileSystemEvent;
import com.inari.firefly.graphics.tile.TileSystemEvent.Type;
import com.inari.firefly.system.FFContext;
import com.inari.firefly.system.utils.Disposable;

public final class Sand extends Unit {

    private int sandEntityId;
    private int tileGridId;
    
    protected Sand( int id ) {
        super( id );
    }
    
    @Override
    public int getEntityId() {
        return sandEntityId;
    }
    
    @Override
    public final UnitType type() {
        return UnitType.SAND;
    }

    @Override
    public void initBDCFFTypesMap( Map<String, UnitType> bdcffMap ) {
        bdcffMap.put( ".", type() );
    }

    @Override
    public Disposable load( FFContext context ) {
        
        context.getComponentBuilder( Asset.TYPE_KEY )
            .set( SpriteAsset.NAME, UnitType.SAND.name() )
            .set( SpriteAsset.TEXTURE_ASSET_NAME, CaveSystem.GAME_UNIT_TEXTURE_NAME )
            .set( SpriteAsset.TEXTURE_REGION, new Rectangle( 32, 7 * 32, 32, 32 ) )
        .activate( SpriteAsset.class );

        sandEntityId = context.getEntityBuilder()
            .set( ETransform.VIEW_NAME, CaveSystem.CAVE_VIEW_NAME )
            .set( ETile.MULTI_POSITION, true )
            .set( ETile.SPRITE_ASSET_NAME, UnitType.SAND.name() )
            .set( EUnit.UNIT_TYPE, type() )
            .add( EUnit.ASPECTS, UnitAspect.DESTRUCTIBLE )
            .add( EUnit.ASPECTS, UnitAspect.CONSUMABLE )
            .add( EUnit.ASPECTS, UnitAspect.WALKABLE )
        .activate();
        
        tileGridId = context.getSystemComponentId( TileGrid.TYPE_KEY, CaveSystem.CAVE_TILE_GRID_NAME );
        
        return this;
    }

    @Override
    public void dispose( FFContext context ) {
        context.deleteEntity( sandEntityId );
        sandEntityId = -1;
        context.deleteSystemComponent( Asset.TYPE_KEY, UnitType.SAND.name() );
        tileGridId = -1;
    }

    @Override
    public final int createOne( int xGridPos, int yGridPos, String type ) {
        context.notify( 
            new TileSystemEvent( 
                Type.MULTIPOSITION_ADD, 
                tileGridId,
                sandEntityId,
                new Position( xGridPos, yGridPos )
            )
        );
        
        return sandEntityId;
    }
}
