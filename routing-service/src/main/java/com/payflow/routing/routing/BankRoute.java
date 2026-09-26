package com.payflow.routing.routing;

public class BankRoute {

    private String bankId;
    private String bankName;
    private double successRate;
    private double avgLatencyMs;
    private double costPerTxn;
    private boolean active;

    public BankRoute() {
    }

    public BankRoute(String bankId, String bankName, double successRate,
                     double avgLatencyMs, double costPerTxn, boolean active) {
        this.bankId = bankId;
        this.bankName = bankName;
        this.successRate = successRate;
        this.avgLatencyMs = avgLatencyMs;
        this.costPerTxn = costPerTxn;
        this.active = active;
    }

    // ... standard getters and setters for all 6 fields ...

    @Override
    public String toString() {
        return "BankRoute{" +
                "bankId='" + bankId + '\'' +
                ", bankName='" + bankName + '\'' +
                ", successRate=" + successRate +
                ", avgLatencyMs=" + avgLatencyMs +
                ", costPerTxn=" + costPerTxn +
                ", active=" + active +
                '}';
    }

}
