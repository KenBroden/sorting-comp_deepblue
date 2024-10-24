import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Scanner;

// To run on a single core, compile and then run as:
// taskset -c 0 java GroupDeepBlue
// To avoid file reading/writing connections to the server, run in /tmp 
// of your lab machine.

// Change where Insertion sort is used in the mergeSort method???

public class GroupDeepBlue {

	private static final int INSERTION_SORT_THRESHOLD = 10;
	public static void main(String[] args) throws InterruptedException, FileNotFoundException {

		if (args.length < 2) {
			System.out.println(
					"Running tests since input and output file names not specified");
			SortingCompetitionComparator.runComparatorTests();
			System.exit(0);
		}

		String inputFileName = args[0];
		String outFileName = args[1];
		
		// Uncomment to test comparator methods

		int [][] data = readData(inputFileName); // read data as strings
		
		int [][] toSort = data.clone(); // clone the data

		sort(toSort); // call the sorting method once for JVM warmup
		
		toSort = data.clone(); // clone again

		Thread.sleep(10); // to let other things finish before timing; adds stability of runs

		long start = System.currentTimeMillis();

		sort(toSort); // sort again

		long end = System.currentTimeMillis();

		System.out.println(end - start);

		writeOutResult(toSort, outFileName); // write out the results

	}


	private static int [][] readData(String inputFileName) throws FileNotFoundException {
		ArrayList<int[]> input = new ArrayList<>();
		Scanner in = new Scanner(new File(inputFileName));

		while (in.hasNext()) {
			String str = in.next();
			input.add(Arrays.stream(str.split(",")).mapToInt(Integer::parseInt).toArray());
		}

		in.close();

		// the string array is passed just so that the correct type can be created
		return input.toArray(new int[0][]);
	}

	// YOUR SORTING METHOD GOES HERE.
	// You may call other methods and use other classes.
	// Note: you may change the return type of the method.
	// You would need to provide your own function that prints your sorted array to
	// a file in the exact same format that my program outputs

	// ORIGINAL SORT METHOD
	// private static void sort(int [][] toSort) {
	// 	Arrays.sort(toSort, new SortingCompetitionComparator());
	// 	// Base case
	// 	if (toSort.length <= 1) {
	// 		return;
	// 	}

	// 	// Split the array into two halves
	// 	int mid = toSort.length / 2;
	// 	int [][] left = Arrays.copyOfRange(toSort, 0, mid);
	// 	int [][] right = Arrays.copyOfRange(toSort, mid, toSort.length);

	// 	// Recursively sort the two halves
	// 	sort(left);
	// 	sort(right);

	// 	// Merge the two halves
	// 	merge(toSort, left, right);
	// }

	// MERGE SORT METHOD 2.0
	// This mergeSort method uses insertion sort for small sub-arrays. Can 
	// we change where insertion is used in the mergeSort method?
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

	// ORIGINAL MERGE METHOD
	// private static void merge(int [][] toSort, int [][] left, int [][] right) {
	// 	int i = 0, j = 0, k = 0;
	// 	while (i < left.length && j < right.length) {
	// 		if (new SortingCompetitionComparator().compare(left[i], right[j]) < 0) {
	// 			toSort[k++] = left[i++];
	// 		} else {
	// 			toSort[k++] = right[j++];
	// 		}
	// 	}
	// 	while (i < left.length) {
	// 		toSort[k++] = left[i++];
	// 	}
	// 	while (j < right.length) {
	// 		toSort[k++] = right[j++];
	// 	}
	// }

	// INSERTION SORT METHOD 2.0
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

	// MERGE METHOD 2.0
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

