package com.inari.dash.game.cave.unit.wall;

import java.util.Map;

import com.inari.commons.geom.Position;
import com.inari.commons.geom.Rectangle;
import com.inari.commons.lang.aspect.AspectSetBuilder;
import com.inari.dash.game.cave.CaveSystem;
import com.inari.dash.game.cave.unit.EUnit;
import com.inari.dash.game.cave.unit.UnitAspect;
import com.inari.dash.game.cave.unit.UnitHandle;
import com.inari.dash.game.cave.unit.UnitType;
import com.inari.firefly.FFInitException;
import com.inari.firefly.entity.ETransform;
import com.inari.firefly.renderer.sprite.ESprite;
import com.inari.firefly.renderer.sprite.SpriteAsset;
import com.inari.firefly.renderer.tile.ETile;
import com.inari.firefly.system.FFContext;

public class BrickWall extends UnitHandle {
    
    public static final String BRICK_WALL_NAME = "brickWall";

    private int brickWallEntityId;
    
    @Override
    public void init( FFContext context ) throws FFInitException {
        super.init( context );

        initialized = true;
    }
    
    @Override
    public void loadCaveData( FFContext context ) {
        super.loadCaveData( context );
        
        assetSystem.getAssetBuilder()
            .set( SpriteAsset.NAME, BRICK_WALL_NAME )
            .set( SpriteAsset.TEXTURE_ASSET_ID, assetSystem.getAssetId( CaveSystem.GAME_UNIT_TEXTURE_NAME ) )
            .set( SpriteAsset.TEXTURE_REGION, new Rectangle( 3 * 32, 6 * 32, 32, 32 ) )
        .activate( SpriteAsset.class );
        
        brickWallEntityId = entitySystem.getEntityBuilder()
            .set( ETransform.VIEW_ID, viewSystem.getViewId( CaveSystem.CAVE_VIEW_NAME ) )
            .set( ETile.MULTI_POSITION, true )
            .set( ESprite.SPRITE_ID, assetSystem.getAssetInstanceId( BRICK_WALL_NAME ) )
            .set( EUnit.UNIT_TYPE, type() )
            .set( EUnit.ASPECTS, AspectSetBuilder.create( 
                UnitAspect.ASLOPE, 
                UnitAspect.DESTRUCTIBLE
            ) )
        .activate();
    }

    @Override
    public void disposeCaveData( FFContext context ) {
        super.disposeCaveData( context );
        entitySystem.delete( brickWallEntityId );
        brickWallEntityId = -1;
        assetSystem.deleteAsset( BRICK_WALL_NAME );
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
    public int createOne( int xGridPos, int yGridPos ) {
        ETile tile = entitySystem.getComponent( brickWallEntityId, ETile.TYPE_KEY );
        tile.getGridPositions().add( new Position( xGridPos, yGridPos ) );
        caveService.setEntityId( brickWallEntityId, xGridPos, yGridPos );
        return brickWallEntityId;
    }
    
    @Override
    public final void dispose( FFContext context ) {
        assetSystem.disposeAsset( BRICK_WALL_NAME );
    }

}
