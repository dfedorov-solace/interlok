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

package com.adaptris.core.services;

import java.util.concurrent.TimeUnit;

import com.adaptris.core.AdaptrisMessageFactory;
import com.adaptris.core.GeneralServiceExample;
import com.adaptris.util.TimeInterval;

public class WaitServiceTest extends GeneralServiceExample {

  public WaitServiceTest(String name) {
    super(name);
  }

  @Override
  protected Object retrieveObjectForSampleConfig() {
    return new WaitService();
  }

  public void testSetWaitInterval() {
    WaitService srv = new WaitService();
    assertNull(srv.getWaitInterval());
    assertEquals(20000, srv.waitMs());
    
    TimeInterval newInterval = new TimeInterval(10L, TimeUnit.SECONDS);
    srv.setWaitInterval(newInterval);
    assertEquals(newInterval, srv.getWaitInterval());
    assertEquals(10000, srv.waitMs());

    srv.setWaitInterval(null);
    assertNull(srv.getWaitInterval());
    assertEquals(20000, srv.waitMs());
  }


  public void testDoService() throws Exception {
    long now = System.currentTimeMillis();
    WaitService srv = new WaitService(new TimeInterval(10L, TimeUnit.MILLISECONDS));
    execute(srv, AdaptrisMessageFactory.getDefaultInstance().newMessage("Hello"));
    assertTrue(now < System.currentTimeMillis());
  }

  public void testDoServiceRandomize() throws Exception {
    long now = System.nanoTime();
    WaitService srv = new WaitService(new TimeInterval(10L, TimeUnit.MILLISECONDS), true);
    execute(srv, AdaptrisMessageFactory.getDefaultInstance().newMessage("Hello"));
    assertTrue(now < System.nanoTime());
  }

}
