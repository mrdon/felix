/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.felix.karaf.gshell.itests;

import org.apache.geronimo.gshell.commandline.CommandLineExecutionFailed;
import org.apache.geronimo.gshell.registry.NoSuchCommandException;
import org.apache.geronimo.gshell.shell.Shell;
import org.apache.geronimo.gshell.command.Command;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Option;
import static org.ops4j.pax.exam.CoreOptions.options;
import static org.ops4j.pax.exam.CoreOptions.systemProperty;
import static org.ops4j.pax.exam.CoreOptions.systemPackages;
import static org.ops4j.pax.exam.CoreOptions.bootClasspathLibrary;
import static org.ops4j.pax.exam.CoreOptions.felix;
import org.ops4j.pax.exam.junit.Configuration;
import org.ops4j.pax.exam.junit.JUnit4TestRunner;
import org.osgi.framework.Bundle;

@RunWith(JUnit4TestRunner.class)
public class CoreTest extends AbstractIntegrationTest {

    @Test
    public void testHelp() throws Exception {
        Thread.sleep(5000);

        Shell shell = getOsgiService(Shell.class);
        shell.execute("help");
    }

    @Test
    public void testInstallCommand() throws Exception {
        Thread.sleep(5000);

        Shell shell = getOsgiService(Shell.class);

        try {
            shell.execute("log/display");
            fail("command should not exist");
        } catch (CommandLineExecutionFailed e) {
            assertNotNull(e.getCause());
            assertTrue(e.getCause() instanceof NoSuchCommandException);
        }

        Bundle b = getInstalledBundle("org.apache.felix.karaf.gshell.log");
        b.start();

        Thread.sleep(1000);

        shell.execute("log/display");

        b.stop();

        Thread.sleep(1000);

        try {
            shell.execute("log/display");
            fail("command should not exist");
        } catch (CommandLineExecutionFailed e) {
            assertNotNull(e.getCause());
            assertTrue(e.getCause() instanceof NoSuchCommandException);
        }
    }

    @Test
    public void testCommandGroup() throws Exception {
        Thread.sleep(5000);

        Shell shell = getOsgiService(Shell.class);
        shell.execute("osgi");
        shell.execute("help");
        shell.execute("..");
    }
    
    @Test
    public void testCommandGroupAfterInstall() throws Exception {
        Bundle b = getInstalledBundle("org.apache.felix.karaf.gshell.log");
        b.start();

        Thread.sleep(5000);

        Shell shell = getOsgiService(Shell.class);
        shell.execute("log");
        shell.execute("help");
        shell.execute("..");
    }

    @Configuration
    public static Option[] configuration() {
        Option[] options = options(
            // this is how you set the default log level when using pax logging (logProfile)
            systemProperty("org.ops4j.pax.logging.DefaultServiceLog.level").value("DEBUG"),
            systemProperty("karaf.name").value("root"),
            systemProperty("karaf.home").value("target/karaf.home"),
            systemProperty("karaf.base").value("target/karaf.home"),
            systemProperty("karaf.startLocalConsole").value("false"),
            systemProperty("karaf.startRemoteShell").value("false"),

            // hack system packages
            systemPackages("org.apache.felix.karaf.main.spi;version=1.0.0", "org.apache.felix.karaf.jaas.boot;version=1.2.0"),
            bootClasspathLibrary(mavenBundle("org.apache.felix.karaf.jaas", "org.apache.felix.karaf.jaas.boot")).afterFramework(),
            bootClasspathLibrary(mavenBundle("org.apache.felix.karaf", "org.apache.felix.karaf.main")).afterFramework(),

            // Log
            mavenBundle("org.ops4j.pax.logging", "pax-logging-api"),
            mavenBundle("org.ops4j.pax.logging", "pax-logging-service"),
            // Felix Config Admin
            mavenBundle("org.apache.felix", "org.apache.felix.configadmin"),
            // Spring-DM
            mavenBundle("org.apache.servicemix.bundles", "org.apache.servicemix.bundles.aopalliance"),
            mavenBundle("org.apache.servicemix.bundles", "org.apache.servicemix.bundles.cglib"),
            mavenBundle("org.springframework", "spring-aop"),
            mavenBundle("org.springframework", "spring-beans"),
            mavenBundle("org.springframework", "spring-context"),
            mavenBundle("org.springframework", "spring-core"),
            mavenBundle("org.springframework.osgi", "spring-osgi-core"),
            mavenBundle("org.springframework.osgi", "spring-osgi-extender"),
            mavenBundle("org.springframework.osgi", "spring-osgi-io"),

            // Bundles
            mavenBundle("org.apache.servicemix.bundles", "org.apache.servicemix.bundles.jline"),
            mavenBundle("org.apache.servicemix.bundles", "org.apache.servicemix.bundles.commons-httpclient"),
            mavenBundle("org.apache.servicemix.bundles", "org.apache.servicemix.bundles.commons-jexl"),
            mavenBundle("org.apache.servicemix.bundles", "org.apache.servicemix.bundles.commons-vfs"),
            mavenBundle("org.apache.mina", "mina-core"),
            mavenBundle("org.apache.servicemix.bundles", "org.apache.servicemix.bundles.oro"),
            mavenBundle("org.apache.felix.karaf.jaas", "org.apache.felix.karaf.jaas.config"),
            mavenBundle("org.apache.sshd", "sshd-core"),
            mavenBundle("org.apache.felix.karaf.gshell", "org.apache.felix.karaf.gshell.core"),
            mavenBundle("org.apache.felix.karaf.gshell", "org.apache.felix.karaf.gshell.run"),
            mavenBundle("org.apache.felix.karaf.gshell", "org.apache.felix.karaf.gshell.osgi"),
            mavenBundle("org.apache.felix.karaf.gshell", "org.apache.felix.karaf.gshell.log").noStart(),

            felix()
        );
        return options;
    }

}