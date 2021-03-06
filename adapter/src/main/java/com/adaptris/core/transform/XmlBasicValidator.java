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

package com.adaptris.core.transform;

import org.w3c.dom.Document;

import com.adaptris.core.AdaptrisMessage;
import com.adaptris.core.CoreException;
import com.adaptris.core.util.DocumentBuilderFactoryBuilder;
import com.adaptris.core.util.ExceptionHelper;
import com.adaptris.core.util.XmlHelper;
import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * Used with {@link XmlValidationService} to validate that a message is in fact XML.
 * 
 * @config xml-basic-validator
 * 
 * @author lchan
 * 
 */
@XStreamAlias("xml-basic-validator")
public class XmlBasicValidator extends MessageValidatorImpl {

  @Override
  public void validate(AdaptrisMessage msg) throws CoreException {
    try {
      Document d = XmlHelper.createDocument(msg, new DocumentBuilderFactoryBuilder());
    }
    catch (Exception e) {
      ExceptionHelper.rethrowCoreException(e);
    }
  }
}
