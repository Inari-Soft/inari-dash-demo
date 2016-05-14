package com.inari.dash.game.cave;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.inari.commons.geom.Rectangle;
import com.inari.dash.game.GameData;
import com.inari.dash.game.GameSystem;
import com.inari.dash.game.cave.CaveSystem.CaveSoundKey;
import com.inari.dash.game.cave.CaveSystem.CaveState;
import com.inari.dash.game.cave.unit.Unit;
import com.inari.dash.game.cave.unit.UnitAspect;
import com.inari.dash.game.cave.unit.UnitType;
import com.inari.dash.game.cave.unit.action.UnitActionType;
import com.inari.dash.game.cave.unit.misc.Exit;
import com.inari.dash.game.tasks.InitGameWorkflow.StateChangeName;
import com.inari.dash.game.tasks.InitGameWorkflow.TaskName;
import com.inari.firefly.FFInitException;
import com.inari.firefly.audio.AudioSystemEvent;
import com.inari.firefly.audio.AudioSystemEvent.Type;
import com.inari.firefly.control.Controller;
import com.inari.firefly.control.action.ActionSystemEvent;
import com.inari.firefly.control.state.StateSystemEvent;
import com.inari.firefly.control.task.TaskSystemEvent;
import com.inari.firefly.entity.EEntity;
import com.inari.firefly.entity.ETransform;
import com.inari.firefly.entity.EntitySystem;
import com.inari.firefly.graphics.output.AnimatedGifOutput;
import com.inari.firefly.graphics.text.EText;
import com.inari.firefly.graphics.view.ViewSystem;
import com.inari.firefly.prototype.Prototype;
import com.inari.firefly.scene.SceneSystemEvent;
import com.inari.firefly.system.external.FFTimer;
import com.inari.firefly.system.external.FFTimer.UpdateScheduler;

public final class CaveController extends Controller {
    
    private CaveSystem caveService;
    private EntitySystem entitySystem;
    
    private UpdateScheduler secondTimer;
    private int initSeconds = 0;
    private boolean init = false;
    
    private final int playerTextPos = 0;
    private final int menTextPos = 12;
    private final int caveTextPos = 21;
    
    private final int diamondTextPos = 0;
    private final int collectedTextPos = 7;
    private final int timeTextPos = 12;
    private final int scoreTextPos = 18;
    
    private int exitEntityId = -1;
    private int playerEntityId = -1;
    
    private StringBuffer headerTextBuffer = null;
    private final AnimatedGifOutput animatedGifOutput = new AnimatedGifOutput();
    
    
    protected CaveController( int id ) {
        super( id );
    }

    @Override
    public void init() throws FFInitException {
        super.init();
        
        caveService = context.getSystem( CaveSystem.SYSTEM_KEY ); 
        entitySystem = context.getSystem( EntitySystem.SYSTEM_KEY );
        secondTimer = context.getTimer().createUpdateScheduler( 1 );
    }

    

