/*
 * Copyright 2015 Adaptris Ltd.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
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

package com.adaptris.core.http.jetty;

import com.adaptris.core.AdaptrisConnection;
import com.adaptris.core.AdaptrisMessageProducer;
import com.adaptris.core.Channel;
import com.adaptris.core.ConfiguredConsumeDestination;
import com.adaptris.core.DefaultEventHandler;
import com.adaptris.core.EventHandler;
import com.adaptris.core.NullProcessingExceptionHandler;
import com.adaptris.core.Service;
import com.adaptris.core.ServiceList;
import com.adaptris.core.StandaloneProducer;
import com.adaptris.core.StandardWorkflow;
import com.adaptris.core.Workflow;
import com.adaptris.core.http.server.HttpStatusProvider.HttpStatus;
import com.adaptris.core.stubs.MockChannel;

/**
 * @author lchan
 *
 */
public class JettyHelper {

  public static Channel createChannel(AdaptrisConnection connection, MessageConsumer consumer, AdaptrisMessageProducer producer)
      throws Exception {
    return createChannel(connection, createWorkflow(consumer, producer));
  }

  public static Channel createChannel(AdaptrisConnection connection, Workflow w) throws Exception {
    Channel result = new MockChannel();
    result.setUniqueId("channel");
    result.registerEventHandler(createEventHandler());
    result.setMessageErrorHandler(new NullProcessingExceptionHandler());
    result.setConsumeConnection(connection);
    result.getWorkflowList().add(w);
    return result;
  }

  public static Workflow createWorkflow(MessageConsumer consumer, AdaptrisMessageProducer producer) {
    return createWorkflow(consumer, producer, new StandardResponseProducer(HttpStatus.OK_200));
  }

  public static Workflow createWorkflow(MessageConsumer consumer, AdaptrisMessageProducer producer, ResponseProducer responder) {
    return createWorkflow(consumer, producer, new ServiceList(new Service[]
    {
      new StandaloneProducer(responder)
    }));
  }

  public static Workflow createWorkflow(MessageConsumer consumer, AdaptrisMessageProducer producer,
      StandardResponseProducer responder) {
    return createWorkflow(consumer, producer, new ServiceList(new Service[] {new StandaloneProducer(responder)}));
  }


  public static Workflow createWorkflow(MessageConsumer consumer, AdaptrisMessageProducer producer, ServiceList list) {
    StandardWorkflow wf = new StandardWorkflow();
    wf.setConsumer(consumer);
    wf.setProducer(producer);
    wf.setServiceCollection(list);
    return wf;
  }

  public static MessageConsumer createConsumer(String dest) {
    MessageConsumer consumer = new MessageConsumer();
    consumer.setAdditionalDebug(true);
    consumer.setDestination(new ConfiguredConsumeDestination(dest));
    return consumer;
  }

  private static EventHandler createEventHandler() throws Exception {
    DefaultEventHandler sch = new DefaultEventHandler();
    sch.requestStart();
    return sch;
  }
}
