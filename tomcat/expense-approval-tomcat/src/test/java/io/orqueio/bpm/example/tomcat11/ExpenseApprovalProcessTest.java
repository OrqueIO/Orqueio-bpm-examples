package io.orqueio.bpm.example.tomcat11;

import io.orqueio.bpm.engine.RuntimeService;
import io.orqueio.bpm.engine.TaskService;
import io.orqueio.bpm.engine.runtime.ProcessInstance;
import io.orqueio.bpm.engine.task.Task;
import io.orqueio.bpm.engine.test.Deployment;
import io.orqueio.bpm.engine.test.ProcessEngineRule;
import org.junit.Rule;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * Integration test for Expense Approval Process.
 * Uses embedded engine to verify BPMN deployment and JavaDelegate execution.
 */
public class ExpenseApprovalProcessTest {

    @Rule
    public ProcessEngineRule processEngineRule = new ProcessEngineRule();

    @Test
    @Deployment(resources = {"expense-approval-process.bpmn"})
    public void shouldRouteToManagerForLowAmountExpense() {
        RuntimeService runtimeService = processEngineRule.getRuntimeService();
        TaskService taskService = processEngineRule.getTaskService();

        // Prepare expense data with amount <= 1000
        Map<String, Object> variables = new HashMap<>();
        variables.put("requester", "john.doe");
        variables.put("requesterManager", "jane.manager");
        variables.put("amount", 500.0);
        variables.put("category", "Travel");
        variables.put("description", "Conference travel expenses");

        // Start process instance (validation executes synchronously)
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("expense-approval-process", variables);
        assertNotNull("Process instance should be created", processInstance);

        // Verify validation was successful
        String validationStatus = (String) runtimeService.getVariable(processInstance.getId(), "validationStatus");
        assertEquals("APPROVED", validationStatus);
        assertNotNull("Validation timestamp should be set", runtimeService.getVariable(processInstance.getId(), "validatedAt"));

        // Verify process reached Manager Approval task (low amount path)
        Task task = taskService.createTaskQuery()
                .processInstanceId(processInstance.getId())
                .singleResult();
        assertNotNull("Manager Approval task should be active", task);
        assertEquals("Manager Approval", task.getName());
        assertEquals("jane.manager", task.getAssignee());

        // Complete manager approval with approval decision
        Map<String, Object> approvalVariables = new HashMap<>();
        approvalVariables.put("approvalDecision", "approved");
        approvalVariables.put("approverComments", "Expense approved for conference attendance");
        taskService.complete(task.getId(), approvalVariables);

        // Verify process completed and check variables in history (payment executes synchronously)
        assertNull("Process should have ended", runtimeService.createProcessInstanceQuery()
                .processInstanceId(processInstance.getId())
                .singleResult());

        // Retrieve variables from history
        String paymentStatus = (String) processEngineRule.getHistoryService()
                .createHistoricVariableInstanceQuery()
                .processInstanceId(processInstance.getId())
                .variableName("paymentStatus")
                .singleResult()
                .getValue();
        String transactionId = (String) processEngineRule.getHistoryService()
                .createHistoricVariableInstanceQuery()
                .processInstanceId(processInstance.getId())
                .variableName("transactionId")
                .singleResult()
                .getValue();
        Double totalAmount = (Double) processEngineRule.getHistoryService()
                .createHistoricVariableInstanceQuery()
                .processInstanceId(processInstance.getId())
                .variableName("totalAmount")
                .singleResult()
                .getValue();

        assertEquals("COMPLETED", paymentStatus);
        assertNotNull("Transaction ID should be set", transactionId);
        assertTrue("Transaction ID should start with TXN-", transactionId.startsWith("TXN-"));
        assertNotNull("Total amount should be calculated", totalAmount);
        assertTrue("Total amount should include processing fee", totalAmount > 500.0);

        // Verify notification was sent
        Boolean notificationSent = (Boolean) processEngineRule.getHistoryService()
                .createHistoricVariableInstanceQuery()
                .processInstanceId(processInstance.getId())
                .variableName("notificationSent")
                .singleResult()
                .getValue();
        String notificationType = (String) processEngineRule.getHistoryService()
                .createHistoricVariableInstanceQuery()
                .processInstanceId(processInstance.getId())
                .variableName("notificationType")
                .singleResult()
                .getValue();

        assertEquals(true, notificationSent);
        assertEquals("APPROVAL_CONFIRMATION", notificationType);
    }

    @Test
    @Deployment(resources = {"expense-approval-process.bpmn"})
    public void shouldRouteToCFOForHighAmountExpense() {
        RuntimeService runtimeService = processEngineRule.getRuntimeService();
        TaskService taskService = processEngineRule.getTaskService();

        // Prepare expense data with amount > 1000
        Map<String, Object> variables = new HashMap<>();
        variables.put("requester", "alice.smith");
        variables.put("requesterManager", "bob.manager");
        variables.put("amount", 5000.0);
        variables.put("category", "Equipment");
        variables.put("description", "New laptop and accessories");

        // Start process instance (validation executes synchronously)
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("expense-approval-process", variables);
        assertNotNull("Process instance should be created", processInstance);

        // Verify validation was successful
        String validationStatus = (String) runtimeService.getVariable(processInstance.getId(), "validationStatus");
        assertEquals("APPROVED", validationStatus);

        // Verify process reached CFO Approval task (high amount path)
        Task task = taskService.createTaskQuery()
                .processInstanceId(processInstance.getId())
                .singleResult();
        assertNotNull("CFO Approval task should be active", task);
        assertEquals("CFO Approval", task.getName());

        // Complete CFO approval with approval decision
        Map<String, Object> approvalVariables = new HashMap<>();
        approvalVariables.put("approvalDecision", "approved");
        approvalVariables.put("approverComments", "Equipment purchase approved");
        taskService.complete(task.getId(), approvalVariables);

        // Verify process completed and check variables in history (payment executes synchronously)
        assertNull("Process should have ended", runtimeService.createProcessInstanceQuery()
                .processInstanceId(processInstance.getId())
                .singleResult());

        // Retrieve variables from history
        String paymentStatus = (String) processEngineRule.getHistoryService()
                .createHistoricVariableInstanceQuery()
                .processInstanceId(processInstance.getId())
                .variableName("paymentStatus")
                .singleResult()
                .getValue();
        String transactionId = (String) processEngineRule.getHistoryService()
                .createHistoricVariableInstanceQuery()
                .processInstanceId(processInstance.getId())
                .variableName("transactionId")
                .singleResult()
                .getValue();

        assertEquals("COMPLETED", paymentStatus);
        assertNotNull("Transaction ID should be set", transactionId);
    }

