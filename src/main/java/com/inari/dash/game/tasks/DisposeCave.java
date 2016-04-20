package com.inari.dash.game.tasks;

import com.inari.commons.geom.Position;
import com.inari.dash.game.cave.CaveSystem;
import com.inari.dash.game.cave.unit.Unit;
import com.inari.dash.game.cave.unit.UnitType;
import com.inari.firefly.asset.AssetSystem;
import com.inari.firefly.control.ControllerSystem;
import com.inari.firefly.control.task.Task;
import com.inari.firefly.entity.EntitySystem;
import com.inari.firefly.graphics.tile.TileGridSystem;
import com.inari.firefly.graphics.view.ViewSystem;
import com.inari.firefly.prototype.Prototype;
import com.inari.firefly.scene.SceneSystemEvent;

public final class DisposeCave extends Task {

    protected DisposeCave( int id ) {
        super( id );
    }

    @Override
    public final void runTask() {
        AssetSystem assetSystem = context.getSystem( AssetSystem.SYSTEM_KEY );
        TileGridSystem tileGridSystem = context.getSystem( TileGridSystem.SYSTEM_KEY );
        ControllerSystem controllerSystem = context.getSystem( ControllerSystem.SYSTEM_KEY );
        EntitySystem entitySystem = context.getSystem( EntitySystem.SYSTEM_KEY );
        ViewSystem viewSystem = context.getSystem( ViewSystem.SYSTEM_KEY );

        context.notify( new SceneSystemEvent( CaveSystem.CAVE_INIT_SCENE_NAME, SceneSystemEvent.EventType.DELETE ) );
        
        // dispose all units
        for ( UnitType unitType : UnitType.values() ) {
            context.getSystemComponent( Prototype.TYPE_KEY, unitType.ordinal(), Unit.class ).dispose( context );
        }
        
        entitySystem.deleteAllActive();
        assetSystem.disposeAsset( CaveSystem.GAME_UNIT_TEXTURE_NAME );
        
        int caveViewId = viewSystem.getViewId( CaveSystem.CAVE_VIEW_NAME );
        tileGridSystem.deleteAllTileGrid( caveViewId );
        controllerSystem.deleteController( CaveSystem.CAVE_CONTROLLER_NAME );
        Position worldPosition = viewSystem.getView( caveViewId ).getWorldPosition();
        worldPosition.x = 0;
        worldPosition.y = 0;
    }

}
