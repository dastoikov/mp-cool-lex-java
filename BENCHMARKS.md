# mp-cool-lex-java

## Benchmarks

### Measure: Average time

| Name                 | Value                                                       |
|----------------------|-------------------------------------------------------------|
| Date                 | 2026-10-14                                                  |
| Environment          | linux, amd64, 11th Gen Intel(R) Core(TM) i5-11400 @ 2.60GHz |
| mp-cool-lex-java     | release 1.1.1, class file version 52.0                      |
| Multiset size        | 10                                                          |
| Element multiplicity | 1 // so the multinomial coefficient (n/k1,k2, ..., km) = n! |

#### Results

---
JDK 1.8.0_472, OpenJDK 64-Bit Server VM, 25.472-b0

| Benchmark            | Microseconds per operation | Rank |
|:---------------------|---------------------------:|-----:|
| Go-style iterator    |                 111614.420 |    1 |
| Standard iterator    |                 119136.437 |    2 |
| Internal spliterator |                 158293.935 |    4 |
| Standard spliterator |                 118344.872 |    2 |

---
JDK 21.0.1, OpenJDK 64-Bit Server VM, 21.0.1+12-29

| Benchmark            | Microseconds per operation | Rank |
|:---------------------|---------------------------:|-----:|
| Go-style iterator    |                  43717.682 |    1 |
| Standard iterator    |                  91643.018 |    3 |
| Internal spliterator |                 122767.141 |    4 |
| Standard spliterator |                  81302.341 |    2 |

---
JDK 25.0.5.0.2, Java HotSpot(TM) 64-Bit Server VM, 25.0.5.0.2+1-LTS-2

| Benchmark            | Microseconds per operation | Rank |
|:---------------------|---------------------------:|-----:|
| Go-style iterator    |                  35902.397 |    1 |
| Standard iterator    |                  78059.859 |    3 |
| Internal spliterator |                 112158.021 |    4 |
| Standard spliterator |                  75325.508 |    2 |

#### Code

**Go-style iterator**

```java
multisetPermutationsSeq(data.multiset)
    .forEach(permutation -> permutation.forEach(consumer::consume));
```

**Standard iterator**

```java
multisetPermutations(data.multiset)
    .forEachRemaining(permutation -> permutation.forEachRemaining(consumer::consume));
```

**Internal spliterator**

```java
multisetPermutationsSpliterator(data.multiset, m -> data.multinomialCoefficient)
    .forEachRemaining(permutation -> permutation.forEachRemaining(consumer::consume));
```

**Standard spliterator**

```java
Spliterators.spliterator(
        .multisetPermutations(data.multiset), // <- Iterator<Iterator<E>>
        data.multinomialCoefficient,
        Spliterator.ORDERED)
    .forEachRemaining(
        permutation ->
            Spliterators.spliterator(permutation, data.multiset.length, Spliterator.ORDERED)
                .forEachRemaining(consumer::consume));
```
