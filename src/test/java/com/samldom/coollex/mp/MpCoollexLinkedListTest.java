package com.samldom.coollex.mp;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class MpCoollexLinkedListTest {

  /**
   * Tests the {@link MpCoollexLinkedList#multisetPermutations(Comparable[])} method.
   *
   * @param multiset the multiset from a specific test vector.
   * @param expectedPermutations the permutations expected to be generated for the multiset above.
   * @param order the order in which the expected permutations above are reported.
   */
  @ParameterizedTest
  @MethodSource
  void multisetPermutations(
      Comparable<Object>[] multiset, Comparable<Object>[][] expectedPermutations, Order order) {

    // MpCoollexLinkedList.multisetPermutations(T[]) generates permutations in the order given
    // in Section 3, Algorithms, page 992. `rot1` translates from Cool-lex to that order.
    if (Order.COOL_LEX == order) {
      rot1(expectedPermutations);
    }

    Object[] actualPermutations =
        stream(MpCoollexLinkedList.multisetPermutations(multiset))
            .map(permutation -> stream(permutation).toArray())
            .toArray();

    assertArrayEquals(expectedPermutations, actualPermutations);
  }

  // Given a deque d: d.addFirst( d.removeLast )
  private static void rot1(Object[] a) {
    for (int i = a.length - 1; i > 0; ) {
      Object t = a[i];
      a[i] = a[--i];
      a[i] = t;
    }
  }

  private static <E> Stream<E> stream(Iterator<E> i) {
    return StreamSupport.stream(
        Spliterators.spliteratorUnknownSize(i, Spliterator.ORDERED | Spliterator.IMMUTABLE), false);
  }

  private static Stream<Arguments> multisetPermutations() {
    return Stream.of(
            w(MpCoollexLinkedListTest::publicationTestVector),
            w(MpCoollexLinkedListTest::documentedIntegersTestVector),
            w(MpCoollexLinkedListTest::documentedCharactersTestVector))
        .flatMap(MpCoollexLinkedListTest::asArgs);
  }

  // Named for better readability of the code above.
  private static <T extends Comparable<T>> TestVector<?> w(TestVector<T> tv) {
    return tv;
  }

  /**
   * Provider of pre-computed data:
   *
   * <ol>
   *   <li>An initial multiset.
   *   <li>The multiset permutations expected to be generated for [1] by some method.
   *   <li>The order in which [2] are described, e.g., Cool-lex.
   * </ol>
   *
   * @param <T> the type of data described by this test vector.
   */
  @FunctionalInterface
  private interface TestVector<T extends Comparable<T>>
      extends BiConsumer<Consumer<T[]>, BiConsumer<T[][], Order>> {}

  /** Ordering options for test vectors to describe multiset permutations. */
  private enum Order {
    /** Permutations are described in strict Cool-lex order. */
    COOL_LEX,

    /** Permutations are described in a modified Cool-lex order: Section 3, Algorithms, p. 922. */
    SECTION_3_ALGORITHMS;
  }

  // A test vector can send data multiple times, so the order of sending is used to match a multiset
  // to its permutations to form an Arguments instance.
  private static Stream<Arguments> asArgs(TestVector<?> testVector) {
    Queue<Comparable<?>[]> multisets = new LinkedList<>();
    Map<Comparable<?>[][], Order> permutations = new LinkedHashMap<>();
    testVector.accept(multisets::add, permutations::put);
    return permutations.entrySet().stream()
        .map(p -> Arguments.of(multisets.remove(), p.getKey(), p.getValue()));
  }

  /**
   * A test vector published in Section 2.3, Cool-lex Order, p. 991. This test vector describes the
   * expected multiset permutations in a strict Cool-lex order.
   *
   * @param m consumer for the multiset
   * @param p consumer for the expected permutations
   */
  private static void publicationTestVector(
      Consumer<Integer[]> m, BiConsumer<Integer[][], Order> p) {

    m.accept(
        // the multiset
        new Integer[] {1, 1, 2, 2, 3});

    p.accept(
        // ... and its expected permutations
        new Integer[][] {
          {1, 3, 2, 2, 1},
          {3, 1, 2, 2, 1},
          {2, 3, 1, 2, 1},
          {1, 2, 3, 2, 1},
          {2, 1, 3, 2, 1},
          {3, 2, 1, 2, 1},
          {1, 3, 2, 1, 2},
          {3, 1, 2, 1, 2},
          {1, 3, 1, 2, 2},
          {1, 1, 3, 2, 2},
          {3, 1, 1, 2, 2},
          {2, 3, 1, 1, 2},
          {1, 2, 3, 1, 2},
          {2, 1, 3, 1, 2},
          {1, 2, 1, 3, 2},
          {1, 1, 2, 3, 2},
          {2, 1, 1, 3, 2},
          {3, 2, 1, 1, 2},
          {2, 3, 2, 1, 1},
          {2, 2, 3, 1, 1},
          {1, 2, 2, 3, 1},
          {2, 1, 2, 3, 1},
          {2, 2, 1, 3, 1},
          {1, 2, 2, 1, 3},
          {2, 1, 2, 1, 3},
          {1, 2, 1, 2, 3},
          {1, 1, 2, 2, 3},
          {2, 1, 1, 2, 3},
          {2, 2, 1, 1, 3},
          {3, 2, 2, 1, 1}
        },
        // ... in Cool-lex order as in Section 2.3, Cool-lex Order, pages 990-991.
        Order.COOL_LEX);
  }

  /**
   * An integers test vector that appeared in the documentation of the initial implemention of
   * {@link MpCoollexLinkedList#multisetPermutations(Comparable[])} in this project.
   *
   * @param m consumer for the multiset
   * @param p consumer for the expected permutations
   */
  private static void documentedIntegersTestVector(
      Consumer<Integer[]> m, BiConsumer<Integer[][], Order> p) {

    Integer[] multiset = {1, 2, 3, 2};
    // the multiset
    m.accept(multiset);

    p.accept(
        // ... and its expected permutations
        new Integer[][] {
          {3, 2, 2, 1},
          {1, 3, 2, 2},
          {3, 1, 2, 2},
          {2, 3, 1, 2},
          {1, 2, 3, 2},
          {2, 1, 3, 2},
          {3, 2, 1, 2},
          {2, 3, 2, 1},
          {2, 2, 3, 1},
          {1, 2, 2, 3},
          {2, 1, 2, 3},
          {2, 2, 1, 3}
        },
        // ... in algorithmic order as in Section 3, Algorithms, p. 992.
        Order.SECTION_3_ALGORITHMS);

    m.accept(multiset);
    p.accept(
        new Integer[][] {
          {1, 3, 2, 2},
          {3, 1, 2, 2},
          {2, 3, 1, 2},
          {1, 2, 3, 2},
          {2, 1, 3, 2},
          {3, 2, 1, 2},
          {2, 3, 2, 1},
          {2, 2, 3, 1},
          {1, 2, 2, 3},
          {2, 1, 2, 3},
          {2, 2, 1, 3},
          {3, 2, 2, 1}
        },
        Order.COOL_LEX);
  }

  /**
   * A characters test vector that appeared in the documentation of the initial implemention of
   * {@link MpCoollexLinkedList#multisetPermutations(Comparable[])} in this project.
   *
   * @param m consumer for the multiset
   * @param p consumer for the expected permutations
   */
  private static void documentedCharactersTestVector(
      Consumer<Character[]> m, BiConsumer<Character[][], Order> p) {

    Character[] multiset = {'A', 'B', 'A'};

    m.accept(multiset);
    p.accept(
        new Character[][] {
          {'B', 'A', 'A'},
          {'A', 'B', 'A'},
          {'A', 'A', 'B'},
        },
        Order.SECTION_3_ALGORITHMS);

    m.accept(multiset);
    p.accept(
        new Character[][] {
          {'A', 'B', 'A'},
          {'A', 'A', 'B'},
          {'B', 'A', 'A'},
        },
        Order.COOL_LEX);
  }
}
