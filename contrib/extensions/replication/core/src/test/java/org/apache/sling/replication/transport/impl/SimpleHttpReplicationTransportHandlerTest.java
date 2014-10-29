/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.sling.replication.transport.impl;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.fluent.Executor;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.fluent.Response;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.replication.communication.ReplicationActionType;
import org.apache.sling.replication.communication.ReplicationEndpoint;
import org.apache.sling.replication.communication.ReplicationRequest;
import org.apache.sling.replication.packaging.ReplicationPackage;
import org.apache.sling.replication.packaging.ReplicationPackageInfo;
import org.apache.sling.replication.serialization.ReplicationPackageBuilder;
import org.apache.sling.replication.transport.authentication.TransportAuthenticationContext;
import org.apache.sling.replication.transport.authentication.TransportAuthenticationProvider;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockingDetails;
import static org.mockito.Mockito.when;

/**
 * Testcase for {@link org.apache.sling.replication.transport.impl.SimpleHttpReplicationTransportHandler}
 */
public class SimpleHttpReplicationTransportHandlerTest {

    @Test
    public void testDeliverPackage() throws Exception {
        TransportAuthenticationProvider<Executor, Executor> authProvider = mock(TransportAuthenticationProvider.class);
        when(authProvider.canAuthenticate(Executor.class)).thenReturn(true);
        Executor executor = mock(Executor.class);
        Response response = mock(Response.class);
        when(executor.execute(any(Request.class))).thenReturn(response);
        when(authProvider.authenticate(any(Executor.class), any(TransportAuthenticationContext.class))).thenReturn(executor);
        ReplicationEndpoint endpoint = new ReplicationEndpoint("http://127.0.0.1:8080/some/resource");
        ReplicationPackageBuilder packageBuilder = mock(ReplicationPackageBuilder.class);
        int maxNoOfPackages = Integer.MAX_VALUE;
        SimpleHttpReplicationTransportHandler simpleHttpReplicationTransportHandler = new SimpleHttpReplicationTransportHandler(
                authProvider, endpoint, packageBuilder, maxNoOfPackages);
        ResourceResolver resourceResolver = mock(ResourceResolver.class);
        ReplicationPackage replicationPackage = mock(ReplicationPackage.class);
        when(replicationPackage.getInfo()).thenReturn(mock(ReplicationPackageInfo.class));
        InputStream stream = mock(InputStream.class);
        when(replicationPackage.createInputStream()).thenReturn(stream);
        simpleHttpReplicationTransportHandler.deliverPackage(resourceResolver, replicationPackage);
    }

    @Test
    public void testRetrievePackagesRemotelyFailing() throws Exception {
        TransportAuthenticationProvider<Executor, Executor> authProvider = mock(TransportAuthenticationProvider.class);
        when(authProvider.canAuthenticate(Executor.class)).thenReturn(true);
        Executor executor = mock(Executor.class);
        Response response = mock(Response.class);
        HttpResponse httpResponse = mock(HttpResponse.class);
        StatusLine statusLine = mock(StatusLine.class);
        when(statusLine.getStatusCode()).thenReturn(404);
        when(httpResponse.getStatusLine()).thenReturn(statusLine);
        when(response.returnResponse()).thenReturn(httpResponse);
        when(executor.execute(any(Request.class))).thenReturn(response);
        when(authProvider.authenticate(any(Executor.class), any(TransportAuthenticationContext.class))).thenReturn(executor);
        ReplicationEndpoint endpoint = new ReplicationEndpoint("http://127.0.0.1:8080/some/resource");
        ReplicationPackageBuilder packageBuilder = mock(ReplicationPackageBuilder.class);
        int maxNoOfPackages = 1;
        SimpleHttpReplicationTransportHandler simpleHttpReplicationTransportHandler = new SimpleHttpReplicationTransportHandler(
                authProvider, endpoint, packageBuilder, maxNoOfPackages);
        ResourceResolver resourceResolver = mock(ResourceResolver.class);
        ReplicationRequest replicationRequest = new ReplicationRequest(ReplicationActionType.ADD, new String[]{"/"});
        List<ReplicationPackage> packages = simpleHttpReplicationTransportHandler.retrievePackages(resourceResolver, replicationRequest);
        assertNotNull(packages);
        assertTrue(packages.isEmpty());
    }

    @Test
    public void testRetrievePackagesRemotelyWorking() throws Exception {
        TransportAuthenticationProvider<Executor, Executor> authProvider = mock(TransportAuthenticationProvider.class);
        when(authProvider.canAuthenticate(Executor.class)).thenReturn(true);
        Executor executor = mock(Executor.class);
        Response response = mock(Response.class);
        HttpResponse httpResponse = mock(HttpResponse.class);
        StatusLine statusLine = mock(StatusLine.class);
        when(statusLine.getStatusCode()).thenReturn(200);
        when(httpResponse.getStatusLine()).thenReturn(statusLine);
        HttpEntity entity = mock(HttpEntity.class);
        InputStream stream = new ByteArrayInputStream("package binary stuff".getBytes("UTF-8"));
        when(entity.getContent()).thenReturn(stream);
        when(httpResponse.getEntity()).thenReturn(entity);
        when(response.returnResponse()).thenReturn(httpResponse);
        when(executor.execute(any(Request.class))).thenReturn(response);
        when(authProvider.authenticate(any(Executor.class), any(TransportAuthenticationContext.class))).thenReturn(executor);
        ReplicationEndpoint endpoint = new ReplicationEndpoint("http://127.0.0.1:8080/some/resource");
        ReplicationPackageBuilder packageBuilder = mock(ReplicationPackageBuilder.class);
        ReplicationPackage replicationPackage = mock(ReplicationPackage.class);
        when(replicationPackage.getInfo()).thenReturn(mock(ReplicationPackageInfo.class));
        when(packageBuilder.readPackage(any(ResourceResolver.class), any(InputStream.class))).thenReturn(replicationPackage);
        int maxNoOfPackages = 1;
        SimpleHttpReplicationTransportHandler simpleHttpReplicationTransportHandler = new SimpleHttpReplicationTransportHandler(
                authProvider, endpoint, packageBuilder, maxNoOfPackages);
        ResourceResolver resourceResolver = mock(ResourceResolver.class);
        ReplicationRequest replicationRequest = new ReplicationRequest(ReplicationActionType.ADD, new String[]{"/"});
        List<ReplicationPackage> packages = simpleHttpReplicationTransportHandler.retrievePackages(resourceResolver, replicationRequest);
        assertNotNull(packages);
        assertFalse(packages.isEmpty());
        assertEquals(1, packages.size());
        assertNotNull(packages.get(0));
    }
}