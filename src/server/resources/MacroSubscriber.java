package server.resources;

import server.data.macro.Macro;

/**
 * A simple, simple interface to allow other classes to listen to any changes in data,
 * as well to receive instant updates
 *
 * This is called by MacroHandler's alertSubscribers() method.
 *
 * Subscribers currently: s/App
 */
public interface MacroSubscriber {
    void updateMacros( Macro[] macros );
}
