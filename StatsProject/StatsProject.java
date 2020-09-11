/************************************************************************** 
  Student Name: Jacob Leiner
  File Name: StatsProject.java
  Assignment number:  Project 1

  This program reads in data from a txt file and then performs basic
  statistical analyses on it.  The results of this analyses are printed to
  console in a formatted manner.  
**************************************************************************/

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class StatsProject {

	public static void main(String[] args) {
		try {
			/** Create input stream and Scanner to read file */
			FileInputStream input = new FileInputStream("scores.txt");
			Scanner inputStream = new Scanner(input);
			inputStream.useDelimiter("\n");

			String[] dates = new String[5];
			int[][] scores = new int[5][24];

			readData(inputStream, dates, scores);

			double[][] meansAndSDs = new double[5][2];

			calcMeans(scores, meansAndSDs);
			calcSDs(scores, meansAndSDs);
			printScoresAndStats(dates, scores, meansAndSDs);

			String[][] tTestResults = new String[5][5];
			calcTTest(meansAndSDs, tTestResults);
			printTTestResults(dates, tTestResults);

		} catch (FileNotFoundException e) {
			System.out.println("Error reading file.  Please try again");
		}
	}

	/**
	 * Prints a formatted table with scores listed for each date as well as the
	 * means and standard deviations for each day's scores
	 */
	private static void printScoresAndStats(String[] dates, int[][] scores, double[][] meansAndSDs) {
		int scoreRow = 1;

		System.out.printf("%-60s\n\n", "Means and Standard Deviations of Scores");
		System.out.printf("%-10s", "Student");
		for (int i = 0; i < dates.length; i++) {
			System.out.printf("%-10s", dates[i]);
			if (i == dates.length - 1) {
				System.out.println();
			}
		}

		for (int i = 0; i < scores[0].length; i++) {
			System.out.printf("%-14d", scoreRow);
			for (int j = 0; j < scores.length; j++) {
				System.out.printf("%-10d", scores[j][i]);
				if (j == scores.length - 1) {
					System.out.println();
				}
			}
			scoreRow++;
		}

		System.out.printf("%s\n", "***********************************************************"); // 60 wide
		System.out.printf("%-14s", "Mean");
		for (int i = 0; i < meansAndSDs[0].length; i++) {
			for (int j = 0; j < meansAndSDs.length; j++) {
				System.out.printf("%-10.2f", meansAndSDs[j][i]);
				if (j == meansAndSDs.length - 1 && i == 0) {
					System.out.println();
					System.out.printf("%-14s", "SD");
				}
			}
		}
		System.out.println("\n");
	}

	/**
	 * This method prints a formatted table that prints to console Y (yes) or N (no) 
	 * values for the results of an independent t-test using each standard deviation 
	 * compared to every other day
	 */
	private static void printTTestResults(String[] dates, String[][] tTestResults) {
		System.out.println("Significant Differences in Mean Scores\n");
		for (int i = 0; i < dates.length; i++) {
			if (i == 0) {
				System.out.printf("%10s", "");
				System.out.printf("%-10s", dates[i]);
			} else {
				System.out.printf("%-10s", dates[i]);
				if (i == dates.length - 1) {
					System.out.println();
				}
			}
		}

		for (int i = 0; i < tTestResults.length; i++) {
			for (int j = 0; j < dates.length; j++) {
				if (j == 0) {
					System.out.printf("%-14s", dates[i]);
				}
				System.out.printf("%-10s", tTestResults[i][j]);
				if (j == dates.length - 1) {
					System.out.println();
				}
			}
		}
	}

	/**
	 * This method uses means and SDs within the meansAndSDs 2D array to perform
	 * T-tests. Each mean and SD is compared to another within the meansAndSDs 2d
	 * array and a calculation is performed, adding the strings "Y" or "N" to the 2D
	 * output array parameter for each comparison when their criteria are met
	 */
	private static void calcTTest(double[][] meansAndSDs, String[][] tTestResults) {
		double result = 0;
		for (int i = 0; i < meansAndSDs.length; i++) {
			for (int j = 0; j < meansAndSDs.length; j++) {
				double mean1 = meansAndSDs[i][0];
				double mean2 = meansAndSDs[j][0];
				double sd1 = meansAndSDs[i][1];
				double sd2 = meansAndSDs[j][1];

				result = (mean1 - mean2) / Math.sqrt(Math.pow(sd1, 2) / 24 + Math.pow(sd2, 2) / 24);
				if (Math.abs(result) < 2.25) {
					tTestResults[i][j] = "N";
				} else {
					tTestResults[i][j] = "Y";
				}
			}

		}
	}

	/**
	 * This method iterates over the scores 2D array and first calculates variances for
	 * each score, adding them to the current sum. Once the variances have been
	 * calculated the SD of each day's scores is copied into the second column of
	 * the meansAndSDs 2d array
	 */
	private static void calcSDs(int[][] scores, double[][] meansAndSDs) {
		for (int i = 0; i < scores.length; i++) {
			double sum = 0;
			for (int j = 0; j < scores[0].length; j++) {
				sum += Math.pow(scores[i][j] - meansAndSDs[i][0], 2);
			}
			meansAndSDs[i][1] = Math.sqrt(sum / scores[0].length);
		}
	}

	/**
	 * This method iterates over the scores array adding each score to a sum and then
	 * dividing that sum by total N. The mean is then copied into the first column
	 * within the meansAndSDs 2D array
	 */
	private static void calcMeans(int[][] scores, double[][] meansAndSDs) {
		for (int i = 0; i < scores.length; i++) {
			double sum = 0;
			for (int j = 0; j < scores[0].length; j++) {
				sum += scores[i][j];
			}

			meansAndSDs[i][0] = sum / scores[0].length;
		}
	}

	/**
	 * This method accepts a scanner as input and then loads date strings into
	 * it's second parameter array and then loads the remaining input from the
	 * scanner into the third 2d array parameter
	 */
	public static void readData(Scanner input, String[] dates, int[][] scores) {
		for (int i = 0; i < scores.length; i++) {
			dates[i] = input.nextLine().trim();
		}

		for (int i = 0; i < scores.length; i++) {
			for (int j = 0; j < scores[0].length; j++) {
				scores[i][j] = Integer.parseInt(input.nextLine().trim());
			}
		}
	}
}