    @Test
    @Deployment(resources = {"expense-approval-process.bpmn"})
    public void shouldSetDefaultManagerWhenNotProvided() {
        RuntimeService runtimeService = processEngineRule.getRuntimeService();

        // Prepare expense data without manager
        Map<String, Object> variables = new HashMap<>();
        variables.put("requester", "test.user");
        variables.put("amount", 300.0);
        variables.put("category", "Office Supplies");
        variables.put("description", "Office supplies purchase");

        // Start process instance (validation executes synchronously)
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("expense-approval-process", variables);
        assertNotNull("Process instance should be created", processInstance);

        // Verify default manager was assigned
        String assignedManager = (String) runtimeService.getVariable(processInstance.getId(), "requesterManager");
        assertEquals("default-manager", assignedManager);
    }

    @Test
    @Deployment(resources = {"expense-approval-process.bpmn"})
    public void shouldHandleRejectionPath() {
        RuntimeService runtimeService = processEngineRule.getRuntimeService();
        TaskService taskService = processEngineRule.getTaskService();

        // Prepare expense data
        Map<String, Object> variables = new HashMap<>();
        variables.put("requester", "john.doe");
        variables.put("requesterManager", "jane.manager");
        variables.put("amount", 800.0);
        variables.put("category", "Entertainment");
        variables.put("description", "Team dinner");

        // Start process instance (validation executes synchronously)
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("expense-approval-process", variables);
        assertNotNull("Process instance should be created", processInstance);

        // Verify process reached Manager Approval task
        Task task = taskService.createTaskQuery()
                .processInstanceId(processInstance.getId())
                .singleResult();
        assertNotNull("Manager Approval task should be active", task);
        assertEquals("Manager Approval", task.getName());

        // Complete manager approval with rejection
        Map<String, Object> rejectionVariables = new HashMap<>();
        rejectionVariables.put("approvalDecision", "rejected");
        rejectionVariables.put("approverComments", "Team dinners are not reimbursable expenses");
        taskService.complete(task.getId(), rejectionVariables);

        // Verify process completed via rejection path (notification executes synchronously)
        assertNull("Process should have ended", runtimeService.createProcessInstanceQuery()
                .processInstanceId(processInstance.getId())
                .singleResult());

        // Verify rejection notification was sent
        Boolean notificationSent = (Boolean) processEngineRule.getHistoryService()
                .createHistoricVariableInstanceQuery()
                .processInstanceId(processInstance.getId())
                .variableName("notificationSent")
                .singleResult()
                .getValue();
        String notificationType = (String) processEngineRule.getHistoryService()
                .createHistoricVariableInstanceQuery()
                .processInstanceId(processInstance.getId())
                .variableName("notificationType")
                .singleResult()
                .getValue();

        assertEquals(true, notificationSent);
        assertEquals("REJECTION", notificationType);

        // Verify payment was NOT processed
        assertNull("Payment should not have been processed",
                processEngineRule.getHistoryService()
                        .createHistoricVariableInstanceQuery()
                        .processInstanceId(processInstance.getId())
                        .variableName("transactionId")
                        .singleResult());
    }

    @Test
    @Deployment(resources = {"expense-approval-process.bpmn"})
    public void shouldHandleValidationError() {
        RuntimeService runtimeService = processEngineRule.getRuntimeService();

        // Prepare expense data with missing required fields (no description)
        Map<String, Object> variables = new HashMap<>();
        variables.put("requester", "john.doe");
        variables.put("amount", 500.0);
        variables.put("category", "Travel");
        // Missing description

        // Start process instance (validation executes synchronously and catches error)
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("expense-approval-process", variables);
        assertNotNull("Process instance should be created", processInstance);

        // Verify process completed via error path
        assertNull("Process should have ended", runtimeService.createProcessInstanceQuery()
                .processInstanceId(processInstance.getId())
                .singleResult());

        // Verify validation error notification was sent
        Boolean notificationSent = (Boolean) processEngineRule.getHistoryService()
                .createHistoricVariableInstanceQuery()
                .processInstanceId(processInstance.getId())
                .variableName("notificationSent")
                .singleResult()
                .getValue();
        String notificationType = (String) processEngineRule.getHistoryService()
                .createHistoricVariableInstanceQuery()
                .processInstanceId(processInstance.getId())
                .variableName("notificationType")
                .singleResult()
                .getValue();

        assertEquals(true, notificationSent);
        assertEquals("VALIDATION_ERROR", notificationType);
    }
}
