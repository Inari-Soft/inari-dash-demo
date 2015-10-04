package com.inari.dash.game.action;

import com.inari.commons.lang.indexed.Indexer;

public enum ActionType implements IActionType {
    MOVE,
    EXPLODE,
    COLLECT_DIAMOND,
    END_CAVE;
    
    private final int index;
    private ActionType() {
        index = Indexer.nextObjectIndex( indexedObjectType() );
    }

    @Override
    public final int index() {
        return index;
    }
    
    @Override
    public final int type() {
        return index;
    }

    @Override
    public final Class<IActionType> indexedObjectType() {
        return IActionType.class;
    } 
}
