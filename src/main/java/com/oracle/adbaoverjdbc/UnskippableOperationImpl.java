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

import java.util.concurrent.CompletionStage;
import java.util.concurrent.Executor;
import java.util.function.Function;

/**
 *
 */
class UnskippableOperationImpl<T> extends SimpleOperationImpl<T> {
  
  static <S> UnskippableOperationImpl<S> newOperation(SessionJdbc session,
                                             OperationGroupJdbc<? super S, ?> group,
                                             Function<SimpleOperationImpl<S>, S> action) {
    return new UnskippableOperationImpl<>(session, group, action);
  }

  protected UnskippableOperationImpl(SessionJdbc session,
                            OperationGroupJdbc<? super T, ?> operationGroup,
                            Function<SimpleOperationImpl<T>, T> action) {
    super(session, operationGroup, (Function<SimpleOperationImpl<T>, T>)action);
  }

  @Override
  CompletionStage<T> follows(CompletionStage<?> tail, Executor executor) {
    return tail.handleAsync(
            (Object v, Throwable t) -> {
              try {
                return get();
              }
              catch (Throwable ex) {
                if (errorHandler != null) errorHandler.accept(ex);
                throw ex;
              }
            },
            executor);
  }

}
