package io.orqueio.bpm.example.tomcat11;

import io.orqueio.bpm.engine.delegate.DelegateExecution;
import io.orqueio.bpm.engine.delegate.JavaDelegate;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.UUID;
import java.util.logging.Logger;

/**
 * JavaDelegate that processes expense reimbursement payment.
 * Simulates integration with a payment system to process approved expenses.
 */
public class ProcessPaymentDelegate implements JavaDelegate {

    private static final Logger LOGGER = Logger.getLogger(ProcessPaymentDelegate.class.getName());

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        String processInstanceId = execution.getProcessInstanceId();

        LOGGER.info("ProcessPaymentDelegate executed for process instance: " + processInstanceId);

        // Retrieve expense data
        String requester = (String) execution.getVariable("requester");

        // Amount can be Long (from forms) or Double (from API)
        Object amountObj = execution.getVariable("amount");
        Double amount = null;
        if (amountObj instanceof Double) {
            amount = (Double) amountObj;
        } else if (amountObj instanceof Long) {
            amount = ((Long) amountObj).doubleValue();
        } else if (amountObj instanceof Integer) {
            amount = ((Integer) amountObj).doubleValue();
        }

        String category = (String) execution.getVariable("category");
        String description = (String) execution.getVariable("description");

        // Validate data exists
        if (requester == null || amount == null) {
            LOGGER.severe("Cannot process payment: Missing required data (requester or amount)");
            throw new IllegalStateException("Cannot process payment: Missing required data");
        }

        // Generate payment transaction ID
        String transactionId = "TXN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        // Calculate processing fee (1% of amount)
        BigDecimal expenseAmount = BigDecimal.valueOf(amount);
        BigDecimal processingFee = expenseAmount.multiply(new BigDecimal("0.01"))
                .setScale(2, RoundingMode.HALF_UP);
        BigDecimal totalAmount = expenseAmount.add(processingFee);

        // Simulate payment processing
        LOGGER.info("Processing reimbursement payment:");
        LOGGER.info("  - Transaction ID: " + transactionId);
        LOGGER.info("  - Payee: " + requester);
        LOGGER.info("  - Expense Amount: $" + expenseAmount);
        LOGGER.info("  - Processing Fee: $" + processingFee);
        LOGGER.info("  - Total Amount: $" + totalAmount);
        LOGGER.info("  - Category: " + category);
        LOGGER.info("  - Description: " + description);

        // Simulate payment gateway delay
        Thread.sleep(500);

        // Set payment result variables
        execution.setVariable("transactionId", transactionId);
        execution.setVariable("processingFee", processingFee.doubleValue());
        execution.setVariable("totalAmount", totalAmount.doubleValue());
        execution.setVariable("paymentStatus", "COMPLETED");
        execution.setVariable("processedAt", System.currentTimeMillis());

        LOGGER.info("Payment processed successfully. Transaction ID: " + transactionId);
    }
}
