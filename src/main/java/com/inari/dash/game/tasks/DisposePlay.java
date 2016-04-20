package com.inari.dash.game.tasks;

import com.inari.dash.game.GameData;
import com.inari.dash.game.GameSystem;
import com.inari.dash.game.cave.CaveSystem;
import com.inari.dash.game.cave.CaveSystem.CaveSoundKey;
import com.inari.dash.game.cave.unit.UnitType;
import com.inari.dash.game.cave.unit.action.UnitActionType;
import com.inari.dash.game.tasks.InitGameWorkflow.TaskName;
import com.inari.firefly.asset.Asset;
import com.inari.firefly.audio.AudioSystemEvent;
import com.inari.firefly.audio.Sound;
import com.inari.firefly.control.Controller;
import com.inari.firefly.control.action.Action;
import com.inari.firefly.control.task.Task;
import com.inari.firefly.control.task.TaskSystemEvent;
import com.inari.firefly.control.task.TaskSystemEvent.Type;
import com.inari.firefly.entity.EntitySystem;
import com.inari.firefly.graphics.view.View;
import com.inari.firefly.prototype.Prototype;
import com.inari.firefly.system.FFContext;

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
        // dispose all units
        for ( UnitType unitType : UnitType.values() ) {
            context.deleteSystemComponent( Prototype.TYPE_KEY, unitType.ordinal() );
        }
        
        context.getSystem( EntitySystem.SYSTEM_KEY ).deleteAllActive();

        context.deleteSystemComponent( View.TYPE_KEY, CaveSystem.HEADER_VIEW_NAME );
        context.deleteSystemComponent( View.TYPE_KEY, CaveSystem.CAVE_VIEW_NAME );
        
        for ( CaveSoundKey caveSoundKey : CaveSoundKey.values() ) {
            context.deleteSystemComponent( Sound.TYPE_KEY, caveSoundKey.name() );
            context.deleteSystemComponent( Asset.TYPE_KEY, caveSoundKey.name() );
        }
        
        context.deleteSystemComponent( Controller.TYPE_KEY, CaveSystem.CAVE_CAMERA_CONTROLLER_NAME );
        context.deleteSystemComponent( Asset.TYPE_KEY, CaveSystem.GAME_UNIT_TEXTURE_NAME );
        
        for ( UnitActionType actionType : UnitActionType.values() ) {
            context.deleteSystemComponent( Action.TYPE_KEY, actionType.index() );
        }
        
        context.disposeContextComponent( GameData.COMPONENT_NAME );
        context.disposeSystem( CaveSystem.SYSTEM_KEY );
    }

}
