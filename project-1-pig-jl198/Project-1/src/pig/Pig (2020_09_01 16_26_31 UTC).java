// Jacob Leiner, COP 5007 - Project 1: Pig, January 19, 2020

//package pig;

import java.util.Random;
import java.util.Scanner;

public class Pig {
	public static void main(String[] args) {
		System.out.println("Welcome to the game of Pig!");  
		
		int target;
		int randSeed;
		Random rand;
		Scanner scnr = new Scanner(System.in);

		System.out.print("How many points are required to win? ");
		target = scnr.nextInt();
		
		System.out.print("Enter a value to use to 'seed' the random number(negative number will not seed): ");
		randSeed = scnr.nextInt();
		scnr.nextLine(); // this let's you use the scanner again
		System.out.println();
		
		// if the user input is greater than or equal to 0, the Random object is seeded using the input
		// if the user input is less than 0, the Random object is not seeded
		if(randSeed < 0) {
			rand = new Random();
		} else {
			rand = new Random(randSeed);
		}
		
		// creates individual players using static class Player object created below
		Player user = new Player(rand, scnr, target, false);
		Player computer = new Player(rand, scnr, target, true);
		
		// this controls the gameplay for each player and continues until one player wins
		while(true) {
			if(user.play()) {
				System.out.println("YOU WIN!");
				break;
			} 
			if (computer.play()) {
				System.out.println("THE COMPUTER WINS!");
				break;
			}
		}
			
	}

	public static class Player {
		private Random rand;
		private Scanner scnr;
		private int score;
		private int target;
		private boolean computer;
		private int tempScore;
		
		// Constructor using user input from client
		public  Player(Random rand, Scanner scnr, int target, boolean computer) {
			this.rand = rand;
			this.scnr = scnr;
			this.target = target;
			this.computer = computer;
		}
		
		public boolean play() {
			this.tempScore = 0;
			int roll;
			
			if(computer) {
					System.out.println("It is the computer's turn.");
				} else {
					System.out.println("Your turn.");
			}

			// control logic for each turn based off whether the user or computer is up
			do {
				if(computer) {
					roll = this.rand.nextInt(6) + 1;
					tempScore += roll;
					System.out.println("\tThe computer rolled: " + roll);
				} else {
					roll = this.rand.nextInt(6) + 1;
					tempScore += roll;
					System.out.println("\tYou rolled: " + roll);
					System.out.println("\tYour turn score is " + tempScore + " and your total score is " + this.score);
					System.out.println("\tIf you hold, you will have " + (tempScore + this.score) + " points.");
				}
			} while (this.rollAgain());
			
			this.score += tempScore;
			if(computer) {
				System.out.println("\tThe computer's score is: " + this.score);
				System.out.println();
			} else {
				System.out.println("\tYour score is: " + this.score);
				System.out.println();
		}
			return this.score >= this.target;
		}
		
		// helper method to control whether each player continues to roll or not
		private boolean rollAgain() {
			if(this.computer) {
				return tempScore <= 20 && tempScore + this.score <= this.target;
			} else {
				if(tempScore + this.score >= this.target) {
					return false;
				}
				System.out.print("\tEnter 'r' to roll again, 's' to stop: ");
				String action = scnr.nextLine().toUpperCase();
				if(action.equals("R")) {
					return true;
				} else {
					return false;
				}
			}
		}
	}
}


