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

package com.adaptris.core.ftp;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileCleaningTracker;
import org.apache.commons.io.FileDeleteStrategy;
import org.apache.commons.io.FileUtils;

import com.adaptris.filetransfer.FileTransferClient;
import com.adaptris.filetransfer.FileTransferException;
import com.adaptris.sftp.DefaultSftpBehaviour;
import com.adaptris.sftp.LenientKnownHosts;
import com.adaptris.sftp.StrictKnownHosts;

public class SftpKeyAuthConnectionTest extends FtpConnectionCase {

  // stop jsch auto-update host key

  protected static final String CFG_HOST = "SftpConsumerTest.host";
  protected static final String CFG_USER = "SftpConsumerTest.username";
  protected static final String CFG_PASSWORD = "SftpConsumerTest.password";
  protected static final String CFG_REMOTE_DIR = "SftpConsumerTest.remotedir";

  protected static final String CFG_PRIVATE_KEY_FILE = "SftpConsumerTest.privateKeyFile";
  protected static final String CFG_PRIVATE_KEY_PW = "SftpConsumerTest.privateKeyPassword";

  // DO NOT use the top two directly, use @setupTempHostsFile and the CFG_TEMP_HOSTS_FILE
  protected static final String CFG_KNOWN_HOSTS_FILE = "SftpConsumerTest.knownHostsFile";
  protected static final String CFG_UNKNOWN_HOSTS_FILE = "SftpConsumerTest.unknownHostsFile";
  protected static final String CFG_TEMP_HOSTS_FILE = "SftpConsumerTest.tempHostsFile";

  private static FileCleaningTracker cleaner = new FileCleaningTracker();
  private Object fileTracker = new Object();

  public SftpKeyAuthConnectionTest(String name) {
    super(name);
  }

  @Override
  protected void setUp() throws Exception {
    super.setUp();
  }

  @Override
  protected void tearDown() throws Exception {
    super.tearDown();
  }

  public void testSocketTimeout() throws Exception {
    SftpKeyAuthConnection conn = new SftpKeyAuthConnection();
    assertNull(conn.getSocketTimeout());
    assertEquals(60000, conn.socketTimeout());

    conn.setSocketTimeout(10);
    assertEquals(Integer.valueOf(10), conn.getSocketTimeout());
    assertEquals(10, conn.socketTimeout());

    conn.setSocketTimeout(null);
    assertNull(conn.getSocketTimeout());
    assertEquals(60000, conn.socketTimeout());

  }

  public void testSetSshConnectionBehaviour() throws Exception {
    SftpKeyAuthConnection conn = new SftpKeyAuthConnection();
    assertNotNull(conn.getSftpConnectionBehaviour());
    assertEquals(DefaultSftpBehaviour.class, conn.getSftpConnectionBehaviour().getClass());

    StrictKnownHosts skh = new StrictKnownHosts();

    conn.setSftpConnectionBehaviour(skh);
    assertEquals(skh, conn.getSftpConnectionBehaviour());
    try {
      conn.setSftpConnectionBehaviour(null);
      fail();
    }
    catch (IllegalArgumentException expected) {

    }
    assertEquals(skh, conn.getSftpConnectionBehaviour());
  }

  public void testConnect_InvalidPrivateKeyFile() throws Exception {

    if (areTestsEnabled()) {
      File tempHostsFile = copyHostsFile(new File(PROPERTIES.getProperty(CFG_KNOWN_HOSTS_FILE)));

      SftpKeyAuthConnection conn = createConnection();
      StrictKnownHosts knownHosts = new StrictKnownHosts(tempHostsFile.getCanonicalPath(), false);

      conn.setSftpConnectionBehaviour(knownHosts);
      conn.setPrivateKeyFilename("/some/file/that/does/not/exist");

      try {
        start(conn);
        FileTransferClient c = conn.connect(getDestinationString());
        fail();
      }
      catch (IOException expected) {

      }
      finally {
        stop(conn);
      }
    }
  }

  public void testConnect_InvalidPrivateKeyPassword() throws Exception {
    if (areTestsEnabled()) {
      File tempHostsFile = copyHostsFile(new File(PROPERTIES.getProperty(CFG_KNOWN_HOSTS_FILE)));

      SftpKeyAuthConnection conn = createConnection();
      StrictKnownHosts knownHosts = new StrictKnownHosts(tempHostsFile.getCanonicalPath(), false);

      conn.setSftpConnectionBehaviour(knownHosts);
      conn.setPrivateKeyPassword("PW:ABCDEF012345");

      try {
        start(conn);
        FileTransferClient c = conn.connect(getDestinationString());
        fail();
      }
      catch (FileTransferException expected) {
      }
      finally {
        stop(conn);
      }
    }
  }

  public void testConnectOnly_DefaultBehaviour() throws Exception {
    if (areTestsEnabled()) {
      File tempHostsFile = copyHostsFile(new File(PROPERTIES.getProperty(CFG_KNOWN_HOSTS_FILE)));

      SftpKeyAuthConnection conn = createConnection();
      conn.setSftpConnectionBehaviour(new DefaultSftpBehaviour());
      try {
        start(conn);
        FileTransferClient c = conn.connect(getDestinationString());
        assertTrue(c.isConnected());
        c.disconnect();
      }
      finally {
        stop(conn);
      }
    }
  }

  public void testConnectOnly_Compression() throws Exception {
    if (areTestsEnabled()) {
      File tempHostsFile = copyHostsFile(new File(PROPERTIES.getProperty(CFG_KNOWN_HOSTS_FILE)));

      SftpKeyAuthConnection conn = createConnection();
      StrictKnownHosts knownHosts = new StrictKnownHosts();
      knownHosts.setKnownHostsFile(tempHostsFile.getCanonicalPath());
      knownHosts.setUseCompression(true);

      conn.setSftpConnectionBehaviour(knownHosts);
      try {
        start(conn);
        FileTransferClient c = conn.connect(getDestinationString());
        assertTrue(c.isConnected());
        c.disconnect();
      }
      finally {
        stop(conn);
      }
    }

  }

