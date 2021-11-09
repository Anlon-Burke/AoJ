/*
 * Copyright (c) 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.oracle.adbaoverjdbc;

import jdk.incubator.sql2.SessionProperty;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Bare bones DataSource. No support for Session caching.
 *
 */
class DataSourceJdbc implements jdk.incubator.sql2.DataSource {

  static DataSourceJdbc newDataSource(Map<SessionProperty, Object> defaultSessionProperties,
          Map<SessionProperty, Object> requiredSessionProperties) {
    return new DataSourceJdbc(defaultSessionProperties, requiredSessionProperties);
  }

  protected final Map<SessionProperty, Object> defaultSessionProperties;
  protected final Map<SessionProperty, Object> requiredSessionProperties;
  
  protected final Set<SessionJdbc> openSessions = new HashSet<>();

  protected DataSourceJdbc(Map<SessionProperty, Object> defaultProps,
          Map<SessionProperty, Object> requiredProps) {
    super();
    defaultSessionProperties = defaultProps;
    requiredSessionProperties = requiredProps;
  }

  @Override
  public SessionJdbc.Builder builder() {
    return SessionBuilderJdbc.newSessionBuilder(this, defaultSessionProperties, requiredSessionProperties);
  }

  @Override
  public void close() {
    openSessions.stream().forEach( c -> c.close() );
  }
  
  
  
  DataSourceJdbc registerSession(SessionJdbc c) {
    openSessions.add(c);
    return this;
  }
  
  DataSourceJdbc deregisterSession(SessionJdbc c) {
    openSessions.remove(c);
    return this;
  }

}
