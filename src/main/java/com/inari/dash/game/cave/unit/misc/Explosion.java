package com.inari.dash.game.cave.unit.misc;

import java.util.Map;

import com.inari.commons.geom.Direction;
import com.inari.commons.geom.Rectangle;
import com.inari.dash.game.cave.CaveSystem;
import com.inari.dash.game.cave.unit.EUnit;
import com.inari.dash.game.cave.unit.UnitType;
import com.inari.firefly.asset.Asset;
import com.inari.firefly.composite.sprite.AnimatedSpriteData;
import com.inari.firefly.composite.sprite.AnimatedTile;
import com.inari.firefly.entity.EEntity;
import com.inari.firefly.entity.ETransform;
import com.inari.firefly.entity.prefab.EntityPrefab;
import com.inari.firefly.entity.prefab.EntityPrefabSystem;
import com.inari.firefly.graphics.tile.ETile;
import com.inari.firefly.system.FFContext;
import com.inari.firefly.system.utils.Disposable;

public final class Explosion extends AbstractExplosionHandle {

    private int prefabId;

    protected Explosion( int id ) {
        super( id );
    }
    
    @Override
    public final UnitType type() {
        return UnitType.EXPLOSION;
    }

    @Override
    public final void initBDCFFTypesMap( Map<String, UnitType> bdcffMap ) {
        // no
    }

    @Override
    public final Disposable load( FFContext context ) {
        initGeneralExplosion();
        
        float updateRate = getUpdateRate();
        AnimatedSpriteData[] animationData = AnimatedSpriteData.create( 
            300 - (int) updateRate * 4, 
            new Rectangle( 32, 0, 32, 32 ), 3, 
            Direction.EAST 
        );
        int animationAssetId = context.getComponentBuilder( Asset.TYPE_KEY )
            .set( AnimatedTile.NAME, UnitType.EXPLOSION.name() )
            .set( AnimatedTile.LOOPING, true )
            .set( AnimatedTile.UPDATE_RESOLUTION, updateRate )
            .set( AnimatedTile.TEXTURE_ASSET_NAME, CaveSystem.GAME_UNIT_TEXTURE_NAME )
            .add( AnimatedTile.ANIMATED_SPRITE_DATA, animationData )
        .activate( AnimatedTile.class );
        int animatioControllerId = context
            .getSystemComponent( Asset.TYPE_KEY, animationAssetId, AnimatedTile.class )
            .getAnimationControllerId();
        
        prefabId = context.getComponentBuilder( EntityPrefab.TYPE_KEY )
            .add( EEntity.CONTROLLER_IDS, CONTROLLER_ID )
            .add( EEntity.CONTROLLER_IDS, animatioControllerId )
            .set( EntityPrefab.NAME, UnitType.EXPLOSION.name() )
            .set( EntityPrefab.INITIAL_CREATE_NUMBER, 100 )
            .set( ETransform.VIEW_NAME, CaveSystem.CAVE_VIEW_NAME )
            .set( ETile.MULTI_POSITION, false )
            .set( EUnit.UNIT_TYPE, type() )
        .build();
        
        return this;
    }

    @Override
    public void dispose( FFContext context ) {
        context.deleteSystemComponent( EntityPrefab.TYPE_KEY, prefabId );
        context.deleteSystemComponent( Asset.TYPE_KEY, UnitType.EXPLOSION.name() );
        
        disposeGeneralExplosion();
    }

    @Override
    public final int createOne( int xGridPos, int yGridPos, String type ) {
        int entityId = context.getSystem( EntityPrefabSystem.SYSTEM_KEY ).buildOne( prefabId );
        ETile tile = context.getEntityComponent( entityId, ETile.TYPE_KEY );
        tile.setGridXPos( xGridPos );
        tile.setGridYPos( yGridPos );
        context.activateEntity( entityId );
        return entityId;
    }

}
