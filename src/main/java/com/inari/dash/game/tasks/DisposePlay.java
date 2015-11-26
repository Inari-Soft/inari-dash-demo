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
import com.inari.firefly.control.ControllerSystem;
import com.inari.firefly.entity.EntitySystem;
import com.inari.firefly.sound.SoundSystem;
import com.inari.firefly.sound.event.SoundEvent;
import com.inari.firefly.system.FFContext;
import com.inari.firefly.system.view.ViewSystem;
import com.inari.firefly.task.Task;
import com.inari.firefly.task.event.TaskEvent;
import com.inari.firefly.task.event.TaskEvent.Type;

public final class DisposePlay extends Task {

    protected DisposePlay( int id ) {
        super( id );
    }

    @Override
    public final void run( FFContext context ) {
        
        context.notify( new TaskEvent( Type.RUN_TASK, TaskName.DISPOSE_CAVE.name() ) ); 
        disposeCaveData( context );
        context.notify( new TaskEvent( Type.RUN_TASK, TaskName.LOAD_GAME_SELECTION.name() ) ); 
        context.notify( new SoundEvent( GameSystem.TITLE_SONG_SOUND_NAME, SoundEvent.Type.PLAY_SOUND ) );
    }
    
    private final void disposeCaveData( FFContext context ) {
        ViewSystem viewSystem = context.getSystem( ViewSystem.CONTEXT_KEY );
        SoundSystem soundSystem = context.getSystem( SoundSystem.CONTEXT_KEY );
        ControllerSystem controllerSystem = context.getSystem( ControllerSystem.CONTEXT_KEY );
        AssetSystem assetSystem = context.getSystem( AssetSystem.CONTEXT_KEY );
        ActionSystem actionSystem = context.getSystem( ActionSystem.CONTEXT_KEY );
        EntitySystem entitySystem = context.getSystem( EntitySystem.CONTEXT_KEY );
        
        // dispose all units
        for ( UnitType unitType : UnitType.values() ) {
            if ( unitType.handler != null ) {
                unitType.handler.dispose( context );
            }
        }
        
        entitySystem.deleteAll();
        
        viewSystem.deleteView( CaveSystem.HEADER_VIEW_NAME );
        viewSystem.deleteView( CaveSystem.CAVE_VIEW_NAME );
        
        for ( CaveSoundKey caveSoundKey : CaveSoundKey.values() ) {
            soundSystem.deleteSound( caveSoundKey.id );
        }
        
        assetSystem.deleteAssets( CaveSystem.CAVE_SOUND_GROUP_NAME );
        
        controllerSystem.deleteController( CaveSystem.CAVE_CAMERA_CONTROLLER_NAME );
        assetSystem.deleteAsset( CaveSystem.GAME_UNIT_TEXTURE_KEY );
        
        for ( UnitActionType actionType : UnitActionType.values() ) {
            actionSystem.deleteAction( actionType.index() );
        }
        
        context.disposeComponent( GameData.CONTEXT_KEY );
        context.disposeSystem( CaveSystem.CONTEXT_KEY );
    }

}
