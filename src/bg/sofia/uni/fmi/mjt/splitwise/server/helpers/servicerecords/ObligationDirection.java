package bg.sofia.uni.fmi.mjt.splitwise.server.helpers.servicerecords;

public record ObligationDirection(String debtor, String creditor, double amount , String currency) {

    @Override
    public String toString() {
        return String.format("%s owes %s %.2f %s", debtor, creditor, amount, currency);
    }
}
