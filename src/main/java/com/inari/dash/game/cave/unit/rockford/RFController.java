package com.inari.dash.game.cave.unit.rockford;

import com.inari.commons.GeomUtils;
import com.inari.commons.geom.Direction;
import com.inari.commons.geom.Position;
import com.inari.commons.lang.aspect.AspectSetBuilder;
import com.inari.dash.game.cave.CaveSystem.CaveState;
import com.inari.dash.game.cave.unit.EUnit;
import com.inari.dash.game.cave.unit.UnitAspect;
import com.inari.dash.game.cave.unit.UnitController;
import com.inari.dash.game.cave.unit.UnitType;
import com.inari.dash.game.cave.unit.action.UnitActionType;
import com.inari.dash.game.cave.unit.rockford.Rockford.StateChangeEnum;
import com.inari.dash.game.cave.unit.rockford.Rockford.StateEnum;
import com.inari.firefly.FFInitException;
import com.inari.firefly.audio.AudioSystemEvent;
import com.inari.firefly.audio.AudioSystemEvent.Type;
import com.inari.firefly.control.action.ActionSystemEvent;
import com.inari.firefly.control.state.StateSystem;
import com.inari.firefly.control.state.StateSystemEvent;
import com.inari.firefly.graphics.tile.ETile;
import com.inari.firefly.prototype.Prototype;
import com.inari.firefly.system.FireFly;
import com.inari.firefly.system.external.FFInput;
import com.inari.firefly.system.external.FFInput.ButtonType;
import com.inari.firefly.system.external.FFTimer;

public final class RFController extends UnitController {
    
    private static final int ENTERING_ANIMATION_DURATION = 5;
    private static final int APPEARING_ANIMATION_DURATION = 6;
    private static final int IDLE_BLINKING_DURATION = 6;
    private static final int IDLE_FRETFUL_DURATION = 20;
    
    private FFInput input;
    private StateSystem stateSystem;

    private Position currentPos = new Position();
    private Position nextPos = new Position();

    protected RFController( int id ) {
        super( id );
    }

    @Override
    public final void init() throws FFInitException {
        super.init();
        
        input = context.getInput();
        stateSystem = context.getSystem( StateSystem.SYSTEM_KEY );
    }

