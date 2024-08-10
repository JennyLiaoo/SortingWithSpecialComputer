
/**
 *  Firstly, this algorithm organizes the individual RAM sticks using a tim/merge sort
 *  This is where a RAM chunk is split into 1s first. Then two sections of 1s are merged together. Now the sorted sections are of 2s, and we merge two sorted sections of 2s together to make sorted sections of 4s... etc
 *  it succeeds in doing this by merging the subsections onto a RAM stick, and then merging those bigger subsections to another RAM stick, moving back and forth as to not require a memory upgrade
 *  I also implemented code to handle the extra bit of data in the RAM that remains unsorted (because the RAM size is not necessairly a power of 2)
 *
 *  With the sorted RAM chunks, I then sort two adjacent RAM chunks to make a sorted chunk of size two RAM sticks.
 *  Then, I basically merge sort these chunks of RAM until the chunk size is as big as the hard drive size.
 *  I do this by first, shifting the RAM chunk positions so that they are in an altering order. Suppose that the As are a sorted RAM chunk, and the Bs are another sorted RAM chunk.
 *  A1 -> A1
 *  A2 -> B1
 *  A3 -> A2
 *  A4 -> B2
 *  B1 -> A3
 *  B2 -> B3
 *  B3 -> A4
 *  B4 -> B4
 *  We first take RAM chunks A1 and B1 and merge them together back into the Hard drive. One will eventually finish first, for example, A1, then we'd load A2 and continue merging.
 *  However, if B1 were to finish first, we would swap the ram chunks so that the pattern goes: B2, A2, B3, A3, B4, A4. This is because the next value we need to load is B2, and we need to make room in the harddrive for merging (and because I refuse to buy more RAM sticks).
 *  Eventually, the As and Bs would merge into a chunk of 8 ram sticks. It would then merge with another to form a sorted chunk of 16, then 32....
 *
 *  TimMerge but faster, can sort 100mil+ with 1000.
 *  However, the condition is that the hard drive size needs to be RAM.size * 2^x, where x is some positive integer. This is because this algorithm's subsections scale by a power of two.
 *
 * @author Jenny Liao
 * @version (probably like 16 by now)
 */
public class MergeMerge {
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

