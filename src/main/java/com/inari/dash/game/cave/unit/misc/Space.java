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
import com.inari.firefly.entity.EEntity;
import com.inari.firefly.entity.ETransform;
import com.inari.firefly.graphics.sprite.SpriteAsset;
import com.inari.firefly.graphics.tile.ETile;
import com.inari.firefly.system.FFContext;
import com.inari.firefly.system.utils.Disposable;

public final class Space extends Unit {

    private int spaceEntityId;
    
    @Override
    public final int getEntityId() {
        return spaceEntityId;
    }
    
     @Override
    public final UnitType type() {
        return UnitType.SPACE;
    }
     
     protected Space( int id ) {
        super( id );
    }

    @Override
    public final void initBDCFFTypesMap( Map<String, UnitType> bdcffMap ) {
        bdcffMap.put( " ", type() );
    }

    @Override
    public final Disposable load( FFContext context ) {

        context.getComponentBuilder( Asset.TYPE_KEY )
            .set( SpriteAsset.NAME, UnitType.SPACE.name() )
            .set( SpriteAsset.TEXTURE_ASSET_NAME, CaveSystem.GAME_UNIT_TEXTURE_NAME )
            .set( SpriteAsset.TEXTURE_REGION, new Rectangle( 5 * 32, 0, 32, 32 ) )
        .activate( SpriteAsset.class );

        spaceEntityId = context.getEntityBuilder()
            .set( ETransform.VIEW_NAME, CaveSystem.CAVE_VIEW_NAME )
            .set( ETile.MULTI_POSITION, true )
            .set( ETile.SPRITE_ASSET_NAME, UnitType.SPACE.name() )
            .set( EUnit.UNIT_TYPE, type() )
            .add( EEntity.ASPECTS, UnitAspect.DESTRUCTIBLE )
            .add( EEntity.ASPECTS, UnitAspect.CONSUMABLE )
            .add( EEntity.ASPECTS, UnitAspect.WALKABLE )
        .activate();
        
        return this;
    }

    @Override
    public final void dispose( FFContext context ) {
        context.deleteEntity( spaceEntityId );
        spaceEntityId = -1;
        context.deleteSystemComponent( Asset.TYPE_KEY, UnitType.SPACE.name() );
    }

    @Override
    public final int createOne( int xGridPos, int yGridPos, String type) {
        ETile tile = context.getEntityComponent( spaceEntityId, ETile.TYPE_KEY );
        tile.getGridPositions().add( new Position( xGridPos, yGridPos ) );
        context.getSystem( CaveSystem.SYSTEM_KEY ).setEntityId( spaceEntityId, xGridPos, yGridPos );
        return spaceEntityId;
    }

}
