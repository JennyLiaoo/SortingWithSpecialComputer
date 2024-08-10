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
public class DriverAlteringExample
{
    static RAM stick1 = new RAM();
    static RAM stick2 = new RAM();

    public static void main(String[] args)
    {
        // Print all the values in the "HARD DRIVE" to the screen
        stick1.driveDump();
        System.out.println();

        int max = -1;

        int startIndex = Computer.DRIVE_SIZE - Computer.RAM_SIZE;
        for(int i = 0; i < Computer.DRIVE_SIZE; i += Computer.RAM_SIZE) {
            stick1.load(i); // Load is first chunk of data from HD
            for(int j = 0; j < Computer.RAM_SIZE; j++) {
                if(stick1.get(j) > max) {
                    max = stick1.get(j);
                }
            }
            //stick1.save(i);
        }
        System.out.println("The max value is " + max);
        //stick1.driveDump();
    }
}
