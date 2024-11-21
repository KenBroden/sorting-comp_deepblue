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

| Array Size       | Time to sort  | Time per array     |
| ---------------- | ------------- | ------------------ |
| 100,000          | 179 ms        | 0.00179 ms/array   |
| 100,000(sorted)  | 33 ms         | 0.00033 ms/array   |
| 500,000          | 504 ms        | 0.001008 ms/array  |
| 500,000(sorted)  | 129 ms        | 0.000258 ms/array  |
  
## Algo Version 2

Still using merge sort, but now insertion sort kicks in when the array drops to some size we define, called the INSERTION_SORT_THRESHOLD

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

| Array Size       | Time to sort  | Time per array     |
| ---------------- | ------------- | ------------------ |
| 100,000          | 34 ms         | 0.00034 ms/array   |
| 100,000(sorted)  | 6 ms          | 0.00006 ms/array   |
| 500,000          | 141 ms        | 0.000282 ms/array  |
| 500,000(sorted)  | 35 ms         | 0.00007 ms/array   |

---------------------------------------

INSERTION_SORT_THRESHOLD: 5 (This is when insertion sort jumps in)

| Array Size       | Time to sort  | Time per array     |
| ---------------- | ------------- | ------------------ |
| 100,000          | 85 ms         | 0.00085 ms/array   |
| 100,000(sorted)  | 8 ms          | 0.00008 ms/array   |
| 500,000          | 231 ms        | 0.000462 ms/array  |
| 500,000(sorted)  | 72 ms         | 0.000144 ms/array   |

---------------------------------------

INSERTION_SORT_THRESHOLD: 20 (This is when insertion sort jumps in)

| Array Size       | Time to sort  | Time per array     |
| ---------------- | ------------- | ------------------ |
| 100,000          | 60 ms         | 0.0006 ms/array   |
| 100,000(sorted)  | 7 ms          | 0.00007 ms/array   |
| 500,000          | 237 ms        | 0.000474 ms/array  |
| 500,000(sorted)  | 63 ms         | 0.000126 ms/array   |

## Algo Version 3

Uses a single auxiliary array that is passed through the recursive calls of mergeSort and used in the merge method, as opposed to leftArray and rightArray.

```java
// SORT METHOD 3.0
private static void sort(int[][] toSort) {
    mergeSort(toSort, 0, toSort.length - 1, new int[toSort.length][]);
}

// MERGE SORT METHOD 3.0
private static void mergeSort(int[][] toSort, int left, int right, int[][] aux) {
    if (right - left <= INSERTION_SORT_THRESHOLD) {
        insertionSort(toSort, left, right);
        return;
    }

    if (left < right) {
        int mid = (left + right) / 2;
        mergeSort(toSort, left, mid, aux);
        mergeSort(toSort, mid + 1, right, aux);
        merge(toSort, left, mid, right, aux);
    }
}

// MERGE METHOD 3.0
private static void merge(int[][] toSort, int left, int mid, int right, int[][] aux) {
    System.arraycopy(toSort, left, aux, left, right - left + 1);

    int i = left, j = mid + 1, k = left;
    while (i <= mid && j <= right) {
        if (new SortingCompetitionComparator().compare(aux[i], aux[j]) <= 0) {
            toSort[k++] = aux[i++];
        } else {
            toSort[k++] = aux[j++];
        }
    }
    while (i <= mid) {
        toSort[k++] = aux[i++];
    }
    while (j <= right) {
        toSort[k++] = aux[j++];
    }
}

// INSERTION SORT METHOD 3.0
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

Data Generation: ```java DataGenerator <input>.txt 500000 0.002```

Testing with single core(```taskset -c 0 java Group<> <input>.txt <output>.txt```)

INSERTION_SORT_THRESHOLD: 10 (This is when insertion sort jumps in)

| Array Size       | Time to sort  | Time per array     |
| ---------------- | ------------- | ------------------ |
| 100,000          | 75 ms         | 0.00075 ms/array   |
| 100,000(sorted)  | 7 ms          | 0.00007 ms/array   |
| 500,000          | 127 ms        | 0.000254 ms/array  |
| 500,000(sorted)  | 41 ms         | 0.00008 ms/array   |
| 700,000          | 196 ms        | 0.000280 ms/array  |
| 700,000(sorted)  | 58 ms         | 0.00008 ms/array   |

---------------------------------------
