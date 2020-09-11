import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class FileSystemSimulation {
	Node root;
	Node currentDirectory;
	
	public FileSystemSimulation() {
		root = new Node("root", "D", null);
		currentDirectory = root;
	}
	
	/** 
	 * Lists all of the files and directories within the current directory 
	 */
	public void ls() {
		// lists all files and directories within current directory
		Node current = currentDirectory.child;
		while(current != null) {
			System.out.printf("%3s ", current.type);
			System.out.println(current.name);
			current = current.sibling;
		}
	}
	
	/**
	 * Creates a directory if the name is not present in
	 * the current directory.
	 */
	public void mkdir(String directoryName) {
		// creates new directory if it doesn't exist in the current directory already
		Node current = currentDirectory;
		if(!exists(directoryName)) {
			Node newDirectory = new Node(directoryName, "D", currentDirectory);
			// should extract this method
			if(current.child == null) {
				current.child = newDirectory;
			} else {
				if(current.child.sibling == null) {
					current.child.sibling = newDirectory;
				} else {
					newDirectory.sibling = current.child.sibling;
					current.child.sibling = newDirectory;
				}
			}
			System.out.println(newDirectory.name);
		} else {
			System.out.println("A directory named " + directoryName + " exists in this directory already");
		}		
	}
	
	/** 
	 * Changes directory into the specified directory name 
	 */
	public void cd (String directoryName) {
		// this can work for both specific directories or just ".." test for the latter first
		if(directoryName.equals("..")) {
			if(inRoot()) {
				System.out.println("Currently in /root");
			} else { // currentDirectory does not equal root
				currentDirectory = currentDirectory.parent;
				pwd();
			}
		} else {
			Node current = currentDirectory.child;
			if(current.parent.name.equals(directoryName)) {
				currentDirectory = current.parent;
				System.out.println(currentDirectory.fullPath());
			} else {
				while(current != null && currentDirectory != current) {
					if(current.type.equals("D") && current.name.equals(directoryName)) {
						currentDirectory = current;
						System.out.println(currentDirectory.fullPath());
						break;
					}
					current = current.sibling;
				}
				if(currentDirectory != current) {
					System.out.println(directoryName + " is not located in " + currentDirectory.name);
				}
			}
		}
	}
	
	/**
	 * Prints to console the current directory's path
	 */	
	public void pwd() {
		if(inRoot()) {
			System.out.println("/root");
		} else {
			System.out.println(currentDirectory.fullPath());
		}
	}
	
	/**
	 *  Adds a file to the current directory 
	 */
	public void addf(String fileName) {
		// adds file if it doesn't already exist in current directory
		Node current = currentDirectory;
		if(!exists(fileName)) {
			Node newFile = new Node(fileName, "F", currentDirectory.parent);
			// should extract this method
			if(current.child == null) {
				current.child = newFile;
			} else {
				if(current.child.sibling == null) {
					current.child.sibling = newFile;
				} else {
					newFile.sibling = current.child.sibling;
					current.child.sibling = newFile;
				}
			}
		} else {
			System.out.println("A file named " + fileName + " exists in this directory already");
		}
	}

	/**
	 * Renames the current file or directory if it is found in the current directory
	 */
	public void mv (String originalName, String newName) {
		// test if the user is trying to rename the root directory
		if(inRoot() && originalName.equals(root.name)) {
			System.out.println("Cannot rename /root");
		} else if(!exists(newName)) {
			// test if the new name exists in the same directory 
			Node current = currentDirectory.child;
			while(current != null && !current.name.equals(newName)) {
				// if renaming a directory
				if(current.name.equals(originalName) && current.type.contentEquals("D")) {
					current.name = newName;
					break;
				} else if (current.name.equals(originalName)) {
					current.name = newName;
					break;
				}
				current = current.sibling;
			}
		}
	}
	
	/**
	 * Deletes the file or directory if the name to delete is found in the current
	 * directory
	 */
	public void rm(String nameToDelete) {
		Node current = currentDirectory.child;
		// checks first to see if the name exists in current path
		if(!exists(nameToDelete)) {
			if(current.type.equals("F")) {
				System.out.println(nameToDelete + " is not in " + currentDirectory.fullPath());
			} else {
				System.out.println(nameToDelete + " is not in " + current.fullPath());
			}
		} else {
			while(current != null) {
				// removing currentDirectory's child where child has no siblings
				if(current.parent.child.name.equals(current.name) && current.name.equals(nameToDelete)) {
					if(current.sibling != null) {
						current = null; 
					} else { 
						// removing currentDirectory's child where child has at least one sibling
						current.parent.child = current.sibling;
						current = null;
					}
				// removing file or directory if sibling's name is the nameToDelete	
				} else if(current.sibling.name.equals(nameToDelete)) {
					if(current.sibling.sibling != null) {
						current.sibling = current.sibling.sibling;
						current.sibling = null;
						break;
					} else {
						current.sibling = null;
						break;
					}
				}
			current = current.sibling;	
			}
		}
	}
	
	/**
	 * Ends the current simulation
	 */
	public boolean bye() {
		return false;
	}
	
	/**
	 * Helper method that tests if the current directory is the root
	 */
	public boolean inRoot() {
		return root == currentDirectory;
	}
	
	/**
	 * Helper method to identify if the current directory holds a directory or file
	 * with the name parameter
	 */
	private boolean exists(String name) {
		Node current = currentDirectory.child;
		
		while(current != null) {
			if(current.name.equals(name)) {
				return true;
			}
			current = current.sibling;
		}		
		return false;
	}
	
	/**
	 * Nested private class used to create files and directories within simulation
	 */
	private class Node {
		private String name;
		private String type;
		private Node child;
		private Node sibling;
		private Node parent;
		
		public Node(String name, String type, Node parent) {
			this.name = name;
			this.type = type.toUpperCase();
			this.parent = parent;
			this.child = null;
			this.sibling = null;	
		}	
		
		/**
		 * This method returns the path name of the file/directory by traversing
		 * up through the tree using the parent reference until the root is reached
		 */		
		public String fullPath() {
			Node current = this;
			String path = "/" + this.name;
			if(this.parent == null) {
				return path;
			} else {
				while (current != null) {
					if(current.parent == null) {
						return path;
					} else {
						path = "/" + current.parent.name + path;
						current = current.parent;
					}
				}
				return path;
			}
		}
	}
	
	public static void main(String[] args) {
		try {
			// Reads in the text file
			FileInputStream input = new FileInputStream("commands.txt");
			Scanner scanner = new Scanner(input);
			
			// Initializes the simulation and strings used to parse the txt file
			FileSystemSimulation sim = new FileSystemSimulation();
			String command = "";
			String commandParameter1 = "";
			String commandParameter2 = "";
			
			// continues executing commands from the file until the command "bye" is read
			while(scanner.hasNextLine() && !command.equals("bye")) {
				String string = scanner.nextLine();
				String[] commandArray = string.split("\\s+");
				if(commandArray.length > 2) {
					command = commandArray[0].trim();
					commandParameter1 = commandArray[1].trim();
					commandParameter2 = commandArray[2].trim();
					System.out.println("$ " + command + " " + commandParameter1 + " " + commandParameter2);
				} else if(commandArray.length > 1) {
					command = commandArray[0].trim();
					commandParameter1 = commandArray[1].trim();	
					System.out.println("$ " + command + " " + commandParameter1);
				} else {
					command = commandArray[0].trim();
					System.out.println("$ " + command);
				}
				
				// this switch statement allows us to perform method calls from the txt file
				switch(command) {
					case "ls":
						sim.ls();
						break;
					case "pwd":
						sim.pwd();
						break;
					case "bye":
						break;
					case "mkdir":
						sim.mkdir(commandParameter1);
						break;
					case "cd":
						sim.cd(commandParameter1);
						break;
					case "addf":
						sim.addf(commandParameter1);
						break;
					case "mv":
						sim.mv(commandParameter1, commandParameter2);
						break;
					case "rm":
						sim.rm(commandParameter1);
				}
			}		
			
			scanner.close();
		} catch(FileNotFoundException e) {
			System.out.println("File not found");
		}
	}
}
