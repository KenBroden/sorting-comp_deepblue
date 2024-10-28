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
  
## Algo Version 2

Still using merge sort, but now insertion sort kicks in when the array drops to sime size we define

```java
private static void sort(int [][] toSort) {
    mergeSort(toSort, 0, toSort.length - 1);
}

private static void mergeSort(int[][] toSort, int left, int right) {
    if (right - left <= INSERTION_SORT_THRESHOLD) {
        insertionSort(toSort, left, right);
        return;
    }

    if(left < right) {
        int mid = (left + right) / 2;
        mergeSort(toSort, left, mid);
        mergeSort(toSort, mid + 1, right);
        merge(toSort, left, mid, right);
    }
}

private static void merge(int [][] toSort, int left, int mid, int right) {
    int[][] leftArray = new int[mid - left + 1][];
    int[][] rightArray = new int[right - mid][];

    System.arraycopy(toSort, left, leftArray, 0, leftArray.length);
    System.arraycopy(toSort, mid + 1, rightArray, 0, rightArray.length);

    int i = 0, j = 0, k = left;
    while (i < leftArray.length && j < rightArray.length) {
        if (new SortingCompetitionComparator().compare(leftArray[i], rightArray[j]) <=  0) {
            toSort[k++] = leftArray[i++];
        } else {
            toSort[k++] = rightArray[j++];
        }
    }
    while (i < leftArray.length) {
        toSort[k++] = leftArray[i++];
    }
    while (j < rightArray.length) {
        toSort[k++] = rightArray[j++];
    }
}

private static void insertionSort(int[][] toSort, int left, int right) {
    for (int i = left + 1; i <= right; i++) {
        int[] key = toSort[i];
        int j = i - 1;

        while (j >= left && new SortingCompetitionComparator().compare(toSort[j], key) > 0) {
            toSort[j + 1] = toSort[j];
            j--;
        }

        toSort[j + 1] = key;
    }
}

```

**Results:**

Data Generation: ```java DataGenerator <input>.txt 1000000 0.002```

Testing with single core(```taskset -c 0 java Group<> <input>.txt <output>.txt```)

INSERTION_SORT_THRESHOLD: 10 (This is when insertion sort jumps in)

- Array size: 100,000
- Time: 34 ms
- Time per array: 0.00034 ms/array

- Array size: 100,000(already sorted)
- Time: 6 ms
- Time per array: 0.00006 ms/array

- Array size: 500,000
- Time: 141 ms
- Time per array: 0.000282 ms/array

- Array size: 500,000(already sorted)
- Time: 35 ms
- Time per array: 0.00007 ms/array
