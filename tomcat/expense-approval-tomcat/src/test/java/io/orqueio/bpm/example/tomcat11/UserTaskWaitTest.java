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
 * Test to demonstrate that the process WAITS at user tasks.
 */
public class UserTaskWaitTest {

    @Rule
    public ProcessEngineRule processEngineRule = new ProcessEngineRule();

    @Test
    @Deployment(resources = {"expense-approval-process.bpmn"})
    public void processInstanceShouldWaitAtUserTask() {
        RuntimeService runtimeService = processEngineRule.getRuntimeService();
        TaskService taskService = processEngineRule.getTaskService();

        // Prepare expense data
        Map<String, Object> variables = new HashMap<>();
        variables.put("requester", "john.doe");
        variables.put("requesterManager", "jane.manager");
        variables.put("amount", 500.0);
        variables.put("category", "Travel");
        variables.put("description", "Conference travel");

        // Start process instance (validation executes synchronously)
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("expense-approval-process", variables);
        assertNotNull("Process instance should be created", processInstance);

        // IMPORTANT: Check that process is STILL ACTIVE (waiting at user task)
        ProcessInstance activeInstance = runtimeService.createProcessInstanceQuery()
                .processInstanceId(processInstance.getId())
                .singleResult();

        assertNotNull("Process instance should STILL BE ACTIVE (waiting at user task)", activeInstance);
        assertFalse("Process should NOT be ended", activeInstance.isEnded());

        // Verify there is an active user task
        Task task = taskService.createTaskQuery()
                .processInstanceId(processInstance.getId())
                .singleResult();

        assertNotNull("There should be an active user task", task);
        assertEquals("Manager Approval", task.getName());
        assertEquals("jane.manager", task.getAssignee());

        // Count active tasks
        long taskCount = taskService.createTaskQuery()
                .processInstanceId(processInstance.getId())
                .count();

        assertEquals("There should be exactly 1 active task", 1, taskCount);

        System.out.println("=========================================");
        System.out.println("PROOF: Process is WAITING at user task");
        System.out.println("=========================================");
        System.out.println("Process Instance ID: " + processInstance.getId());
        System.out.println("Process Status: ACTIVE (waiting)");
        System.out.println("Active Task: " + task.getName());
        System.out.println("Assigned to: " + task.getAssignee());
        System.out.println("Task ID: " + task.getId());
        System.out.println("=========================================");

        // NOW complete the task to continue the process (payment and notification execute synchronously)
        Map<String, Object> approvalVars = new HashMap<>();
        approvalVars.put("approvalDecision", "approved");
        approvalVars.put("approverComments", "Approved");
        taskService.complete(task.getId(), approvalVars);

        // NOW the process should be completed
        ProcessInstance completedInstance = runtimeService.createProcessInstanceQuery()
                .processInstanceId(processInstance.getId())
                .singleResult();

        assertNull("Process should NOW be completed", completedInstance);

        System.out.println("After completing user task: Process is FINISHED");
    }
}