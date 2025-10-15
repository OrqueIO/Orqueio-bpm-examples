package io.orqueio.bpm.exemple.dmn;

import io.orqueio.bpm.engine.DecisionService;
import io.orqueio.bpm.engine.delegate.DelegateExecution;
import io.orqueio.bpm.engine.delegate.JavaDelegate;
import io.orqueio.bpm.engine.variable.VariableMap;
import io.orqueio.bpm.engine.variable.Variables;
import org.springframework.stereotype.Component;

@Component("DecideNotification")
public class EvaluateNotificationDecisionDelegate implements JavaDelegate {
    @Override
    public void execute(DelegateExecution execution) throws Exception {
        String premium = (String) execution.getVariable("premium");
        String status = (String) execution.getVariable("status");
        VariableMap variables = Variables.createVariables()
            .putValue("premium", premium)
            .putValue("status", status);
        DecisionService decisionService = execution.getProcessEngineServices().getDecisionService();
        VariableMap result = (VariableMap) decisionService
            .evaluateDecisionTableByKey("NotificationDecision")
            .variables(variables)
            .evaluate()
            .get(0); 
        Object notification = result.get("notification");
        execution.setVariable("notification", notification);
    }
}