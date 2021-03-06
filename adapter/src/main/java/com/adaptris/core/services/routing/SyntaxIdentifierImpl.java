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

package com.adaptris.core.services.routing;

import java.util.ArrayList;
import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamImplicit;

public abstract class SyntaxIdentifierImpl implements SyntaxIdentifier {
  private String destination = null;
  @XStreamImplicit(itemFieldName = "pattern")
  private List<String> patterns = null;

  public SyntaxIdentifierImpl() {
    patterns = new ArrayList<String>();
  }

  /**
   *  @see SyntaxIdentifier#setDestination(java.lang.String)
   */
  @Override
  public void setDestination(String dest) {
    if (dest == null) {
      throw new IllegalArgumentException("dest is null");
    }
    destination = dest;
  }

  /**
   *  @see SyntaxIdentifier#getDestination()
   */
  @Override
  public String getDestination() {
    return destination;
  }

  /**
   *  @see SyntaxIdentifier#addPattern(java.lang.String)
   */
  @Override
  public void addPattern(String pattern) {
    if (pattern == null) {
      throw new IllegalArgumentException("pattern is null");
    }
    patterns.add(pattern);
  }

  /**
   *  @see SyntaxIdentifier#getPatterns()
   */
  @Override
  public List<String> getPatterns() {
    return patterns;
  }

  /**
   *  @see SyntaxIdentifier#getPatterns()
   */
  @Override
  public void setPatterns(List<String> l) {
    if (l == null) {
      throw new IllegalArgumentException("list is null");
    }
    patterns = l;
  }
}
