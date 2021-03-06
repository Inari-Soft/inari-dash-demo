
package com.inari.dash;

import java.util.Collection;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.inari.dash.game.GameSystem;
import com.inari.dash.game.tasks.InitGameWorkflow;
import com.inari.firefly.component.attr.AttributeKey;
import com.inari.firefly.control.action.ActionSystem;
import com.inari.firefly.control.task.Task;
import com.inari.firefly.control.task.TaskSystemEvent;
import com.inari.firefly.control.task.TaskSystemEvent.Type;
import com.inari.firefly.graphics.text.TextSystem;
import com.inari.firefly.libgdx.GdxFFApplicationAdapter;
import com.inari.firefly.libgdx.GdxFireflyApp;
import com.inari.firefly.prototype.PrototypeSystem;
import com.inari.firefly.scene.SceneSystem;
import com.inari.firefly.system.FFContext;

public class InariDash extends GdxFFApplicationAdapter {

    @Override
    public String getTitle() {
        return "Inari Dash";
    }
    
    public static void main (String[] arg) {
        try {
            LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
            config.resizable = false;
            config.width = 800;
            config.height = 600;
            new LwjglApplication( new InariDash(), config );
        } catch ( Throwable t ) {
            t.printStackTrace();
        }
    }

    @Override
    protected Collection<AttributeKey<?>> getDynamicAttributes() {
        Collection<AttributeKey<?>> dynamicAttributes = super.getDynamicAttributes();
        dynamicAttributes.add( GdxFireflyApp.DynamicAttributes.TEXTURE_COLOR_CONVERTER_NAME );
        return dynamicAttributes;
    }

    @Override
    protected final void init( FFContext context ) {
        // TODO load from attribute map
        Configuration configuration = new Configuration();
        context.setContextComponent( configuration );
        context.loadSystem( ActionSystem.SYSTEM_KEY );
        context.loadSystem( GameSystem.SYSTEM_KEY );
        context.loadSystem( SceneSystem.SYSTEM_KEY );
        context.loadSystem( TextSystem.SYSTEM_KEY );
        context.loadSystem( PrototypeSystem.SYSTEM_KEY );
        
        int startTaskId = context.getComponentBuilder( Task.TYPE_KEY )
            .set( Task.REMOVE_AFTER_RUN, true )
            .set( Task.NAME, InitGameWorkflow.TASK_NAME )
            .build( InitGameWorkflow.class );
        context.notify( new TaskSystemEvent( Type.RUN_TASK, startTaskId ) );
    }

    @Override
    protected void resize( int width, int height, FFContext context ) {
        // TODO Auto-generated method stub
    }

    @Override
    protected void pause( FFContext context ) {
        // TODO Auto-generated method stub
    }

    @Override
    protected void resume( FFContext context ) {
        // TODO Auto-generated method stub
    }


}
