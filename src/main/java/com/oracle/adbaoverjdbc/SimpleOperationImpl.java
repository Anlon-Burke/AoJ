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
import java.util.function.Supplier;

/**
 *
 * @param <T>
 */
class SimpleOperationImpl<T> extends OperationJdbc<T> implements Supplier<T> {

  static <S> SimpleOperationImpl<S> newOperation(SessionJdbc session,
                                             OperationGroupJdbc<? super S, ?> group,
                                             Function<SimpleOperationImpl<S>, S> act) {
    return new SimpleOperationImpl<>(session, group, act);
  }

  private final Function<SimpleOperationImpl<T>, T> action;

  protected SimpleOperationImpl(SessionJdbc session,
                            OperationGroupJdbc<? super T, ?> operationGroup,
                            Function<SimpleOperationImpl<T>, T> act) {
    super(session, operationGroup);
    action = act;
  }

  @Override
  CompletionStage<T> follows(CompletionStage<?> tail, Executor executor) {
    return tail.thenApplyAsync(x -> get(), executor);
  }

  /**
   * Computes the value of this Operation by calling the action. If this
   * Operation has been canceled throws SqlSkippedException. If the action
   * throws a checked exception, wrap that checked exception in a SqlException.
   * SqlException is unchecked as required by Supplier, and can be handled by
   * CompletionStage.
   */
  @Override
  public T get() {
    checkCanceled();
    try {
      return action.apply(this);
    }
    finally {
      operationLifecycle = OperationLifecycle.COMPLETED;
    }
  }

}
