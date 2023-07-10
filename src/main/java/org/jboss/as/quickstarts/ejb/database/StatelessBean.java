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

package org.jboss.as.quickstarts.ejb.database;

import jakarta.annotation.Resource;
import jakarta.ejb.Remote;
import jakarta.ejb.Stateless;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.TransactionManager;
import org.jboss.as.quickstarts.ejb.entity.CalleeUser;
import org.jboss.logging.Logger;

/**
 * Stateless bean to be called.
 */
@Stateless
@Remote (RemoteBeanInterface.class)
public class StatelessBean implements RemoteBeanInterface {
    private static final Logger log = Logger.getLogger(StatelessBean.class);

    @Resource(lookup = "java:/TransactionManager")
    private TransactionManager tm;

    @PersistenceContext
    private EntityManager em;

    /**
     * Stateless remote ejb method to be called from the client side.
     *
     * @return information about the host that this EJB resides on
     */
    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public String call() {
        log.debugf("Called '%s.successOnCall()' with transaction status %s",
                this.getClass().getName(), InfoUtils.getTransactionStatus());

        CalleeUser calleeUser = new CalleeUser("Bard", "The Bowman");
        em.persist(calleeUser);
        // transaction enlists XAResource #2

        return calleeUser.toString();
    }
}
