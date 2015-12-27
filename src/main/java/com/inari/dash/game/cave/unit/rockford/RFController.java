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
import com.inari.dash.game.cave.unit.rockford.RFUnit.RFState;
import com.inari.firefly.action.event.ActionEvent;
import com.inari.firefly.audio.event.AudioEvent;
import com.inari.firefly.audio.event.AudioEvent.Type;
import com.inari.firefly.graphics.tile.ETile;
import com.inari.firefly.system.FFContext;
import com.inari.firefly.system.FireFly;
import com.inari.firefly.system.external.FFInput;
import com.inari.firefly.system.external.FFInput.ButtonType;
import com.inari.firefly.system.external.FFTimer;

public final class RFController extends UnitController {
    
    private static final int ENTERING_ANIMATION_DURATION = 5;
    private static final int APPEARING_ANIMATION_DURATION = 6;
    private static final int IDLE_BLINKING_DURATION = 6;
    private static final int IDLE_FRETFUL_DURATION = 20;
    
    private final FFInput input;

    private Position currentPos = new Position();
    private Position nextPos = new Position();

    protected RFController( int id, FFContext context ) {
        super( id, context );
        input = context.getInput();
    }

    @Override
    protected final void update( FFTimer timer, int entityId ) {
        Rockford rfHandle = UnitType.ROCKFORD.getHandle();
        CaveState caveState = caveService.getCaveState();
        if ( caveState == CaveState.WON || caveState == CaveState.INIT ) {
            return;
        }
        
        RFUnit rockford = context.getEntityComponent( entityId, RFUnit.TYPE_KEY );
        EUnit unit = context.getEntityComponent( entityId, EUnit.TYPE_KEY );
        RFState state = rockford.getState();
        
        if ( state == RFState.APPEARING || state == RFState.ENTERING ) {
            unit.incrementAnimationCount();
            int animationCount = unit.getAnimationCount();
            if ( state == RFState.ENTERING && animationCount > ENTERING_ANIMATION_DURATION ) {
                rockford.setState( RFState.APPEARING );
                unit.resetAnimationCount();
                context.notify( new AudioEvent( rfHandle.inSoundId, Type.PLAY_SOUND ) );
                return;
            }
            if ( state == RFState.APPEARING && animationCount > APPEARING_ANIMATION_DURATION ) {
                rockford.setState( RFState.IDLE );
                unit.resetAnimationCount();
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
            context.notify( new ActionEvent( UnitActionType.EXPLODE.index(), entityId ) );
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
            if ( state == RFState.IDLE_BLINKING || state == RFState.IDLE_FRETFUL ) {
                unit.incrementAnimationCount();
                int animationCount = unit.getAnimationCount();
                if ( state == RFState.IDLE_BLINKING && animationCount > IDLE_BLINKING_DURATION ) {
                    rockford.setState( RFState.IDLE );
                    unit.resetAnimationCount();
                    return;
                }
                if ( state == RFState.IDLE_FRETFUL && animationCount > IDLE_FRETFUL_DURATION ) {
                    rockford.setState( RFState.IDLE );
                    unit.resetAnimationCount();
                    return;
                }
                return;
            }
            if ( FireFly.RANDOM.nextInt( 100 ) < 5 ) {
                rockford.setState( RFState.IDLE_BLINKING );
                return;
            }
            if ( FireFly.RANDOM.nextInt( 100 ) < 3 ) {
                rockford.setState( RFState.IDLE_FRETFUL );
                return;
            }
            rockford.setState( RFState.IDLE );
            return;
        }
        
        ETile tile = context.getEntityComponent( entityId, ETile.TYPE_KEY );
        currentPos.x = tile.getGridXPos();
        currentPos.y = tile.getGridYPos();
        nextPos.x = currentPos.x;
        nextPos.y = currentPos.y;
        Direction move = unit.getMovement();
        boolean grabbing = input.isPressed( ButtonType.FIRE_1 );
        
        if ( move == Direction.WEST || move == Direction.NORTH ) {
            rockford.setState( RFState.LEFT );
        } else if ( move == Direction.EAST || move == Direction.SOUTH ) {
            rockford.setState( RFState.RIGHT );
        }
        
        GeomUtils.movePositionOnDirection( nextPos, move, 1, true );
        int nextEntityId = caveService.getEntityId( nextPos.x, nextPos.y );
        EUnit nextUnit = context.getEntityComponent( nextEntityId, EUnit.TYPE_KEY );
        UnitType nextType = nextUnit.getUnitType();

        if ( nextUnit.has( UnitAspect.WALKABLE ) ) {
           if ( nextType == UnitType.DIAMOND ) {
               context.notify( new ActionEvent( UnitActionType.COLLECT.index(), nextEntityId ) );
               context.notify( new AudioEvent( rfHandle.collectSoundId, Type.PLAY_SOUND ) ); 
           } else if ( nextType == UnitType.SPACE ) {
               if ( !grabbing ) {
                   context.notify( new AudioEvent( rfHandle.spaceSoundId, Type.PLAY_SOUND ) ); 
               }
           } else {
               context.notify( new AudioEvent( rfHandle.sandSoundId, Type.PLAY_SOUND ) ); 
           }
           
           if ( grabbing ) {
               if ( nextType != UnitType.SPACE ) {
                   caveService.deleteUnit( nextPos.x, nextPos.y );
                   caveService.createOne( nextPos.x, nextPos.y, UnitType.SPACE );
               }
           } else {
               context.notify( new ActionEvent( UnitActionType.MOVE.index(), entityId ) );
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
        GeomUtils.movePositionOnDirection( nextPos, move, 1, true );
        if ( !caveService.isOfType( nextPos.x, nextPos.y, UnitType.SPACE ) ) {
            return;
        }
        
        if ( FireFly.RANDOM.nextInt( 100 ) < 20 ) {
            EUnit unit = context.getEntityComponent( rockEntityId, EUnit.TYPE_KEY );
            unit.setMovement( move );
            context.notify( new ActionEvent( UnitActionType.MOVE.index(), rockEntityId ) );
            if ( !grabbing ) {
                context.notify( new ActionEvent( UnitActionType.MOVE.index(), rockfordId ) );
            }
            unit.setMovement( Direction.NONE );
            context.notify( new AudioEvent( UnitType.ROCK.handler.getSoundId(), Type.PLAY_SOUND ) ); 
        }
    }

}
