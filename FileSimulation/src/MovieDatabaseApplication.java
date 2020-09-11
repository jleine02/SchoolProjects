
/**************************************************************************
 Student Name: Jacob Leiner
 File Name: MovieDatabaseApplication.java
 Assignment number:  Project 3

 This program simulates a database application that stores information on 
 movies by using a hash table.  The user is prompted to enter commands to 
 add, search for, remove, and list movies in the database. 
 **************************************************************************/

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class MovieDatabaseApplication {
	private static final double MAX_LOAD_FACTOR = .5;
	private Node[] table;
	private int count;

	/**
	 * Constructor initializes table to size 13 per instructions
	 */
	public MovieDatabaseApplication() {
		System.out.println("Welcome to The Movie Database");
		this.table = new Node[13];
		this.count = 0;
	}

	/**
	 * Loads movie records from a filename that the user enters after being
	 * prompted.
	 */
	public void readFileIntoDatabase(String filename) {
		try {
			FileInputStream file = new FileInputStream(filename);
			Scanner scanner = new Scanner(file);
			while (scanner.hasNextLine()) {
				String[] temp = scanner.nextLine().split(",");
				addMovie(temp[0], temp[0] + "," + temp[1] + "," + temp[2] + "," + temp[3]);
			}
			System.out.println("File loaded succesffully.");
		} catch (FileNotFoundException e) {
			System.out.println("File not found");
		} catch (Exception e) {
			System.out.println("Error in file.  Please check file and try again");
		}
	}

	/**
	 * Searches hash table for movie title parameter
	 */
	public void checkDatabaseForMovie(String movieTitle) {
		int index = this.getIndex(movieTitle, this.table.length);

		if (table[index] == null || index == -1) {
			System.out.println("Movie not found!\n");
			return;
		} else {
			// start at the first node at the hash index in the array
			Node record = table[index];
			while (record != null && !record.key.equals(movieTitle)) {
				record = record.next;
			}
			if (record == null) {
				System.out.println("Movie not found!\n");
			} else {
				System.out.println("Movie found:");
				record.printInfo();
			}
		}
	}

	/**
	 * Add new movie to hash table. Checks if adding the movie to the hash table
	 * causes the load factor to increase past the MAX_LOAD_FACTOR and doubles the
	 * array size before adding the new movie to the table.
	 */
	public void addMovie(String movieTitle, String concatenatedValue) {
		// create new movie record and increase total movie count
		Node newRecord = new Node(movieTitle, concatenatedValue);
		this.count++;

		// check if adding this movie has exceeded the max load factor
		if (currentlyExceedingLoadFactor(this.count)) {
			doubleTableSize();
		}

		// if there is not currently an entry at the current index add the table there
		if (table[getIndex(movieTitle, this.table.length)] == null) {
			table[getIndex(movieTitle, this.table.length)] = newRecord;
		} else {
			Node current = table[getIndex(movieTitle, this.table.length)];
			while (current.next != null) {
				current = current.next;
			}
			current.next = newRecord;
		}
	}

	/**
	 * Searches for the movieTitle parameter at the hash code index and deletes it
	 * if found
	 */
	public void removeMovie(String movieTitle) {
		int index = this.getIndex(movieTitle, this.table.length);

		// set current to first node to begin searching this linked list
		Node current = table[index];
		// this set of control flow statements handles the deletiong at the first entry
		// at the current index
		if (current.key.equals(movieTitle) && current.next == null) {
			table[index] = null;
			this.count--;
			return;
		} else if (current.key.equals(movieTitle) && !(current.next == null)) {
			table[index] = current.next;
			current = null;
			this.count--;
			return;
		}

		// this while loop iterates through the remaining nodes at the index and deletes
		// the record if its found
		while (current.next != null) {
			if (current.key.equals(movieTitle) && current.next == null) {
				current = null;
			} else if (current.key.equals(movieTitle)) {
				table[index] = current.next;
				current = null;
				this.count--;
				return;
			} else if (current.next.key.equals(movieTitle)) {
				if (current.next.next == null) {
					current.next = null;
					this.count--;
					return;
				} else {
					current.next = current.next.next;
					this.count--;
					return;
				}
			} else {
				current = current.next;
			}
		}
	}

	/**
	 * Prints out all movies currently stored in database in toString() format
	 */
	public void printAllMovies() {
		for (int i = 0; i < table.length; i++) {
			Node current = table[i];
			while (current != null) {
				System.out.println(current.toString());
				current = current.next;
			}
		}
	}

	/**
	 * Copies current table into a temp array and then sets the table to a new array
	 * of double the size adds back all of the items in the temp array to the new
	 * table
	 */
	private void doubleTableSize() {
		Node[] temp = table;
		int tempCount = this.count;
		table = new Node[temp.length * 2];
		this.count = 1;

		for (int i = 0; i < temp.length; i++) {
			Node current = temp[i];
			while (current != null) {
				addMovie(current.key, current.value);
				current = current.next;
			}
		}
	}

	/**
	 * This checks if the current load factor is greater than or equal to the max
	 * load factor allowed
	 */
	private boolean currentlyExceedingLoadFactor(int count) {
		return (double) count / (double) this.table.length >= MAX_LOAD_FACTOR;
	}

	/**
	 * This function hashes the movie title key and returns the index that it should
	 * be stored at if it exists or should be added.  Uses Dan Bernstein's Hashing Method
	 */
	public int getIndex(String key, int tableSize) {
		long stringHash = 5381;
		int hashMultiplier = 33;
		for (int i = 0; i < key.length(); i++) {
			stringHash = (stringHash * hashMultiplier) + (int) key.charAt(i);
		}
		int index = Math.abs((int) (stringHash % table.length));
		return index;
	}

	/**
	 * Node class to store movie entries as a singly linked list within the hash
	 * table
	 */
	private class Node {
		private String key;
		private String value;
		private Node next;

		public Node(String key, String value) {
			this.key = key;
			this.value = value;
			this.next = null;
		}

		/**
		 * Returns a string of the movie field values
		 */
		public String toString() {
			String[] valueStringArray = this.value.split(",");
			return "[Movie Title: " + valueStringArray[0] + ", Lead Actor/Acress: " + valueStringArray[1]
					+ ", Description: " + valueStringArray[2] + ", Year Released: " + valueStringArray[3] + "]";
		}

		/**
		 * Prints the movie records info in the format specified for the search for
		 * movie function in the prompt
		 */
		public void printInfo() {
			String[] valueStringArray = this.value.split(",");
			for (int i = 0; i < valueStringArray.length; i++) {
				if (i == 0) {
					System.out.println("Title: " + valueStringArray[i]);
				} else if (i == 1) {
					System.out.println("Lead Actor/Actress: " + valueStringArray[i]);
				} else if (i == 2) {
					System.out.println("Description: " + valueStringArray[i]);
				} else {
					System.out.println("Year released: " + valueStringArray[i]);
				}
			}
		}
	}

	public static void main(String[] args) {
		Scanner keyboard = new Scanner(System.in);
		MovieDatabaseApplication test = new MovieDatabaseApplication();
		String choice;
		// user prompt to perform operations on the "database"
		do {
			System.out.println("--------Make a choice: ----------");
			System.out.println("Read database file (1)");
			System.out.println("Add a new movie (2)");
			System.out.println("Search for a movie by title (3)");
			System.out.println("Delete a movie (4)");
			System.out.println("Display all movies (5)");
			System.out.println("Exit (-1)");
			System.out.println("-------------------------------");
			System.out.print("Your choice: ");
			choice = keyboard.nextLine();

			// Initializing fields needed to operate on "database"
			String movieTitle;
			String leadActorActress;
			String description;
			String releaseYear;
			String fileName;

			switch (choice) {
			case "-1":
				System.out.println("Thank you for using The Movie Database!\n" + "Goodbye!!!");
				break;
			case "1":
				System.out.print("Enter filename: ");
				fileName = keyboard.nextLine();
				test.readFileIntoDatabase(fileName);
				System.out.println();
				break;
			case "2":
				System.out.print("Enter movie's title: ");
				movieTitle = keyboard.nextLine();
				System.out.print("Enter movie's lead actor/actress: ");
				leadActorActress = keyboard.nextLine();
				System.out.print("Enter movie's description: ");
				description = keyboard.nextLine();
				System.out.print("Enter movie's release year: ");
				releaseYear = keyboard.nextLine();
				test.addMovie(movieTitle, movieTitle + "," + leadActorActress + "," + description + "," + releaseYear);
				System.out.println("Movie inserted succesfully...\n");
				break;
			case "3":
				System.out.print("Enter movie's title: ");
				movieTitle = keyboard.nextLine();
				test.checkDatabaseForMovie(movieTitle);
				System.out.println();
				break;
			case "4":
				System.out.print("Enter movie's title: ");
				movieTitle = keyboard.nextLine();
				test.removeMovie(movieTitle);
				System.out.println();
				break;
			case "5":
				System.out.println("\nCurrent Movies in Database: ");
				test.printAllMovies();
				System.out.println();
				break;
			default:
				System.out.println("Number entered not valid.  Please make " + "another selection from the\n"
						+ "available options.\n");
				break;
			}
		} while (!choice.equals("-1"));
		keyboard.close();
	}
}
