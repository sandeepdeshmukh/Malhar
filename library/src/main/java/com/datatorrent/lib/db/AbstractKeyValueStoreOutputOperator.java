/*
 * Copyright (c) 2013 DataTorrent, Inc. ALL Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.datatorrent.lib.db;

import com.datatorrent.api.BaseOperator;
import com.datatorrent.api.Context.OperatorContext;
import com.datatorrent.api.DefaultInputPort;
import com.datatorrent.api.annotation.InputPortFieldAnnotation;
import java.io.IOException;
import java.util.Map;

/**
 * This abstract class is for any implementation of an output adapter of non-transactional key value store without the transactional exactly once feature.
 *
 * @param <T> The tuple type
 * @param <S> The store type
 * @since 0.9.3
 */
public abstract class AbstractKeyValueStoreOutputOperator<T, S extends KeyValueStore> extends BaseOperator
{
  protected S store;

  /**
   * The input port.
   */
  @InputPortFieldAnnotation(name = "in", optional = true)
  public final transient DefaultInputPort<T> input = new DefaultInputPort<T>()
  {
    @Override
    public void process(T t)
    {
      processTuple(t);
    }

  };

  /**
   * Gets the store.
   * @return
   */
  public S getStore()
  {
    return store;
  }

  /**
   * Sets the store.
   * @param store
   */
  public void setStore(S store)
  {
    this.store = store;
  }

  @Override
  public void setup(OperatorContext context)
  {
    try {
      store.connect();
    }
    catch (IOException ex) {
      throw new RuntimeException(ex);
    }
  }

  @Override
  public void beginWindow(long windowId)
  {
  }

  @Override
  public void teardown()
  {
    try {
      store.disconnect();
    }
    catch (IOException ex) {
    }
  }

  /**
   * Processes the incoming tuple.
   *
   * @param tuple
   */
  public void processTuple(T tuple)
  {
    Map<Object, Object> m = convertToMap(tuple);
    store.putAll(m);
  }

  /**
   * Implementation should provide the routine on how to convert a tuple to a map of key value pairs to be written to the store
   *
   * @param tuple
   * @return
   */
  public abstract Map<Object, Object> convertToMap(T tuple);

}