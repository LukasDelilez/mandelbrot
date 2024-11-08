public class PixelBuffer {

    private final int buffers;
    private final int bufferSize;

    private final PixelUpdate[][]updates;
    private final int[] readIndexes;
    private final int[] writeIndexes;

    private volatile boolean isComplete = false;

    public PixelBuffer(int buffers, int bufferSize) {
        this.buffers = buffers;
        this.bufferSize = bufferSize;
        updates = new PixelUpdate[buffers][bufferSize];
        readIndexes = new int[buffers];
        writeIndexes = new int[buffers];
    }

    public void offer(PixelUpdate update, int threadId) {
        while (true) {
            int writeIndex = writeIndexes[threadId];
            if(writeIndex - readIndexes[threadId] < bufferSize) {
                updates[threadId][writeIndex % bufferSize] = update;
                writeIndexes[threadId]++;
                return;
            }
            Thread.yield();
        }
    }

    public PixelUpdate poll() {
        for (int i = 0; i < buffers; i++) {
            if(readIndexes[i] < writeIndexes[i]) {
                PixelUpdate update = updates[i][readIndexes[i] % bufferSize];
                readIndexes[i]++;
                return update;
            }
        }
        return null;
    }

    public boolean isEmpty() {
        if(isComplete) return true;

        for (int i = 0; i < buffers; i++) {
            if(readIndexes[i] < writeIndexes[i]) return false;
        }

        return true;
    }

    public void setComplete() {
        isComplete = true;
    }

}
