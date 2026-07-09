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
package io.orqueio.bpm.quarkus.example.datasource;

import io.orqueio.bpm.engine.repository.Deployment;
import io.orqueio.bpm.quarkus.engine.extension.event.OrqueioEngineStartupEvent;

import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;

import io.orqueio.bpm.engine.RepositoryService;
import org.jboss.logging.Logger;

public class Deployments {

  protected static final Logger LOG = Logger.getLogger(Deployments.class);

  @Inject
  protected RepositoryService repositoryService;

  public void performDeployment(@Observes OrqueioEngineStartupEvent startupEvent) {
    Deployment deployment = repositoryService.createDeployment()
        .enableDuplicateFiltering(true)
        .name("example-deployment")
        .addClasspathResource("bpmn/process.bpmn")
        .deploy();

    LOG.infov("Deployment with id {0} created", deployment.getId());
  }

}