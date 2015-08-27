package com.inari.dash.unit;

import com.inari.firefly.Loadable;

public interface UnitLoader extends Loadable {
    
    UnitType unitType();
    
    int prefabId();

}
