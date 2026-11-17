package io.orqueio.bpm.example.tomcat11;

import io.orqueio.bpm.engine.delegate.BpmnError;
import io.orqueio.bpm.engine.delegate.DelegateExecution;
import io.orqueio.bpm.engine.delegate.JavaDelegate;

import java.math.BigDecimal;
import java.util.logging.Logger;

/**
 * JavaDelegate that validates expense request data.
 * Performs business rule validation on expense submissions.
 */
public class ValidateExpenseDelegate implements JavaDelegate {

    private static final Logger LOGGER = Logger.getLogger(ValidateExpenseDelegate.class.getName());
    private static final BigDecimal MAX_EXPENSE_AMOUNT = new BigDecimal("50000.00");

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        String processInstanceId = execution.getProcessInstanceId();

        LOGGER.info("ValidateExpenseDelegate executed for process instance: " + processInstanceId);

        String description = (String) execution.getVariable("description");

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
        String requester = (String) execution.getVariable("requester");
        String requesterManager = (String) execution.getVariable("requesterManager");

        if (description == null || description.trim().isEmpty()) {
            LOGGER.severe("Expense validation failed: Description is required");
            throw new BpmnError("VALIDATION_ERROR", "Description is required");
        }

        if (amount == null || amount <= 0) {
            LOGGER.severe("Expense validation failed: Amount must be greater than zero");
            throw new BpmnError("VALIDATION_ERROR", "Amount must be greater than zero");
        }

        if (category == null || category.trim().isEmpty()) {
            LOGGER.severe("Expense validation failed: Category is required");
            throw new BpmnError("VALIDATION_ERROR", "Category is required");
        }

        if (requester == null || requester.trim().isEmpty()) {
            LOGGER.severe("Expense validation failed: Requester is required");
            throw new BpmnError("VALIDATION_ERROR", "Requester is required");
        }

        BigDecimal expenseAmount = BigDecimal.valueOf(amount);
        if (expenseAmount.compareTo(MAX_EXPENSE_AMOUNT) > 0) {
            LOGGER.severe("Expense validation failed: Amount exceeds maximum allowed: " + MAX_EXPENSE_AMOUNT);
            throw new BpmnError("AMOUNT_EXCEEDED", "Expense amount exceeds maximum allowed: $" + MAX_EXPENSE_AMOUNT);
        }

        if (requesterManager == null || requesterManager.trim().isEmpty()) {
            requesterManager = "default-manager";
            execution.setVariable("requesterManager", requesterManager);
            LOGGER.info("No manager specified, assigned default manager: " + requesterManager);
        }

        LOGGER.info("Expense validation successful:");
        LOGGER.info("  - Requester: " + requester);
        LOGGER.info("  - Amount: $" + amount);
        LOGGER.info("  - Category: " + category);
        LOGGER.info("  - Description: " + description);

        execution.setVariable("validatedAt", System.currentTimeMillis());
        execution.setVariable("validationStatus", "APPROVED");
    }
}
