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

package com.adaptris.fs;

import java.io.File;


/**
 */
public class AppendingFsWorkerTest extends StandardWorkerTest {
  public AppendingFsWorkerTest(String arg0) {
    super(arg0);
  }

  @Override
  protected AppendingFsWorker createWorker() {
    return new AppendingFsWorker();
  }

  @Override
  public void testPutFileExists() throws Exception {
    FsWorker worker = createWorker();
    String[] testFiles = createTestFiles();
    worker.put(DATA.getBytes(), new File(baseDir, testFiles[0]));
    // So it should have appended it.
    byte[] readBytes = worker.get(new File(baseDir, testFiles[0]));

    assertEquals(DATA + DATA, new String(readBytes));
  }
}
