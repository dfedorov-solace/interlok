/*
 * Copyright 2016 Adaptris Ltd.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.adaptris.core.common;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;

import com.adaptris.core.AdaptrisMessage;
import com.adaptris.core.AdaptrisMessageFactory;

public class MetadataStreamDataOutputParameterTest {
  private static final String UTF_8 = "UTF-8";

  private static final String TEXT = "Hello World";

  @Rule
  public TestName testName = new TestName();

  @Test
  public void testMetadataKey() throws Exception {
    MetadataStreamOutputParameter p = new MetadataStreamOutputParameter();
    assertEquals(MetadataStreamOutputParameter.DEFAULT_METADATA_KEY, p.getMetadataKey());
    p.setMetadataKey("myKey");
    assertEquals("myKey", p.getMetadataKey());
    try {
      p.setMetadataKey("");
      fail();
    } catch (IllegalArgumentException e) {

    }
    assertEquals("myKey", p.getMetadataKey());
  }
  
  @Test
  public void testContentEncoding() {
    MetadataStreamOutputParameter p = new MetadataStreamOutputParameter();
    assertNull(p.getContentEncoding());
    p.setContentEncoding(UTF_8);
    assertEquals(UTF_8, p.getContentEncoding());
    p.setContentEncoding(null);
    assertNull(p.getContentEncoding());
  }

  public void testInsert_NoEncoding() throws Exception {
    MetadataStreamOutputParameter p = new MetadataStreamOutputParameter();
    AdaptrisMessage msg = AdaptrisMessageFactory.getDefaultInstance().newMessage();
    ByteArrayInputStream in = new ByteArrayInputStream(TEXT.getBytes());
    p.insert(new InputStreamWithEncoding(in, null), msg);
    assertNotSame(TEXT, msg.getContent());
    assertEquals(TEXT, msg.getMetadataValue(MetadataStreamOutputParameter.DEFAULT_METADATA_KEY));
  }

  public void testInsert_Encoding() throws Exception {
    MetadataStreamOutputParameter p = new MetadataStreamOutputParameter();
    p.setContentEncoding(UTF_8);
    AdaptrisMessage msg = AdaptrisMessageFactory.getDefaultInstance().newMessage();
    ByteArrayInputStream in = new ByteArrayInputStream(TEXT.getBytes(UTF_8));
    p.insert(new InputStreamWithEncoding(in, null), msg);
    assertNotSame(TEXT, msg.getContent());
    assertEquals(TEXT, msg.getMetadataValue(MetadataStreamOutputParameter.DEFAULT_METADATA_KEY));
  }
}
