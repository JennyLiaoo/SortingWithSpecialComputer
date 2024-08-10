import java.util.Arrays;
import java.util.Collections;
import java.util.Random;

/**
 * The RAM class represents a finite size of memory on a computer.  The size is defined by the Computer Interface
 * class that contains all the constants.  All memory instances access the same HardDrive object. They will read
 * blocks (pages) of data from the Hard drive. They will also save blocks (pages) of data to the Harddrive.
 *
 * There is memory and harddrive dumping methods to allow for better debugging.
 *
 * @author Adam Drenth
 * @version 2.0
 */
public class RAM
{
    public final int MAX_SIZE = Computer.RAM_SIZE;
    protected int[] memory = new int[MAX_SIZE];
    private static HardDrive hd = null;
    private long numGets = 0;
    private long numSets = 0;

    /** Constructors */
    public RAM() {
        if(hd == null) {
            hd = new HardDrive();
        }

        for(int i = 0; i < memory.length; i++) {
            memory[i] = 0;
        }
    }

    /** Basic Getters/Setters */
    public int size() { return MAX_SIZE; }

    /** printStats
     * Prints the number of times the memory and hard drive have been accessed to allow for efficiency analysis
     */
    public void printStats() {
        System.out.println("Get: " + String.format("%1$4s",numGets) + "\tSet:  " + String.format("%1$4s",numSets));
        System.out.println("Read:" + String.format("%1$4s",hd.numReads) + "\tWrite:" + String.format("%1$4s",hd.numWrites));
    }

    /** get(index)
     * Gets a data value from memory (RAM)
     *
     * @param index the memory address in the RAM. It must beb between 0 and MAX_SIZE
     * @returns the integer value at the given index
     */
    public int get(int index) {
        if(index < 0 || index >= MAX_SIZE) {
            /*
            System.err.println("Segmentation Fault: loading outside of possible RAM index");
            System.err.println("Error caused in get(index)");
            System.exit(-1);
l
             */
        }
        numGets++;
        return memory[index];
    }

    /** set(index, value)
     * Sets a new value at the given index in memory (RAM)
     *
     * @param index the memory address in the RAM. It must 0 <= index < MAX_SIZE
     * @param value the value to be stored in memory
     */
    public void set(int index, int value) {
        if(index < 0 || index >= MAX_SIZE) {
            System.err.println("Segmentation Fault: loading outside of possible RAM index");
            System.err.println("Error caused in set(index)");
            System.exit(-1);
        }
        numSets++;
        memory[index] = value;
    }

    /** load(startIndex)
     * Loads a block of data from the hard drive and copies it into memory (RAM).  It is simple in that it will fill
     * itself with data from the hard drive.
     *
     * @param startIndex where to start loading from the hard drive, where startIndex + memory.length < hd.size()
     *        and startIndex >= 0
     */
    public void load(int startIndex) {
        if(startIndex < 0 || startIndex + memory.length > hd.size()) {
            /*
            System.err.println("Segmentation Fault: loading outside of possible hard drive index");
            System.err.println("Error caused in load(startIndex)");
            System.exit(-1);

             */
        }
        hd.read(startIndex, MAX_SIZE, memory, 0, true);
    }

    /** save(startIndex)
     * Saves a block of data to the hard drive.  It is simple in that it will dump the data into the
     * hard drive starting at the index given.
     *
     * @param startIndex where to start loading from in the hard drive, where startIndex + memory.length > hd.size()
     *        and startIndex >= 0
     */
    public void save(int startIndex) {
        if(startIndex < 0 || startIndex + memory.length > hd.size()) {
            System.err.println("Segmentation Fault: loading outside of possible hard drive index");
            System.err.println("Error caused in save(startIndex)");
            System.exit(-1);
        }
        hd.write(startIndex, MAX_SIZE, memory, 0, true);
    }

