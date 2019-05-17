package server.types;

import res.Out;
import server.network.info.Message;

import java.util.Arrays;

/**
 * A queue for messages. I don't want to have Java's semi inefficient set up,
 * so we're making our own. Plus, why not?
 *
 * FIFO queue
 */
public class MessageQueue {

    // Declare a default amount of message slots for the queue.
    // This shouldn't ever be exceeded, since the commands are sent
    // instantly and it would be hard to actually have a backlog.
    private int slots = 10;
    private int size = 0;

    private Message[] queue;

    /**
     * Constructor that assigns the queue a default amount of spots.
     */
    public MessageQueue() {
        queue = new Message[ slots ];

        // Fill the array with Null
        Arrays.fill( queue, null );
    }

    /**
     * Add a message to the queue
     * @param toAdd the Message to add
     */
    public void addMessage( Message toAdd ) {
        queue[ size ] = toAdd;
        size += 1;
    }

    /**
     * Pop the next Message
     * @return the next Message
     */
    public Message pop() {

        // Get the first message, since FIFO
        Message popped = queue[ 0 ];

        // Move all the messages down a slot
        System.arraycopy( queue, 1, queue, 0, slots - 1 );

        // Set the last spot as null
        queue[ slots - 1 ] = null;

        // Reduce the size
        size -= 1;

        // Return the message
        return popped;
    }

    /**
     * Get how many items are currently in the queue
     * @return the item count as int
     */
    public int getSize(){
        return size;
    }
}
