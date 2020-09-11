# MTCollatz
UWF COP 5518 Project #1 

## Description

This is a java program to calculate the Collatz stopping times for each number in a given range specified by the first argument passed to the program using a user defined number of threads passed as a second argument to the program as well as a third argument which specifies whether synchronization should be used or not.

## Usage

$java MTCollatz.java 10000 5 # 10,000 is the max number to be tested using 5 threads

$java MTCollatz.java 10000 5  -nolock # 10,000 is the max number to be tested using 5 threads

​																		without synchronization

## Notes on creation
Originally I had implemented a solution to the project by setting a lower and upper bound for each thread using a for loop and maxNum / numberOfThreads = numsPerThread.  While this exectued in parallel, it was not what the project requested.  This method only needed to protect the global shared histogram array to make sure it wasn't updated synchronously.  

Upon further consideration, I decided to redo the project using two protected shared resources: a counter variable to keep track of the current number to be calculated and an array to update the frequency of stopping times for a given number.  I had initially created two different objects, SharedInt and SharedData, but decided two combine the two into SharedData to cut down on noise and streamline my code using a ReentrantLock() each when accessing the shared counter and array.

Once this was working, I needed to add a flag to my program that controlled whether synchronization was used or not to note the difference in elapsed time calculating all of the Collatz stopping times for a given range of numbers.  I did this by adding a flag useLock that was set to false when the command line arg "-nolock" was used when running the program.  This flag was passed to all methods that stipulated if a ReentrantLock() was to be used when reading or writing to a shared resource.



