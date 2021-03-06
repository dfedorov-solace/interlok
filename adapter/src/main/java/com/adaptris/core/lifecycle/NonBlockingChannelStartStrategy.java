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

package com.adaptris.core.lifecycle;

import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import com.adaptris.core.AdapterLifecycleEvent;
import com.adaptris.core.Channel;
import com.adaptris.core.ChannelLifecycleStrategy;
import com.adaptris.core.CoreException;
import com.adaptris.core.DefaultChannelLifecycleStrategy;
import com.adaptris.core.util.LifecycleHelper;
import com.adaptris.core.util.ManagedThreadFactory;
import com.adaptris.util.TimeInterval;
import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * Non Blocking start strategy for channels.
 * <p>
 * This strategy attempts to start each channel in a non-blocking manner through the use of an {@link ExecutorService} for each
 * channel.
 * </p>
 * <p>
 * If this strategy is chosen then it is possible that {@link AdapterLifecycleEvent}s will be generated that do not accurately
 * reflect the internal state of the Adapter.
 * </p>
 * 
 * @config non-blocking-channel-start-strategy
 * @author lchan
 * @author $Author: lchan $
 */
@XStreamAlias("non-blocking-channel-start-strategy")
public class NonBlockingChannelStartStrategy extends DefaultChannelLifecycleStrategy {
  private static final TimeInterval DEFAULT_SHUTDOWN_WAIT = new TimeInterval(20L, TimeUnit.SECONDS.name());

  private transient Map<String, ExecutorService> channelStarters;

  public NonBlockingChannelStartStrategy() {
    channelStarters = new Hashtable<String, ExecutorService>();
  }

  /**
   * @see ChannelLifecycleStrategy#start(java.util.List)
   */
  @Override
  public void start(List<Channel> channels) throws CoreException {
    for (int i = 0; i < channels.size(); i++) {
      final Channel c = channels.get(i);
      final String name = c.hasUniqueId() ? c.getUniqueId() : "Channel(" + i + ")";
      if (!c.shouldStart()) {
        continue;
      }
      ExecutorService es = getExecutor(name);
      es.execute(new Runnable() {
        @Override
        public void run() {
          Thread.currentThread().setName(name + " Start");
          try {
            LifecycleHelper.start(c);
          }
          catch (CoreException e) {
            log.error("Failed to initialise channel " + name, e);
          }
        }
      });
    }
  }

  private ExecutorService getExecutor(String name) {
    ExecutorService es = channelStarters.get(name);
    if (es == null || es.isShutdown()) {
      es = Executors.newSingleThreadExecutor(new ManagedThreadFactory());
      channelStarters.put(name, es);
    }
    return es;
  }


  /**
   * @see ChannelLifecycleStrategy#init(java.util.List)
   */
  @Override
  public void init(List<Channel> channels) throws CoreException {
    for (int i = 0; i < channels.size(); i++) {
      final Channel c = channels.get(i);
      final String name = c.hasUniqueId() ? c.getUniqueId() : "Channel(" + i + ")";
      if (!c.shouldStart()) {
        continue;
      }
      ExecutorService es = getExecutor(name);
      es.execute(new Runnable() {
        @Override
        public void run() {
          Thread.currentThread().setName(name + " Init");
          try {
            LifecycleHelper.init(c);
          }
          catch (CoreException e) {
            log.error("Failed to initialise channel " + name, e);
          }
        }
      });
    }
  }

  @Override
  public void stop(List<Channel> channels) {
    stopExecutors(channels);
    super.stop(channels);
  }

  @Override
  public void close(List<Channel> channels) {
    stopExecutors(channels);
    super.close(channels);
  }


  // private void stopExecutors(List<Channel> channels) {
  // for (int i = 0; i < channels.size(); i++) {
  // final Channel c = channels.get(i);
  // final String name = c.hasUniqueId() ? c.getUniqueId() : "Channel(" + i + ")";
  // ExecutorService es = getExecutor(name);
  // es.shutdown();
  // try {
  // if (!es.awaitTermination(shutdownWaitMs(), TimeUnit.MILLISECONDS)) {
  // es.shutdownNow();
  // }
  // }
  // catch (InterruptedException e) {
  // log.warn("Failed to shutdown execution pool, results may be undefined for " + name);
  // }
  // }
  // }

  private void stopExecutors(List<Channel> channels) {
    for (int i = 0; i < channels.size(); i++) {
      final Channel c = channels.get(i);
      final String name = c.hasUniqueId() ? c.getUniqueId() : "Channel(" + i + ")";
      ExecutorService es = getExecutor(name);
      es.shutdownNow();
    }
  }
}
