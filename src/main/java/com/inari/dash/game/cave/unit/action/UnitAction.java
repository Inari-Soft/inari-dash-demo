package com.inari.dash.game.cave.unit.action;

import com.inari.dash.game.cave.CaveSystem;
import com.inari.firefly.FFInitException;
import com.inari.firefly.control.action.Action;
import com.inari.firefly.entity.EntitySystem;

public abstract class UnitAction extends Action {
    
    protected CaveSystem caveService;
    protected EntitySystem entitySystem;

    protected UnitAction( int id ) {
        super( id );
    }
    
    @Override
    public final void init() throws FFInitException {
        entitySystem = context.getSystem( EntitySystem.SYSTEM_KEY );
        caveService = context.getSystem( CaveSystem.SYSTEM_KEY );
    }

}
