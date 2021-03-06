package com.inari.dash.game.cave.unit.action;

import com.inari.commons.GeomUtils;
import com.inari.commons.geom.Direction;
import com.inari.commons.geom.Position;
import com.inari.dash.game.cave.unit.EUnit;
import com.inari.dash.game.cave.unit.Unit;
import com.inari.dash.game.cave.unit.UnitAspect;
import com.inari.dash.game.cave.unit.UnitType;
import com.inari.firefly.audio.AudioSystemEvent;
import com.inari.firefly.graphics.tile.ETile;
import com.inari.firefly.prototype.Prototype;


public final class ExplosionAction extends UnitAction {
    
    private Position tmpPos = new Position();
    private UnitType explosionType = UnitType.EXPLOSION;

    public ExplosionAction( int id ) {
        super( id );
    }

    @Override
    public final void action( int entityId ) {
        ETile tile = entitySystem.getComponent( entityId, ETile.TYPE_KEY );
        EUnit unit = entitySystem.getComponent( entityId, EUnit.TYPE_KEY );
        tmpPos.x = tile.getGridXPos();
        tmpPos.y = tile.getGridYPos();
        
        UnitType explosionType = unit.getExplosionType();
        if ( explosionType != null ) {
            this.explosionType = explosionType;
        }
        UnitType explodeTo = unit.getChangeTo();
        
        createExplosion( explodeTo );
        GeomUtils.movePosition( tmpPos, Direction.NORTH, 1, true );
        createExplosion( explodeTo );
        GeomUtils.movePosition( tmpPos, Direction.EAST, 1, true );
        createExplosion( explodeTo );
        GeomUtils.movePosition( tmpPos, Direction.SOUTH, 1, true );
        createExplosion( explodeTo );
        GeomUtils.movePosition( tmpPos, Direction.SOUTH, 1, true );
        createExplosion( explodeTo );
        GeomUtils.movePosition( tmpPos, Direction.WEST, 1, true );
        createExplosion( explodeTo );
        GeomUtils.movePosition( tmpPos, Direction.WEST, 1, true );
        createExplosion( explodeTo );
        GeomUtils.movePosition( tmpPos, Direction.NORTH, 1, true );
        createExplosion( explodeTo );
        GeomUtils.movePosition( tmpPos, Direction.NORTH, 1, true );
        createExplosion( explodeTo );
        
        context.notify( new AudioSystemEvent( 
            getPrototype( this.explosionType ).getSoundId(), 
            AudioSystemEvent.Type.PLAY_SOUND ) 
        );
        explosionType = UnitType.EXPLOSION;
    }

    private void createExplosion( UnitType explodeTo ) {
        int entityId = caveService.getEntityId( tmpPos.x, tmpPos.y );
        if ( caveService.hasAspect( entityId, UnitAspect.DESTRUCTIBLE ) ) {
            caveService.deleteUnit( entityId, tmpPos.x, tmpPos.y );
            int newEntityId = getPrototype( explosionType ).createOne( tmpPos.x, tmpPos.y );
            EUnit unit = entitySystem.getComponent( newEntityId, EUnit.TYPE_KEY );
            unit.setChangeTo( explodeTo );
        }
    }
    
    private Unit getPrototype( UnitType unitType ) {
        if ( unitType == null ) {
            throw new IllegalArgumentException( "************" );
        }
        return context.getSystemComponent( Prototype.TYPE_KEY, unitType.ordinal(), Unit.class );
    }

}
