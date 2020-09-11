/**
 * This program prompts the user for input values, N, # of threads, and an optional "-nolock" 
 * stipulation to compute the Collatz stopping time for each number in the range of 1 to N.  
 * The frequency of each stopping time is recorded in a histogram array that is printed to console
 * and the max number tested, the number of threads used to calculate the Collatz stopping times,
 * as well as the time to complete the calculations are printed to standard error.
 *
 * @author Jacob Leiner
 * @date 07/05/2020
 * @info Course COP5518
 */

import java.time.Instant;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.lang.Thread;

public class MTCollatz {

	public static void main(String[] args) {

		// Stores input from cmd line into global variables
			int maxNum = Integer.parseInt(args[0]);
			int numberOfThreads = Integer.parseInt(args[1]);
			boolean useLock;
			if (args.length == 3 && args[2].equals("-nolock")) {
				useLock = false;
			} else {
				useLock = true;
			}

		// Instantiate thread array and shared data object
		Thread[] threads = new MyThread[numberOfThreads];
		SharedData sharedData = new SharedData();

		// Begin Timer
		long start = Instant.now().toEpochMilli();

		// Create and run each thread in thread array
		for (int i = 0; i < numberOfThreads; i++) {
			threads[i] = new MyThread(sharedData, maxNum, useLock);
			threads[i].start();
		}

		// Join all threads
		for (int i = 0; i < numberOfThreads; i++) {
			try {
				threads[i].join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		// Stop Timer
		long end = Instant.now().toEpochMilli();

		// Print result of stopping time histogram to console.
		for (int i = 0; i < sharedData._a.length; i++) {
			System.out.println((i + 1) + " " + sharedData._a[i]);
		}
		
		// Print N, Number of Threads, and Elapsed time to stderr
		System.out.println(maxNum +", " + numberOfThreads + ", " + (end - start));
	}

	/*
	 * Shared data class shared amongst all threads protecting shared resources
	 * using ReentrantLock()
	 */
	public static class SharedData {
		private int counter;
		private int[] _a;
		private Lock _counterLock;
		private Lock _arrayLock;

		/**
		 * Constructor for SharedData Object
		 * 
		 */
		public SharedData() {
			this.counter = 1;
			this._a = new int[500];
			this._counterLock = new ReentrantLock();
			this._arrayLock = new ReentrantLock();
		}

		/**
		 * Increments array index for relevant stopping time using ReentrantLock()
		 * 
		 * @param int    indexToIncrement index to increment
		 * @param Thread thread incrementing array
		 */
		public void incrementArrayIndex(int indexToIncrement, Thread thread, boolean useLock) {
			if (useLock) {
				_arrayLock.lock();
				try {
					this._a[indexToIncrement] += 1;
				} finally {
					_arrayLock.unlock();
				}
			} else {
				this._a[indexToIncrement] += 1;
			}
		}

		/**
		 * Retrieves shared counter value and increments counter using ReentrantLock()
		 * 
		 * @param Thread thread incrementing array
		 * 
		 * @return returns current value of global counter variable
		 */
		public int getCounterValue(Thread thread, boolean useLock) {
			int temp;
			if (useLock) {
				_counterLock.lock();
				try {
					temp = this.counter;
					this.counter += 1;
				} finally {
					_counterLock.unlock();
				}
				return temp;
			} else {
				temp = this.counter;
				this.counter += 1;
				return temp;
			}
		}
	}

	/*
	 * Extended thread class that calculates the Collatz stopping time for each
	 * number in the range until the global counter data within the SharedData
	 * object exceeds the maxNum
	 */
	public static class MyThread extends Thread {
		private SharedData sharedData;
		private int maxNum;
		private boolean useLock;

		/**
		 * Constructor for extended Thread class Object
		 * 
		 * @param SharedData sharedData object that houses protected global variables
		 *                   and resources
		 * 
		 * @param int        maxNum is N input from user
		 */
		public MyThread(SharedData sharedData, int maxNum, boolean useLock) {
			this.maxNum = maxNum;
			this.sharedData = sharedData;
			this.useLock = useLock;
		}

		/**
		 * Overrides extended Thread class run() method to calculate Collatz stopping
		 * times until the global variable counter is reached
		 * 
		 */
		@Override
		public void run() {
			while (true) {
				int currentNum = sharedData.getCounterValue(this, useLock);
				if (currentNum > maxNum) {
					break;
				}
				int indexToIncrement = collatzPathLength(currentNum);
				sharedData.incrementArrayIndex(indexToIncrement, this, useLock);
			}
		}

		/**
		 * Casts int parameter to long data type to avoid int overflow issue and calls
		 * overloaded collatzPathLength method using the long data type
		 * 
		 * @param int num current number whose Collatz stopping time is being calculated
		 * 
		 * @return the Collatz Stopping time
		 */
		private int collatzPathLength(int num) {
			return collatzPathLength((long) num);
		}

		/**
		 * Calculates Collatz stopping time for a given number
		 * 
		 * @param long num current number whose Collatz stopping time is being
		 *             calculated
		 * 
		 * @return the Collatz Stopping time
		 */
		private int collatzPathLength(long num) {
			int pathLength = 0;
			while (num > 1) {
				if (num % 2 == 1) {
					num = (num * 3) + 1;
				} else {
					num /= 2;
				}
				pathLength++;
			}
			return pathLength;
		}
	}
}
