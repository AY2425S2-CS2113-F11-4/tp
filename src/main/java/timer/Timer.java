package timer;

import java.util.ArrayList;

/**
 * Represents a timer that contains the timestamp of creation.
 * <p>This class provides the functionality of keeping track of time elapsed.</p>
 */
public class Timer {
    private long startTime;
    private final ArrayList<Long> timestamps = new ArrayList<>();

    public Timer(long startTime) {
        this.startTime = startTime;
    }

    /**
     * Calculates the difference in time between object creation and method called.
     *
     * @return the time duration in seconds
     */
    public long getDuration() {
        long currentTime = System.nanoTime();
        timestamps.add(currentTime);
        return ((currentTime - startTime)/1000000000);
    }
}
