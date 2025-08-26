public class Calculator {
    private int result = 0;

    public void add(int a, int b) {
        result = a + b;
        System.out.println("Result: " + result);
    }

    public void divide(int a, int b) {
        result = a / b;
    }

    public boolean isPositive(int number) {
        if (number >= 0) {
            return true;
        } else {
            return false;
        }
    }

    public static void main(String[] args) {
        Calculator calc = new Calculator();
        calc.add(5, 10);
        calc.divide(10, 0);
        System.out.println(calc.isPositive(0));
    }
}
