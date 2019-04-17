package server.data;

import server.ui.App;

import java.awt.*;
import javax.swing.Timer;

public class MousePositionHandler{

    private static int delay = 50;
    private static Timer t;

    public MousePositionHandler(){

    }

    public static void startTracker( ) {
        if ( t == null )
            make();
        t.start();
    }
    public static void stopTracker(){
        if ( t.isRunning() )
            t.stop();
    }
    public static Point getMouseLoc(){
        return MouseInfo.getPointerInfo().getLocation();
    }

    private static void make(){
        t = new Timer( delay, actionEvent -> {
            Point mouseLocation = MouseInfo.getPointerInfo().getLocation();
            App.getInstance().updateMousePosition( mouseLocation.x, mouseLocation.y );
        });
    }
}
