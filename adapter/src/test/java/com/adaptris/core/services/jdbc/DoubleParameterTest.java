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

package com.adaptris.core.services.jdbc;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class DoubleParameterTest {

  @Before
  public void setUp() throws Exception {
  }

  @After
  public void tearDown() throws Exception {
  }

  @Test
  public void testConvert() throws Exception {
    DoubleStatementParameter sp = new DoubleStatementParameter();
    assertEquals(Double.valueOf(55.0), sp.toDouble("55.0"));
  }

  @Test
  public void testConvertNull() throws Exception {
    DoubleStatementParameter sp = new DoubleStatementParameter();
    sp.setConvertNull(false);
    try {
      sp.toDouble(null);
      fail("Expected Exception");
    }
    catch (RuntimeException expected) {
      // expected
    }
    try {
      sp.toDouble("");
      fail("Expected Exception");
    }
    catch (RuntimeException expected) {
      // expected
    }
  }

  @Test
  public void testConvertWithConvertNull() throws Exception {
    DoubleStatementParameter sp = new DoubleStatementParameter();
    sp.setConvertNull(true);
    assertEquals(Double.valueOf(0), sp.toDouble(""));
  }

}
