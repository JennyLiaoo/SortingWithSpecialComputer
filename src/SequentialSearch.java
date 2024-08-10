import java.util.Scanner;

/**
 * This is the drive class where you will implement a storing algorithm where you have a VERY large data set and
 * limit memory to access all the data.  The Computer interface contains the constants that you can adjust to allow
 * you to adjust the size of the memory and the hard drive for testing purposes. However, your final work should
 * be able to sort 1 billion (1 000 000 000) integers when you only have two sticks of RAM that can only 1000
 * integers.
 *
 * You will be assessed on correctness and effeciency.
 *
 * YOU ARE NOT ALLOWED TO MODIFY THE RAM CLASS!!!!
 *
 * @author Jenny
 * @version 1
 */
public class SequentialSearch
{
    static RAM memory = new RAM();
    static RAM stick2 = new RAM();


    public static void main(String[] args) // Assuming that RAM_SIZE is a divisor of DRIVE_SIZE
    {
        Scanner s = new Scanner(System.in);

        memory.driveDump();

        int value = s.nextInt();
        s.nextLine();


        for(int i = 0; i < Computer.DRIVE_SIZE; i+=Computer.RAM_SIZE) {
            memory.load(i);
            int index = Sequential(value);
            if (index != -1) {
                System.out.println("Found value at: " + (index+i));
                break;
            }
        }
        stick2.printStats();
    }

    /**
     * @return index if found
     */
    public static int Sequential (int searchFor) {
        for(int i = 0; i < Computer.RAM_SIZE; i++) {
            if(memory.get(i) == searchFor) {
                return i;
            }
        }
        return -1;
    }

}
