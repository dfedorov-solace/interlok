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

package com.adaptris.util.stream;

import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;

/**
 * Workaround for a Sun JVM bug whereby it does not handle streams that have a UTF-8 BOM.
 *
 * <p>
 * This is recorded as <a href="http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=4508058">This Sun JVM bug</a>.
 * </p>
 * <p>
 * This inputstream will recognize unicode BOM marks and will skip bytes if getEncoding() method is called before any of the
 * read(...) methods. The source is taken from <a href= "http://koti.mbnet.fi/akini/java/unicodereader/UnicodeInputStream.java.txt"
 * >here</a> with minor changes.
 * </p>
 *
 * @author Thomas Weidenfeller
 * @author Aki Nieminen
 * @author $Author: lchan $
 */
public class UnicodeDetectingInputStream extends InputStream {

  public static final String UTF_16_LE = "UTF-16LE";
  public static final String UTF_16_BE = "UTF-16BE";
  public static final String UTF_8 = "UTF-8";
  public static final String UTF_32_LE = "UTF-32LE";
  public static final String UTF_32_BE = "UTF-32BE";

  private PushbackInputStream internalIn;
  private boolean isInited = false;
  private String defaultEnc;
  private String encoding;

  private static final int BOM_SIZE = 4;

  public UnicodeDetectingInputStream(InputStream in, String defaultEnc) {
    internalIn = new PushbackInputStream(in, BOM_SIZE);
    this.defaultEnc = defaultEnc;
  }

  public String getDefaultEncoding() {
    return defaultEnc;
  }

  public String getEncoding() {
    if (!isInited) {
      try {
        init();
      }
      catch (IOException ex) {
        IllegalStateException ise = new IllegalStateException("Init method failed.");
        ise.initCause(ise);
        throw ise;
      }
    }
    return encoding;
  }

  /**
   * Read-ahead four bytes and check for BOM marks. Extra bytes are unread back to the stream, only BOM bytes are skipped.
   */
  protected void init() throws IOException {
    if (isInited) {
      return;
    }

    byte bom[] = new byte[BOM_SIZE];
    int n, unread;
    n = internalIn.read(bom, 0, bom.length);

    if (bom[0] == (byte) 0x00 && bom[1] == (byte) 0x00 && bom[2] == (byte) 0xFE && bom[3] == (byte) 0xFF) {
      encoding = UTF_32_BE;
      unread = n - 4;
    }
    else if (bom[0] == (byte) 0xFF && bom[1] == (byte) 0xFE && bom[2] == (byte) 0x00 && bom[3] == (byte) 0x00) {
      encoding = UTF_32_LE;
      unread = n - 4;
    }
    else if (bom[0] == (byte) 0xEF && bom[1] == (byte) 0xBB && bom[2] == (byte) 0xBF) {
      encoding = UTF_8;
      unread = n - 3;
    }
    else if (bom[0] == (byte) 0xFE && bom[1] == (byte) 0xFF) {
      encoding = UTF_16_BE;
      unread = n - 2;
    }
    else if (bom[0] == (byte) 0xFF && bom[1] == (byte) 0xFE) {
      encoding = UTF_16_LE;
      unread = n - 2;
    }
    else {
      // Unicode BOM mark not found, unread all bytes
      encoding = defaultEnc;
      unread = n;
    }
    // System.out.println("read=" + n + ", unread=" + unread);

    if (unread > 0) {
      internalIn.unread(bom, n - unread, unread);
    }

    isInited = true;
  }

  @Override
  public void close() throws IOException {
    // init();
    isInited = true;
    internalIn.close();
  }

  @Override
  public int read() throws IOException {
    // init();
    isInited = true;
    return internalIn.read();
  }
}
