package com.inari.dash.game.tasks;

import com.inari.commons.event.IEventDispatcher;
import com.inari.dash.game.GameData;
import com.inari.dash.game.GameService;
import com.inari.dash.game.GameService.TaskName;
import com.inari.dash.game.cave.CaveService;
import com.inari.dash.game.cave.CaveService.CaveSoundKey;
import com.inari.dash.game.cave.unit.UnitType;
import com.inari.dash.game.cave.unit.action.UnitActionType;
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
        IEventDispatcher eventDispatcher = context.getComponent( FFContext.EVENT_DISPATCHER );
        
        eventDispatcher.notify( new TaskEvent( Type.RUN_TASK, TaskName.DISPOSE_CAVE.name() ) ); 
        disposeCaveData( context );
        eventDispatcher.notify( new TaskEvent( Type.RUN_TASK, TaskName.LOAD_GAME_SELECTION.name() ) ); 
        eventDispatcher.notify( new SoundEvent( GameService.TITLE_SONG_SOUND_NAME, SoundEvent.Type.PLAY_SOUND ) );
    }
    
    private final void disposeCaveData( FFContext context ) {
        ViewSystem viewSystem = context.getComponent( ViewSystem.CONTEXT_KEY );
        SoundSystem soundSystem = context.getComponent( SoundSystem.CONTEXT_KEY );
        ControllerSystem controllerSystem = context.getComponent( ControllerSystem.CONTEXT_KEY );
        AssetSystem assetSystem = context.getComponent( AssetSystem.CONTEXT_KEY );
        ActionSystem actionSystem = context.getComponent( ActionSystem.CONTEXT_KEY );
        EntitySystem entitySystem = context.getComponent( EntitySystem.CONTEXT_KEY );
        
        // dispose all units
        for ( UnitType unitType : UnitType.values() ) {
            if ( unitType.handler != null ) {
                unitType.handler.dispose( context );
            }
        }
        
        System.out.println( assetSystem.getNameKeys() );
        
        entitySystem.deleteAll();
        
        viewSystem.deleteView( CaveService.HEADER_VIEW_NAME );
        viewSystem.deleteView( CaveService.CAVE_VIEW_NAME );
        
        for ( CaveSoundKey caveSoundKey : CaveSoundKey.values() ) {
            soundSystem.deleteSound( caveSoundKey.id );
        }
        
        System.out.println( assetSystem.getNameKeys() );
        
        assetSystem.deleteAssets( CaveService.CAVE_SOUND_GROUP_NAME );
        
        controllerSystem.deleteController( CaveService.CAVE_CAMERA_CONTROLLER_NAME );
        assetSystem.deleteAsset( CaveService.GAME_UNIT_TEXTURE_KEY );
        
        for ( UnitActionType actionType : UnitActionType.values() ) {
            actionSystem.deleteAction( actionType.index() );
        }
        
        context.putComponent( GameData.CONTEXT_KEY, null );
        context.putComponent( CaveService.CONTEXT_KEY, null );
    }

}
