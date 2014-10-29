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
package org.apache.sling.replication.packaging;

import org.apache.sling.replication.serialization.ReplicationPackageReadingException;

/**
 * This represents an error happening while importing a {@link org.apache.sling.replication.packaging.ReplicationPackage}
 */
@SuppressWarnings("serial")
public class ReplicationPackageImportException extends Exception {

    public ReplicationPackageImportException(String message) {
        super(message);
    }

    public ReplicationPackageImportException(Throwable t) {
        super(t);
    }

    public ReplicationPackageImportException(String message, ReplicationPackageReadingException e) {
        super(message, e);
    }
}
