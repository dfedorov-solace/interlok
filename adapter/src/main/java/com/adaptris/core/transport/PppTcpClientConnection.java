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

package com.adaptris.core.transport;

import com.adaptris.annotation.AdvancedConfig;
import com.adaptris.annotation.ComponentProfile;
import com.adaptris.annotation.DisplayOrder;
import com.adaptris.transport.Transport;
import com.adaptris.transport.TransportException;
import com.adaptris.transport.ppp.PppSocketTransport;
import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * Class that acts as configuration for a client tcp connection over PPP.
 * 
 * @config ppp-tcp-client-connection
 * 
 * @author lchan
 * @author $Author: hfraser $
 */
@XStreamAlias("ppp-tcp-client-connection")
@ComponentProfile(summary = "Connection for a client tcp connection over PPP", tag = "connections,socket,tcp")
@DisplayOrder(order = {"host", "port", "connectCommand", "disconnectCommand", "checkConnectionCommand", "noConnectionFilter",
    "timeout", "blocksize"})
public class PppTcpClientConnection extends TcpClientConnection {

  private String connectCommand;
  private String disconnectCommand;
  @AdvancedConfig
  private String checkConnectionCommand;
  @AdvancedConfig
  private String noConnectionFilter;

  /** @see Object#Object()
   *
   *
   */
  public PppTcpClientConnection() {
    super();
  }

  public PppTcpClientConnection(String host, int port, String connect, String disconnect) {
    this();
    setConnectCommand(connect);
    setDisconnectCommand(disconnect);
    setHost(host);
    setPort(port);
  }

  /** Set the connect command to use to dial.
   *  <p>e.g. <code>rasdial "MyAccount" "MyUser" "MyPassword"</code></p>
   * @param s the connect command
   */
  public void setConnectCommand(String s) {
    connectCommand = s;
  }

  /** Return the configured connect command.
   *
   * @return the connect command.
   */
  public String getConnectCommand() {
    return connectCommand;
  }

  /** Set the connect command to use to dial.
   *  <p>e.g. <code>rasdial /disconnect</code></p>
   * @param s the connect command
   */
  public void setDisconnectCommand(String s) {
    disconnectCommand = s;
  }

  /** Get the configured disconnect command.
   *
   * @return the command.
   */
  public String getDisconnectCommand() {
    return disconnectCommand;
  }

  /** Set the command for checking a connection.
   *  <p>If this is null or empty, then no checks are performed prior to
   *  attempting the connect command.
   *  </p>
   * @param s the command to check a connection
   */
  public void setCheckConnectionCommand(String s) {
    checkConnectionCommand = s;
  }

  /** Get the command for checking a connection.
   *
   * @return the command.
   */
  public String getCheckConnectionCommand() {
    return checkConnectionCommand;
  }

  /** Set the filter for checkig there is no connection present.
   *  <p>If this is null, then no check is performed prior to attempting
   *  a connect command
   *  @see String#matches(String)
   * @param s the filter.
   */
  public void setNoConnectionFilter(String s) {
    noConnectionFilter = s;
  }

  /** Get the connection filter.
   *
   * @return the filter.
   */
  public String getNoConnectionFilter() {
    return noConnectionFilter;
  }

  /** @see com.adaptris.core.transport.TransportConfig#createTransport()
   */
  @Override
  public Transport createTransport() throws TransportException {
    return initialiseTransport();
  }

  private Transport initialiseTransport() throws TransportException {
    PppSocketTransport layer = new PppSocketTransport();
    layer.setBlockSize(getBlockSize());
    layer.setPort(getPort());
    layer.setHost(getHost());
    layer.setConnectTimeout(getTimeoutMs());
    layer.setNoConnectionFilter(getNoConnectionFilter());
    layer.setConnectCommand(getConnectCommand());
    layer.setCheckConnectionCommand(getCheckConnectionCommand());
    layer.setDisconnectCommand(getDisconnectCommand());
    return layer;
  }
}
