package com.inari.dash.game.cave.unit.enemy;

import java.util.HashMap;
import java.util.Map;

import com.inari.commons.geom.Direction;
import com.inari.commons.geom.Rectangle;
import com.inari.commons.lang.aspect.AspectSetBuilder;
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
import com.inari.firefly.system.Disposable;
import com.inari.firefly.system.FFContext;

public final class Firefly extends Unit {

    private static final Map<String, Direction> INIT_DIRECTION_OF_TYPE = new HashMap<String, Direction>();
    
    private int prefabId;
    
    @Override
    public final UnitType type() {
        return UnitType.FIREFLY;
    }
    
    protected Firefly( int id ) {
        super( id );
    }

    @Override
    public final void initBDCFFTypesMap( Map<String, UnitType> bdcffMap ) {
        bdcffMap.put( "q", type() );
        bdcffMap.put( "Q", type() );
        bdcffMap.put( "o", type() );
        bdcffMap.put( "O", type() );
        bdcffMap.put( "ol", type() );
        bdcffMap.put( "or", type() );
        bdcffMap.put( "ou", type() );
        bdcffMap.put( "od", type() );
        
        INIT_DIRECTION_OF_TYPE.put( "q", Direction.EAST );
        INIT_DIRECTION_OF_TYPE.put( "Q", Direction.WEST );
        INIT_DIRECTION_OF_TYPE.put( "o", Direction.SOUTH );
        INIT_DIRECTION_OF_TYPE.put( "O", Direction.NORTH );
        INIT_DIRECTION_OF_TYPE.put( "ol", Direction.WEST );
        INIT_DIRECTION_OF_TYPE.put( "or", Direction.EAST );
        INIT_DIRECTION_OF_TYPE.put( "ou", Direction.NORTH );
        INIT_DIRECTION_OF_TYPE.put( "od", Direction.SOUTH );
    }

    @Override
    public final Disposable load( FFContext context ) {

        float updateRate = getUpdateRate();
        AnimatedSpriteData[] animationData = AnimatedSpriteData.create( 
            80 - (int) updateRate * 4, 
            new Rectangle( 0, 9 * 32, 32, 32 ), 
            8, Direction.EAST 
        );
        int animationAssetId = context.getComponentBuilder( Asset.TYPE_KEY )
            .set( AnimatedTile.NAME, UnitType.FIREFLY.name() )
            .set( AnimatedTile.LOOPING, true )
            .set( AnimatedTile.UPDATE_RESOLUTION, updateRate )
            .set( AnimatedTile.TEXTURE_ASSET_NAME, CaveSystem.GAME_UNIT_TEXTURE_NAME )
            .add( AnimatedTile.ANIMATED_SPRITE_DATA, animationData )
        .activate( AnimatedTile.class );
        int animatioControllerId = context
            .getSystemComponent( Asset.TYPE_KEY, animationAssetId, AnimatedTile.class )
            .getAnimationControllerId();
        
        int controllerId = context.getComponentBuilder( Controller.TYPE_KEY )
            .set( EntityController.NAME, UnitType.FIREFLY.name() )
            .set( EntityController.UPDATE_RESOLUTION, updateRate )
        .build( FireflyController.class );
        
        prefabId = context.getComponentBuilder( EntityPrefab.TYPE_KEY )
            .add( EEntity.CONTROLLER_IDS, controllerId )
            .add( EEntity.CONTROLLER_IDS, animatioControllerId )
            .set( EntityPrefab.NAME, UnitType.FIREFLY.name() )
            .set( EntityPrefab.INITIAL_CREATE_NUMBER, 200 )
            .set( ETransform.VIEW_NAME, CaveSystem.CAVE_VIEW_NAME )
            .set( ETile.MULTI_POSITION, false )
            .set( EUnit.UNIT_TYPE, type() )
            .set( EUnit.MOVEMENT, Direction.SOUTH )
            .set( EUnit.EXPLOSION_TYPE, UnitType.EXPLOSION )
            .set( EUnit.CHANGE_TO, UnitType.SPACE )
            .set( EUnit.ASPECTS, AspectSetBuilder.create( 
                UnitAspect.DESTRUCTIBLE, 
                UnitAspect.ENEMY,
                UnitAspect.ALIVE
            ) )
        .build();
        
        return this;
    }

    @Override
    public void dispose( FFContext context ) {
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
