package com.inari.dash.game.cave;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.inari.commons.lang.aspect.AspectSetBuilder;
import com.inari.dash.game.GameData;
import com.inari.dash.game.GameSystem;
import com.inari.dash.game.cave.CaveSystem.CaveSoundKey;
import com.inari.dash.game.cave.CaveSystem.CaveState;
import com.inari.dash.game.cave.unit.EUnit;
import com.inari.dash.game.cave.unit.UnitAspect;
import com.inari.dash.game.cave.unit.UnitType;
import com.inari.dash.game.cave.unit.action.UnitActionType;
import com.inari.dash.game.cave.unit.misc.Exit;
import com.inari.dash.game.tasks.InitGameWorkflow.StateChangeName;
import com.inari.dash.game.tasks.InitGameWorkflow.TaskName;
import com.inari.firefly.FFInitException;
import com.inari.firefly.action.ActionSystemEvent;
import com.inari.firefly.asset.AssetSystem;
import com.inari.firefly.audio.AudioSystemEvent;
import com.inari.firefly.audio.AudioSystemEvent.Type;
import com.inari.firefly.control.Controller;
import com.inari.firefly.entity.ETransform;
import com.inari.firefly.entity.EntitySystem;
import com.inari.firefly.graphics.text.EText;
import com.inari.firefly.scene.SceneSystemEvent;
import com.inari.firefly.state.StateSystemEvent;
import com.inari.firefly.system.external.FFTimer;
import com.inari.firefly.system.external.FFTimer.UpdateScheduler;
import com.inari.firefly.system.view.ViewSystem;
import com.inari.firefly.task.TaskSystemEvent;

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
    
    private final int exitEntityId;
    private final int playerEntityId;
    
    
    protected CaveController( int id ) {
        super( id );
        exitEntityId = UnitType.EXIT.getHandle().getEntityId();
        playerEntityId = UnitType.ROCKFORD.getHandle().getEntityId();
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
        GameData gameData = caveService.getGameData();
        CaveData caveData = caveService.getCaveData();
        
        if ( caveService.caveState == CaveState.INIT ) {
            if ( !init ) {
                context.notify( new SceneSystemEvent( CaveSystem.CAVE_INIT_SCENE_NAME, SceneSystemEvent.EventType.RUN )  );
                initHeader( gameData );
                init = true;
            }
            if ( secondTimer.needsUpdate() ) {
                initSeconds ++;
            }
            if ( initSeconds > 3 ) {
                context.notify( new SceneSystemEvent( CaveSystem.CAVE_INIT_SCENE_NAME, SceneSystemEvent.EventType.STOP )  );
                caveService.caveState = CaveState.ENTERING;
                playHeader();
                init = false;
            }
            return;
        }
        
        if ( caveService.caveState == CaveState.ENTERING ) {
            EUnit playerUnit = entitySystem.getComponent( playerEntityId, EUnit.TYPE_KEY );
            initSeconds = 0;
            if ( playerUnit.has( UnitAspect.ALIVE ) ) {
                caveService.caveState = CaveState.PLAY;
            }
            return;
        }
        
        if ( caveService.caveState == CaveState.PLAY ) {
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
                    EUnit playerUnit = entitySystem.getComponent( playerEntityId, EUnit.TYPE_KEY );
                    playerUnit.resetAspect( UnitAspect.ALIVE );
                    initSeconds = 0;
                    return;
                }
            }
            
            EUnit exitUnit = entitySystem.getComponent( exitEntityId, EUnit.TYPE_KEY );
            if ( !exitUnit.has( UnitAspect.ACTIVE ) ) {
                boolean enough = caveData.getDiamondsToCollect() == caveData.getDiamondsCollected();
                if ( enough ) {
                    exitUnit.setAspects( AspectSetBuilder.create( UnitAspect.ACTIVE, UnitAspect.WALKABLE ) );
                    context.notify( StateSystemEvent.createDoStateChangeEvent( Exit.EXIT_NAME, Exit.getStateChangeName() ) );
                    context.notify( new ActionSystemEvent( UnitActionType.FLASH.index(), exitEntityId ) );
                    context.notify( new AudioSystemEvent( CaveSoundKey.CRACK.name(), Type.PLAY_SOUND ) );
                }
            }
            
            if ( caveData.isModified() || gameData.isModified() ) {
                updatePlayHeader( gameData, caveData );
            }
            
            return;
        }
        
        if ( caveService.caveState == CaveState.WON ) {
            if ( initSeconds <= 0 ) {
                EUnit playerUnit = entitySystem.getComponent( playerEntityId, EUnit.TYPE_KEY );
                playerUnit.resetAspect( UnitAspect.ALIVE );
                context.notify( new AudioSystemEvent( CaveSystem.CaveSoundKey.FINISHED.name(), Type.PLAY_SOUND ) );
                initSeconds++;
            }
            int time = caveData.getTime();
            if ( time > 0 ) {
                caveData.tick();
                updatePlayHeader( gameData, caveData );
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
                    gameOverHeader();
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
    
    private void initHeader( GameData gameData ) {
        clearHeader();
        char[] charArray = "PLAYER 1".toCharArray();
        char[] headerText = caveService.getHeaderText();
        System.arraycopy( charArray, 0, headerText, playerTextPos, charArray.length );
        charArray = ( gameData.getLives() + " MEN" ).toCharArray();
        System.arraycopy( charArray, 0, headerText, menTextPos, charArray.length );
        charArray = ( "1:A" ).toCharArray();
        System.arraycopy( charArray, 0, headerText, caveTextPos, charArray.length );
    }
    
    private void playHeader() {
        clearHeader();
        updatePlayHeader( caveService.getGameData(), caveService.getCaveData() );
    }
    
    private void gameOverHeader() {
        clearHeader();
        caveService.caveState = CaveState.GAME_OVER;
        AssetSystem assetSystem = context.getSystem( AssetSystem.SYSTEM_KEY );
        ViewSystem viewSystem = context.getSystem( ViewSystem.SYSTEM_KEY );
        entitySystem.getEntityBuilder()
            .set( ETransform.VIEW_ID, viewSystem.getViewId( CaveSystem.HEADER_VIEW_NAME ) )
            .set( ETransform.XPOSITION, 100 )
            .set( ETransform.YPOSITION, 8 )
            .set( EText.FONT_ID, assetSystem.getAssetId( GameSystem.GAME_FONT_TEXTURE_NAME ) )
            .set( EText.TEXT_STRING, "%%% GAME OVER %%%" )
            .set( EText.TINT_COLOR, GameSystem.YELLOW_FONT_COLOR )
        .activate();
    }
    
    private void updatePlayHeader( GameData gameData, CaveData caveData ) {
        int diamondsToCollect = caveData.getDiamondsToCollect();
        boolean enough = caveData.getDiamondsCollected() >= diamondsToCollect;
        String diamondsToCollectString = 
            ( enough )?
                "%%" : ( ( diamondsToCollect < 10 )? "0" + diamondsToCollect : String.valueOf( diamondsToCollect ) );
        int pointsForDiamond = ( enough )? caveData.getExtraDiamondPoints() : caveData.getNeededDiamondPoints();
        String neededDiamondPointsString = ( pointsForDiamond < 10 )? "0" + pointsForDiamond : String.valueOf( pointsForDiamond );
        char[] charArray = ( diamondsToCollectString + "%" + neededDiamondPointsString ).toCharArray();
        char[] headerText = caveService.getHeaderText();
        System.arraycopy( charArray, 0, headerText, diamondTextPos, charArray.length );
        
        int collected = caveData.getDiamondsCollected();
        String collectedString = ( collected < 10 )? "0" + collected : String.valueOf( collected );
        charArray = collectedString.toCharArray();
        System.arraycopy( charArray, 0, headerText, collectedTextPos, charArray.length );
        
        int time = caveData.getTime();
        String timeString = ( time < 10 )? "00" + time : ( time < 100 )? "0" + time : String.valueOf( time );
        charArray = timeString.toCharArray();
        System.arraycopy( charArray, 0, headerText, timeTextPos, charArray.length );
        
        charArray = "000000".toCharArray();
        char[] score = String.valueOf( gameData.getScore() ).toCharArray();
        System.arraycopy( score, 0, charArray, charArray.length - score.length, score.length );
        System.arraycopy( charArray, 0, headerText, scoreTextPos, charArray.length );
    }
    
    private void clearHeader() {
        char[] headerText = caveService.getHeaderText();
        for ( int i = 0; i < headerText.length; i++ ) {
            headerText[ i ] = ' ';
        }
    }

}
