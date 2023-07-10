/*
 * JBoss, Home of Professional Open Source
 * Copyright 2021, Red Hat, Inc. and/or its affiliates, and individual
 * contributors by the @authors tag. See the copyright.txt in the
 * distribution for a full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jboss.as.quickstarts.ejb.queue;

import jakarta.ejb.Stateless;

import javax.naming.NamingException;

import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.jboss.as.quickstarts.ejb.database.RemoteBeanInterface;
import org.jboss.logging.Logger;

/**
 * EJB which runs the remote calls to the EJBs.
 * We use the EJB here for benefit of automatic transaction management.
 */
@Stateless
public class RemoteBeanCaller {
    private static final Logger log = Logger.getLogger(RemoteBeanCaller.class);

    @Inject
    private QueueBean queueBean;

    /**
     * <p>
     * The method calls the remote EJB {@link Stateless} endpoint.<br/>
     * The invocation runs over EJB remoting where first call is run over HTTP,
     * then the HTTP upgrade requests the change to remoting protocol
     * </p>
     * <p>
     * To lookup the remote endpoint is used direct definition of the hostname, port and credentials
     * here in code. More specifically at time when {@link javax.naming.InitialContext} is defined,
     * see {@link RemoteLookupHelper#lookupRemoteEJBDirect(String, Class, boolean, String, int, boolean)}.
     * </p>
     *
     * @return list of strings as return values from the remote beans,
     *         in this case the return values are hostname and the jboss node names of the remote application server
     * @throws NamingException when remote lookup fails
     */
    @Transactional(Transactional.TxType.REQUIRED)
    public String directLookupStatelessBeanOverEjbRemotingCall() throws NamingException {
        log.debugf("Calling direct lookup with transaction to StatelessBean.successOnCall()");

        String remoteHost = System.getProperty("remote.server.host");
        int remotePort = Integer.getInteger("remote.server.port", 0);

        RemoteBeanInterface databaseBean = RemoteLookupHelper.lookupRemoteEJBDirect("StatelessBean", RemoteBeanInterface.class, false,
                remoteHost, remotePort, false);
        // This call should be carried out in a new sub-transaction
        String returnString = databaseBean.call().toString();
        // This call should be carried out in the transaction initialised by this method
        queueBean.send(returnString);

        return returnString;
    }

    /**
     * This is the same invocation as for {@link #directLookupStatelessBeanOverEjbRemotingCall}.
     * The difference is that there is used the HTTP protocol for EJB calls. Each invocation will be a HTTP request.
     *
     * @return list of strings as return values from the remote beans,
     *         in this case the return values are hostname and the jboss node names of the remote application server
     * @throws NamingException when remote lookup fails
     */
    @Transactional(Transactional.TxType.REQUIRED)
    public String directLookupStatelessBeanOverHttpCall() throws NamingException {
        log.debugf("Calling direct lookup with transaction to StatelessBean.successOnCall()");

        String remoteHost = System.getProperty("remote.server.host");
        int remotePort = Integer.getInteger("remote.server.port", 0);

        RemoteBeanInterface databaseBean = RemoteLookupHelper.lookupRemoteEJBDirect("StatelessBean", RemoteBeanInterface.class, false,
                remoteHost, remotePort,true);
        String returnString = databaseBean.call().toString();
        queueBean.send(returnString);
        return returnString;
    }

}
