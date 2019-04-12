package server.resources;

import server.data.Computer;

/**
 * A simple, simple interface to allow other classes to listen to any changes in data,
 * as well to receive instant updates
 *
 * This is called by DataHandler's alertSubscribers() method.
 *
 * Subscribers currently: s/App, s/ConnectionHandler
 */
public interface ComputerSubscriber {
    void updateComputer( Computer data );
}
