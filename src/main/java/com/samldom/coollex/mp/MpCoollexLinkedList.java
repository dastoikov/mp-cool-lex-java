/**
 * Copyright 2025-2026 The MP-Cool-lex-Java Contributors, see the CONTRIBUTORS file.
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

import static java.util.Spliterators.emptySpliterator;

import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import com.samldom.util.iter.Seq;

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

  private MpCoollexLinkedList() {
    throw new Error("Suppressed instantiation");
  }

  static class Algorithm<T extends Comparable<? super T>> {
    // variable names as in the paper
    Node<T> h, i, j;

    /**
     * @param elements non-empty.
     */
    Algorithm(T[] a) {
      Arrays.sort(a);

      h = new Node<>(a[a.length - 1]);
      j = h;
      for (int counter = a.length - 2; counter >= 0; counter--) {
        i = j;
        Node<T> curr = new Node<>(a[counter]);
        j.next = curr;
        j = curr;
      }
    }

    /** End of algorithm? */
    boolean hasNext() {
      return j.next != null || j.compareTo(h) < 0;
    }

    /** Advances to the next permutation. */
    void next() {
      Node<T> s, t;
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

    @Override
    public String toString() {
      StringBuilder buf = new StringBuilder();
      Node<T> tmp = h;
      do {
        buf.append(tmp.value);
      } while ((tmp = tmp.next) != null);
      return buf.toString();
    }
  }

  private static class Node<T extends Comparable<? super T>> implements Comparable<Node<T>> {
    final T value;
    Node<T> next;

    Node(T value) {
      this.value = value;
    }

    @Override
    public int compareTo(Node<T> o) {
      return value.compareTo(o.value);
    }
  }

  private static class MultisetPermutationsIterator<T extends Comparable<? super T>>
      implements Iterator<Iterator<T>> {

    private Iterator<Iterator<T>> delegate;

    MultisetPermutationsIterator(Algorithm<T> permutator) {
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
                      return new PermutationElementsIterator<>(permutator.h);
                    }
                  };
              return new PermutationElementsIterator<>(permutator.h);
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
  }

  private static class PermutationElementsIterator<T extends Comparable<? super T>>
      implements Iterator<T> {
    Node<T> currNode;

    PermutationElementsIterator(Node<T> currNode) {
      this.currNode = currNode;
    }

    @Override
    public boolean hasNext() {
      return currNode != null;
    }

    @Override
    public T next() {
      Node<T> c = currNode;
      if (c == null) {
        throw new NoSuchElementException();
      }
      currNode = currNode.next;
      return c.value;
    }
  }

  /**
   * Generates multiset permutations. The generation is lazy and governed by invoking {@link
   * Iterator#next()} methods. The generation order is as in Section 3, Algorithms, p. 992. This
   * order differs from Cool-lex in that the Cool-lex' last permutation is generated first.
   *
   * <p>In other words, applying a circular shift by one position to the left to this method's
   * generation order yields the Cool-lex order.
   *
   * <p>The permutations of the multiset {@code {"A", "B", "A"} } are then:
   *
   * <table border=1>
   * <tr><th>Cool-lex<th>This method's generation order
   * <tr><td>ABA<td>BAA
   * <tr><td>AAB<td>ABA
   * <tr><td>BAA<td>AAB
   * </table>
   *
   * <p>The permutations of the multiset {@code {1, 2, 3, 2} } are then:
   *
   * <table border=1>
   * <tr><th>Cool-lex<th>This method's generation order
   * <tr><td>1322<td>3221
   * <tr><td>3122<td>1322
   * <tr><td>2312<td>3122
   * <tr><td>1232<td>2312
   * <tr><td>2132<td>1232
   * <tr><td>3212<td>2132
   * <tr><td>2321<td>3212
   * <tr><td>2231<td>2321
   * <tr><td>1223<td>2231
   * <tr><td>2123<td>1223
   * <tr><td>2213<td>2123
   * <tr><td>3221<td>2213
   * </table>
   *
   * @param multiset to permute. <strong>Note</strong>: it may be modified by this method.
   * @return an empty iterator if the multiset is empty; the generated permutations otherwise.
   */
  public static <T extends Comparable<? super T>> Iterator<Iterator<T>> multisetPermutations(
      T[] multiset) {
    return multiset.length == 0
        ? Collections.emptyIterator()
        : new MultisetPermutationsIterator<>(new Algorithm<>(multiset));
  }

  /**
   * Generates multiset permutations. The generation is lazy and governed by affirmative feedback by
   * the API client via the predicate passed to {@link Seq#doWhile(Predicate)} methods. The
   * generation order is as in Section 3, Algorithms, p. 992. This order differs from Cool-lex in
   * that the Cool-lex' last permutation is generated first. See {@link
   * #multisetPermutations(Comparable[])} for details.
   *
   * @param multiset to permute. <strong>Note</strong>: it may be modified by this method.
   * @return an empty sequence if the multiset is empty; the generated permutations otherwise.
   */
  public static <T extends Comparable<? super T>> Seq<Seq<T>> multisetPermutationsSeq(
      T[] multiset) {
    return multiset.length == 0
        ? yield -> {} // empty sequence
        : coollexSeq(new Algorithm<>(multiset));
  }

  private static <T extends Comparable<? super T>> Seq<Seq<T>> coollexSeq(Algorithm<T> coollex) {
    return yield -> {
      for (; yield.test(elementsSeq(coollex.h)) && coollex.hasNext(); coollex.next())
        ;
    };
  }

  private static <T extends Comparable<? super T>> Seq<T> elementsSeq(Node<T> from) {
    return yield -> {
      for (Node<T> curr = from; curr != null && yield.test(curr.value); curr = curr.next)
        ;
    };
  }

  /**
   * Generates multiset permutations. The generation is lazy and governed by invoking {@link
   * Spliterator#tryAdvance(Consumer)} methods. The generation order is as in Section 3, Algorithms,
   * p. 992. This order differs from Cool-lex in that the Cool-lex' last permutation is generated
   * first. See {@link #multisetPermutations(Comparable[])} for details.
   *
   * <p>The spliterators are {@link Spliterator#ORDERED ordered} and {@link Spliterator#IMMUTABLE
   * immutable}.
   *
   * <p>Permutation spliterators are, in addition, {@link Spliterator#SIZED sized}. Multiset
   * permutations spliterators will be sized if the value of the multinomial coefficient supplied is
   * a positive number. It shall be an <em>accurate</em> value.
   *
   * @param multiset to permute. <strong>Note</strong>: it may be modified by this method.
   * @param supplyMultinomialCoefficient supplier of an accurate value for {@code C(n; k1, k2, ...,
   *     km)}, or any non-positive integer if the calculation would be too expensive.
   * @return an empty spliterator if the multiset is empty; the generated permutations otherwise.
   */
  public static <T extends Comparable<? super T>>
      Spliterator<Spliterator<T>> multisetPermutationsSpliterator(
          T[] multiset, Function<T[], Long> supplyMultinomialCoefficient) {
    return multiset.length == 0
        ? emptySpliterator()
        : coollexSpliterator(
            multiset.length,
            supplyMultinomialCoefficient.apply(multiset),
            new Algorithm<>(multiset));
  }

  // pSize: size of permutation spliterators
  // mSize: size of multiset permutations spliterators
  private static <T extends Comparable<? super T>> Spliterator<Spliterator<T>> coollexSpliterator(
      long pSize, long mpSize, Algorithm<T> coollex) {
    Supplier<CoollexSpliterator<T>> elementsSpliterator =
        () -> {
          Mut<Node<T>> curr = Mut.of(coollex.h);
          return new CoollexSpliterator<>(
              pSize,
              rcv -> {
                if (curr.e == null) {
                  return false;
                }
                rcv.accept(curr.e.value);
                curr.e = curr.e.next;
                return true;
              });
        };
    Mut<Boolean> first = Mut.of(false);
    return new CoollexSpliterator<>(
        mpSize,
        rcv -> {
          if (!first.e) {
            rcv.accept(elementsSpliterator.get());
            return first.e = true;
          }
          if (!coollex.hasNext()) {
            return false;
          }
          coollex.next();
          rcv.accept(elementsSpliterator.get());
          return true;
        });
  }

  private static class CoollexSpliterator<T> implements Spliterator<T> {
    private static final int DEFAULT_CHARACTERISTICS = ORDERED | IMMUTABLE;

    final long size;
    final Predicate<Consumer<? super T>> advancer;
    final int characteristics;

    CoollexSpliterator(long wantedSize, Predicate<Consumer<? super T>> advancer) {
      this.advancer = advancer;

      // non<=0 if the computation is too expensive or the result is too large
      if (wantedSize <= 0) {
        size = Long.MAX_VALUE;
        characteristics = DEFAULT_CHARACTERISTICS;
      } else {
        size = wantedSize;
        characteristics = DEFAULT_CHARACTERISTICS | SIZED;
      }
    }

    @Override
    public boolean tryAdvance(Consumer<? super T> action) {
      return advancer.test(action);
    }

    @Override
    public long estimateSize() {
      return size;
    }

    @Override
    public Spliterator<T> trySplit() {
      return null;
    }

    @Override
    public int characteristics() {
      return characteristics;
    }
  }
}
