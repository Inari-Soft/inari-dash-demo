package com.inari.dash.game.tasks;

import com.inari.commons.geom.Position;
import com.inari.dash.game.cave.CaveSystem;
import com.inari.dash.game.cave.unit.UnitType;
import com.inari.firefly.asset.AssetSystem;
import com.inari.firefly.control.ControllerSystem;
import com.inari.firefly.entity.EntitySystem;
import com.inari.firefly.graphics.tile.TileGridSystem;
import com.inari.firefly.scene.SceneEvent;
import com.inari.firefly.system.FFContext;
import com.inari.firefly.system.view.ViewSystem;
import com.inari.firefly.task.Task;

public final class DisposeCave extends Task {

    protected DisposeCave( int id ) {
        super( id );
    }

    @Override
    public final void run( FFContext context ) {
        AssetSystem assetSystem = context.getSystem( AssetSystem.SYSTEM_KEY );
        TileGridSystem tileGridSystem = context.getSystem( TileGridSystem.SYSTEM_KEY );
        ControllerSystem controllerSystem = context.getSystem( ControllerSystem.SYSTEM_KEY );
        EntitySystem entitySystem = context.getSystem( EntitySystem.SYSTEM_KEY );
        ViewSystem viewSystem = context.getSystem( ViewSystem.SYSTEM_KEY );

        context.notify( new SceneEvent( CaveSystem.CAVE_INIT_SCENE_NAME, SceneEvent.EventType.DELETE ) );
        
        // dispose all units
        for ( UnitType unitType : UnitType.values() ) {
            if ( unitType.handler != null ) {
                unitType.handler.disposeCaveData( context );
            }
        }
        
        entitySystem.deleteAll();
        assetSystem.disposeAsset( CaveSystem.GAME_UNIT_TEXTURE_NAME );
        
        int caveViewId = viewSystem.getViewId( CaveSystem.CAVE_VIEW_NAME );
        tileGridSystem.deleteAllTileGrid( caveViewId );
        controllerSystem.deleteController( CaveSystem.CAVE_CONTROLLER_NAME );
        Position worldPosition = viewSystem.getView( caveViewId ).getWorldPosition();
        worldPosition.x = 0;
        worldPosition.y = 0;
    }

}
