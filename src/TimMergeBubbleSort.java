import com.sun.org.apache.xerces.internal.impl.XMLEntityManager;

/**
 * Firstly, this algorithm organizes the individual RAM sticks using a tim/merge sort
 * This is where a RAM chunk is split into 1s first. Then two sections of 1s are merged together. Now the sorted sections are of 2s, and we merge two sorted sections of 2s together to make sorted sections of 4s... etc
 * it succeeds in doing this by merging the subsections onto a RAM stick, and then merging those bigger subsections to another RAM stick, moving back and forth as to not require a memory upgrade
 * I also implemented code to handle the extra bit of data in the RAM that remains unsorted (because the RAM size is not necessairly a power of 2)
 * Then it merges two adjacent RAM chunks together, and it iterates through the entire hard drive continuosly until all the data is sorted
 *
 * Tim6 but faster, can sort 10mil with 1000
 *
 * @author Jenny Liao
 * @version 8
 */
public class TimMergeBubbleSort
{
    static RAM stick1 = new RAM();
    static RAM stick2 = new RAM();



    //would actually just update all the indexes instead of updating all the values
    public static void main(String[] args) {
        for (int hdCounter = 0; hdCounter < Computer.DRIVE_SIZE; hdCounter+=Computer.RAM_SIZE) {    //do for all
            stick1.load(hdCounter);
            //counts to determine which memory stick is being saved into (i.e when even, data from memory is saving into stick2. when odd, data from stick2 is being saved into memory)
            int stickCounter = 0;
            //the size that the RAM is split into (in powers of 2). Once the whole RAM stick is organized by 2s, it will organize the smaller sections by 4s... until the small section is as big as the RAM stick)
            for (int section = 2; section <= Computer.RAM_SIZE; section *= 2) {
                int positionInRAM = 0;

                for (int left = 0; left < section + left - 1; left += section) {                    //now, we are sorting the individual subsection. the subsection starts at index "left," and increments until the end of sectionTim
                    if (left + section - 1 < Computer.RAM_SIZE) {                           //ensures that the incrementer does not go past the max RAM size if the final section is not "full" (i.e RAM size 10, section size: 4, 4, 2)
                        int right = section / 2;                     //start of the second half of a section
                        int leftCounter = left;
                        int rightCounter = left + right;
                        int endPointFirstHalf = rightCounter;
                        int endPointSecondHalf = left + section;

                        //values being saved into stick 2
                        if (stickCounter % 2 == 0) {
                            positionInRAM = organizeIntoStick2(leftCounter,endPointFirstHalf, rightCounter, endPointSecondHalf, positionInRAM);
                        }
                        //same as above, just now the values are being saved into stick 1
                        else {
                            positionInRAM = organizeIntoStick1(leftCounter,endPointFirstHalf, rightCounter, endPointSecondHalf, positionInRAM);
                        }
                    }
                    //there are (majority of the time) some remaining bits of data that are unsorted in the RAM stick (as the subsections are in the powers of two). This code integrates them back in.
                    else {
                        int extra = Computer.RAM_SIZE % section;                          //number of extra unsorted values
                        int positionOfExtra = Computer.RAM_SIZE - extra;
                        int posExtraCounter = positionOfExtra;
                        int sortedEnd = positionOfExtra;
                        int sortedCounter = Computer.RAM_SIZE - extra - section;          //start position of the last sorted section of memory
                        int ramCounter = sortedCounter;

                       //organizing values into stick1
                        if (stickCounter % 2 == 0) {
                            while (posExtraCounter < Computer.RAM_SIZE && section == 2) {
                                stick2.set(posExtraCounter, stick1.get(posExtraCounter));        //load the extra values into stick2
                                posExtraCounter++;
                            }
                            posExtraCounter = positionOfExtra;
                            //sorting the extra bit into the last sorted section (merging)
                            organizeIntoStick1(posExtraCounter, Computer.RAM_SIZE, sortedCounter, sortedEnd, ramCounter);
                            int temp = Computer.RAM_SIZE - extra - section;
                            while (temp < Computer.RAM_SIZE) {
                                stick2.set(temp, stick1.get(temp));
                                temp++;
                            }
                        }
                        //same as above, just for stick 2
                        else {
                            //loading extras into memory
                            while (posExtraCounter < Computer.RAM_SIZE && section == 2) {
                                stick1.set(posExtraCounter, stick2.get(posExtraCounter));
                                posExtraCounter++;
                            }
                            posExtraCounter = positionOfExtra;
                            organizeIntoStick2(posExtraCounter, Computer.RAM_SIZE, sortedCounter, sortedEnd, ramCounter);

                            int temp = Computer.RAM_SIZE - extra - section;
                            while (temp < Computer.RAM_SIZE) {
                                stick1.set(temp, stick2.get(temp));
                                temp++;
                            }
                        }
                        break;
                    }
                }
                stickCounter++;      //counter increments, signifying that the other memory stick is now empty
            }
            if ((stickCounter + 1) % 2 == 0) {
                stick1.save(hdCounter);
            } else {
                stick2.save(hdCounter);
            }
        }

        while(!checkOrder()) {
            for(int count = 0; count < Computer.DRIVE_SIZE; count+=Computer.RAM_SIZE) {
                merge(count);
            }
        }
        swapSticks();

        // Prints summary
        stick1.driveDump();
        System.out.println("summary done");
        stick1.printStats();
    }
    /**
     * Merges two chunks of memory together by comparison, and partial loading the smaller value into the hard drive.
     * Condition: they need to be sorted before merging
     * @param hdIndex, int
     */
    public static void merge (int hdIndex) {
        int i = 0;
        int j = 0;
        int k = hdIndex - Computer.RAM_SIZE;
        if (k >= 0) {
            stick1.load(k);
            stick2.load(hdIndex);
            //sort two sticks back in memory
            while (i < Computer.RAM_SIZE && j < Computer.RAM_SIZE) {
                if (stick1.get(i) <= stick2.get(j)) {
                    stick1.partialSave(i++, k++, 1);
                } else {
                    stick2.partialSave(j++, k++, 1);
                }
            }
            while (i < Computer.RAM_SIZE) {
                stick1.partialSave(i++, k++, 1);
            }
            while (j < Computer.RAM_SIZE) {
                stick2.partialSave(j++, k++, 1);
            }
        }
    }
    /**
     * Checks the current order of the hard drive to see if we need to continue sorting
     */
    public static boolean checkOrder () {
        for(int i = 0; i < Computer.DRIVE_SIZE-Computer.RAM_SIZE*2; i+=Computer.RAM_SIZE) {
            stick1.load(i);
            stick2.load(i+Computer.RAM_SIZE);
            if(stick2.get(0) < stick1.get(Computer.RAM_SIZE-1)) {
                System.out.println("unsorted"+ i);
                return false;
            }
            for(int j = 1; j < Computer.RAM_SIZE; j++){
                if(stick1.get(j) < stick1.get(j-1) ) {
                    return false;
                }
                else if(stick2.get(j) < stick2.get(j-1)) {
                    return false;
                }
            }
        }
        return true;
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
     * Merges values from one RAM stick into another
     * @param end1, int
     * @param start1, int
     * @param end2, int
     * @param posRAM, int
     * @param start2, int
     */
    public static int organizeIntoStick2 (int start1, int end1, int start2, int end2, int posRAM) {
        int positionInRAM = posRAM;

        while (start1 < end1 && start2 < end2) {
            //Comparing the values
            if (stick1.get(start1) <= stick1.get(start2)) {
                stick2.set(positionInRAM++, stick1.get(start1++));
            } else {
                stick2.set(positionInRAM++, stick1.get(start2++));
            }
        }
        //loading in all remaining values if one half of a section is already completely loaded
        while (start1 < end1) {
            stick2.set(positionInRAM++, stick1.get(start1++));
        }
        while (start2 < end2) {
            stick2.set(positionInRAM++, stick1.get(start2++));
        }
        return positionInRAM;
    }
    /**
     * Merges values from one RAM stick into another
     * @param end1, int
     * @param start1, int
     * @param end2, int
     * @param posRAM, int
     * @param start2, int
     */
    public static int organizeIntoStick1 (int start1, int end1, int start2, int end2, int posRAM) {
        int positionInRAM = posRAM;

        while (start1 < end1 && start2 < end2) {
            if (stick2.get(start1) <= stick2.get(start2)) {
                stick1.set(positionInRAM++, stick2.get(start1++));
            } else {
                stick1.set(positionInRAM++, stick2.get(start2++));
            }
        }
        //adding in any remaining items in the subsections
        while (start1 < end1) {
            stick1.set(positionInRAM++, stick2.get(start1++));
        }
        while (start2 < end2) {
            stick1.set(positionInRAM++, stick2.get(start2++));
        }
        return positionInRAM;
    }

}
