package com.inari.dash.game.cave.unit.wall;

import java.util.Map;

import com.inari.commons.geom.Position;
import com.inari.commons.geom.Rectangle;
import com.inari.commons.lang.aspect.AspectSetBuilder;
import com.inari.dash.game.cave.CaveSystem;
import com.inari.dash.game.cave.unit.EUnit;
import com.inari.dash.game.cave.unit.UnitHandle;
import com.inari.dash.game.cave.unit.UnitType;
import com.inari.firefly.FFInitException;
import com.inari.firefly.entity.ETransform;
import com.inari.firefly.graphics.sprite.ESprite;
import com.inari.firefly.graphics.sprite.SpriteAsset;
import com.inari.firefly.graphics.tile.ETile;
import com.inari.firefly.system.FFContext;

public final class SolidWall extends UnitHandle {
    
    public static final String SOLID_WALL_NAME = "solidWall";
    
    private int solidWallEntityId;

    @Override
    public final void init( FFContext context ) throws FFInitException {
        super.init( context );

        initialized = true;
    }

    @Override
    public void loadCaveData( FFContext context ) {
        super.loadCaveData( context );
        
        assetSystem.getAssetBuilder()
            .set( SpriteAsset.NAME, SOLID_WALL_NAME )
            .set( SpriteAsset.TEXTURE_ASSET_ID, assetSystem.getAssetId( CaveSystem.GAME_UNIT_TEXTURE_NAME ) )
            .set( SpriteAsset.TEXTURE_REGION, new Rectangle( 32, 6 * 32, 32, 32 ) )
        .activate( SpriteAsset.class );
        
        solidWallEntityId = entitySystem.getEntityBuilder()
            .set( ETransform.VIEW_ID, viewSystem.getViewId( CaveSystem.CAVE_VIEW_NAME ) )
            .set( ETile.MULTI_POSITION, true )
            .set( ESprite.SPRITE_ID, assetSystem.getAssetInstanceId( SOLID_WALL_NAME ) )
            .set( EUnit.UNIT_TYPE, type() )
            .set( EUnit.ASPECTS, AspectSetBuilder.create() )
        .activate();
    }

    @Override
    public void disposeCaveData( FFContext context ) {
        super.disposeCaveData( context );
        entitySystem.delete( solidWallEntityId );
        solidWallEntityId = -1;
        assetSystem.deleteAsset( SOLID_WALL_NAME );
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
    public final int createOne( int xGridPos, int yGridPos ) {
        ETile tile = entitySystem.getComponent( solidWallEntityId, ETile.TYPE_KEY );
        tile.getGridPositions().add( new Position( xGridPos, yGridPos ) );
        caveService.setEntityId( solidWallEntityId, xGridPos, yGridPos );
        return solidWallEntityId;
    }
    
    @Override
    public final void dispose( FFContext context ) {
        assetSystem.disposeAsset( SOLID_WALL_NAME );
    }

}
