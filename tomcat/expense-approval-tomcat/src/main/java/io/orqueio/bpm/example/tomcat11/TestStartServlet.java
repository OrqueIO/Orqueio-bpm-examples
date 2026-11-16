package io.orqueio.bpm.example.tomcat11;

import io.orqueio.bpm.engine.ProcessEngine;
import io.orqueio.bpm.engine.ProcessEngines;
import io.orqueio.bpm.engine.runtime.ProcessInstance;
import io.orqueio.bpm.engine.task.Task;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Test servlet to start expense process and display debugging information.
 * Access at: http://localhost:8080/expense-approval-tomcat/test-start
 */
@WebServlet("/test-start")
public class TestStartServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/html");
        PrintWriter out = resp.getWriter();

        out.println("<html><head><title>Test Expense Process</title></head><body>");
        out.println("<h1>Expense Process Diagnostic</h1>");

        try {
            ProcessEngine engine = ProcessEngines.getDefaultProcessEngine();
            if (engine == null) {
                out.println("<p style='color:red'>ERROR: Process engine is null!</p>");
                out.println("</body></html>");
                return;
            }

            out.println("<p>✓ Process engine found: " + engine.getName() + "</p>");

            Map<String, Object> variables = new HashMap<>();
            variables.put("requester", "john.doe");
            variables.put("requesterManager", "jane.manager");
            variables.put("amount", 500L);
            variables.put("category", "Travel");
            variables.put("description", "Conference travel expenses - testing");

            out.println("<h2>Starting Process with Variables:</h2>");
            out.println("<ul>");
            for (Map.Entry<String, Object> entry : variables.entrySet()) {
                out.println("<li><b>" + entry.getKey() + "</b>: " + entry.getValue() + " (" + entry.getValue().getClass().getSimpleName() + ")</li>");
            }
            out.println("</ul>");

            ProcessInstance processInstance = engine.getRuntimeService()
                    .startProcessInstanceByKey("expense-approval-process", variables);

            out.println("<h2>Process Started:</h2>");
            out.println("<p>Process Instance ID: <b>" + processInstance.getId() + "</b></p>");
            out.println("<p>Process Definition ID: " + processInstance.getProcessDefinitionId() + "</p>");

            ProcessInstance activeInstance = engine.getRuntimeService()
                    .createProcessInstanceQuery()
                    .processInstanceId(processInstance.getId())
                    .singleResult();

            if (activeInstance != null) {
                out.println("<p style='color:green; font-size:20px'>✓ Process is ACTIVE (waiting at user task)</p>");
                out.println("<p>Process is NOT ended: <b>TRUE</b></p>");

                List<Task> tasks = engine.getTaskService()
                        .createTaskQuery()
                        .processInstanceId(processInstance.getId())
                        .list();

                if (!tasks.isEmpty()) {
                    out.println("<h2>Active Tasks:</h2>");
                    out.println("<table border='1' cellpadding='5'>");
                    out.println("<tr><th>Task Name</th><th>Assignee</th><th>Task ID</th></tr>");
                    for (Task task : tasks) {
                        out.println("<tr>");
                        out.println("<td>" + task.getName() + "</td>");
                        out.println("<td>" + task.getAssignee() + "</td>");
                        out.println("<td>" + task.getId() + "</td>");
                        out.println("</tr>");
                    }
                    out.println("</table>");

                    out.println("<h3>How to Complete Task:</h3>");
                    out.println("<p>Go to Tasklist: <a href='/orqueio-webapp-jakarta/app/tasklist/' target='_blank'>Open Tasklist</a></p>");
                    out.println("<p>Or use Cockpit: <a href='/orqueio-webapp-jakarta/app/cockpit/' target='_blank'>Open Cockpit</a></p>");
                } else {
                    out.println("<p style='color:orange'>⚠ Process is active but NO TASKS found!</p>");
                    out.println("<p>This might mean the process is waiting at a different activity.</p>");
                }

                Map<String, Object> currentVars = engine.getRuntimeService()
                        .getVariables(processInstance.getId());
                out.println("<h2>Current Process Variables:</h2>");
                out.println("<ul>");
                for (Map.Entry<String, Object> entry : currentVars.entrySet()) {
                    out.println("<li><b>" + entry.getKey() + "</b>: " + entry.getValue() + "</li>");
                }
                out.println("</ul>");

            } else {
                out.println("<p style='color:red; font-size:20px'>✗ Process COMPLETED IMMEDIATELY!</p>");
                out.println("<p>The process ended without waiting at a user task.</p>");

                String endActivityId = (String) engine.getHistoryService()
                        .createHistoricActivityInstanceQuery()
                        .processInstanceId(processInstance.getId())
                        .activityType("endEvent")
                        .singleResult()
                        .getActivityId();

                out.println("<p>End Event: <b>" + endActivityId + "</b></p>");

                if ("EndEvent_ValidationFailed".equals(endActivityId)) {
                    out.println("<p style='color:red'>✗ Process ended at VALIDATION FAILED</p>");
                    out.println("<p>This means the validation threw a BpmnError.</p>");

                    Object notificationType = engine.getHistoryService()
                            .createHistoricVariableInstanceQuery()
                            .processInstanceId(processInstance.getId())
                            .variableName("notificationType")
                            .singleResult()
                            .getValue();

                    out.println("<p>Notification Type: <b>" + notificationType + "</b></p>");
                } else if ("EndEvent_ExpenseRejected".equals(endActivityId)) {
                    out.println("<p style='color:orange'>Process ended at EXPENSE REJECTED</p>");
                } else if ("EndEvent_ExpenseApproved".equals(endActivityId)) {
                    out.println("<p style='color:blue'>Process ended at EXPENSE APPROVED</p>");
                    out.println("<p style='color:red'>BUT THIS SHOULD NOT HAPPEN WITHOUT USER APPROVAL!</p>");
                }

                out.println("<h3>Check the logs for details:</h3>");
                out.println("<p>Look in catalina.out for error messages from ValidateExpenseDelegate</p>");
            }

        } catch (Exception e) {
            out.println("<p style='color:red'>ERROR: " + e.getMessage() + "</p>");
            out.println("<pre>");
            e.printStackTrace(out);
            out.println("</pre>");
        }

        out.println("<hr>");
        out.println("<p><a href='/test-start'>Run Test Again</a></p>");
        out.println("</body></html>");
    }
}