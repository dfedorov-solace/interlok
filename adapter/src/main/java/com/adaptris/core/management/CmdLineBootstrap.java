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

package com.adaptris.core.management;

import static com.adaptris.core.management.Constants.CFG_KEY_START_QUIETLY;
import static com.adaptris.core.management.Constants.DBG;

import java.io.PrintWriter;
import java.io.StringWriter;

import com.adaptris.core.runtime.AdapterManagerMBean;
import com.adaptris.core.util.ManagedThreadFactory;

/**
 * Abstract boostrap that contains standard commandline parsing.
 * 
 * @author lchan
 */
abstract class CmdLineBootstrap {

  private static final String[] ARG_CONFIG_CHECK = new String[]
  {
      "-configtest", "-configcheck", "--configtest", "--configcheck"

  };

  private static final String[] ARG_VERSION = new String[]
  {
      "-version", "--version"

  };

  private static final String[] ARG_BOOTSTRAP_PROPERTIES = new String[]
  {
      "-file", "--file"

  };

  private transient String bootstrapResource;
  private transient ArgUtil arguments;
  private transient boolean startAdapter = true;

  protected boolean isStartAdapter() {
    return startAdapter;
  }

  protected String getBootstrapResource() {
    return bootstrapResource;
  }

  protected ArgUtil getCommandlineArguments() {
    return arguments;
  }

  protected CmdLineBootstrap(String[] argv) throws Exception {
    bootstrapResource = Constants.DEFAULT_PROPS_RESOURCE;
    arguments = ArgUtil.getInstance(argv);
    if (argv.length == 1 && !argv[0].startsWith("-")) {
      bootstrapResource = argv[0];
    }
    parseCommandline();
  }

  public abstract void boot() throws Exception;

  protected void startAdapter(BootstrapProperties bootProperties) throws Exception {
    boolean startQuietly = Boolean.valueOf(bootProperties.getProperty(CFG_KEY_START_QUIETLY, "true")).booleanValue();
    final UnifiedBootstrap bootstrap = new UnifiedBootstrap(bootProperties);
    AdapterManagerMBean adapter = bootstrap.createAdapter();
    if (startAdapter) {
      bootstrap.init(adapter);
      Runtime.getRuntime().addShutdownHook(new ShutdownHandler(bootProperties.getConfigManager().getAdapterRegistry()));
      launchAdapter(bootstrap, startQuietly);
    }
  }

  private void parseCommandline() throws Exception {
    // In the event that someone is using
    // -ignoreSubDirs then we have the possibility that we can't load
    // from a "different" file than bootstrap.properties
    // So we have a specific -arg for specifying a different file.
    if (arguments.hasArgument(ARG_BOOTSTRAP_PROPERTIES)) {
      bootstrapResource = arguments.getArgument(ARG_BOOTSTRAP_PROPERTIES);
    }
    if (arguments.hasArgument(ARG_CONFIG_CHECK)) {
      bootstrapResource = arguments.getArgument(ARG_CONFIG_CHECK);
      startAdapter = false;
    }
  }

  /**
   * Log version information.
   * <p>
   * Note that if the commandline arguments contain "-version" then System.exit() will be invoked after printing out version
   * information.
   * </p>
   */
  protected void logVersionInformation() {
    // Version information
    logBuildVersion();
    if (arguments.hasArgument(ARG_VERSION)) {
      reportPackageVersions();
      System.exit(0);
    }
  }

  private static void logBuildVersion() {
    VersionReport r = VersionReport.getInstance();
    System.err.println(String.format("Bootstrap of Interlok %1$s complete", r.getAdapterBuildVersion()));
  }

  private static void reportPackageVersions() {
    VersionReport r = VersionReport.getInstance();
    System.err.println("Version Information");
    for (String modules : r.getReport()) {
      System.err.println("  " + modules);
    }
    return;
  }

  // Jira 154 : If the adapter is configured with a shared connection that has a fixed-number of retries
  // which fails, then it throws an exception back to "main" which can terminate the JVM depending
  // on how you're starting it.
  // Not so much if you're using java -jar, but definitely if you're using the wrapper script.
  private void launchAdapter(final UnifiedBootstrap bootstrap, boolean quietly) throws Exception {
    final String threadName = this.getClass().getSimpleName();
    if (quietly) {
      Thread launcher = new ManagedThreadFactory().newThread(new Runnable() {
        @Override
        public void run() {
          Thread.currentThread().setName(threadName);
          try {
            bootstrap.start();
          }
          catch (Exception e) {
            System.err.println("(Error) Adapter Startup failure :" + e.getMessage());
            logException(e);
          }
        }
      });
      launcher.setDaemon(false);
      launcher.start();
    }
    else {
      bootstrap.start();
    }
  }

  private static void logException(Exception e) {
    if (DBG) {
      try (StringWriter sw = new StringWriter(); PrintWriter pw = new PrintWriter(sw, true)) {
        pw.println("(Error) Adapter Startup Failure Exception Details");
        e.printStackTrace(pw);
        System.err.println(sw.toString());
      }
      catch (Exception exc) {

      }
    }
  }
}