    /** partialLoad(memory, ramStart, hdStart, n)
     * Loads n data values into memory, starting at ramStart, from the hard drive starting at hdStart.
     *
     * @param memory is the RAM chip that is loading data from the hard drive.
     * @param ramStart is the start index on the RAM where 0 <= ramStart + n < MAX_SIZE
     * @param hdStart where to start loading from in the hard drive, where 0 <= hdStart + n < hd.size()
     * @param n is the number of elements to load from the hard drive into RAM
     */
    public void partialLoad(int ramStart, int hdStart, int n)
    {
        if(n < 0) {
            System.err.println("Segmentation Fault: bad array index");
            System.err.println("Error caused in partialLoad(memory, ramStart, hdStart, n)");
            System.exit(-1);
        }
        if(ramStart + n > MAX_SIZE) {
            System.err.println("Segmentation Fault: Loading outside of possible RAM index");
            System.err.println("Error caused in partialLoad(memory, ramStart, hdStart, n)");
            System.exit(-1);
        }
        if(hdStart + n > Computer.DRIVE_SIZE) {
            System.err.println("Segmentation Fault: Loading outside of possible hard drive index");
            System.err.println("Error caused in partialLoad(memory, ramStart, hdStart, n)");
            System.exit(-1);
        }

        hd.read(hdStart, n, memory, ramStart, true);
    }

    /** partialSave(memory, ramStart, hdStart, n)
     * Save n data elements from memory, starting at ramStart, to the hard drive starting at hdStart.
     *
     * @param ramStart is the start index on the RAM where 0 <= ramStart + n < MAX_SIZE
     * @param hdStart where to start saving to the hard drive, where 0 <= hdStart + n < hd.size()
     * @param n is the number of elements to save to the hard drive from RAM
     */
    public void partialSave(int ramStart, int hdStart, int n)
    {
        if(n < 0) {
            System.err.println("Segmentation Fault: bad array index");
            System.err.println("Error caused in partialSave(memory, ramStart, hdStart, n)");
            System.exit(-1);
        }
        if(ramStart + n > MAX_SIZE) {
            System.err.println("Segmentation Fault: Loading outside of possible RAM index");
            System.err.println("Error caused in partialSave(memory, ramStart, hdStart, n)");
            System.exit(-1);
        }
        if(hdStart + n > Computer.DRIVE_SIZE) {
            System.err.println("Segmentation Fault: Loading outside of possible hard drive index");
            System.err.println("Error caused in partialSave(memory, ramStart, hdStart, n)");
            System.exit(-1);
        }

        hd.write(hdStart, n, memory, ramStart, true);
    }

    private void memoryShiftLeft(RAM memory, int n)
    {
        for (int i = 0; i < Computer.RAM_SIZE - n; ++i) {
            memory.set(i, memory.get(i + n));
        }
    }

    /** memoryDump()
     * Prints out all the values in the memory (RAM).
     */
    public void memoryDump() {
        memoryDump(0, memory.length);
    }

    /** memoryDump(start, end)
     * Prints out all values in the memory between start and stopping before end.
     * @param start is the starting value.  0 < start < end
     * @param end is the value to stop at.  start < end <= MAX_SIZE
     */
    public void memoryDump(int start, int end) {
        StringBuilder output = new StringBuilder();
        for(int i = start; i < end; i++ ) {
            Formatter.print(memory[i]);
        }

        Formatter.flush();
    }

    /** driveDump()
     * Prints out all the values in the hard drive.
     */
    public void driveDump() {
        driveDump(0, hd.size());
    }

    /** driveDump(start, end)
     * Prints out all values in the hard drive between start and stopping before end.
     * @param start is the starting value.  0 < start < end
     * @param end is the value to stop at.  start < end <= MAX_SIZE
     */
    public void driveDump(int start, int end) {
        for(int i = start; i < end; i++) {
            Formatter.print(hd.cells[i]);
        }

        Formatter.flush();
    }

    /**
     * This represents a "Hard Drive" that is storing 1 billion integers. They
     * start by being completely scrambled.
     *
     * @author Mr. Drenth
     * @version 1.0
     */
    private class HardDrive
    {
        public static final int MAX_SIZE = Computer.DRIVE_SIZE;
        public int[] cells = new int[MAX_SIZE];
        public int numReads = 0;
        public int numWrites = 0;

