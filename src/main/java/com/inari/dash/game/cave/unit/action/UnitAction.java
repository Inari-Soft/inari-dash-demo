package com.inari.dash.game.cave.unit.action;

import com.inari.dash.game.cave.CaveSystem;
import com.inari.firefly.FFInitException;
import com.inari.firefly.action.Action;
import com.inari.firefly.entity.EntitySystem;
import com.inari.firefly.system.FFContext;

public abstract class UnitAction extends Action {
    
    protected FFContext context;
    protected CaveSystem caveService;
    protected EntitySystem entitySystem;

    protected UnitAction( int id ) {
        super( id );
    }
    
    @Override
    public final void init( FFContext context ) throws FFInitException {
        this.context = context;
        entitySystem = context.getSystem( EntitySystem.SYSTEM_KEY );
        caveService = context.getSystem( CaveSystem.SYSTEM_KEY );
    }
    
    @Override
    public final void dispose( FFContext context ) {
    }


}
