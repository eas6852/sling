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
package org.apache.sling.replication.it;

import java.io.IOException;
import org.apache.sling.testing.tools.sling.SlingTestBase;
import org.junit.Test;

/**
 * Integration test for default replication agents.
 */
public class DefaultAgentsTest extends SlingTestBase {

    @Test
    public void testDefaultAgentConfigurations() throws IOException {
        String[] defaultAgentConfigPaths = new String[]{
                "/libs/sling/replication/config/agent/publish.json",
                "/libs/sling/replication/config/agent/publish-reverse.json",
                "/libs/sling/replication/config/agent/reverserepo.json",
                "/libs/sling/replication/config/agent/author.json",
                "/libs/sling/replication/config/agent/cache-flush.json"
        };
        for (String path : defaultAgentConfigPaths) {
            assertResourceExists(path);
        }

    }

    private void assertResourceExists(String path) throws IOException {
        getRequestExecutor().execute(
                getRequestBuilder().buildGetRequest(path)
                        .withCredentials(getServerUsername(), getServerPassword())
        ).assertStatus(200);
    }
}