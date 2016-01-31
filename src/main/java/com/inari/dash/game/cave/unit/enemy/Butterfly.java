package com.inari.dash.game.cave.unit.enemy;

import java.util.HashMap;
import java.util.Map;

import com.inari.commons.geom.Direction;
import com.inari.commons.geom.Rectangle;
import com.inari.commons.lang.aspect.AspectSetBuilder;
import com.inari.dash.game.cave.CaveSystem;
import com.inari.dash.game.cave.unit.EUnit;
import com.inari.dash.game.cave.unit.UnitAspect;
import com.inari.dash.game.cave.unit.UnitHandle;
import com.inari.dash.game.cave.unit.UnitType;
import com.inari.firefly.composite.Composite;
import com.inari.firefly.composite.sprite.AnimatedSpriteData;
import com.inari.firefly.composite.sprite.AnimatedTile;
import com.inari.firefly.entity.EEntity;
import com.inari.firefly.entity.ETransform;
import com.inari.firefly.entity.EntityController;
import com.inari.firefly.entity.prefab.EntityPrefab;
import com.inari.firefly.graphics.tile.ETile;
import com.inari.firefly.system.FFContext;

public final class Butterfly extends UnitHandle {
    
    public static final String BUTTERFLY_NAME = "butterfly";
    private static final Map<String, Direction> INIT_DIRECTION_OF_TYPE = new HashMap<String, Direction>();
    
    private int prefabId;
    private int controllerId;
    private int animationAssetId;

    @Override
    public final void loadCaveData( FFContext context ) {
        super.loadCaveData( context );
        
        float updateRate = caveService.getUpdateRate();
        AnimatedSpriteData[] animationData = AnimatedSpriteData.create( 80 - (int) updateRate * 4, new Rectangle( 0, 11 * 32, 32, 32 ), 8, Direction.EAST );
        animationAssetId = context.getComponentBuilder( Composite.TYPE_KEY )
            .set( AnimatedTile.NAME, BUTTERFLY_NAME )
            .set( AnimatedTile.LOOPING, true )
            .set( AnimatedTile.UPDATE_RESOLUTION, updateRate )
            .set( AnimatedTile.TEXTURE_ASSET_ID, assetSystem.getAssetId( CaveSystem.GAME_UNIT_TEXTURE_NAME ) )
            .add( AnimatedTile.ANIMATED_SPRITE_DATA, animationData )
        .activate( AnimatedTile.class );
        int animatioControllerId = context
            .getSystemComponent( Composite.TYPE_KEY, animationAssetId, AnimatedTile.class )
            .getAnimationControllerId();
        
        controllerId = controllerSystem.getControllerBuilder()
            .set( EntityController.NAME, BUTTERFLY_NAME )
            .set( EntityController.UPDATE_RESOLUTION, updateRate )
        .build( FireflyController.class );
        
        prefabId = prefabSystem.getEntityPrefabBuilder()
            .add( EEntity.CONTROLLER_IDS, controllerId )
            .add( EEntity.CONTROLLER_IDS, animatioControllerId )
            .set( EntityPrefab.NAME, BUTTERFLY_NAME )
            .set( ETransform.VIEW_ID, viewSystem.getViewId( CaveSystem.CAVE_VIEW_NAME ) )
            .set( ETile.MULTI_POSITION, false )
            .set( EUnit.UNIT_TYPE, type() )
            .set( EUnit.MOVEMENT, Direction.SOUTH )
            .set( EUnit.EXPLOSION_TYPE, UnitType.EXPLOSION_TO_DIAMOND )
            .set( EUnit.CHANGE_TO, UnitType.DIAMOND )
            .set( EUnit.ASPECTS, AspectSetBuilder.create( 
                UnitAspect.DESTRUCTIBLE, 
                UnitAspect.ENEMY,
                UnitAspect.ALIVE
            ) )
        .build();
        prefabSystem.cacheComponents( prefabId, 200 );
    }

    @Override
    public void disposeCaveData( FFContext context ) {
        super.disposeCaveData( context );
        
        prefabSystem.deletePrefab( BUTTERFLY_NAME );
        context.deleteSystemComponent( Composite.TYPE_KEY, animationAssetId );
    }

    @Override
    public final UnitType type() {
        return UnitType.BUTTERFLY;
    }

    @Override
    public final void initBDCFFTypesMap( Map<String, UnitType> bdcffMap ) {
        bdcffMap.put( "c", type() );
        bdcffMap.put( "C", type() );
        bdcffMap.put( "b", type() );
        bdcffMap.put( "B", type() );
        bdcffMap.put( "bl", type() );
        bdcffMap.put( "br", type() );
        bdcffMap.put( "bu", type() );
        bdcffMap.put( "bd", type() );
        
        INIT_DIRECTION_OF_TYPE.put( "c", Direction.EAST );
        INIT_DIRECTION_OF_TYPE.put( "C", Direction.WEST );
        INIT_DIRECTION_OF_TYPE.put( "b", Direction.SOUTH );
        INIT_DIRECTION_OF_TYPE.put( "B", Direction.NORTH );
        INIT_DIRECTION_OF_TYPE.put( "bl", Direction.WEST );
        INIT_DIRECTION_OF_TYPE.put( "br", Direction.EAST );
        INIT_DIRECTION_OF_TYPE.put( "bu", Direction.NORTH );
        INIT_DIRECTION_OF_TYPE.put( "bd", Direction.SOUTH );
    }
    
    @Override
    public final int createOne( String type, int xGridPos, int yGridPos ) {
        int entityId = prefabSystem.buildOne( prefabId );
        ETile tile = entitySystem.getComponent( entityId, ETile.TYPE_KEY );
        EUnit unit = entitySystem.getComponent( entityId, EUnit.TYPE_KEY );
        tile.setGridXPos( xGridPos );
        tile.setGridYPos( yGridPos );
        unit.setMovement( INIT_DIRECTION_OF_TYPE.get( type ) );
        entitySystem.activateEntity( entityId );
        return entityId;
    }

    @Override
    public final int createOne( int xGridPos, int yGridPos ) {
        throw new UnsupportedOperationException( "Use createOne(String,int,int) instead." );    }
    
    @Override
    public final void dispose( FFContext context ) {
        controllerSystem.deleteController( controllerId );
    }

}
