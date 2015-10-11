package com.inari.dash.game.cave.unit;

import com.inari.commons.geom.Direction;
import com.inari.commons.lang.aspect.Aspect;
import com.inari.dash.game.cave.CaveService;
import com.inari.firefly.entity.EntityController;
import com.inari.firefly.renderer.tile.TileGrid;
import com.inari.firefly.sound.SoundSystem;
import com.inari.firefly.system.FFContext;

public abstract class UnitController extends EntityController {
    
    protected CaveService caveService;
    protected SoundSystem soundSystem;
    

    protected UnitController( int id, FFContext context ) {
        super( id, context );
        caveService = context.getComponent( CaveService.CONTEXT_KEY );
        soundSystem = context.getComponent( SoundSystem.CONTEXT_KEY ); 
    }
    
    protected final int getEntityId( int x, int y, Direction dir ) {
        TileGrid tileGrid = caveService.getTileGrid();
        switch ( dir ) {
            case NORTH: return tileGrid.get( x, y - 1 );
            case NORTH_WEST: return tileGrid.get( x - 1, y - 1 );
            case WEST: return tileGrid.get( x - 1, y );
            case SOUTH_WEST: return tileGrid.get( x - 1, y + 1 );
            case SOUTH: return tileGrid.get( x, y + 1 );
            case SOUTH_EAST: return tileGrid.get( x + 1, y + 1 );
            case EAST: return tileGrid.get( x + 1, y );
            case NORTH_EAST: return tileGrid.get( x + 1, y - 1 );
            default: return tileGrid.get( x, y );
        }
    }
    
    protected final boolean isOfType( int x, int y, UnitType type ) {
        return isOfType( x, y, Direction.NONE, type );
    }
    
    protected final boolean isOfType( int x, int y, Direction dir, UnitType type ) {
        EUnit unit = entitySystem.getComponent( getEntityId( x, y, dir ), EUnit.class );
        return unit.getUnitType() == type;
    }
    
    protected final boolean hasAspect( int x, int y, Direction dir, Aspect aspect ) {
        EUnit unit = entitySystem.getComponent( getEntityId( x, y, dir ), EUnit.class );
        return unit.has( aspect );
    }
    
    protected final boolean isMoving( int x, int y, Direction tileOnDirection, Direction moveDirection ) {
        EUnit unit = entitySystem.getComponent( getEntityId( x, y, tileOnDirection ), EUnit.class );
        return unit.getMovement() == moveDirection;
    }

}
