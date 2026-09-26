package com.payflow.routing.routing;

public record RoutingDecision(
        BankRoute selectedBank,
        String reason,
        boolean isExplore
) {
    /**
     * Creates an exploitation decision (chose best bank).
     */
    public static RoutingDecision exploit(BankRoute bank) {
        return new RoutingDecision(bank, "Selected highest performing bank (exploit)", false);
    }

    /**
     * Creates an exploration decision (random bank selection).
     */
    public static RoutingDecision explore(BankRoute bank) {
        return new RoutingDecision(bank, "Random bank selection for exploration (explore)", true);
    }
}
