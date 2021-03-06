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

/**
 *
 */
class SqlOperationImpl<T> extends SimpleOperationImpl<T> {
  
  static <S> SqlOperationImpl<S> newOperation(SessionJdbc session, OperationGroupJdbc<? super S, ?> group, String sql) {
    return new SqlOperationImpl<>(session, group, sql);
  } 
  
  protected SqlOperationImpl(SessionJdbc session, OperationGroupJdbc<? super T, ?> group, String sql) {
    super(session, group, op -> (T)session.jdbcExecute(op, sql));
  }
}
