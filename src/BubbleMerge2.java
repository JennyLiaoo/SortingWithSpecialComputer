
/**
 * A combination of a bubble sort and a merge sort to completely sort the given data.
 * With a ram size of 100, it can sort up to 500,000 integers (although very slowly).
 * It first loads a chunk of data into each individual RAM stick, and sorts them individually with a bubble sort (worst case: O(N^2), best case: O(N))
 * Then, the pieces of data in the memory are sorted together (merged) back into the hard drive (all cases: O(N))
 * Lastly, we also bubble sort the data by comparing the smallest values of each chunk of memory to allow for a more sorted set of data to approach (worst case O(M^2), best case: O(N))
 * I also include a "checkOrder" method to verify if everything has been sorted: O(N).
 *
 * Removes all non essential instructions from thr loop (i.e no longer checking if certain data is sorted because we KNOW it is)
 * Can do 500,000 with 100. Much faster than version 1
 *
 * @author Jenny Liao
 * @version 5
 */
public class BubbleMerge2 {
    static RAM stick1 = new RAM();
    static RAM stick2 = new RAM();

    public static void main(String[] args) {
        stick1.driveDump();
        //organize one stick
        for (int hdIndex = 0; hdIndex < Computer.DRIVE_SIZE - Computer.RAM_SIZE; hdIndex += Computer.RAM_SIZE*2) {
            stick1.load(hdIndex);
            bubbleSort(1);
            stick1.save(hdIndex);
            stick2.load(hdIndex + Computer.RAM_SIZE);
            bubbleSort(2);
            stick2.save(hdIndex+Computer.RAM_SIZE);
        }
        while (!checkOrder()) {
            for (int hdIndex = 0; hdIndex < Computer.DRIVE_SIZE - Computer.RAM_SIZE; hdIndex += Computer.RAM_SIZE) {
                merge(hdIndex);
            }
        }
        swapSticks();

        // Prints the stats
        stick1.printStats();
        stick1.driveDump();
        System.out.println("summary done");
    }

    /**
     * Swaps the chunks of memory based on the smallest value (first value) in the chunk using a bubble sort. This is used to create a generally more sorted array.
     */
    public static void swapSticks () {
        int smallestValueIndex = 0;
        int checkForSwap = 0;
        boolean switchDone = false;
        while (!switchDone) {   //bubble sorting ram sticks
            while (smallestValueIndex < Computer.DRIVE_SIZE - Computer.RAM_SIZE*2) {
                stick1.load(smallestValueIndex);
                stick2.load(smallestValueIndex + Computer.RAM_SIZE);
                if (stick2.get(0) < stick1.get(0)) {   //compare the smallest value of one with another.
                    stick2.save(smallestValueIndex);
                    stick1.save(smallestValueIndex + Computer.RAM_SIZE);
                    checkForSwap++;
                }
                smallestValueIndex += Computer.RAM_SIZE;
            }
            smallestValueIndex = 0;
            if (checkForSwap == 0) {    //if there have been no swaps, then stop (fully sorted)
                break;
            }
            checkForSwap = 0;           //resets
        }
    }
    /**
     * Checks the current order of the hard drive to see if we need to continue sorting
     */
    public static boolean checkOrder () {
        for(int i = 0; i < Computer.DRIVE_SIZE-Computer.RAM_SIZE*2; i+=Computer.RAM_SIZE) {
            stick1.load(i);
            stick2.load(i+Computer.RAM_SIZE);
            if(stick2.get(0) < stick1.get(Computer.RAM_SIZE-1)) {       //if the smallest value of stick2 is bigger than the biggest value of stick1
                //System.out.println("unsorted"+ i);
                return false;
            }
        }
        return true;
    }
    /**
     * Bubble sort: where we loop through each chunk of memory
     * Continuosly swaps larger and smaller values until we reach the end of the chunk of memory.
     * If memory is not sorted, starts swapping values from the beginning once again
     * Can sort fully randoomized memory, but is more suitable for smaller sets of data.
     * @param stick, int
     */
    public static void bubbleSort (int stick) {
        for (int i = 0; i < stick1.size() -1; i++) {
            for (int j = 0; j < stick1.size() - i -1; j++) {
                if(stick==1) {
                    if (stick1.get(j) > stick1.get(j + 1)) {
                        int temp = stick1.get(j);
                        stick1.set(j, stick1.get(j + 1));
                        stick1.set(j + 1, temp);
                    }
                }
                else {
                    if (stick2.get(j) > stick2.get(j + 1)) {
                        int temp = stick2.get(j);
                        stick2.set(j, stick2.get(j + 1));
                        stick2.set(j + 1, temp);
                    }
                }
            }
        }
    }
    /**
     * Merges two chunks of memory together by comparison, and partial loading the smaller value into the hard drive.
     * Condition: they need to be sorted before merging
     * @param hdIndex, int
     */
    public static void merge (int hdIndex) {
        int stick1Index = 0;        //increments the index of what is being saved back to the hard drive
        int stick2Index = 0;
        int hdCounter = hdIndex;
        stick1.load(hdIndex);
        stick2.load(hdIndex+Computer.RAM_SIZE);
        //traverses each chunk of memory, comparing their values and saving whichever one is smaller
        while (stick1Index < stick1.size() && stick2Index < stick1.size()) {
            if (stick1.get(stick1Index) <= stick2.get(stick2Index)) {
                stick1.partialSave(stick1Index++, hdCounter++, 1);
            } else {
                stick2.partialSave(stick2Index++, hdCounter++, 1);
            }
        }
        //accounts for any remaining data on a stick that has nothing else to be compared to
        while (stick1Index < stick1.size()) {
            stick1.partialSave(stick1Index++, hdCounter++, 1);
        }
        while (stick2Index < stick2.size()) {
            stick2.partialSave(stick2Index++, hdCounter++, 1);
        }
    }


}