        /**
         * HardDrive Constructor
         * It fills the hard drive and scrambles the data on it.
         */
        public HardDrive()
        {
            init();
        }

        /** init
         * Initializes the values in the hard drive and makes sure they are randomized.
         */
        private void init() {
            // Load with fully pseudorandom data
            Random gen = new Random();
            for(int i = 0; i < cells.length; i++) {
                cells[i] = gen.nextInt(MAX_SIZE);
            }

            scrambleDrive();
        }

        /** size()
         * returns the size of the hard drive
         */
        public int size() { return cells.length; }

        /**
         * Allows data to be read from the hard drive into memory
         *
         * @param  start  Where to start reading from on the hard drive
         * @param  numValues How many values to save into memory
         * @param  memory  The memory (RAM) that data is being saved into
         * @param  memStart Where to start saving the data into RAM
         */
        public void read(int start, int numValues, int[] memory, int memStart, boolean track)
        {
            if(numValues > Computer.RAM_SIZE) {
                System.err.println("RAM error: Segmentation Fault - Not enough memory for this operation");
                return;
            }

            for(int i = 0; i < numValues; i++) {
                memory[memStart + i] = cells[start + i];
            }

            if(track) { numReads++; }
        }

        /**
         * Allows data to be loaded onto the hard drive from memory
         *
         * @param  start  Where to start writing to on the hard drive
         * @param  numValues How many values to save into the hard drive
         * @param  memory  The memory (RAM) that data is being loaded
         * @param  memStart Where to start loading the data from the RAM
         *
         */
        public void write(int start, int numValues, int[] memory, int memStart, boolean track)
        {
            if(start + numValues > cells.length) {
                System.err.println("RAM error: Segmentation Fault - Not enough memory for this operation");
                return;
            }

            for(int i = 0; i < numValues; i++) {
                cells[start + i] = memory[memStart + i];
            }

            if(track) { numWrites++; }
        }

        /** scrambleDrive()
         * Randomizes the values in the drive according to the preset ORDER that is specified in the Computer
         * Pre: The drive is already filled with values in a randomized way.
         */
        private void scrambleDrive() {
            Random gen = new Random();
            if(Computer.DRIVE_STATE == Order.ORDERED) {             // Pre-sort the values so no real sorting is actually needed
                // Sort to all
                Arrays.sort(cells);
                return;
            } else if(Computer.DRIVE_STATE == Order.REVERSED) {     // Pre-sort in the worst way possible
                Arrays.sort(cells);
                for(int i = 0; i < cells.length/2; i++) {
                    int temp = cells[i];
                    cells[i] = cells[cells.length - i - 1];
                    cells[cells.length - i - 1] = temp;
                }
            } else if(Computer.DRIVE_STATE == Order.SHIFTED) {      // Kind of sorted but out of line
                Arrays.sort(cells);

                // Pick a random position to shift to/by
                int numShifts = gen.nextInt(cells.length);
                int[] buffer = new int[numShifts];

                for(int i = 0; i < numShifts; i++) {
                    buffer[i] = cells[i];
                }
                for(int i = numShifts; i < cells.length; i++) {
                    cells[i - numShifts] = cells[i];
                }
                int index = cells.length - numShifts - 1;
                for(int i = 0; i < buffer.length; i++) {
                    cells[index] = buffer[i];
                    index++;
                }
            } else if(Computer.DRIVE_STATE == Order.PARTIAL_RANDOM) { // Sort and then lightly shuffle
                Arrays.sort(cells);

                // Lightly sort the values. For 1 000 000 values, max 2000 numbers would be out of place
                int numCycles = (int)Math.sqrt(cells.length);
                for(int i = 0; i < numCycles; i++) {
                    int a = gen.nextInt(cells.length);
                    int b = gen.nextInt(cells.length);
                    int temp = cells[a];
                    cells[a] = cells[b];
                    cells[b] = temp;
                }
            }
            // Otherwise, the drive is already scrambled
        }
    }

}
