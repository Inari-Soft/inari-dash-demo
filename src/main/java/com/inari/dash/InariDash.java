
package com.inari.dash;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.inari.dash.game.GameService;
import com.inari.dash.game.selection.GameSelectionService;
import com.inari.firefly.app.FFApplicationManager;
import com.inari.firefly.libgdx.GDXConfiguration;
import com.inari.firefly.libgdx.GDXFFApplicationAdapter;
import com.inari.firefly.system.FFContextImpl.InitMap;

public class InariDash extends GDXFFApplicationAdapter {
    
    @Override
    public String getTitle() {
        return "Inari Dash";
    }

    @Override
    protected final FFApplicationManager getApplicationManager() {
        return new GameService();
    }
    
    public static void main (String[] arg) {
        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.resizable = false;
        config.width = 800;
        config.height = 600;
        new LwjglApplication( new InariDash(), config );
    }

    @Override
    protected InitMap getInitMap() {
        InitMap initMap = GDXConfiguration.getInitMap();
        initMap.put( GameSelectionService.CONTEXT_KEY, GameSelectionService.class );
        return initMap;
    }

}
