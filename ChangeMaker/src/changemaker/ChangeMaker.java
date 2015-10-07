
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
    private int[] coinCount;

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

            coinCount = new int[1];
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

        // get each denomination
        for (int i = 0; i < denominations.length; i++)
        {
            if (inputFile.hasNextInt())
            {
                denominations[i] = inputFile.nextInt();
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

        // get each denomination
        for (int i = 0; i < problems.length; i++)
        {
            if (inputFile.hasNextInt())
            {
                problems[i] = inputFile.nextInt();
            }
            else
            {
                String err = "No problem info found in file, check file.";
                throw new InvalidInputFileException(err);
            }
        }

        inputFile.close();
    }

    /**
     * Makes change using an internally stored dynamic array.
     *
     * @param value monetary value we are making change for
     * @return tally of which denominations were used (mirrors order of the
     * denominations array).
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

            // TODO assign the tally based on already-computed data
            int tally[] = new int[0];

            runtime = startTime - System.nanoTime();

            return tally;
        }
        // if we haven't already found that value, find all of the new values
        // we need in order to find it
        else
        {
            // we are extending our coinCount array
            int newCoinCount[] = new int[value + 1];
            System.arraycopy(coinCount, 0, newCoinCount, 0, coinCount.length);
            
            coinCount = newCoinCount;
            
            // extend lastCoinTaken array
            int newLastCoinTaken[] = new int[newCoinCount.length];
            System.arraycopy(lastCoinTaken, 0, newLastCoinTaken, 0, lastCoinTaken.length);
            
            lastCoinTaken = newLastCoinTaken;
            

            int[] tally = new int[denominations.length];

            long startTime = System.nanoTime();
            
            for (int i = 0; i <= value; i++)
            {
                // if we are only working with our 1-value "penny" currency, we 
                // need exactly as many coins as our current index
                if (i < denominations[1])
                {
                    coinCount[i] = i;
                    
                    lastCoinTaken[i] = (i != 0 ? 1 : 0);
                }
                else
                {
                    // finds the index of the largest denomination under i.
                    //
                    // this index is used to select currencies from the 
                    //  denomination array
                    int maxDenomIndex = findMaxDenominationIndex(i);

                    // one smaller because we skip the first currency
                    int coinTally[] = new int[maxDenomIndex];

                    // for each currency value, subtract that currency then
                    // store the result in an array. These will be used to 
                    // index back into the newCoinCount array so that the values
                    // there can be compared.
                    for (int k = 1; k <= maxDenomIndex; k++)
                    {
                        // skips the 1-value currency
                        int j = i - denominations[k];
                        coinTally[k - 1] = coinCount[j];
                    }

                    coinCount[i] = coinCount[minValue(coinTally)] + 1;
                }
            }
            
            System.out.println("Coins: ");
            for (int c : coinCount)
            {
                System.out.print(c + ", ");
            }
            System.out.println("");
            runtime = System.nanoTime() - startTime;
            
            return tally;
        }
    }

    private int findMaxDenominationIndex(int value)
    {
        for (int i = 0; i < denominations.length; i++)
        {
            if (denominations[i] > value)
            {
                return (i - 1);
            }
        }
        return denominations.length - 1;
    }

    private int[] makeChangeRecursively(int value)
        throws InvalidProblemException
    {
        // assign the solution to the solution variable
        // return the runtime
        long startTime = System.nanoTime();
        int[] numDenom = new int[denominations.length];
        for (int i = 0; i < denominations.length; i++)
        {
            if (denominations[i] < value)
            {
                numDenom[i]++;

            }
        }
        long endTime = System.nanoTime();
        runtime = startTime - endTime;
        return numDenom;
    }

    private int min(int[] values)
    {
        int least = 0;

        for (int i = 1; i < values.length; i++)
        {
            if (values[least] > values[i])
            {
                least = i;
            }
        }

        return least;
    }
    
    private int minValue(int[] values)
    {
        return values[min(values)];
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

    private int sumCoins(int[] tally)
    {
        int sum = 0;

        for (int i : tally)
        {
            sum += i;
        }

        return sum;
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

    public void printInfo(int[] tally)
    {
        System.out.println("Runtime: " + this.runtime + "ns");
        for (int i = 0; i < denominations.length; i++)
        {
            System.out.print(denominations[i] + ":" + tally[i]);
            System.out.print((i == denominations.length - 1) ? (" = " + sumTally(tally) + "\n") : " + ");
        }
    }

    public static void main(String[] args)
    {
        try
        {
            File file = new File("make_change_input.txt");

            parseInputFile(file);

            ChangeMaker chg = new ChangeMaker(denominations);

            //chg.printInfo(
            chg.makeChangeDynamically(22);
            //);
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
