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

import io.orqueio.bpm.engine.RuntimeService;
import io.orqueio.bpm.engine.TaskService;
import io.orqueio.bpm.engine.runtime.ProcessInstance;
import io.orqueio.bpm.engine.task.Task;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@ApplicationScoped
@Path("/hello-world")
@Produces(MediaType.APPLICATION_JSON)
public class HelloWorldResource {

  @Inject
  RuntimeService runtimeService;

  @Inject
  TaskService taskService;

  @POST
  @Path("/start")
  public Response startProcess() {
    ProcessInstance instance = runtimeService.startProcessInstanceByKey("hello-world");
    Map<String, String> result = new HashMap<>();
    result.put("processInstanceId", instance.getId());
    result.put("status", "started");
    return Response.ok(result).build();
  }

  @GET
  @Path("/tasks")
  public List<Map<String, String>> getTasks() {
    return taskService.createTaskQuery()
        .processDefinitionKey("hello-world")
        .list()
        .stream()
        .map(task -> {
          Map<String, String> m = new HashMap<>();
          m.put("id", task.getId());
          m.put("name", task.getName());
          m.put("assignee", task.getAssignee());
          m.put("processInstanceId", task.getProcessInstanceId());
          return m;
        })
        .collect(Collectors.toList());
  }

  @POST
  @Path("/tasks/{taskId}/complete")
  public Response completeTask(@PathParam("taskId") String taskId) {
    Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
    if (task == null) {
      throw new NotFoundException("Task not found: " + taskId);
    }
    taskService.complete(taskId);
    Map<String, String> result = new HashMap<>();
    result.put("taskId", taskId);
    result.put("status", "completed");
    return Response.ok(result).build();
  }

  @GET
  @Path("/instances")
  public List<Map<String, String>> getInstances() {
    return runtimeService.createProcessInstanceQuery()
        .processDefinitionKey("hello-world")
        .list()
        .stream()
        .map(instance -> {
          Map<String, String> m = new HashMap<>();
          m.put("id", instance.getId());
          m.put("processDefinitionId", instance.getProcessDefinitionId());
          return m;
        })
        .collect(Collectors.toList());
  }
}