    @Override
    protected final void update( FFTimer timer, int entityId ) {
        Rockford rfHandle = context.getSystemComponent( Prototype.TYPE_KEY, UnitType.ROCKFORD.name(), Rockford.class );
        CaveState caveState = caveService.getCaveState();
        if ( caveState == CaveState.WON || caveState == CaveState.INIT ) {
            return;
        }
        
        EUnit unit = context.getEntityComponent( entityId, EUnit.TYPE_KEY );
        String state = stateSystem.getCurrentState( Rockford.NAME );
        
        if ( StateEnum.APPEARING.is( state ) || StateEnum.ENTERING.is( state ) ) {
            unit.incrementAnimationCount();
            int animationCount = unit.getAnimationCount();
            if ( StateEnum.ENTERING.is( state ) && animationCount > ENTERING_ANIMATION_DURATION ) {
                unit.resetAnimationCount();
                context.notify( StateSystemEvent.createDoStateChangeEvent( Rockford.NAME, StateChangeEnum.ENTERING_APPEARING.name() ) );
                context.notify( new AudioSystemEvent( rfHandle.inSoundId, Type.PLAY_SOUND ) );
                return;
            }
            if ( StateEnum.APPEARING.is( state ) && animationCount > APPEARING_ANIMATION_DURATION ) {
                unit.resetAnimationCount();
                context.notify( StateSystemEvent.createDoStateChangeEvent( Rockford.NAME, StateChangeEnum.APPEARING_IDLE.name() ) );
                unit.setAspects( AspectSetBuilder.create( UnitAspect.ALIVE, UnitAspect.DESTRUCTIBLE  ) );
                return;
            }
            return;
        }
        
        if ( !caveService.hasAspect( entityId, UnitAspect.ALIVE ) ) {
            return;
        }
        
        if ( unit.isHit() ) {
            unit.setChangeTo( UnitType.SPACE );
            context.notify( new ActionSystemEvent( UnitActionType.EXPLODE.index(), entityId ) );
            return;
        }
        
        if ( input.isPressed( ButtonType.UP ) ) {
            unit.setMovement( Direction.NORTH );
        } else if ( input.isPressed( ButtonType.RIGHT ) ) {
            unit.setMovement( Direction.EAST );
        } else if ( input.isPressed( ButtonType.DOWN ) ) {
            unit.setMovement( Direction.SOUTH );
        } else if ( input.isPressed( ButtonType.LEFT ) ) {
            unit.setMovement( Direction.WEST );
        } else {
            unit.setMovement( Direction.NONE );
        }
        
        if ( unit.getMovement() == Direction.NONE ) {
            if ( StateEnum.BLINKING.is( state ) || StateEnum.FRETFUL.is( state ) ) {
                unit.incrementAnimationCount();
                int animationCount = unit.getAnimationCount();
                if ( StateEnum.BLINKING.is( state ) && animationCount > IDLE_BLINKING_DURATION ) {
                    context.notify( StateSystemEvent.createDoStateChangeEvent( Rockford.NAME, StateChangeEnum.BLINKING_IDLE.name() ) );
                    unit.resetAnimationCount();
                    return;
                }
                if ( StateEnum.FRETFUL.is( state ) && animationCount > IDLE_FRETFUL_DURATION ) {
                    context.notify( StateSystemEvent.createDoStateChangeEvent( Rockford.NAME, StateChangeEnum.FRETFUL_IDLE.name() ) );
                    unit.resetAnimationCount();
                    return;
                }
                return;
            } else if ( !StateEnum.IDLE.is( state ) ) {
                context.notify( StateSystemEvent.createDoStateChangeEventTo( Rockford.NAME, StateEnum.IDLE.name() ) );
            }
            if ( FireFly.RANDOM.nextInt( 100 ) < 5 ) {
                context.notify( StateSystemEvent.createDoStateChangeEvent( Rockford.NAME, StateChangeEnum.IDLE_BLINKING.name() ) );
                return;
            }
            if ( FireFly.RANDOM.nextInt( 100 ) < 3 ) {
                context.notify( StateSystemEvent.createDoStateChangeEvent( Rockford.NAME, StateChangeEnum.IDLE_FRETFUL.name() ) );
                return;
            }
            
            return;
        }
        
        ETile tile = context.getEntityComponent( entityId, ETile.TYPE_KEY );
        currentPos.x = tile.getGridXPos();
        currentPos.y = tile.getGridYPos();
        nextPos.x = currentPos.x;
        nextPos.y = currentPos.y;
        Direction move = unit.getMovement();
        boolean grabbing = input.isPressed( ButtonType.FIRE_1 );
        
        if ( ( move == Direction.WEST || move == Direction.NORTH ) && !StateEnum.LEFT.is( state ) ) {
            context.notify( StateSystemEvent.createDoStateChangeEventTo( Rockford.NAME, StateEnum.LEFT.name() ) );
        } else if ( ( move == Direction.EAST || move == Direction.SOUTH ) && !StateEnum.RIGHT.is( state ) ) {
            context.notify( StateSystemEvent.createDoStateChangeEventTo( Rockford.NAME, StateEnum.RIGHT.name() ) );
        }
        
        GeomUtils.movePosition( nextPos, move, 1, true );
        int nextEntityId = caveService.getEntityId( nextPos.x, nextPos.y );
        EUnit nextUnit = context.getEntityComponent( nextEntityId, EUnit.TYPE_KEY );
        UnitType nextType = nextUnit.getUnitType();

        if ( nextUnit.has( UnitAspect.WALKABLE ) ) {
           if ( nextType == UnitType.DIAMOND ) {
               context.notify( new ActionSystemEvent( UnitActionType.COLLECT.index(), nextEntityId ) );
               context.notify( new AudioSystemEvent( rfHandle.collectSoundId, Type.PLAY_SOUND ) ); 
           } else if ( nextType == UnitType.SPACE ) {
               if ( !grabbing ) {
                   context.notify( new AudioSystemEvent( rfHandle.spaceSoundId, Type.PLAY_SOUND ) ); 
               }
           } else {
               context.notify( new AudioSystemEvent( rfHandle.sandSoundId, Type.PLAY_SOUND ) ); 
           }
           
           if ( grabbing ) {
               if ( nextType != UnitType.SPACE ) {
                   caveService.deleteUnit( nextPos.x, nextPos.y );
                   caveService.createOne( nextPos.x, nextPos.y, UnitType.SPACE );
               }
           } else {
               context.notify( new ActionSystemEvent( UnitActionType.MOVE.index(), entityId ) );
           }
           return;
        }
        
        if ( GeomUtils.isHorizontal( move ) && ( nextType == UnitType.ROCK ) ) {
            pushRock( move, grabbing, entityId );
            return;
        }
    }
    
    private final void pushRock( Direction move, boolean grabbing, int rockfordId ) {
        int rockEntityId = caveService.getEntityId( nextPos.x, nextPos.y );
        GeomUtils.movePosition( nextPos, move, 1, true );
        if ( !caveService.isOfType( nextPos.x, nextPos.y, UnitType.SPACE ) ) {
            return;
        }
        
        if ( FireFly.RANDOM.nextInt( 100 ) < 20 ) {
            EUnit unit = context.getEntityComponent( rockEntityId, EUnit.TYPE_KEY );
            unit.setMovement( move );
            context.notify( new ActionSystemEvent( UnitActionType.MOVE.index(), rockEntityId ) );
            if ( !grabbing ) {
                context.notify( new ActionSystemEvent( UnitActionType.MOVE.index(), rockfordId ) );
            }
            unit.setMovement( Direction.NONE );
            context.notify( new AudioSystemEvent( getUnit( UnitType.ROCK ).getSoundId(), Type.PLAY_SOUND ) ); 
        }
    }

}
