package com.inari.dash.game.tasks;

import com.inari.commons.geom.Position;
import com.inari.dash.game.cave.CaveService;
import com.inari.dash.game.cave.unit.UnitType;
import com.inari.firefly.asset.AssetSystem;
import com.inari.firefly.control.ControllerSystem;
import com.inari.firefly.entity.EntitySystem;
import com.inari.firefly.renderer.tile.TileGridSystem;
import com.inari.firefly.system.FFContext;
import com.inari.firefly.system.view.ViewSystem;
import com.inari.firefly.task.Task;

public final class DisposeCave extends Task {

    protected DisposeCave( int id ) {
        super( id );
    }

    @Override
    public final void run( FFContext context ) {
        AssetSystem assetSystem = context.getComponent( AssetSystem.CONTEXT_KEY );
        TileGridSystem tileGridSystem = context.getComponent( TileGridSystem.CONTEXT_KEY );
        ControllerSystem controllerSystem = context.getComponent( ControllerSystem.CONTEXT_KEY );
        EntitySystem entitySystem = context.getComponent( EntitySystem.CONTEXT_KEY );
        ViewSystem viewSystem = context.getComponent( ViewSystem.CONTEXT_KEY );

        // dispose all units
        for ( UnitType unitType : UnitType.values() ) {
            if ( unitType.handler != null ) {
                unitType.handler.disposeCaveData( context );
            }
        }
        
        entitySystem.deleteAll();
        assetSystem.disposeAsset( CaveService.GAME_UNIT_TEXTURE_KEY );
        
        int caveViewId = viewSystem.getViewId( CaveService.CAVE_VIEW_NAME );
        tileGridSystem.deleteAllTileGrid( caveViewId );
        controllerSystem.deleteController( CaveService.CAVE_CONTROLLER_NAME );
        Position worldPosition = viewSystem.getView( caveViewId ).getWorldPosition();
        worldPosition.x = 0;
        worldPosition.y = 0;
    }

}
