package io.orqueio.bpm.example.tomcat11;

import io.orqueio.bpm.application.ProcessApplication;
import io.orqueio.bpm.application.impl.JakartaServletProcessApplication;

/**
 * Process Application for Expense Approval Process on Tomcat 11 (Jakarta EE 11 / Servlet 6.1).
 * Extends JakartaServletProcessApplication to use jakarta.servlet APIs.
 */
@ProcessApplication(name = "Expense Management Application")
public class ExpenseManagementApplication extends JakartaServletProcessApplication {
}
