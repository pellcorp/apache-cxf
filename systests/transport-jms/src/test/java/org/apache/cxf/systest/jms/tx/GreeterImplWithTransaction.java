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

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import javax.jws.WebService;

import org.apache.cxf.systest.jms.GreeterImplDocBase;
import org.apache.hello_world_doc_lit.PingMeFault;
import org.apache.hello_world_doc_lit.types.FaultDetail;

@WebService(endpointInterface = "org.apache.hello_world_doc_lit.Greeter")
public class GreeterImplWithTransaction extends GreeterImplDocBase {
    private AtomicBoolean flag = new AtomicBoolean(true);
    private AtomicInteger countRetry = new AtomicInteger();
    
    public String greetMe(String requestType) {
        //System.out.println("Reached here :" + requestType);
        if ("Bad guy".equals(requestType)) {
            if (flag.getAndSet(false)) {
                //System.out.println("Throw exception here :" + requestType);
                throw new RuntimeException("Got a bad guy call for greetMe");
            } else {
                requestType = "[Bad guy]";
                flag.set(true);
            }
        }
        return "Hello " + requestType;
    }
    
    public void pingMe() throws PingMeFault {
        int value = countRetry.getAndIncrement();
        
        FaultDetail faultDetail = new FaultDetail();
        faultDetail.setMajor((short)2);
        faultDetail.setMinor((short)1);
        throw new PingMeFault("Retry Count " + value, faultDetail);
    }
}