  public void testConnectOnly_StrictKnownHosts_UnknownHost() throws Exception {
    if (areTestsEnabled()) {
      File tempHostsFile = copyHostsFile(new File(PROPERTIES.getProperty(CFG_UNKNOWN_HOSTS_FILE)));

      SftpKeyAuthConnection conn = createConnection();
      StrictKnownHosts knownHosts = new StrictKnownHosts();
      knownHosts.setKnownHostsFile(tempHostsFile.getCanonicalPath());
      knownHosts.setUseCompression(false);

      conn.setSftpConnectionBehaviour(knownHosts);
      try {
        start(conn);
        FileTransferClient c = conn.connect(getDestinationString());
        fail();
      }
      catch (FileTransferException expected) {

      }
      finally {
        stop(conn);
      }
    }
  }

  public void testConnectOnly_StrictKnownHosts_KnownHost() throws Exception {
    if (areTestsEnabled()) {
      File tempHostsFile = copyHostsFile(new File(PROPERTIES.getProperty(CFG_KNOWN_HOSTS_FILE)));

      SftpKeyAuthConnection conn = createConnection();
      StrictKnownHosts knownHosts = new StrictKnownHosts();
      knownHosts.setKnownHostsFile(tempHostsFile.getCanonicalPath());
      knownHosts.setUseCompression(false);

      conn.setSftpConnectionBehaviour(knownHosts);
      try {
        start(conn);
        FileTransferClient c = conn.connect(getDestinationString());
        assertTrue(c.isConnected());
        c.disconnect();
      }
      finally {
        stop(conn);
      }
    }
  }

  public void testConnectOnly_LenientKnownHosts_UnknownHost() throws Exception {
    if (areTestsEnabled()) {
      File tempHostsFile = copyHostsFile(new File(PROPERTIES.getProperty(CFG_UNKNOWN_HOSTS_FILE)));

      SftpKeyAuthConnection conn = createConnection();
      LenientKnownHosts knownHosts = new LenientKnownHosts();
      knownHosts.setKnownHostsFile(tempHostsFile.getCanonicalPath());
      knownHosts.setUseCompression(true);

      conn.setSftpConnectionBehaviour(knownHosts);
      try {
        start(conn);
        FileTransferClient c = conn.connect(getDestinationString());
        assertTrue(c.isConnected());
        c.disconnect();
      }
      finally {
        stop(conn);
      }
    }
  }

  public void testConnectOnly_LenientKnownHosts_KnownHost() throws Exception {
    if (areTestsEnabled()) {
      File tempHostsFile = copyHostsFile(new File(PROPERTIES.getProperty(CFG_KNOWN_HOSTS_FILE)));

      SftpKeyAuthConnection conn = createConnection();
      LenientKnownHosts knownHosts = new LenientKnownHosts();
      knownHosts.setKnownHostsFile(tempHostsFile.getCanonicalPath());
      knownHosts.setUseCompression(false);

      conn.setSftpConnectionBehaviour(knownHosts);
      try {
        start(conn);
        FileTransferClient c = conn.connect(getDestinationString());
        assertTrue(c.isConnected());
        c.disconnect();
      }
      finally {
        stop(conn);
      }
    }
  }

  private File copyHostsFile(File srcKnownHosts) throws Exception {
    File tempDir = new File(PROPERTIES.getProperty(CFG_TEMP_HOSTS_FILE));
    if (!tempDir.exists() && !tempDir.mkdirs()) {
      throw new Exception("Couldn't make directory " + tempDir.getCanonicalPath());
    }
    File tempFile = File.createTempFile(SftpKeyAuthConnectionTest.class.getSimpleName(), "", tempDir);
    FileUtils.copyFile(srcKnownHosts, tempFile);
    tempFile.deleteOnExit();
    cleaner.track(tempFile, fileTracker, FileDeleteStrategy.FORCE);
    return tempFile;
  }



  @Override
  protected String getDestinationString() {
    return "sftp://" + PROPERTIES.getProperty(CFG_HOST) + "/" + PROPERTIES.getProperty(CFG_REMOTE_DIR);
  }

  @Override
  protected SftpKeyAuthConnection createConnection() throws Exception {
    File tempHostsFile = copyHostsFile(new File(PROPERTIES.getProperty(CFG_KNOWN_HOSTS_FILE)));

    SftpKeyAuthConnection c = new SftpKeyAuthConnection();
    c.setPrivateKeyFilename(PROPERTIES.getProperty(CFG_PRIVATE_KEY_FILE));
    c.setPrivateKeyPassword(PROPERTIES.getProperty(CFG_PRIVATE_KEY_PW));
    c.setDefaultUserName(PROPERTIES.getProperty(CFG_USER));
    c.setSftpConnectionBehaviour(new LenientKnownHosts(tempHostsFile.getCanonicalPath(), false));
    c.setAdditionalDebug(true);
    return c;
  }

  @Override
  protected String getDestinationStringWithOverride() throws Exception {
    return "sftp://" + PROPERTIES.getProperty(CFG_USER) + "@" + PROPERTIES.getProperty(CFG_HOST) + "/"
        + PROPERTIES.getProperty(CFG_REMOTE_DIR);
  }

  protected void assertDefaultControlPort(int defaultControlPort) {
    assertEquals(SftpKeyAuthConnection.DEFAULT_CONTROL_PORT, defaultControlPort);
  }
}
