package com.inari.dash.game.cave.unit.rockford;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.inari.commons.GeomUtils;
import com.inari.commons.geom.Direction;
import com.inari.commons.geom.Position;
import com.inari.commons.lang.aspect.AspectSetBuilder;
import com.inari.dash.game.cave.CaveService.CaveState;
import com.inari.dash.game.cave.unit.EUnit;
import com.inari.dash.game.cave.unit.UnitAspect;
import com.inari.dash.game.cave.unit.UnitController;
import com.inari.dash.game.cave.unit.UnitType;
import com.inari.dash.game.cave.unit.action.UnitActionType;
import com.inari.dash.game.cave.unit.rockford.RFUnit.RFState;
import com.inari.firefly.action.event.ActionEvent;
import com.inari.firefly.renderer.tile.ETile;
import com.inari.firefly.sound.event.SoundEvent;
import com.inari.firefly.sound.event.SoundEvent.Type;
import com.inari.firefly.system.FFContext;
import com.inari.firefly.system.FFTimer;
import com.inari.firefly.system.FireFly;

public final class RFController extends UnitController {
    
    private static final int ENTERING_ANIMATION_DURATION = 20;
    private static final int APPEARING_ANIMATION_DURATION = 6;
    private static final int IDLE_BLINKING_DURATION = 6;
    private static final int IDLE_FRETFUL_DURATION = 20;

    private Position currentPos = new Position();
    private Position nextPos = new Position();

    protected RFController( int id, FFContext context ) {
        super( id, context );
    }

    @Override
    protected final void update( FFTimer timer, int entityId ) {
        Rockford rfHandle = UnitType.ROCKFORD.getHandle();
        CaveState caveState = caveService.getCaveState();
        if ( caveState == CaveState.WON || caveState == CaveState.INIT ) {
            return;
        }
        
        RFUnit rockford = entitySystem.getComponent( entityId, RFUnit.class );
        EUnit unit = entitySystem.getComponent( entityId, EUnit.class );
        RFState state = rockford.getState();
        
        if ( state == RFState.APPEARING || state == RFState.ENTERING ) {
            unit.incrementAnimationCount();
            int animationCount = unit.getAnimationCount();
            if ( state == RFState.ENTERING && animationCount > ENTERING_ANIMATION_DURATION ) {
                rockford.setState( RFState.APPEARING );
                unit.resetAnimationCount();
                eventDispatcher.notify( new SoundEvent( rfHandle.inSoundId, Type.PLAY_SOUND ) );
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
            eventDispatcher.notify( new ActionEvent( UnitActionType.EXPLODE.index(), entityId ) );
            return;
        }
        
        if ( Gdx.input.isKeyPressed( Input.Keys.W ) ) {
            unit.setMovement( Direction.NORTH );
        } else if ( Gdx.input.isKeyPressed( Input.Keys.D ) ) {
            unit.setMovement( Direction.EAST );
        } else if ( Gdx.input.isKeyPressed( Input.Keys.S ) ) {
            unit.setMovement( Direction.SOUTH );
        } else if ( Gdx.input.isKeyPressed( Input.Keys.A ) ) {
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
        
        ETile tile = entitySystem.getComponent( entityId, ETile.class );
        currentPos.x = tile.getGridXPos();
        currentPos.y = tile.getGridYPos();
        nextPos.x = currentPos.x;
        nextPos.y = currentPos.y;
        Direction move = unit.getMovement();
        boolean grabbing = Gdx.input.isKeyPressed( Input.Keys.SPACE );
        
        if ( move == Direction.WEST || move == Direction.NORTH ) {
            rockford.setState( RFState.LEFT );
        } else if ( move == Direction.EAST || move == Direction.SOUTH ) {
            rockford.setState( RFState.RIGHT );
        }
        
        GeomUtils.movePositionOnDirection( nextPos, move, 1, true );
        int nextEntityId = caveService.getEntityId( nextPos.x, nextPos.y );
        EUnit nextUnit = entitySystem.getComponent( nextEntityId, EUnit.class );
        UnitType nextType = nextUnit.getUnitType();

        if ( nextUnit.has( UnitAspect.WALKABLE ) ) {
           if ( nextType == UnitType.DIAMOND ) {
               eventDispatcher.notify( new ActionEvent( UnitActionType.COLLECT.index(), nextEntityId ) );
               eventDispatcher.notify( new SoundEvent( rfHandle.collectSoundId, Type.PLAY_SOUND ) ); 
           } else if ( nextType == UnitType.SPACE ) {
               if ( !grabbing ) {
                   eventDispatcher.notify( new SoundEvent( rfHandle.spaceSoundId, Type.PLAY_SOUND ) ); 
               }
           } else {
               eventDispatcher.notify( new SoundEvent( rfHandle.sandSoundId, Type.PLAY_SOUND ) ); 
           }
           
           if ( grabbing ) {
               if ( nextType != UnitType.SPACE ) {
                   caveService.deleteUnit( nextPos.x, nextPos.y );
                   caveService.createOne( nextPos.x, nextPos.y, UnitType.SPACE );
               }
           } else {
               eventDispatcher.notify( new ActionEvent( UnitActionType.MOVE.index(), entityId ) );
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
            EUnit unit = entitySystem.getComponent( rockEntityId, EUnit.class );
            unit.setMovement( move );
            eventDispatcher.notify( new ActionEvent( UnitActionType.MOVE.index(), rockEntityId ) );
            if ( !grabbing ) {
                eventDispatcher.notify( new ActionEvent( UnitActionType.MOVE.index(), rockfordId ) );
            }
            unit.setMovement( Direction.NONE );
            eventDispatcher.notify( new SoundEvent( UnitType.ROCK.handler.getSoundId(), Type.PLAY_SOUND ) ); 
        }
    }

}
