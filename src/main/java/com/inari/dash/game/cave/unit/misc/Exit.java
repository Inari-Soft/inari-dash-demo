package com.inari.dash.game.cave.unit.misc;

import java.util.Map;

import com.inari.commons.geom.Rectangle;
import com.inari.commons.lang.aspect.AspectSetBuilder;
import com.inari.dash.game.cave.CaveSystem;
import com.inari.dash.game.cave.unit.EUnit;
import com.inari.dash.game.cave.unit.UnitHandle;
import com.inari.dash.game.cave.unit.UnitType;
import com.inari.firefly.FFInitException;
import com.inari.firefly.animation.sprite.SpriteAnimationBuilder;
import com.inari.firefly.animation.sprite.SpriteAnimationBuilder.SpriteAnimationHandler;
import com.inari.firefly.entity.EEntity;
import com.inari.firefly.entity.ETransform;
import com.inari.firefly.graphics.sprite.ESprite;
import com.inari.firefly.graphics.tile.ETile;
import com.inari.firefly.system.FFContext;

public final class Exit extends UnitHandle {
    
    public enum State {
        CLOSED,
        OPEN
    }
    
    public static final String EXIT_NAME = "exit";
    
    private SpriteAnimationHandler spriteAnimationHandler;
    private int exitEntityId;

    @Override
    public final void init( FFContext context ) throws FFInitException {
        super.init( context );

        initialized = true;
    }

    @Override
    public final void loadCaveData( FFContext context ) {
        super.loadCaveData( context );
        
        spriteAnimationHandler = new SpriteAnimationBuilder( context )
            .setLooping( true )
            .setNamePrefix( EXIT_NAME )
            .setTextureAssetName( CaveSystem.GAME_UNIT_TEXTURE_NAME )
            .setStatedAnimationType( ExitAnimationController.class )
            .setState( State.CLOSED.ordinal() )
            .addSpritesToAnimation( 0, new Rectangle( 32, 6 * 32, 32, 32 ), 1, true )
            .setState( State.OPEN.ordinal() )
            .addSpritesToAnimation( 0, new Rectangle( 32, 6 * 32, 32, 32 ), 2, true )
        .build();
        
        float updateRate = caveService.getUpdateRate();
        spriteAnimationHandler.setFrameTime( State.CLOSED.ordinal(), Integer.MAX_VALUE );
        spriteAnimationHandler.setFrameTime( State.OPEN.ordinal(), 400 - (int) updateRate * 4 );
    }

    @Override
    public void disposeCaveData( FFContext context ) {
        super.disposeCaveData( context );
        
        entitySystem.delete( exitEntityId );
        spriteAnimationHandler.dispose( context );
    }

    @Override
    public final UnitType type() {
        return UnitType.EXIT;
    }

    @Override
    public final void initBDCFFTypesMap( Map<String, UnitType> bdcffMap ) {
        bdcffMap.put( "X", type() );
        bdcffMap.put( "H", type() );
    }

    @Override
    public final int createOne( int xGridPos, int yGridPos ) {
        exitEntityId = entitySystem.getEntityBuilder()
            .add( EEntity.CONTROLLER_IDS, spriteAnimationHandler.getControllerId() )
            .set( ETransform.VIEW_ID, viewSystem.getViewId( CaveSystem.CAVE_VIEW_NAME ) )
            .set( ESprite.SPRITE_ID, spriteAnimationHandler.getStartSpriteId() )
            .set( ETile.GRID_X_POSITION, xGridPos )
            .set( ETile.GRID_Y_POSITION, yGridPos )
            .set( EUnit.UNIT_TYPE, type() )
            .set( EUnit.ASPECTS, AspectSetBuilder.create() )
        .activate();
        return exitEntityId;
    }
    
    @Override
    public final int getEntityId() {
        return exitEntityId;
    }

    @Override
    public final void dispose( FFContext context ) {
        
    }
}
