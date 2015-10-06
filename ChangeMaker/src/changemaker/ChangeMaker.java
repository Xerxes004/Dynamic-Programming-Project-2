/**
 * Title : Project 2, CS3410 Description: A dynamic-programming and naive
 * solution to the "making change" problem.
 *
 * Copyright : Copyright (c) 2015 Wesley Kelly, James Von Eiff, Cedarville
 * University
 *
 * @author : Wesley Kelly, James Von Eiff
 * @version 1.0
 */

package changemaker;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.Scanner;

public class ChangeMaker
{

    private static int[] denominations;
    private static int[] problems;
    private long runtime;

    private int[] lastCoinTaken;

    /**
     * Constructor which takes an array of denominations.
     *
     * @param denominations_ an array of integer denominations for our money
     * system
     * @throws ChangeMakerException
     */
    public ChangeMaker(int denominations_[])
        throws ChangeMakerException
    {
        System.nanoTime();

        if (denominations_ == null)
        {
            String err;
            err = "Denomination array passed was null. Check your input file";
            throw new ChangeMakerException(err);
        }
        else if (denominations_.length == 1)
        {
            String err;
            err = "No denominations specified, denomination array length 0";
            throw new ChangeMakerException(err);
        }
        else
        {
            denominations = denominations_;
            Arrays.sort(denominations);
            lastCoinTaken = new int[1];
            // seed the last coin taken array
            lastCoinTaken[0] = 1;
        }
    }

    /**
     * Finds denomination and problem information in a file and stores it in
     * static internal variables.
     *
     * @param inputFile file that contains denomination information
     * @return the array of denominations
     * @throws InvalidDenominationException
     */
    private static void parseInputFile(File inputFile_)
        throws InvalidInputFileException, FileNotFoundException
    {
        Scanner inputFile = new Scanner(inputFile_);

        // fill denominations array
        int numOfDenominations = 0;

        // first integer is the number of denominations
        if (inputFile.hasNextInt())
        {
            numOfDenominations = inputFile.nextInt();
        }
        else
        {
            String err = "No denomination info found in file, check file.";
            throw new InvalidInputFileException(err);
        }

        // make an array large enough to hold all of the denominations
        denominations = new int[numOfDenominations];
        //System.out.println("Denominations found: " + denominations.length);

        // get each denomination
        for (int i = 0; i < denominations.length; i++)
        {
            if (inputFile.hasNextInt())
            {
                denominations[i] = inputFile.nextInt();
                //System.out.println("Denomination " + (i + 1) + ": " + denominations[i]);
            }
            else
            {
                String err = "No denomination info found in file, check file.";
                throw new InvalidInputFileException(err);
            }
        }

        // fill problems array
        int numOfProblems = 0;

        // first integer is the number of denominations
        if (inputFile.hasNextInt())
        {
            numOfProblems = inputFile.nextInt();
        }
        else
        {
            String err = "No problems info found in file, check file.";
            throw new InvalidInputFileException(err);
        }

        // make an array large enough to hold all of the denominations
        problems = new int[numOfProblems];
        //System.out.println("Problems found: " + problems.length);

        // get each denomination
        for (int i = 0; i < problems.length; i++)
        {
            if (inputFile.hasNextInt())
            {
                problems[i] = inputFile.nextInt();
                //System.out.println("Problem " + (i + 1) + ": " + problems[i]);
            }
            else
            {
                String err = "No problem info found in file, check file.";
                throw new InvalidInputFileException(err);
            }
        }
    }

    /**
     * Makes change using an internally stored dynamic array.
     * @param value monetary value we are making change for
     * @return tally of which denominations were used (mirrors order of the
     *         denominations array).
     * @throws InvalidProblemException throws when value is less than 1
     */
    private int[] makeChangeDynamically(int value)
        throws InvalidProblemException
    {
        if (value < 1)
        {
            throw new InvalidProblemException("Invalid money value: " + value);
        }
        
        // if we already have stored that value
        if (value < lastCoinTaken.length)
        {
            long startTime = System.nanoTime();
            
            int tally[] = new int[0];
            
            runtime = startTime - System.nanoTime();
            
            return tally;
        }
        // if we haven't already found that value, find all of the new values
        // we need in order to find it
        else
        {
            int newLastCoinArray[] = new int[value];
            System.arraycopy(lastCoinTaken, 0, newLastCoinArray, 0, lastCoinTaken.length);
            
            long startTime = System.nanoTime();
            
            int[] tally = new int[denominations.length];
            
            
            // THIS ALGORITHM DOES NOT WORK YET
            for (int i = 0; i < value; i++)
            {
                int k = i + 1;
                
                while (k > 0)
                {
                    // find the max denomination under k
                    int max = findMaxDenomination(k);
                    k -= max;
                    
                    int lastCoin = max;
                    
                    System.out.println(max + " >= " + k);
                    
                    for (int j = 0; j < denominations.length; j++)
                    {
                        if (lastCoin == denominations[j])
                        {
                            tally[j]++;
                            newLastCoinArray[i] = lastCoin;
                            break;
                        }
                    }
                    
                    
                    
                    newLastCoinArray[i] = lastCoin;
                    
                    System.out.println("new index " + k);
                    
                    // find the previous coin by indexing to the sub-problem
                    // solution
                    
                    
                }
            }
            
            runtime =  System.nanoTime() - startTime;
            
            lastCoinTaken = newLastCoinArray;
            
            return tally;
        }
    }
    
    private int findMaxDenomination(int value)
    {
        for (int i = 1; i < denominations.length; i++)
        {
            if (denominations[i] > value)
            {
                // this can never index out of bounds because we start at 1
                return denominations[i - 1];
            }
        }
        return denominations[denominations.length - 1];
    }

    private int makeChangeRecursively(int value)
        throws InvalidProblemException
    {
        // assign the solution to the solution variable
        // return the runtime
        long startTime = System.nanoTime();

        return -1;
    }
    
    private int min(int[] values) {
        int least = 0;
        for(int i = 1; i < values.length; i++) {
            if(values[least] > values[i]) {
                least = i;
            }
        }
        return least;
    }

    private int makeChangeWithMemoization(int value)
        throws InvalidProblemException
    {
        // assign the solution to the solution variable
        // return the runtime
        return -1;
    }
    
    public long getRuntime()
    {
        return runtime;
    }
    
    public void printInfo(int[] tally)
    {
        System.out.println("Runtime: " + this.runtime + "ns");
        for (int i = 0; i < denominations.length; i++)
        {
            System.out.print(denominations[i] + ":" + tally[i]);
            System.out.print( (i == denominations.length - 1) ? (" = " + sumTally(tally) + "\n") : " + ");
        }
    }
    
    private int sumTally(int[] tally)
    {
        int sum = 0;
        
        for (int i = 0; i < tally.length; i++)
        {
            for (int j = 0; j < tally[i]; j++)
            {
                sum += denominations[i];
            }
        }
        
        return sum;
    }

    public static void main(String[] args)
    {
        try
        {
            File file = new File("make_change_input.txt");

            parseInputFile(file);

            ChangeMaker chg = new ChangeMaker(denominations);
            
            chg.printInfo(chg.makeChangeDynamically(7));
            //chg.printInfo(chg.makeChangeDynamically(8));
            //chg.printInfo(chg.makeChangeDynamically(22));
        }
        catch (InvalidInputFileException |
            FileNotFoundException |
            ChangeMakerException |
            InvalidProblemException ex)
        {
            System.out.println(ex.getMessage());
        }

    }
}
