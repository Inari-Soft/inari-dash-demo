package com.inari.dash.game.unit;

import com.inari.firefly.Disposable;
import com.inari.firefly.Loadable;

public interface UnitHandle extends Loadable, Disposable {
    
    UnitType type();

}
