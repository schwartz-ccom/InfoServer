package server.resources;

import server.types.Computer;

/**
 * A simple, simple interface to allow other classes to listen to any changes in data,
 * as well to receive instant updates
 *
 * This is called by DataHandler's alertSubscribers() method.
 *
 * Subscribers currently: s/App, s/NetworkHandler
 */
public interface ComputerSubscriber {
    void updateComputer( Computer data );
}
