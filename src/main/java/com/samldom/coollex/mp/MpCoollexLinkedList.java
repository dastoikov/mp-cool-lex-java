/**
 * Copyright 2025 The MP-Cool-lex-Java Contributors, see the CONTRIBUTORS file.
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

import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * The <em>cool-lex</em> order and associated algorithms for multiset permutations were invented by
 * Aaron Williams. Hats off.
 *
 * <p>See <a href= "https://epubs.siam.org/doi/abs/10.1137/1.9781611973068.107">
 * https://epubs.siam.org/doi/abs/10.1137/1.9781611973068.107</a>.
 *
 * <p>See <b>Algorithm 1</b> in section <b>3. Algorithms</b>. <hr/>
 *
 * <p>Unless otherwise stated, all iterators returned by methods of this class are lazy, in the
 * sense that the backing generator constructs the next object upon invocation of {@link
 * Iterator#next()}.
 */
public class MpCoollexLinkedList {

  /** Suppressed instantiation for utility classes. */
  private MpCoollexLinkedList() {}

  static class Algorithm<T extends Comparable<? super T>> {
    // variable names as in the paper
    Node h, i, j;

    /**
     * @param elements non-empty.
     */
    Algorithm(T[] a) {
      Arrays.sort(a);

      h = new Node(a[a.length - 1]);
      j = h;
      for (int counter = a.length - 2; counter >= 0; counter--) {
        i = j;
        Node curr = new Node(a[counter]);
        j.setNext(curr);
        j = curr;
      }
    }

    /** End of algorithm? */
    boolean hasNext() {
      return j.next != null || j.compareTo(h) < 0;
    }

    /** Advances to the next permutation. */
    void next() {
      Node s, t;
      if (j.next != null && i.compareTo(j.next) >= 0) {
        s = j;
      } else {
        s = i;
      }

      t = s.next;
      s.next = t.next;
      t.next = h;

      if (t.compareTo(h) < 0) {
        i = t;
      }
      j = i.next;
      h = t;
    }

    class Node implements Comparable<Node> {
      T value;
      Node next;

      Node(T value) {
        this.value = value;
      }

      void setNext(Node next) {
        this.next = next;
      }

      @Override
      public String toString() {
        return value.toString();
      }

      @Override
      public int compareTo(Algorithm<T>.Node o) {
        return value.compareTo(o.value);
      }
    }

    @Override
    public String toString() {
      StringBuilder buf = new StringBuilder();
      Node tmp = h;
      do {
        buf.append(tmp);
      } while ((tmp = tmp.next) != null);
      return buf.toString();
    }
  }

  static class MultisetPermutationsIterator<T extends Comparable<? super T>>
      implements Iterator<Iterator<T>> {

    private final Algorithm<T> permutator;
    private Iterator<Iterator<T>> delegate;

    MultisetPermutationsIterator(Algorithm<T> permutator) {
      this.permutator = permutator;
      delegate =
          new Iterator<Iterator<T>>() {

            @Override
            public Iterator<T> next() {
              delegate =
                  new Iterator<Iterator<T>>() {

                    @Override
                    public boolean hasNext() {
                      return permutator.hasNext();
                    }

                    @Override
                    public Iterator<T> next() {
                      if (!permutator.hasNext()) {
                        throw new NoSuchElementException();
                      }
                      permutator.next();
                      return new PermutationElementsIterator();
                    }
                  };
              return new PermutationElementsIterator();
            }

            @Override
            public boolean hasNext() {
              return true;
            }
          };
    }

    @Override
    public boolean hasNext() {
      return delegate.hasNext();
    }

    @Override
    public Iterator<T> next() {
      return delegate.next();
    }

    private class PermutationElementsIterator implements Iterator<T> {
      Algorithm<T>.Node currNode = permutator.h;

      @Override
      public boolean hasNext() {
        return currNode != null;
      }

      @Override
      public T next() {
        Algorithm<T>.Node c = currNode;
        if (c == null) {
          throw new NoSuchElementException();
        }
        currNode = currNode.next;
        return c.value;
      }
    }
  }

  /**
   * Returns an iterator of the generated permutations of the specified multiset.
   *
   * <p>The permutations of the multiset {@code {"A", "B", "A"} } are:
   *
   * <pre>
   *  BAA
   *  ABA
   *  AAB
   * </pre>
   *
   * <p>The permutations of the multiset {@code {1, 2, 3, 2} } are:
   *
   * <pre>
   *  3221
   *  1322
   *  3122
   *  2312
   *  1232
   *  2132
   *  3212
   *  2321
   *  2231
   *  1223
   *  2123
   *  2213
   * </pre>
   *
   * @param elements the multiset to permute. It will not be modified by this method.
   * @return an empty iterator if the specified multiset is empty; the generated permutations
   *     otherwise.
   */
  public static <T extends Comparable<? super T>> Iterator<Iterator<T>> multisetPermutations(
      T[] elements) {
    return elements.length == 0
        ? Collections.emptyIterator()
        : new MultisetPermutationsIterator<>(new Algorithm<>(elements));
  }
}
