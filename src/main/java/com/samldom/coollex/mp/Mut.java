/**
 * Copyright 2026 The MP-Cool-lex-Java Contributors, see the CONTRIBUTORS file.
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
package com.samldom.coollex.mp;

/**
 * A <em>package-private</em> mutable reference.
 *
 * @param <E> element type.
 */
class Mut<E> {
  E e;

  static <E> Mut<E> of(E e) {
    Mut<E> ref = new Mut<E>();
    ref.e = e;
    return ref;
  }
}
