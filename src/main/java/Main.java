import java.sql.SQLOutput;
import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class Main {

    public static BlockingQueue<String> queueA = new ArrayBlockingQueue<>(100);
    public static BlockingQueue<String> queueB = new ArrayBlockingQueue<>(100);
    public static BlockingQueue<String> queueC = new ArrayBlockingQueue<>(100);

    public static void main(String[] args) throws InterruptedException {
        Thread textGenerator = new Thread(
                () -> {
                    for (int i = 0; i < 10_000; i++) {
                        String text = generateText("abc", 100_000);
                        try {
                            queueA.put(text);
                            queueB.put(text);
                            queueC.put(text);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }
                });
        textGenerator.start();

        Thread a = new Thread(
                () -> {
                    char letter = 'a';
                    int maxA = maxCharCount(queueA, letter);
                    System.out.println("Максимальное количество символов '" + letter + "' составляет: " + maxA);
                }
        );
        a.start();

        Thread b = new Thread(
                () -> {
                    char letter = 'b';
                    int maxB = maxCharCount(queueB, letter);
                    System.out.println("Максимальное количество символов '" + letter + "' составляет: " + maxB);
                }
        );
        b.start();

        Thread c = new Thread(
                () -> {
                    char letter = 'c';
                    int maxC = maxCharCount(queueC, letter);
                    System.out.println("Максимальное количество символов '" + letter + "' составляет: " + maxC);
                }
        );
        c.start();

        a.join();
        b.join();
        c.join();
    }

    public static String generateText(String letters, int length) {
        Random random = new Random();
        StringBuilder text = new StringBuilder();
        for (int i = 0; i < length; i++) {
            text.append(letters.charAt(random.nextInt(letters.length())));
        }
        return text.toString();
    }

    public static int maxCharCount(BlockingQueue<String> queue, char letter) {
        int count = 0;
        int max = 0;

        String text;
        try {
            for (int i = 0; i < 10_000; i++) {
                text = queue.take();
                for (char ch : text.toCharArray()) {
                    if (ch == letter) count++;
                }
                if (count > max) max = count;
                count = 0;
            }
        } catch (InterruptedException e) {
            return -1;
        }
        return max;
    }
}
