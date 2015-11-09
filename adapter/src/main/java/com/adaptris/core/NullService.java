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

package com.adaptris.core;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * <p>
 * Null implementation of <code>Service</code>.
 * </p>
 * 
 * @config null-service
 */
@XStreamAlias("null-service")
public class NullService extends ServiceImp {

  public NullService() {
    super();
  }

  public NullService(String uniqueId) {
    this();
    setUniqueId(uniqueId);
  }

  public void doService(AdaptrisMessage msg) throws ServiceException {
    // do nothing
  }

  protected void initService() throws CoreException {
    // do nothing
  }

  protected void closeService() {
    // do nothing
  }

  @Override
  public void prepare() throws CoreException {
  }

}
