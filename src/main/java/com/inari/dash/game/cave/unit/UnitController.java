package com.inari.dash.game.cave.unit;

import com.inari.dash.game.cave.CaveService;
import com.inari.firefly.component.attr.AttributeKey;
import com.inari.firefly.entity.EntityController;
import com.inari.firefly.sound.SoundSystem;
import com.inari.firefly.system.FFContext;

public abstract class UnitController extends EntityController {
    
    protected CaveService caveService;
    protected SoundSystem soundSystem;
    

    protected UnitController( int id, FFContext context ) {
        super( id, context );
        caveService = context.getComponent( CaveService.CONTEXT_KEY );
        soundSystem = context.getComponent( SoundSystem.CONTEXT_KEY ); 
    }

    @Override
    public AttributeKey<?>[] getControlledAttribute() {
        return null;
    }

}