    @Override
    public final void update( FFTimer timer ) {
        if ( exitEntityId < 0 ) {
            exitEntityId = context.getSystemComponent( Prototype.TYPE_KEY, UnitType.EXIT.ordinal(), Unit.class ).getEntityId();
        }
        if ( playerEntityId < 0 ) {
            playerEntityId = context.getSystemComponent( Prototype.TYPE_KEY, UnitType.ROCKFORD.ordinal(), Unit.class ).getEntityId();
        }
        
        GameData gameData = caveService.getGameData();
        CaveData caveData = caveService.getCaveData();
        
        if ( headerTextBuffer == null ) {
            EText headerTextEntity = context.getEntityComponent( CaveSystem.HEADER_VIEW_NAME, EText.TYPE_KEY );
            headerTextBuffer = headerTextEntity.getTextBuffer();
        }
        
        if ( caveService.caveState == CaveState.INIT ) {
            if ( !init ) {
                context.notify( new SceneSystemEvent( CaveSystem.CAVE_INIT_SCENE_NAME, SceneSystemEvent.EventType.RUN )  );
                initHeader( gameData, headerTextBuffer );
                init = true;
            }
            if ( secondTimer.needsUpdate() ) {
                initSeconds ++;
            }
            if ( initSeconds > 3 ) {
                context.notify( new SceneSystemEvent( CaveSystem.CAVE_INIT_SCENE_NAME, SceneSystemEvent.EventType.STOP )  );
                caveService.caveState = CaveState.ENTERING;
                playHeader( headerTextBuffer );
                init = false;
            }
            return;
        }
        
        if ( caveService.caveState == CaveState.ENTERING ) {
            EEntity playerEntity = entitySystem.getComponent( playerEntityId, EEntity.TYPE_KEY );
            initSeconds = 0;
            if ( playerEntity.hasAspect( UnitAspect.ALIVE ) ) {
                caveService.caveState = CaveState.PLAY;
            }
            return;
        }
        
        if ( caveService.caveState == CaveState.PLAY ) {
            if ( Gdx.input.isKeyPressed( Input.Keys.P ) ) {
                
                animatedGifOutput.setArea( new Rectangle( 100, 0, 300, 300 ) );
                animatedGifOutput.setFrames( 30 );
                animatedGifOutput.setFrameDelay( 100 );
                animatedGifOutput.setFileName( "animatedGifTest.gif" );
                animatedGifOutput.run( context );
            }
            
            initSeconds = 0;
            if ( secondTimer.needsUpdate() ) {
                caveData.tick();
                int caveTime = caveData.getTime();
                if ( caveTime < 10 && caveTime > 0 ) {
                    String soundName = "TIMEOUT" + caveTime;
                    context.notify( new AudioSystemEvent( CaveSoundKey.valueOf( soundName ).name(), Type.PLAY_SOUND ) );
                }
                
                if ( caveTime == 0 ) {
                    caveService.caveState = CaveState.LOOSE;
                    EEntity playerEntity = entitySystem.getComponent( playerEntityId, EEntity.TYPE_KEY );
                    playerEntity.resetAspect( UnitAspect.ALIVE );
                    initSeconds = 0;
                    return;
                }
            }
            
            EEntity exitEntity = entitySystem.getComponent( exitEntityId, EEntity.TYPE_KEY );
            if ( !exitEntity.hasAspect( UnitAspect.ACTIVE ) ) {
                boolean enough = caveData.getDiamondsToCollect() == caveData.getDiamondsCollected();
                if ( enough ) {
                    exitEntity.setAspects( EEntity.ENTITY_ASPECT_GROUP.createAspects( UnitAspect.ACTIVE, UnitAspect.WALKABLE ) );
                    context.notify( StateSystemEvent.createDoStateChangeEvent( UnitType.EXIT.name(), Exit.getStateChangeName() ) );
                    context.notify( new ActionSystemEvent( UnitActionType.FLASH.index(), exitEntityId ) );
                    context.notify( new AudioSystemEvent( CaveSoundKey.CRACK.name(), Type.PLAY_SOUND ) );
                }
            }
            
            if ( caveData.isModified() || gameData.isModified() ) {
                updatePlayHeader( gameData, caveData, headerTextBuffer );
            }
            
            return;
        }
        
        if ( caveService.caveState == CaveState.WON ) {
            if ( initSeconds <= 0 ) {
                EEntity playerEntity = entitySystem.getComponent( playerEntityId, EEntity.TYPE_KEY );
                playerEntity.resetAspect( UnitAspect.ALIVE );
                context.notify( new AudioSystemEvent( CaveSystem.CaveSoundKey.FINISHED.name(), Type.PLAY_SOUND ) );
                initSeconds++;
            }
            int time = caveData.getTime();
            if ( time > 0 ) {
                caveData.tick();
                updatePlayHeader( gameData, caveData, headerTextBuffer );
            } else {
                context.notify( new AudioSystemEvent( CaveSystem.CaveSoundKey.FINISHED.name(), Type.STOP_PLAYING ) );
                if ( gameData.hasNextCave() ) {
                    context.notify( new TaskSystemEvent( TaskSystemEvent.Type.RUN_TASK, TaskName.NEXT_CAVE.name() ) );
                } else {
                    exitPlay();
                }
                return;
            }
        }
        
        if ( caveService.caveState == CaveState.LOOSE ) {
            if ( initSeconds > 2 ) {
                int lives = gameData.getLives() - 1;
                gameData.setLives( lives );
                if ( lives >= 1 ) {
                    context.notify( new TaskSystemEvent( TaskSystemEvent.Type.RUN_TASK, TaskName.REPLAY_CAVE.name() ) );
                } else {
                    gameOverHeader( headerTextBuffer );
                }
                initSeconds = 0;
                return;
            }
            if ( secondTimer.needsUpdate() ) {
                initSeconds ++;
            }
            return;
        }
        
        if ( caveService.caveState == CaveState.GAME_OVER ) {
            if ( Gdx.input.isKeyPressed( Input.Keys.ENTER ) || Gdx.input.isKeyPressed( Input.Keys.SPACE ) ) {
                exitPlay();
            }
            return;
        }
    }

