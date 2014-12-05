/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.cxf.systest.jms.tx;

import java.net.URL;

import javax.xml.namespace.QName;

import org.apache.cxf.testutil.common.AbstractBusClientServerTestBase;
import org.apache.cxf.testutil.common.AbstractBusTestServerBase;
import org.apache.cxf.testutil.common.EmbeddedJMSBrokerLauncher;
import org.apache.hello_world_doc_lit.Greeter;
import org.apache.hello_world_doc_lit.SOAPService2;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class JMSTransactionClientServerSameSessionTest extends AbstractBusClientServerTestBase {
    static EmbeddedJMSBrokerLauncher broker;

    public static class Server extends AbstractBusTestServerBase {
        protected void run()  {
            // create the application context
            ClassPathXmlApplicationContext context = 
                new ClassPathXmlApplicationContext(
                        "org/apache/cxf/systest/jms/tx/jms_server_config-same-session.xml");
            context.start();
        }
    }

    @BeforeClass
    public static void startServers() throws Exception {
        broker = new EmbeddedJMSBrokerLauncher("vm://JMSTransactionClientServerTest");
        System.setProperty("EmbeddedBrokerURL", broker.getBrokerURL());
        launchServer(broker);
        launchServer(new Server());
        createStaticBus();
    }
    
    @AfterClass
    public static void clearProperty() {
        System.clearProperty("EmbeddedBrokerURL");
    }
    
    public URL getWSDLURL(String s) throws Exception {
        return getClass().getResource(s);
    }
    
    @Test
    public void testDocBasicConnection() throws Exception {
        QName serviceName = new QName("http://apache.org/hello_world_doc_lit", "SOAPService2");
        QName portName = new QName("http://apache.org/hello_world_doc_lit", "SoapPort2");
        URL wsdl = getWSDLURL("/wsdl/hello_world_doc_lit.wsdl");
        assertNotNull(wsdl);
        String wsdlString = wsdl.toString();
        SOAPService2 service = new SOAPService2(wsdl, serviceName);
        broker.updateWsdl(getBus(), wsdlString);
        assertNotNull(service);

        Greeter greeter = service.getPort(portName, Greeter.class);
        greeter.greetMe("Bad guy");
    }
}
