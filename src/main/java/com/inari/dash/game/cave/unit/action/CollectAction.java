package com.inari.dash.game.cave.unit.action;

import com.inari.dash.game.cave.unit.EUnit;
import com.inari.dash.game.cave.unit.UnitType;

public final class CollectAction extends UnitAction {

    protected CollectAction( int id ) {
        super( id );
    }

    @Override
    public final void action( int entityId ) {
        EUnit unit = entitySystem.getComponent( entityId, EUnit.TYPE_KEY );
        if ( unit.getUnitType() == UnitType.DIAMOND ) {
            caveService.collectDiamond();
        }
    }

}
