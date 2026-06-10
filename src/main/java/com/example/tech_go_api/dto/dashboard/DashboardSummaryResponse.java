package com.example.tech_go_api.dto.dashboard;

public class DashboardSummaryResponse {

    private long totalPlayers;
    private long totalGames;
    private long totalPaymentsThisMonth;
    private long totalPaymentsPaidThisMonth;
    private long totalPaymentsPendingThisMonth;

    public DashboardSummaryResponse(long totalPlayers, long totalGames,
                                    long totalPaymentsThisMonth,
                                    long totalPaymentsPaidThisMonth,
                                    long totalPaymentsPendingThisMonth) {
        this.totalPlayers = totalPlayers;
        this.totalGames = totalGames;
        this.totalPaymentsThisMonth = totalPaymentsThisMonth;
        this.totalPaymentsPaidThisMonth = totalPaymentsPaidThisMonth;
        this.totalPaymentsPendingThisMonth = totalPaymentsPendingThisMonth;
    }

    public long getTotalPlayers() { return totalPlayers; }
    public long getTotalGames() { return totalGames; }
    public long getTotalPaymentsThisMonth() { return totalPaymentsThisMonth; }
    public long getTotalPaymentsPaidThisMonth() { return totalPaymentsPaidThisMonth; }
    public long getTotalPaymentsPendingThisMonth() { return totalPaymentsPendingThisMonth; }
}
