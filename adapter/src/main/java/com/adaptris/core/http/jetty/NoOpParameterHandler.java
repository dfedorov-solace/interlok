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

import javax.servlet.http.HttpServletRequest;

import com.adaptris.core.AdaptrisMessage;
import com.adaptris.core.http.server.ParameterHandler;
import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * {@link ParameterHandler} implementation that ignores HTTP parameters.
 * 
 * @config jetty-http-ignore-parameters
 * 
 */
@XStreamAlias("jetty-http-ignore-parameters")
public class NoOpParameterHandler implements ParameterHandler<HttpServletRequest> {

  @Override
  public void handleParameters(AdaptrisMessage message, HttpServletRequest request, String itemPrefix) {
    // No operation
  }

  @Override
  public void handleParameters(AdaptrisMessage message, HttpServletRequest request) {}

}
