package com.inari.dash.tests;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.files.FileHandle;
import com.inari.firefly.libgdx.GdxFFTestAdapter;
import com.inari.firefly.system.FFContext;

public class FindCaveConfigFilesTest extends GdxFFTestAdapter {
    
    @Override
    public void initTest( FFContext context ) {
        FileHandle[] list = Gdx.files.classpath( "assets/games/" ).list();
        System.out.println( list );
    }

    @Override
    public String getTitle() {
        return "TextureDisplayTest";
    }
    
    public static void main (String[] arg) {
        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.resizable = false;
        config.width = 800;
        config.height = 600;
        new LwjglApplication( new FindCaveConfigFilesTest(), config );
    }

}
