package com.inari.dash.game.cave.unit.action;

import com.inari.commons.event.IEventDispatcher;
import com.inari.dash.game.cave.CaveService;
import com.inari.firefly.action.Action;
import com.inari.firefly.entity.EntitySystem;
import com.inari.firefly.system.FFContext;
import com.inari.firefly.system.FFInitException;

public abstract class UnitAction extends Action {
    
    protected CaveService caveService;
    protected EntitySystem entitySystem;
    protected IEventDispatcher eventDispatcher;

    protected UnitAction( int id ) {
        super( id );
    }
    
    @Override
    public final void init( FFContext context ) throws FFInitException {
        entitySystem = context.getComponent( EntitySystem.CONTEXT_KEY );
        caveService = context.getComponent( CaveService.CONTEXT_KEY );
        eventDispatcher = context.getComponent( FFContext.EVENT_DISPATCHER );
    }
    
    @Override
    public final void dispose( FFContext context ) {
    }


}
