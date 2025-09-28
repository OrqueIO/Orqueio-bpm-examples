/*
 * Copyright Camunda Services GmbH and/or licensed to Camunda Services GmbH
 * under one or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information regarding copyright
 * ownership. Camunda licenses this file to you under the Apache License,
 * Version 2.0; you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.orqueio.bpm.tutorial.multitenancy;

import io.orqueio.bpm.engine.RepositoryService;
import io.orqueio.bpm.engine.delegate.DelegateExecution;
import io.orqueio.bpm.engine.repository.ProcessDefinition;
import io.orqueio.bpm.model.bpmn.instance.CallActivity;

/**
 * Provides the tenant-id of a called process definition. Can be used if shared
 * process definitions can be overridden by tenant specific ones.
 */
public class CalledElementTenantIdProvider {

  public String resolveTenantId(DelegateExecution execution) {
    RepositoryService repositoryService = execution.getProcessEngineServices().getRepositoryService();

    String tenantId = execution.getTenantId();

    // resolve the process definition key
    CallActivity callActivity = (CallActivity) execution.getBpmnModelElementInstance();
    String processDefinitionKey = callActivity.getCalledElement();

    // and check if a process definition is deployed for the tenant
    ProcessDefinition processDefinition = repositoryService
        .createProcessDefinitionQuery()
        .processDefinitionKey(processDefinitionKey)
        .tenantIdIn(tenantId)
        .latestVersion()
        .singleResult();

    if (processDefinition != null) {
      // use tenant-specific process definition
      return tenantId;
    } else {
      // use default process definition
      return null;
    }
  }

}
