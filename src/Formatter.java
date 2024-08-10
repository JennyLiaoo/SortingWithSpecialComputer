
/**
 * Write a description of class Formatter here.
 *
 * @author (your name)
 * @version (a version number or a date)
 */
public class Formatter
{
    private static final int WIDTH = 10;
    private static int inputsOnLine = 0;
    private static StringBuilder output = new StringBuilder();

    public static void print(String data) {
        String formatCode = "%1$"+calcColSize()+"s";

        inputsOnLine++;
        if(inputsOnLine >= WIDTH) {
            output.append(String.format(formatCode, data) + "\n");
            inputsOnLine = 0;
        } else {
            output.append(String.format(formatCode, data) + " ");
        }
    }

    public static void print(int data) {
        String formatCode = "%1$"+calcColSize()+"s";

        inputsOnLine++;
        if(inputsOnLine >= WIDTH) {
            output.append(String.format(formatCode, data)+ "\n");
            inputsOnLine = 0;
        } else {
            output.append(String.format(formatCode, data) + " ");
        }
    }

    public static void flush() {
        System.out.println(output+ "\n");
        inputsOnLine = 0;
        output = new StringBuilder();
    }

    private static int calcColSize() {
        return (int)Math.ceil(Math.log(Computer.DRIVE_SIZE)/Math.log(10));
    }
}
