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
 * @author (your name)
 * @version (date of completion)
 */
public class DriverLinearSearchExample
{
    static RAM memory = new RAM();
    static RAM stick2 = new RAM();

    public static void main(String[] args)
    {
        Scanner input = new Scanner(System.in);
        System.out.println("Please tell me what value you would like to find:");
        memory.driveDump();
        System.out.println();
        int key = input.nextInt(); input.nextLine();

        int startIndex = Computer.DRIVE_SIZE - Computer.RAM_SIZE;
        boolean found = false;
        for(int i = 0; i < Computer.DRIVE_SIZE && !found; i += Computer.RAM_SIZE) {
            memory.load(i); // Load is first chunk of data from HD
            for(int j = 0; j < Computer.RAM_SIZE && !found; j++) {
                if(memory.get(j) == key) {
                    System.out.println("Memory address = " + i + j);
                    found = true;
                }
            }
        }
        if(!found) {
            System.out.println("Value does not exist on H/D");
        }
        /*System.out.println("Printing all values in Hard Drive");
        memory.driveDump();

        // Load up Memory
        memory.load(startIndex);
        stick2.load(0);

        // Swap the last two values
        int temp = memory.get(Computer.RAM_SIZE - 1);
        memory.set(Computer.RAM_SIZE - 1,memory.get(Computer.RAM_SIZE - 2));
        memory.set(Computer.RAM_SIZE - 2, temp);

        // Save back to Hard Drive
        memory.save(startIndex);

        // Print the last 10 values in the hard drive
        System.out.println("Result in the Drive:");
        memory.driveDump(Computer.DRIVE_SIZE - 10, Computer.DRIVE_SIZE);

        // Print the values in the second memory stick
        System.out.println("Printing all values in the Second memory stick");
        stick2.memoryDump();

        // Prints the stats
        memory.printStats();*/
    }
}
