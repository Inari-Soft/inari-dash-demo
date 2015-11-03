package com.inari.dash.game.cave.unit.action;

import com.inari.commons.lang.indexed.IndexedObject;
import com.inari.commons.lang.indexed.Indexer;
import com.inari.firefly.action.Action;

public enum UnitActionType implements IndexedObject {
    MOVE( MoveAction.class ),
    EXPLODE( ExplosionAction.class ),
    COLLECT( CollectAction.class ), 
    FLASH( FlashAction.class ), 
    ;
    
    private final int index;
    private final Class<? extends Action> actionTypeClass;
    private UnitActionType( Class<? extends Action> actionTypeClass ) {
        index = Indexer.getIndexedObjectSize( Action.class ) + ordinal();
        this.actionTypeClass = actionTypeClass;
    }

    @Override
    public final int index() {
        return index;
    }
    
    public final int type() {
        return index;
    }

    public final Class<? extends Action> getActionTypeClass() {
        return actionTypeClass;
    }

    @Override
    public final Class<UnitActionType> indexedObjectType() {
        return UnitActionType.class;
    } 
}
