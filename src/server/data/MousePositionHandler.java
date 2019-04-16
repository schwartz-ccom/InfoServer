package server.data;

import server.ui.App;

import java.awt.*;
import java.awt.event.AWTEventListener;

public class MousePositionHandler implements AWTEventListener {
    @Override
    public void eventDispatched( AWTEvent event ) {
        Point mouseLocation = MouseInfo.getPointerInfo().getLocation();
        App.getInstance().updateMousePosition( mouseLocation.x, mouseLocation.y );
    }
}
