package io.orqueio.bpm.example.tomcat11;

import io.orqueio.bpm.engine.delegate.DelegateExecution;
import io.orqueio.bpm.engine.delegate.JavaDelegate;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.logging.Logger;

/**
 * JavaDelegate that sends email notifications for expense process events.
 * Simulates email sending with detailed logging.
 */
public class SendNotificationDelegate implements JavaDelegate {

    private static final Logger LOGGER = Logger.getLogger(SendNotificationDelegate.class.getName());
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
            .withZone(ZoneId.systemDefault());

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        String processInstanceId = execution.getProcessInstanceId();
        String currentActivityId = execution.getCurrentActivityId();

        LOGGER.info("SendNotificationDelegate executed for process instance: " + processInstanceId);
        LOGGER.info("Current activity: " + currentActivityId);

        // Determine notification type based on current activity
        switch (currentActivityId) {
            case "Task_NotifyValidationError":
                sendValidationErrorNotification(execution);
                break;
            case "Task_NotifyRejection":
                sendRejectionNotification(execution);
                break;
            case "Task_SendConfirmation":
                sendApprovalConfirmationNotification(execution);
                break;
            default:
                LOGGER.warning("Unknown notification task: " + currentActivityId);
        }
    }

    private void sendValidationErrorNotification(DelegateExecution execution) {
        String requester = (String) execution.getVariable("requester");
        String description = (String) execution.getVariable("description");

        LOGGER.info("===== VALIDATION ERROR NOTIFICATION =====");
        LOGGER.info("To: " + (requester != null ? requester + "@company.com" : "unknown@company.com"));
        LOGGER.info("Subject: Expense Request Validation Failed");
        LOGGER.info("Body:");
        LOGGER.info("  Dear " + (requester != null ? requester : "User") + ",");
        LOGGER.info("");
        LOGGER.info("  Your expense request has failed validation.");
        LOGGER.info("  Description: " + (description != null ? description : "N/A"));
        LOGGER.info("");
        LOGGER.info("  Please review the requirements and submit a new request with valid information.");
        LOGGER.info("");
        LOGGER.info("  Common validation errors:");
        LOGGER.info("  - Missing required fields (description, amount, category)");
        LOGGER.info("  - Amount must be greater than zero");
        LOGGER.info("  - Amount cannot exceed $50,000");
        LOGGER.info("");
        LOGGER.info("  If you need assistance, please contact the finance department.");
        LOGGER.info("");
        LOGGER.info("  Best regards,");
        LOGGER.info("  Expense Management System");
        LOGGER.info("=========================================");

        execution.setVariable("notificationSent", true);
        execution.setVariable("notificationType", "VALIDATION_ERROR");
        execution.setVariable("notificationSentAt", System.currentTimeMillis());
    }

    private void sendRejectionNotification(DelegateExecution execution) {
        String requester = (String) execution.getVariable("requester");

        // Amount can be Long (from forms) or Double (from API)
        Object amountObj = execution.getVariable("amount");
        Double amount = convertToDouble(amountObj);

        String category = (String) execution.getVariable("category");
        String description = (String) execution.getVariable("description");
        String approverComments = (String) execution.getVariable("approverComments");

        String formattedAmount = amount != null ? String.format("$%.2f", amount) : "$0.00";
        String timestamp = DATE_FORMATTER.format(Instant.now());

        LOGGER.info("===== EXPENSE REJECTION NOTIFICATION =====");
        LOGGER.info("To: " + (requester != null ? requester + "@company.com" : "unknown@company.com"));
        LOGGER.info("Subject: Expense Request Rejected");
        LOGGER.info("Body:");
        LOGGER.info("  Dear " + (requester != null ? requester : "User") + ",");
        LOGGER.info("");
        LOGGER.info("  Your expense request has been rejected.");
        LOGGER.info("");
        LOGGER.info("  Expense Details:");
        LOGGER.info("  - Amount: " + formattedAmount);
        LOGGER.info("  - Category: " + (category != null ? category : "N/A"));
        LOGGER.info("  - Description: " + (description != null ? description : "N/A"));
        LOGGER.info("  - Rejection Date: " + timestamp);
        LOGGER.info("");
        if (approverComments != null && !approverComments.trim().isEmpty()) {
            LOGGER.info("  Approver Comments:");
            LOGGER.info("  " + approverComments);
            LOGGER.info("");
        }
        LOGGER.info("  If you believe this rejection was made in error, please contact your manager");
        LOGGER.info("  or the finance department for further clarification.");
        LOGGER.info("");
        LOGGER.info("  Best regards,");
        LOGGER.info("  Expense Management System");
        LOGGER.info("==========================================");

        execution.setVariable("notificationSent", true);
        execution.setVariable("notificationType", "REJECTION");
        execution.setVariable("notificationSentAt", System.currentTimeMillis());
    }

    private void sendApprovalConfirmationNotification(DelegateExecution execution) {
        String requester = (String) execution.getVariable("requester");

        // Amount can be Long (from forms) or Double (from API)
        Double amount = convertToDouble(execution.getVariable("amount"));
        Double totalAmount = convertToDouble(execution.getVariable("totalAmount"));
        Double processingFee = convertToDouble(execution.getVariable("processingFee"));

        String category = (String) execution.getVariable("category");
        String description = (String) execution.getVariable("description");
        String transactionId = (String) execution.getVariable("transactionId");
        String approverComments = (String) execution.getVariable("approverComments");

        String formattedAmount = amount != null ? String.format("$%.2f", amount) : "$0.00";
        String formattedTotal = totalAmount != null ? String.format("$%.2f", totalAmount) : "$0.00";
        String formattedFee = processingFee != null ? String.format("$%.2f", processingFee) : "$0.00";
        String timestamp = DATE_FORMATTER.format(Instant.now());

        LOGGER.info("===== EXPENSE APPROVAL CONFIRMATION =====");
        LOGGER.info("To: " + (requester != null ? requester + "@company.com" : "unknown@company.com"));
        LOGGER.info("Subject: Expense Approved and Payment Processed");
        LOGGER.info("Body:");
        LOGGER.info("  Dear " + (requester != null ? requester : "User") + ",");
        LOGGER.info("");
        LOGGER.info("  Great news! Your expense request has been approved and payment has been processed.");
        LOGGER.info("");
        LOGGER.info("  Expense Details:");
        LOGGER.info("  - Amount: " + formattedAmount);
        LOGGER.info("  - Category: " + (category != null ? category : "N/A"));
        LOGGER.info("  - Description: " + (description != null ? description : "N/A"));
        LOGGER.info("");
        LOGGER.info("  Payment Information:");
        LOGGER.info("  - Transaction ID: " + (transactionId != null ? transactionId : "N/A"));
        LOGGER.info("  - Processing Fee: " + formattedFee);
        LOGGER.info("  - Total Amount Processed: " + formattedTotal);
        LOGGER.info("  - Processing Date: " + timestamp);
        LOGGER.info("");
        if (approverComments != null && !approverComments.trim().isEmpty()) {
            LOGGER.info("  Approver Comments:");
            LOGGER.info("  " + approverComments);
            LOGGER.info("");
        }
        LOGGER.info("  You should receive the reimbursement in your account within 3-5 business days.");
        LOGGER.info("");
        LOGGER.info("  For questions regarding this payment, please reference the transaction ID above.");
        LOGGER.info("");
        LOGGER.info("  Best regards,");
        LOGGER.info("  Expense Management System");
        LOGGER.info("=========================================");

        execution.setVariable("notificationSent", true);
        execution.setVariable("notificationType", "APPROVAL_CONFIRMATION");
        execution.setVariable("notificationSentAt", System.currentTimeMillis());
    }

    /**
     * Helper method to convert various number types to Double.
     */
    private Double convertToDouble(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Double) {
            return (Double) value;
        } else if (value instanceof Long) {
            return ((Long) value).doubleValue();
        } else if (value instanceof Integer) {
            return ((Integer) value).doubleValue();
        } else if (value instanceof Float) {
            return ((Float) value).doubleValue();
        }
        return null;
    }
}
