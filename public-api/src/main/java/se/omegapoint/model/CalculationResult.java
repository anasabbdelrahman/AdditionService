package se.omegapoint.model;

public class CalculationResult {
    private double numberOne;
    private double numberTwo;
    private double result;

    public CalculationResult(double numberOne, double numberTwo, double result) {
        this.numberOne = numberOne;
        this.numberTwo = numberTwo;
        this.result = result;
    }

    public double getNumberOne() {
        return numberOne;
    }

    public void setNumberOne(double numberOne) {
        this.numberOne = numberOne;
    }

    public double getNumberTwo() {
        return numberTwo;
    }

    public void setNumberTwo(double numberTwo) {
        this.numberTwo = numberTwo;
    }

    public double getResult() {
        return result;
    }

    public void setResult(double result) {
        this.result = result;
    }
}