    private void exitPlay() {
        context.notify( StateSystemEvent.createDoStateChangeEvent( GameSystem.GAME_WORKFLOW_NAME, StateChangeName.EXIT_PLAY.name() ) );
    }
    
    private void initHeader( GameData gameData, StringBuffer headerTextBuffer ) {
        clearHeader( headerTextBuffer );
        String player1 = "PLAYER 1";
        String men = gameData.getLives() + " MEN";
        String level = "1:A";
        
        headerTextBuffer.replace( playerTextPos, playerTextPos + player1.length(), player1 );
        headerTextBuffer.replace( menTextPos, menTextPos + men.length(), men );
        headerTextBuffer.replace( caveTextPos, caveTextPos + level.length(), level );
    }
    
    private void playHeader( StringBuffer headerTextBuffer ) {
        clearHeader( headerTextBuffer );
        updatePlayHeader( caveService.getGameData(), caveService.getCaveData(), headerTextBuffer );
    }
    
    private void gameOverHeader( StringBuffer headerTextBuffer ) {
        clearHeader( headerTextBuffer );
        caveService.caveState = CaveState.GAME_OVER;
        ViewSystem viewSystem = context.getSystem( ViewSystem.SYSTEM_KEY );
        entitySystem.getEntityBuilder()
            .set( ETransform.VIEW_ID, viewSystem.getViewId( CaveSystem.HEADER_VIEW_NAME ) )
            .set( ETransform.XPOSITION, 100 )
            .set( ETransform.YPOSITION, 8 )
            .set( EText.FONT_ASSET_NAME, GameSystem.GAME_FONT_TEXTURE_NAME )
            .set( EText.TEXT, "%%% GAME OVER %%%" )
            .set( EText.TINT_COLOR, GameSystem.YELLOW_FONT_COLOR )
        .activate();
    }
    
    private void updatePlayHeader( GameData gameData, CaveData caveData, StringBuffer headerTextBuffer ) {
        int diamondsToCollect = caveData.getDiamondsToCollect();
        boolean enough = caveData.getDiamondsCollected() >= diamondsToCollect;
        String diamondsToCollectString = 
            ( enough )?
                "%%" : ( ( diamondsToCollect < 10 )? "0" + diamondsToCollect : String.valueOf( diamondsToCollect ) );
        int pointsForDiamond = ( enough )? caveData.getExtraDiamondPoints() : caveData.getNeededDiamondPoints();
        String neededDiamondPointsString = ( pointsForDiamond < 10 )? "0" + pointsForDiamond : String.valueOf( pointsForDiamond );
        String neededDiamondText = diamondsToCollectString + "%" + neededDiamondPointsString;
        headerTextBuffer.replace( diamondTextPos, diamondTextPos + neededDiamondText.length(), neededDiamondText );

        int collected = caveData.getDiamondsCollected();
        String collectedString = ( collected < 10 )? "0" + collected : String.valueOf( collected );
        headerTextBuffer.replace( collectedTextPos, collectedTextPos + collectedString.length(), collectedString );

        int time = caveData.getTime();
        String timeString = ( time < 10 )? "00" + time : ( time < 100 )? "0" + time : String.valueOf( time );
        headerTextBuffer.replace( timeTextPos, timeTextPos + timeString.length(), timeString );
      
        StringBuffer score = new StringBuffer( String.valueOf( gameData.getScore() ) );
        while ( score.length() < 6 ) {
            score.insert( 0, '0' );
        }
        String scoreString = score.toString();
        headerTextBuffer.replace( scoreTextPos, scoreTextPos + scoreString.length(), scoreString );
    }
    
    private void clearHeader( StringBuffer headerTextBuffer ) {
        for ( int i = 0; i < headerTextBuffer.length(); i++ ) {
            headerTextBuffer.setCharAt( i, ' ' );
        }
    }

}
