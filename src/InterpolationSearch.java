import java.util.Scanner;

/**
 * A search algorithm that is potentially faster than binary search.
 * It guesses the position of the given value using a specific equation.
 * Then it compares the found value with the given value, shrinking the search area with each attempt at guessing
 * Condition: only works with sorted values, and more efficient when data is uniformly distributed
 * worst case: O(N)
 * best case: O(log(log n))
 *
 * @author Jenny
 * @version 14/10/22
 */
public class InterpolationSearch
{
    static RAM stick1 = new RAM();

    public static void main(String[] args) {
        Scanner user = new Scanner(System.in);
        stick1.driveDump();
        //value we are searching for
        int value = user.nextInt(); user.nextLine();
        //loop through hard drive
        int found = 0;
        for(int hdCounter = 0; hdCounter < Computer.DRIVE_SIZE; hdCounter+=Computer.RAM_SIZE) {
            stick1.load(hdCounter);
            int indexOfValue = interpolationSearch(value);
            if (indexOfValue != -1) {
                System.out.println("Given value was found at index" + (indexOfValue+hdCounter));
                found = 1;
                break;
            }
        }
        if(found == 0) {
            System.out.println("not found");
        }
        stick1.printStats();
    }

    /**
     * Compares the value and the guess, then determines a new search area
     * @param searchFor, int
     * @return int
     */
    public static int interpolationSearch (int searchFor) {
        int start = 0;
        int end = Computer.RAM_SIZE-1;

        while (searchFor >= stick1.get(start) && searchFor <= stick1.get(end) && start <= end) {
            //equation which guesses the index
            int guess = start + (end - start)*(searchFor- stick1.get(start))/(stick1.get(end)- stick1.get(start));
            System.out.println("guess: " + guess);
            if(stick1.get(guess) == searchFor) {
                return guess;
            }
            else if(stick1.get(guess) < searchFor) {
                start = guess + 1;
            }
            else  {
                end = guess - 1;
            }
        }
        return -1;
    }

}
