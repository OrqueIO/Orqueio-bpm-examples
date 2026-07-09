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
package io.orqueio.bpm.quarkus.example.helloworld;

import static org.assertj.core.api.Assertions.assertThat;

import io.orqueio.bpm.engine.HistoryService;
import io.orqueio.bpm.engine.RuntimeService;
import io.orqueio.bpm.engine.TaskService;
import io.orqueio.bpm.engine.history.HistoricProcessInstance;
import io.orqueio.bpm.engine.runtime.ProcessInstance;
import io.orqueio.bpm.engine.task.Task;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

@QuarkusTest
public class HelloWorldProcessTest {

  @Inject
  RuntimeService runtimeService;

  @Inject
  TaskService taskService;

  @Inject
  HistoryService historyService;

  @AfterEach
  void cleanUp() {
    runtimeService.createProcessInstanceQuery()
        .processDefinitionKey("hello-world")
        .list()
        .forEach(pi -> runtimeService.deleteProcessInstance(pi.getId(), "test-cleanup"));

    historyService.createHistoricProcessInstanceQuery()
        .processDefinitionKey("hello-world")
        .list()
        .forEach(pi -> historyService.deleteHistoricProcessInstance(pi.getId()));
  }

  @Test
  public void shouldStartProcessAndPauseAtUserTask() {
    // when
    ProcessInstance instance = runtimeService.startProcessInstanceByKey("hello-world");

    // then — process is active, waiting at the user task
    assertThat(instance).isNotNull();
    assertThat(runtimeService.createProcessInstanceQuery()
        .processInstanceId(instance.getId())
        .count()).isOne();

    Task task = taskService.createTaskQuery()
        .processInstanceId(instance.getId())
        .singleResult();
    assertThat(task).isNotNull();
    assertThat(task.getName()).isEqualTo("do something");
    assertThat(task.getAssignee()).isEqualTo("demo");
  }

  @Test
  public void shouldCompleteProcessAfterUserTask() {
    // given
    ProcessInstance instance = runtimeService.startProcessInstanceByKey("hello-world");
    Task task = taskService.createTaskQuery()
        .processInstanceId(instance.getId())
        .singleResult();

    // when
    taskService.complete(task.getId());

    // then — SayHelloDelegate ran and process is complete
    assertThat(runtimeService.createProcessInstanceQuery()
        .processInstanceId(instance.getId())
        .count()).isZero();

    List<HistoricProcessInstance> historicInstances = historyService
        .createHistoricProcessInstanceQuery()
        .processInstanceId(instance.getId())
        .list();
    assertThat(historicInstances).hasSize(1);
    assertThat(historicInstances.get(0).getState())
        .isEqualTo(HistoricProcessInstance.STATE_COMPLETED);
  }
}
