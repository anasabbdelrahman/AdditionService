package se.omegapoint.model;

public class CalculationResponse {
    private String asyncId;
    private CalculationResult result;

    public CalculationResponse(String asyncId) {
        this.asyncId = asyncId;
    }

    public CalculationResponse(String asyncId, CalculationResult result) {
        this.asyncId = asyncId;
        this.result = result;
    }

    public String getAsyncId() {
        return asyncId;
    }

    public void setAsyncId(String asyncId) {
        this.asyncId = asyncId;
    }

    public CalculationResult getResult() {
        return result;
    }

    public void setResult(CalculationResult result) {
        this.result = result;
    }
}