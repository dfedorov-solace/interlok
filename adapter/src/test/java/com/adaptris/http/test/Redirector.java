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

package com.adaptris.http.test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.Iterator;
import java.util.Properties;

import com.adaptris.http.Http;
import com.adaptris.http.HttpException;
import com.adaptris.http.HttpHeaders;
import com.adaptris.http.HttpMessage;
import com.adaptris.http.HttpResponse;
import com.adaptris.http.HttpSession;
import com.adaptris.util.stream.StreamUtil;

/**
 * @author lchan
 * @author $Author: lchan $
 */
public class Redirector extends BaseRequestProcessor {

  Redirector(String uri, Integer id, Properties config) {
    super(uri, id, config);
  }

  /**
   * Process in the request in some fashion
   *
   * @param session the HttpSession.
   *
   */
  public synchronized void processRequest(HttpSession session)
      throws IOException, IllegalStateException {

    try {
      String redirectUrl = getRedirectUrl(config);
      HttpMessage request = session.getRequestMessage();
      HttpHeaders requestHeaders = request.getHeaders();
      logR.debug("Read \n" + requestHeaders.toString());

      ByteArrayOutputStream out = new ByteArrayOutputStream();
      if (requestHeaders.getContentLength() > 0) {
        StreamUtil.copyStream(request.getInputStream(), out, requestHeaders
            .getContentLength());
      }
      logR.debug("Read Data portion\n" + out);

      HttpResponse httpResponse = session.getResponseLine();
      httpResponse.setResponseCode(HttpURLConnection.HTTP_MOVED_TEMP);
      httpResponse.setResponseMessage("Moved Temporarily");
      HttpMessage reply = session.getResponseMessage();
      HttpHeaders replyHeaders = reply.getHeaders();
      replyHeaders.put(Http.LOCATION, redirectUrl);
    }
    catch (HttpException e) {
      IOException ioe = new IOException(e.getMessage());
      ioe.initCause(e);
      throw ioe;
    }
    return;
  }

  private String getRedirectUrl(Properties p) throws IOException {
    Iterator i = p.keySet().iterator();
    logR.trace("Configuration " + p);
    while (i.hasNext()) {
      String key = i.next().toString();
      if (key.indexOf("redirectto") > -1) {
        return p.getProperty(key);
      }
    }
    throw new IOException("No configured redirectto key");
  }
}
