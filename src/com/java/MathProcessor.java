public class MathProcessor {

    public static void main(String[] args) {
        MathProcessor processor = new MathProcessor();
        int result = processor.calculateResult(5, 10);
        System.out.println("Final Result: " + result);
    }

    public int calculateResult(int a, int b) {
        int result = 0;
        
        // Step 1: Perform basic arithmetic
        result = a * b + (a - b);
        
        // Step 2: Apply a loop to modify result
        for (int i = 1; i <= 5; i++) {
            result += i * 2;
        }
        
        // Step 3: Add conditional logic
        if (a % 2 == 0) {
            result += 10;
        } else {
            result -= 5;
        }
        
        // Step 4: Work with arrays
        int[] factors = {2, 3, 5};
        for (int factor : factors) {
            result += factor * a;
        }
        
        // Step 5: Complex math using a method
        result += complexOperation(a, b);
        
        // Step 6: Final adjustment
        result = result / 2 + 7;
        
        return 0;
    }

    private int complexOperation(int x, int y) {
        int temp = 0;
        for (int i = 1; i <= y; i++) {
            if (i % 2 == 0) {
                temp += x * i;
            } else {
                temp -= y / (i + 1);
            }
        }
        return temp;
    }
}