                for (int left = 0; left < section + left - 1; left += section) {            //now, we are sorting the individual subsection. the subsection starts at index "left," and increments until the end of sectionTim
                    if (left + section - 1 < Computer.RAM_SIZE) {                           //ensures that the incrementer does not go past the max RAM size if the final section is not "full" (i.e RAM size 10, section size: 4, 4, 2)
                        int right = section / 2;                                            //start of the second half of a section
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

        //merging two sticks
        for (int count = 0; count < Computer.DRIVE_SIZE - Computer.RAM_SIZE; ) {
            count = merge(count);

        }

        //sorting RAM chunks together by merging
        for(int section = 2; section < Computer.DRIVE_SIZE/Computer.RAM_SIZE; section*=2) {
        //chunk is defined as two sections that are to be sorted together.
            for(int chunk = 0; chunk < Computer.DRIVE_SIZE; chunk+=section*2*Computer.RAM_SIZE) {
                //shift rows so that the chunks follow an altering pattern
                int numShift = section-1;
                for(int i = 0; i < section*Computer.RAM_SIZE-Computer.RAM_SIZE; i+= Computer.RAM_SIZE) {  //looping through a chunk
                    stick1.load(chunk+i+section*Computer.RAM_SIZE);                              //value we want to shift

                        int shiftCounter = numShift;
                        for(int shift = chunk+i+section*Computer.RAM_SIZE-Computer.RAM_SIZE; shiftCounter > 0; shift-=Computer.RAM_SIZE) { //index of things being shifted
                                stick2.load(shift);
                                stick2.save(shift+Computer.RAM_SIZE);
                            shiftCounter--;
                        }
                    stick1.save(chunk+i+section*Computer.RAM_SIZE - numShift*Computer.RAM_SIZE);
                    numShift--;
                }

                    int stick1Counter = 0;
                    int stick2Counter = 0;
                    int index = chunk;                          //start index of hd Ram load
                    int startIndex = chunk;
                    stick1.load(index);
                    int memoryTracker = 1; //tracks the section of the next row of memory. (i.e if the next row are values of section 1/memory stick, memoryTracker=1
                    index+=Computer.RAM_SIZE;
                    stick2.load(index);
                    int loadCounter = 2;
                    int loadEndInFrom = 1;
                    int sortingIndex = chunk;

                while(sortingIndex < chunk+section*2*Computer.RAM_SIZE ) { //continue passing through until all values in the chunk are in place
                    //If no more values to load in a chunk
                    if (loadCounter > section * 2) {  //already loaded all ram in this chunk
                        if (loadEndInFrom == 1) {
                            while (stick1Counter < Computer.RAM_SIZE) {
                                stick1.partialSave(stick1Counter++, sortingIndex++, 1);
                            }
                        } else {
                            while (stick2Counter < Computer.RAM_SIZE) {
                                stick2.partialSave(stick2Counter++, sortingIndex++, 1);
                            }
                        }
                        break;
                    }

                    //sorting the chunks
                    while (stick1Counter < Computer.RAM_SIZE && stick2Counter < Computer.RAM_SIZE) {
                        if (stick1.get(stick1Counter) <= stick2.get(stick2Counter)) {
                            stick1.partialSave(stick1Counter++, sortingIndex++, 1);
                        } else {
                            stick2.partialSave(stick2Counter++, sortingIndex++, 1);
                        }
                    }
                    //if there are no more values in stick 1 and the next chunk of RAM is supposed to go into stick 1
                    if (stick1Counter == Computer.RAM_SIZE && memoryTracker == 1) {
                        stick1Counter = 0;
                        if (index + Computer.RAM_SIZE < Computer.DRIVE_SIZE) {
                            index += Computer.RAM_SIZE;
                        }
                        memoryTracker = 2;          //next chunk of RAM is supposed to go into stick2
                        stick1.load(index); //edit
                        loadCounter++;
                        loadEndInFrom = 2;
                    }
                    //if there are no more values in stick 1 and the next chunk of RAM is supposed to go into stick 2, then swap all the following RAM chunks so that the next chunk is supposed to go into stick 2
                    else if (stick1Counter == Computer.RAM_SIZE && memoryTracker == 2) {
                        //temporarly save extra values in stick2 back to hd
                        int tempHD = sortingIndex;
                        int temp2Counter = stick2Counter;
                        int numberExtra = 0;
                        while (temp2Counter < Computer.RAM_SIZE) {
                            stick2.partialSave(temp2Counter++, tempHD++, 1);
                            numberExtra++;
                        }

                        //swap chunks
                        for (int i = index + Computer.RAM_SIZE; i < 2 * section * Computer.RAM_SIZE + startIndex - Computer.RAM_SIZE; i += Computer.RAM_SIZE * 2) { //swapping code
                            swapChunks(i);
                        }
                        //load back in extra values into stick2
                        stick2.partialLoad(stick2Counter, sortingIndex, numberExtra);
                        memoryTracker = 2;                              //now the next value in memory is from section 1
                        if (index + Computer.RAM_SIZE < Computer.DRIVE_SIZE) {
                            index += Computer.RAM_SIZE;
                        }
                        loadEndInFrom = 2;
                        stick1Counter = 0;
                        stick1.load(index);
                        loadCounter++;
                    }

                    //if stick 2 finishes first and the next chunk of memory is supposed to go into stick2
                    if (stick2Counter == Computer.RAM_SIZE && memoryTracker == 2) {
                        stick2Counter = 0;
                        if (index + Computer.RAM_SIZE < Computer.DRIVE_SIZE) {
                            index += Computer.RAM_SIZE;
                        }
                        memoryTracker = 1;
                        stick2.load(index); //edit
                        loadCounter++;
                        loadEndInFrom = 1;
                    }
                    //if stick 2 finishes first and the next chunk of memory is not supposed to go into stick2
                    else if (stick2Counter == Computer.RAM_SIZE && memoryTracker == 1) {
                        //temporarly save extra values in stick1 back to hd
                        int tempHD = sortingIndex;
                        int temp1Counter = stick1Counter;
                        int numberExtra = 0;
                        while (temp1Counter < Computer.RAM_SIZE) {
                            stick1.partialSave(temp1Counter++, tempHD++, 1);
                            numberExtra++;
                        }
                        //swap the memory chunks
                        for (int i = index + Computer.RAM_SIZE; i < 2 * section * Computer.RAM_SIZE + startIndex - Computer.RAM_SIZE; i += Computer.RAM_SIZE * 2) { //swapping code
                            swapChunks(i);
                        }
                        //load back in extra values into stick1
                        stick1.partialLoad(stick1Counter, sortingIndex, numberExtra);
                        memoryTracker = 1;                  //now the next value in memory is from section 1
                        if (index + Computer.RAM_SIZE < Computer.DRIVE_SIZE) {
                            index += Computer.RAM_SIZE;
                        }
                        loadEndInFrom = 1;
                        stick2Counter = 0;
                        stick2.load(index);
                        loadCounter++;
                    }
                }
            }
        }



        // Prints summary
        stick1.printStats();
        stick1.driveDump();
     //   System.out.println("summary done");
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
    /**
     * Merges two chunks of memory together by comparison, and partial loading the smaller value into the hard drive.
     * Condition: they need to be sorted before merging
     * @param hdIndex, int
     */
    public static int merge (int hdIndex) {
        int i = 0;
        int j = 0;
        stick1.load(hdIndex);
        stick2.load(hdIndex + Computer.RAM_SIZE);
        //sort two sticks back in memory
        while (i < Computer.RAM_SIZE && j < Computer.RAM_SIZE) {
            if (stick1.get(i) <= stick2.get(j)) {
                stick1.partialSave(i++, hdIndex++, 1);
            } else {
                stick2.partialSave(j++, hdIndex++, 1);
            }
        }
        while (i < Computer.RAM_SIZE) {
            stick1.partialSave(i++, hdIndex++, 1);
        }
        while (j < Computer.RAM_SIZE) {
            stick2.partialSave(j++, hdIndex++, 1);
        }
        return hdIndex;
    }
    /**
     * Merges two chunks of memory together by comparison, and partial loading the smaller value into the hard drive.
     * Condition: they need to be sorted before merging
     * @param i, int
     */
    public static void swapChunks (int i) {
        stick1.load(i);
        stick2.load(i + Computer.RAM_SIZE);
        stick2.save(i);
        stick1.save(i + Computer.RAM_SIZE);
    }
}
