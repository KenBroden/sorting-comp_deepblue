# Testing notes

## Algo Version 1

Standard implementation of Mergesort, with use of given compare() method:

```java
private static void sort(int [][] toSort) {
    Arrays.sort(toSort, new SortingCompetitionComparator());
    // Base case
    if (toSort.length <= 1) {
        return;
    }

    // Split the array into two halves
    int mid = toSort.length / 2;
    int [][] left = Arrays.copyOfRange(toSort, 0, mid);
    int [][] right = Arrays.copyOfRange(toSort, mid, toSort.length);

    // Recursively sort the two halves
    sort(left);
    sort(right);

    // Merge the two halves
    merge(toSort, left, right);
}

// compare() and merge the two halves
private static void merge(int [][] toSort, int [][] left, int [][] right) {
    int i = 0, j = 0, k = 0;
    while (i < left.length && j < right.length) {
        if (new SortingCompetitionComparator().compare(left[i], right[j]) < 0) {
            toSort[k++] = left[i++];
        } else {
            toSort[k++] = right[j++];
        }
    }
    while (i < left.length) {
        toSort[k++] = left[i++];
    }
    while (j < right.length) {
        toSort[k++] = right[j++];
    }
}
```

**Results:**

Data Generation: ```java DataGenerator <input>.txt 1000000 0.002```

Testing with single core(```taskset -c 0 java Group<> <input>.txt <output>.txt```)

- Array size: 100,000
- Time: 179 ms
- Time per array: 0.00179 ms/array

- Array size: 100,000(already sorted)
- Time: 33 ms
- Time per array: 0.00033 ms/array

- Array size: 500,000
- Time: 504 ms
- Time per array: 0.001008 ms/array

- Array size: 500,000(already sorted)
- Time: 129 ms
- Time per array: 0.000258 ms/array
  