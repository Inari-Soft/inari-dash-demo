package com.inari.dash.game.cave.unit.enemy;

import java.util.HashMap;
import java.util.Map;

import com.inari.commons.geom.Direction;
import com.inari.commons.geom.Rectangle;
import com.inari.dash.game.cave.CaveSystem;
import com.inari.dash.game.cave.unit.EUnit;
import com.inari.dash.game.cave.unit.Unit;
import com.inari.dash.game.cave.unit.UnitAspect;
import com.inari.dash.game.cave.unit.UnitType;
import com.inari.firefly.asset.Asset;
import com.inari.firefly.composite.sprite.AnimatedSpriteData;
import com.inari.firefly.composite.sprite.AnimatedTile;
import com.inari.firefly.control.Controller;
import com.inari.firefly.entity.EEntity;
import com.inari.firefly.entity.ETransform;
import com.inari.firefly.entity.EntityController;
import com.inari.firefly.entity.prefab.EntityPrefab;
import com.inari.firefly.entity.prefab.EntityPrefabSystem;
import com.inari.firefly.graphics.tile.ETile;
import com.inari.firefly.system.FFContext;
import com.inari.firefly.system.utils.Disposable;

public final class Butterfly extends Unit {

    private static final Map<String, Direction> INIT_DIRECTION_OF_TYPE = new HashMap<String, Direction>();
    
    private int prefabId;
    
    protected Butterfly( int id ) {
        super( id );
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
    public final Disposable load( FFContext context ) {
        
        float updateRate = getUpdateRate();
        AnimatedSpriteData[] animationData = AnimatedSpriteData.create( 
            80 - (int) updateRate * 4, 
            new Rectangle( 0, 11 * 32, 32, 32 ), 
            8, Direction.EAST 
        );
        int animationAssetId = context.getComponentBuilder( Asset.TYPE_KEY )
            .set( AnimatedTile.NAME, UnitType.BUTTERFLY.name() )
            .set( AnimatedTile.LOOPING, true )
            .set( AnimatedTile.UPDATE_RESOLUTION, updateRate )
            .set( AnimatedTile.TEXTURE_ASSET_NAME, CaveSystem.GAME_UNIT_TEXTURE_NAME )
            .add( AnimatedTile.ANIMATED_SPRITE_DATA, animationData )
        .activate( AnimatedTile.class );
        int animatioControllerId = context
            .getSystemComponent( Asset.TYPE_KEY, animationAssetId, AnimatedTile.class )
            .getAnimationControllerId();
        
        int controllerId = context.getComponentBuilder( Controller.TYPE_KEY )
            .set( EntityController.NAME, UnitType.BUTTERFLY.name() )
            .set( EntityController.UPDATE_RESOLUTION, updateRate )
        .build( FireflyController.class );
        
        prefabId = context.getComponentBuilder( EntityPrefab.TYPE_KEY )
            .set( EntityPrefab.NAME, UnitType.BUTTERFLY.name() )
            .add( EEntity.CONTROLLER_IDS, controllerId )
            .add( EEntity.CONTROLLER_IDS, animatioControllerId )
            .set( EntityPrefab.INITIAL_CREATE_NUMBER, 200 )
            .set( ETransform.VIEW_NAME, CaveSystem.CAVE_VIEW_NAME )
            .set( ETile.MULTI_POSITION, false )
            .set( EUnit.UNIT_TYPE, type() )
            .set( EUnit.MOVEMENT, Direction.SOUTH )
            .set( EUnit.EXPLOSION_TYPE, UnitType.EXPLOSION_TO_DIAMOND )
            .set( EUnit.CHANGE_TO, UnitType.DIAMOND )
            .add( EUnit.ASPECTS, UnitAspect.DESTRUCTIBLE )
            .add( EUnit.ASPECTS, UnitAspect.ENEMY )
            .add( EUnit.ASPECTS, UnitAspect.ALIVE )
        .build();
        
        return this;
    }

    @Override
    public final void dispose( FFContext context ) {
        context.deleteSystemComponent( EntityPrefab.TYPE_KEY, type().name() );
        context.deleteSystemComponent( Asset.TYPE_KEY, type().name() );
        context.deleteSystemComponent( Controller.TYPE_KEY, type().name() );
    }

    @Override
    public final int createOne( int xGridPos, int yGridPos, String type ) {
        int entityId = context.getSystem( EntityPrefabSystem.SYSTEM_KEY ).buildOne( prefabId );
        ETile tile = context.getEntityComponent( entityId, ETile.TYPE_KEY );
        EUnit unit = context.getEntityComponent( entityId, EUnit.TYPE_KEY );
        tile.setGridXPos( xGridPos );
        tile.setGridYPos( yGridPos );
        unit.setMovement( INIT_DIRECTION_OF_TYPE.get( type ) );
        context.activateEntity( entityId );
        return entityId;
    }

}
