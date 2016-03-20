package com.inari.dash.game.tasks;

import com.inari.dash.game.GameData;
import com.inari.dash.game.GameSystem;
import com.inari.dash.game.cave.CaveSystem;
import com.inari.dash.game.cave.CaveSystem.CaveSoundKey;
import com.inari.dash.game.cave.unit.UnitType;
import com.inari.dash.game.cave.unit.action.UnitActionType;
import com.inari.dash.game.tasks.InitGameWorkflow.TaskName;
import com.inari.firefly.action.ActionSystem;
import com.inari.firefly.asset.AssetSystem;
import com.inari.firefly.audio.AudioSystem;
import com.inari.firefly.audio.AudioSystemEvent;
import com.inari.firefly.control.ControllerSystem;
import com.inari.firefly.entity.EntitySystem;
import com.inari.firefly.system.FFContext;
import com.inari.firefly.system.view.ViewSystem;
import com.inari.firefly.task.Task;
import com.inari.firefly.task.TaskSystemEvent;
import com.inari.firefly.task.TaskSystemEvent.Type;

public final class DisposePlay extends Task {

    protected DisposePlay( int id ) {
        super( id );
    }

    @Override
    public final void runTask()  {
        
        context.notify( new TaskSystemEvent( Type.RUN_TASK, TaskName.DISPOSE_CAVE.name() ) ); 
        disposeCaveData( context );
        context.notify( new TaskSystemEvent( Type.RUN_TASK, TaskName.LOAD_GAME_SELECTION.name() ) ); 
        context.notify( new AudioSystemEvent( GameSystem.TITLE_SONG_SOUND_NAME, AudioSystemEvent.Type.PLAY_SOUND ) );
    }
    
    private final void disposeCaveData( FFContext context ) {
        ViewSystem viewSystem = context.getSystem( ViewSystem.SYSTEM_KEY );
        AudioSystem soundSystem = context.getSystem( AudioSystem.SYSTEM_KEY );
        ControllerSystem controllerSystem = context.getSystem( ControllerSystem.SYSTEM_KEY );
        AssetSystem assetSystem = context.getSystem( AssetSystem.SYSTEM_KEY );
        ActionSystem actionSystem = context.getSystem( ActionSystem.SYSTEM_KEY );
        EntitySystem entitySystem = context.getSystem( EntitySystem.SYSTEM_KEY );
        
        // dispose all units
        for ( UnitType unitType : UnitType.values() ) {
            if ( unitType.handler != null ) {
                unitType.handler.dispose( context );
            }
        }
        
        entitySystem.deleteAllActive();

        viewSystem.deleteView( CaveSystem.HEADER_VIEW_NAME );
        viewSystem.deleteView( CaveSystem.CAVE_VIEW_NAME );
        
        for ( CaveSoundKey caveSoundKey : CaveSoundKey.values() ) {
            soundSystem.deleteSound( caveSoundKey.name() );
            assetSystem.deleteAsset( caveSoundKey.name() );
        }
        
        controllerSystem.deleteController( CaveSystem.CAVE_CAMERA_CONTROLLER_NAME );
        assetSystem.deleteAsset( CaveSystem.GAME_UNIT_TEXTURE_NAME );
        
        for ( UnitActionType actionType : UnitActionType.values() ) {
            actionSystem.deleteAction( actionType.index() );
        }
        
        context.disposeContextComponent( GameData.CONTEXT_KEY );
        context.disposeSystem( CaveSystem.SYSTEM_KEY );
    }

}
