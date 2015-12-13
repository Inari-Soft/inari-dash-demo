package com.inari.dash.game.cave.unit.enemy;

import java.util.Collection;
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
import com.inari.firefly.animation.sprite.SpriteAnimationBuilder;
import com.inari.firefly.animation.sprite.SpriteAnimationBuilder.SpriteAnimationHandler;
import com.inari.firefly.asset.AssetId;
import com.inari.firefly.entity.EEntity;
import com.inari.firefly.entity.ETransform;
import com.inari.firefly.entity.EntityController;
import com.inari.firefly.entity.EntityPrefab;
import com.inari.firefly.renderer.sprite.ESprite;
import com.inari.firefly.renderer.tile.ETile;
import com.inari.firefly.system.FFContext;
import com.inari.firefly.system.FFInitException;

public final class Butterfly extends UnitHandle {
    
    public static final String BUTTERFLY_NAME = "butterfly";
    private static final Map<String, Direction> INIT_DIRECTION_OF_TYPE = new HashMap<String, Direction>();
    
    private int prefabId;
    private int controllerId;
    private SpriteAnimationHandler spriteAnimationHandler;

    @Override
    public final void init( FFContext context ) throws FFInitException {
        super.init( context );
        
        spriteAnimationHandler = new SpriteAnimationBuilder( context )
            .setGroup( CaveSystem.GAME_UNIT_TEXTURE_KEY.group )
            .setLooping( true )
            .setNamePrefix( BUTTERFLY_NAME )
            .setTextureAssetKey( CaveSystem.GAME_UNIT_TEXTURE_KEY )
            .addSpritesToAnimation( 0, new Rectangle( 0, 11 * 32, 32, 32 ), 8, true )
        .build();
        
        Collection<AssetId> allSpriteAssetKeys = spriteAnimationHandler.getAllSpriteAssetKeys();
        caveAssetsToReload.addAll( allSpriteAssetKeys );
        
        controllerId = controllerSystem.getControllerBuilder()
            .set( EntityController.NAME, BUTTERFLY_NAME )
        .build( FireflyController.class );
        
        prefabId = prefabSystem.getEntityPrefabBuilder()
            .set( EEntity.CONTROLLER_IDS, new int[] { controllerId, spriteAnimationHandler.getControllerId() } )
            .set( EntityPrefab.NAME, BUTTERFLY_NAME )
            .set( ETransform.VIEW_ID, viewSystem.getViewId( CaveSystem.CAVE_VIEW_NAME ) )
            .set( ESprite.SPRITE_ID, allSpriteAssetKeys.iterator().next().id )
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
        
        initialized = true;
    }

    @Override
    public final void loadCaveData( FFContext context ) {
        super.loadCaveData( context );
        
        float updateRate = caveService.getUpdateRate();
        controllerSystem.getController( controllerId ).setUpdateResolution( updateRate );
        spriteAnimationHandler.setFrameTime( 80 - (int) updateRate * 4 );
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
        prefabSystem.deletePrefab( BUTTERFLY_NAME );
        controllerSystem.deleteController( controllerId );
        spriteAnimationHandler.dispose( context );
    }

}
