/**
 * Copyright 2021-2026 The Cool-lex-Java Contributors, see the CONTRIBUTORS file.
 *
 * <p>Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * <p>http://www.apache.org/licenses/LICENSE-2.0
 *
 * <p>Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.samldom.util.iter;

import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * A Go-style iterator that yields T-values to a yield function. The yield function signals the
 * iterator back whether to continue producing values.
 *
 * @param <T> the type of the values produced by this iterator.
 */
public interface Seq<T> {
  /**
   * Yields the next T-value to the specified yield function.
   *
   * @param yield signals the iterator whether to continue producing T-values.
   */
  void doWhile(Predicate<? super T> yield);

  /**
   * Yields the T-values in this sequence to the specified consumer.
   *
   * @param consumer of each T-value.
   */
  default void forEach(Consumer<? super T> consumer) {
    doWhile(
        t -> {
          consumer.accept(t);
          return true;
        });
  }
}
