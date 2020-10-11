import java.io.*;
import java.util.*;

public class SortingTest
{
	public static void main(String args[])
	{
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

		try
		{
			boolean isRandom = false;	// 입력받은 배열이 난수인가 아닌가?
			int[] value;	// 입력 받을 숫자들의 배열
			String nums = br.readLine();	// 첫 줄을 입력 받음
			if (nums.charAt(0) == 'r')
			{
				// 난수일 경우
				isRandom = true;	// 난수임을 표시

				String[] nums_arg = nums.split(" ");

				int numsize = Integer.parseInt(nums_arg[1]);	// 총 갯수
				int rminimum = Integer.parseInt(nums_arg[2]);	// 최소값
				int rmaximum = Integer.parseInt(nums_arg[3]);	// 최대값

				Random rand = new Random();	// 난수 인스턴스를 생성한다.

				value = new int[numsize];	// 배열을 생성한다.
				for (int i = 0; i < value.length; i++)	// 각각의 배열에 난수를 생성하여 대입
					value[i] = rand.nextInt(rmaximum - rminimum + 1) + rminimum;
			}
			else
			{
				// 난수가 아닐 경우
				int numsize = Integer.parseInt(nums);

				value = new int[numsize];	// 배열을 생성한다.
				for (int i = 0; i < value.length; i++)	// 한줄씩 입력받아 배열원소로 대입
					value[i] = Integer.parseInt(br.readLine());
			}

			// 숫자 입력을 다 받았으므로 정렬 방법을 받아 그에 맞는 정렬을 수행한다.
			while (true)
			{
				int[] newvalue = (int[])value.clone();	// 원래 값의 보호를 위해 복사본을 생성한다.

				String command = br.readLine();

				long t = System.currentTimeMillis();
				switch (command.charAt(0))
				{
					case 'B':	// Bubble Sort
						newvalue = DoBubbleSort(newvalue);
						break;
					case 'I':	// Insertion Sort
						newvalue = DoInsertionSort(newvalue);
						break;
					case 'H':	// Heap Sort
						newvalue = DoHeapSort(newvalue);
						break;
					case 'M':	// Merge Sort
						newvalue = DoMergeSort(newvalue);
						break;
					case 'Q':	// Quick Sort
						newvalue = DoQuickSort(newvalue);
						break;
					case 'R':	// Radix Sort
						newvalue = DoRadixSort(newvalue);
						break;
					case 'X':
						return;	// 프로그램을 종료한다.
					default:
						throw new IOException("잘못된 정렬 방법을 입력했습니다.");
				}
				if (isRandom)
				{
					// 난수일 경우 수행시간을 출력한다.
					System.out.println((System.currentTimeMillis() - t) + " ms");
				}
				else
				{
					// 난수가 아닐 경우 정렬된 결과값을 출력한다.
					for (int i = 0; i < newvalue.length; i++)
					{
						System.out.println(newvalue[i]);
					}
				}

			}
		}
		catch (IOException e)
		{
			System.out.println("입력이 잘못되었습니다. 오류 : " + e.toString());
		}
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	private static void swap(int [] value, int i, int j) {
		int temp = value[j];
		value[j] = value[i];
		value[i] = temp;
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	private static int[] DoBubbleSort(int[] value)
	{
		for (int i = value.length; i > 0; i--) {
			for (int j = 1; j < i; j++) {
				if (value[j] < value[j-1]) swap(value,j,j-1);
			}
		}
		return (value);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////
	private static int[] DoInsertionSort(int[] value)
	{
		for (int i = 1; i < value.length; i++) {
			for (int j = i-1; j >= 0; j--) {
				if (value[j] > value[j+1]) swap(value,j,j+1);
				else break;
			}
		}
		return (value);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////	
	/**
	 * smaller element go down
	 */
	private static void percolateDown(int[] value, int i, int n)
	{
		int child = 2 * i + 1;
		int rightchild = 2 * i + 2;
		if (child<=n) {
			if ((rightchild<=n)&&(value[child]<value[rightchild])) {
				child = rightchild;
			}
			if (value[i] < value[child]) {
				swap(value, i, child);
				percolateDown(value, child, n);
			}
		}
	}

	private static int[] DoHeapSort(int[] value)
	{
		for (int i = value.length/2 - 1; i >= 0;i--) percolateDown(value, i, value.length - 1); //build heap
		for (int i = value.length - 1; i >= 1; i--) { //delete
			swap(value, 0, i);
			percolateDown(value, 0, i-1);
		}
		return (value);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////
	private static int[] Merge(int[] value1, int[] value2)
	{
		int[] value = new int[value1.length + value2.length];
		int index1 = 0, index2 = 0;
		while(index1<value1.length && index2<value2.length) value[index1 + index2] = (value1[index1] > value2[index2]) ? value2[index2++]:value1[index1++]; //copy value1, value2 until one of them become empty
		if(index1 == value1.length) System.arraycopy(value2, index2, value, index1 + index2, value2.length - index2); //copy remaining value2 to value
		else System.arraycopy(value1, index1, value, index1 + index2, value1.length - index1); //copy remaining value1 to value
		return (value);
	}

	private static int[] DoMergeSort(int[] value)
	{
		if(value.length > 1) {
			int[] value1 = Arrays.copyOfRange(value, 0, value.length/2);
			int[] value2 = Arrays.copyOfRange(value, value.length/2, value.length);
			value = Merge(DoMergeSort(value1), DoMergeSort(value2));
		}
		return (value);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * returns index of a pivot
	 */
	private static int partition(int[] value, int start, int end) { //set leftmost element as a pivot
		int pivot = value[start];
		int left = start+1, right = end-1;
		outerloop:
		while(true) {
			while(value[right]>pivot) { 
				right--; //value[right] is rightmost item smaller than pivot
				if(right == start) break outerloop;
			}
			while(value[left]<=pivot) {				
				left++; //value[left] is leftmost item bigger than pivot
				if(left == end) break outerloop;
			}
			if(left>=right) break; 
			swap(value, left, right);
		}
		swap(value, start, right); //change pivot and rightmost item smaller than pivot
		return right; // index of pivot
	}
	
	private static int[] QuickSort(int[] value, int start, int end) {
		if(start+1<end) {
			int index = partition(value, start, end);
			QuickSort(value, start, index);
			QuickSort(value, index+1, end);
		}
		return (value);
	}

	private static int[] DoQuickSort(int[] value)
	{
		return QuickSort(value,0,value.length);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * sort by coefficient of n^k(
	 */
	private static int[] CountSort(int[] value, int n, int k)
	{
		int[] count = new int[2*n - 1]; // coefficient is between -(n-1) and (n-1)
		int[] temp = new int[value.length]; //change value into (coefficient of n^k)+(n-1) and store them in temp
		for(int i = 0; i < value.length; i++) count[temp[i] = (value[i]/(int)Math.pow(n, k) % n + n -1)]++; //count[i] indicates number of element whose coefficient of n^k is -n+1+i
		for(int i = 1; i < 2*n - 1; i++) count[i] += count[i - 1]; //now count[i]-1 is index where last element whose coefficient of n^k is -n+1+i should go
		int[] result = new int[value.length];
		for(int i = value.length - 1; i >= 0; i--) {
			result[count[temp[i]] - 1] = value[i]; //store stable sorted array in result
			count[temp[i]]--;
		}
		return result;
	}

	private static int[] DoRadixSort(int[] value)
	{
		int max = 0;
		for(int i = 0; i < value.length; i++) max = value[i] > max ? value[i] : max; //max is maximum element
		int n = 16;  //radix
		max= (int)(Math.log(max)/Math.log(n)); //number of digits for maximum element
		for (int i = 0; i<= max; i++) value = CountSort(value, n, i);
		return (value);
	}
}
