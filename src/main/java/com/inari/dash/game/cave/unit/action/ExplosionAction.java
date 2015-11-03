package com.inari.dash.game.cave.unit.action;

import com.inari.commons.GeomUtils;
import com.inari.commons.geom.Direction;
import com.inari.commons.geom.Position;
import com.inari.dash.game.cave.unit.EUnit;
import com.inari.dash.game.cave.unit.UnitAspect;
import com.inari.dash.game.cave.unit.UnitType;
import com.inari.firefly.renderer.tile.ETile;
import com.inari.firefly.sound.event.SoundEvent;


public final class ExplosionAction extends UnitAction {
    
    private Position tmpPos = new Position();
    private UnitType explosionType = UnitType.EXPLOSION;

    public ExplosionAction( int id ) {
        super( id );
    }

    @Override
    public final void performAction( int entityId ) {
        ETile tile = entitySystem.getComponent( entityId, ETile.class );
        EUnit unit = entitySystem.getComponent( entityId, EUnit.class );
        tmpPos.x = tile.getGridXPos();
        tmpPos.y = tile.getGridYPos();
        
        UnitType explosionType = unit.getExplosionType();
        if ( explosionType != null ) {
            this.explosionType = explosionType;
        }
        UnitType explodeTo = unit.getChangeTo();
        
        createExplosion( explodeTo );
        GeomUtils.movePositionOnDirection( tmpPos, Direction.NORTH, 1, true );
        createExplosion( explodeTo );
        GeomUtils.movePositionOnDirection( tmpPos, Direction.EAST, 1, true );
        createExplosion( explodeTo );
        GeomUtils.movePositionOnDirection( tmpPos, Direction.SOUTH, 1, true );
        createExplosion( explodeTo );
        GeomUtils.movePositionOnDirection( tmpPos, Direction.SOUTH, 1, true );
        createExplosion( explodeTo );
        GeomUtils.movePositionOnDirection( tmpPos, Direction.WEST, 1, true );
        createExplosion( explodeTo );
        GeomUtils.movePositionOnDirection( tmpPos, Direction.WEST, 1, true );
        createExplosion( explodeTo );
        GeomUtils.movePositionOnDirection( tmpPos, Direction.NORTH, 1, true );
        createExplosion( explodeTo );
        GeomUtils.movePositionOnDirection( tmpPos, Direction.NORTH, 1, true );
        createExplosion( explodeTo );
        
        eventDispatcher.notify( new SoundEvent( UnitType.EXPLOSION.handler.getSoundId(), SoundEvent.Type.PLAY_SOUND ) );
        explosionType = UnitType.EXPLOSION;
    }

    private void createExplosion( UnitType explodeTo ) {
        int entityId = caveService.getEntityId( tmpPos.x, tmpPos.y );
        if ( caveService.hasAspect( entityId, UnitAspect.DESTRUCTIBLE ) ) {
            caveService.deleteUnit( entityId, tmpPos.x, tmpPos.y );
            int newEntityId = explosionType.handler.createOne( tmpPos.x, tmpPos.y );
            EUnit unit = entitySystem.getComponent( newEntityId, EUnit.class );
            unit.setChangeTo( explodeTo );
        }
    }

}