	private static class SortingCompetitionComparator implements Comparator<int []> {
		@Override
		public int compare(int [] seq1, int [] seq2) {
			// looking for different elements in the same positions
			for (int i = 0; i < seq1.length && i < seq2.length ; ++i) {
				int diff = seq1[i] - seq2[i];
				if (diff != 0) return diff;
			}
			
			// two sequences are identical:
			if (seq1.length == seq2.length) return 0;
			
			// one sequence is a prefix of the other:
			
			// comparing even values:
			int seq1_evens = 0;
			for (int i = 0; i < seq1.length; ++i) {
				if (seq1[i] % 2 == 0) seq1_evens++;
			}
			
			int seq2_evens = 0;
			for (int i = 0; i < seq2.length; ++i) {
				if (seq2[i] % 2 == 0) seq2_evens++;
			}
			
			int diff = seq1_evens - seq2_evens;
			if (diff != 0) return diff; 
			
			// return the negated difference of odds 
			return (seq2.length - seq2_evens) - (seq1.length - seq1_evens);
		}

		public static void runComparatorTests() {
			int [] arr1 = {1, 3, 2};
			int [] arr2 = {1, 2, 3};
			System.out.println("Comparing arr1 and arr2");
			System.out.println((new SortingCompetitionComparator()).compare(arr1, arr2));	
			System.out.println((new SortingCompetitionComparator()).compare(arr2, arr1));
			System.out.println((new SortingCompetitionComparator()).compare(arr1, arr1));
			
			int [] arr3 = {1, 3, 2, 5, 4};
			
			System.out.println("Comparing arr1 and arr3");
			// arr3 should be larger:
			System.out.println((new SortingCompetitionComparator()).compare(arr1, arr3));
			System.out.println((new SortingCompetitionComparator()).compare(arr3, arr1));
			
			int [] arr4 = {1, 3, 2, 7, 6, 5, 4};
			
			System.out.println("Comparing arr1 and arr4");
			// arr1 should be larger since they have the same number of evens, but the number
			// of odds is higher in arr4, and the comparison goes the opposite way:
			System.out.println((new SortingCompetitionComparator()).compare(arr1, arr4));
			System.out.println((new SortingCompetitionComparator()).compare(arr4, arr1));
			
			System.out.println("Comparing arr4 and arr3");
			System.out.println((new SortingCompetitionComparator()).compare(arr4, arr3));
			
			int [] arr5 = {1, 3, 2, 5, 6, 7, 4};
			
			System.out.println("Comparing arr1 and arr5");

			System.out.println((new SortingCompetitionComparator()).compare(arr1, arr5));
			System.out.println((new SortingCompetitionComparator()).compare(arr5, arr1));			
			
			System.out.println("Comparing arr5 and arr3");
			System.out.println((new SortingCompetitionComparator()).compare(arr5, arr3));
			
			System.out.println("Comparing arr5 and arr4");
			System.out.println((new SortingCompetitionComparator()).compare(arr5, arr4));
			
			int [] arr6 = {1, 3, 2, 6, 5, 7, 4};
			System.out.println("Comparing arr1 and arr6");
			System.out.println((new SortingCompetitionComparator()).compare(arr1, arr6));
			
			System.out.println("Comparing arr3 and arr6");
			System.out.println((new SortingCompetitionComparator()).compare(arr3, arr6));
			
			System.out.println("Comparing arr5 and arr6");
			System.out.println((new SortingCompetitionComparator()).compare(arr5, arr6));
			
			
			System.out.println("Comparing arr4 and arr6");
			System.out.println((new SortingCompetitionComparator()).compare(arr4, arr6));
			 
		}
		

	}

	// SORT METHOD 2.0
	private static void sort(int [][] toSort) {
		mergeSort(toSort, 0, toSort.length - 1);
	}
	
	private static void writeOutResult(int [][] sorted, String outputFilename) throws FileNotFoundException {
		PrintWriter out = new PrintWriter(outputFilename);
		for (int [] s : sorted) {
			for (int i = 0; i < s.length; ++i) {
				out.print(s[i]+(i<s.length-1?",":""));
			}
			out.println();
		}
		out.close();
	}
	
}
