/*
 * Copyright 2012 Harald Wellmann.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied.
 *
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.ops4j.pax.cdi.test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.matchers.JUnitMatchers.hasItems;
import static org.ops4j.pax.cdi.test.TestConfiguration.cdiProviderBundles;
import static org.ops4j.pax.cdi.test.TestConfiguration.regressionDefaults;
import static org.ops4j.pax.cdi.test.TestConfiguration.workspaceBundle;
import static org.ops4j.pax.exam.CoreOptions.mavenBundle;
import static org.ops4j.pax.exam.CoreOptions.options;
import static org.ops4j.pax.exam.CoreOptions.systemProperty;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.cdi.api.Info;
import org.ops4j.pax.cdi.sample1.client.IceCreamClient;
import org.ops4j.pax.cdi.spi.CdiContainer;
import org.ops4j.pax.cdi.spi.CdiContainerFactory;
import org.ops4j.pax.exam.Configuration;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerClass;
import org.ops4j.pax.exam.util.Filter;

@RunWith(PaxExam.class)
@ExamReactorStrategy(PerClass.class)
public class NoWabWebContainerTest {

    @Inject
    private CdiContainerFactory containerFactory;

    @Inject
    @Filter
    private IceCreamClient client;

    @Configuration
    public Option[] config() {
        return options(
            regressionDefaults(),

            workspaceBundle("pax-cdi-samples/pax-cdi-sample1"),
            workspaceBundle("pax-cdi-samples/pax-cdi-sample1-client"),
            
            workspaceBundle("pax-cdi-extender"),
            workspaceBundle("pax-cdi-extension"),
            workspaceBundle("pax-cdi-api"),
            workspaceBundle("pax-cdi-spi"),
            workspaceBundle("pax-cdi-web"),
            workspaceBundle("pax-cdi-openwebbeans").startLevel(2),
            workspaceBundle("pax-cdi-web-openwebbeans"),
            
            cdiProviderBundles(),

            mavenBundle("org.apache.openwebbeans", "openwebbeans-web").versionAsInProject(),
            mavenBundle("org.apache.openwebbeans", "openwebbeans-el22").versionAsInProject(),
            
            // Pax Web

            systemProperty("org.osgi.service.http.port").value("8181"),
            mavenBundle("org.ops4j.pax.web", "pax-web-spi").version(Info.getPaxWebVersion()),
            mavenBundle("org.ops4j.pax.web", "pax-web-api").version(Info.getPaxWebVersion()),
            mavenBundle("org.ops4j.pax.web", "pax-web-extender-war").version(Info.getPaxWebVersion()),
            mavenBundle("org.ops4j.pax.web", "pax-web-extender-whiteboard").version(Info.getPaxWebVersion()),
            mavenBundle("org.ops4j.pax.web", "pax-web-jetty").version(Info.getPaxWebVersion()),
            mavenBundle("org.ops4j.pax.web", "pax-web-runtime").version(Info.getPaxWebVersion()),
            mavenBundle("org.ops4j.pax.web", "pax-web-jsp").version(Info.getPaxWebVersion()),
            mavenBundle("org.eclipse.jdt.core.compiler", "ecj").version("3.5.1"),
            mavenBundle("org.eclipse.jetty", "jetty-util").version("8.1.4.v20120524"),
            mavenBundle("org.eclipse.jetty", "jetty-io").version("8.1.4.v20120524"),
            mavenBundle("org.eclipse.jetty", "jetty-http").version("8.1.4.v20120524"),
            mavenBundle("org.eclipse.jetty", "jetty-continuation").version("8.1.4.v20120524"),
            mavenBundle("org.eclipse.jetty", "jetty-server").version("8.1.4.v20120524"),
            mavenBundle("org.eclipse.jetty", "jetty-security").version("8.1.4.v20120524"),
            mavenBundle("org.eclipse.jetty", "jetty-xml").version("8.1.4.v20120524"),
            mavenBundle("org.eclipse.jetty", "jetty-servlet").version("8.1.4.v20120524"),
            mavenBundle("org.apache.geronimo.specs", "geronimo-servlet_3.0_spec").version("1.0"),
            mavenBundle("org.osgi", "org.osgi.compendium").version("4.3.1"),
            mavenBundle("com.sun.jersey", "jersey-core").version("1.13"),
            mavenBundle("com.sun.jersey", "jersey-client").version("1.13"),
            mavenBundle("com.sun.jersey.contribs", "jersey-apache-client").version("1.13"),
            mavenBundle("org.apache.servicemix.bundles", "org.apache.servicemix.bundles.commons-httpclient", "3.1_7"),
            mavenBundle("commons-codec", "commons-codec", "1.6"),
            mavenBundle("org.slf4j", "jcl-over-slf4j", "1.6.0")
            );
    }

    @Test
    public void checkContainers() throws InterruptedException {
//        assertThat(containerFactory.getProviderName(), is("org.apache.webbeans.config.WebBeansContext"));
        assertThat(containerFactory.getContainers().size(), is(2));
        List<String> beanBundles = new ArrayList<String>();
        for (CdiContainer container : containerFactory.getContainers()) {
            beanBundles.add(container.getBundle().getSymbolicName());
        }
        assertThat(beanBundles,
            hasItems("org.ops4j.pax.cdi.sample1", "org.ops4j.pax.cdi.sample1.client"));
        assertThat(beanBundles.size(), is(2));
    }

    @Test
    public void checkBeanBundleClient() throws InterruptedException {
        assertThat(client.getFlavour(), is("Chocolate"));
    }

    @Test
    public void checkMultipleInstances() throws InterruptedException {
        assertThat(client.getAllFlavours().size(), is(2));
        assertThat(client.getAllFlavours(), hasItems("Vanilla", "Chocolate"));
    }
}
