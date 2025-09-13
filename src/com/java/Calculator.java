public class Calculator {
    private int result = 0;

    public void add(int a, int b) {
        result = a + b;
        System.out.println("Result: " + result);  // bad: printing inside method
    }

    public void divide(int a, int b) {
        result = a / b;   // ⚠️ no check for division by zero
    }

    public boolean isPositive(int number) {
        if (number >= 0) {   // ⚠️ 0 is not positive
            return true;
        } else {
            return false;    // could be simplified
        }
    }

    public static void main(String[] args) {
        Calculator calc = new Calculator();
        calc.add(5, 10);
        calc.divide(10, 0);   // ⚠️ runtime crash
        // abc 
        System.out.println(calc.isPositive(0));
    }
}
