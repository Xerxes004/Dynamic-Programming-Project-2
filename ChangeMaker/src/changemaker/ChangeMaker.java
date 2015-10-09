
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

    private int[] lastCoin;
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
            System.out.print("Denominations: ");
            int i = 0;
            for (int coin : denominations)
            {
                System.out.print(coin + (++i == denominations.length ? "\n" : ", "));
            }
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
     * @throws changemaker.DenominationNotFoundException
     */
    public int[] makeChangeDynamically(int value)
        throws InvalidProblemException, DenominationNotFoundException
    {
        if (value < 1)
        {
            throw new InvalidProblemException("Invalid money value: " + value);
        }
        else
        {
            int dynamicCoinCount[] = new int[value + 1];
            int lastCoinTaken[] = new int[dynamicCoinCount.length];

            long startTime = System.nanoTime();

            for (int i = 0; i <= value; i++)
            {
                // If we are only working with our 1-value "penny" currency, we 
                // need exactly as many coins as our current index.
                if (i < denominations[1])
                {
                    dynamicCoinCount[i] = i;
                    // if index is zero, the last coin is zero, otherwise it's 1
                    lastCoinTaken[i] = (i != 0 ? 1 : 0);
                }
                else
                {
                    // Finds the index of the largest denomination under i.
                    // This index is used to select currencies from the 
                    // denomination array.
                    int maxDenomIndex = findMaxDenominationIndex(i);

                    int possibleSolutions[] = new int[maxDenomIndex + 1];

                    // For each currency value, subtract that currency then
                    // store the result in an array. These will be used to 
                    // index back into the newCoinCount array so that the values
                    // there can be compared.
                    for (int k = 0; k <= maxDenomIndex; k++)
                    {
                        int j = i - denominations[k];
                        possibleSolutions[k] = dynamicCoinCount[j];
                    }

                    // the solution to problem i is the the best-possible 
                    // sub-problem solution
                    dynamicCoinCount[i] = dynamicCoinCount[minValue(possibleSolutions)] + 1;

                    lastCoinTaken[i] = denominations[min(possibleSolutions)];
                }
            }

            runtime = System.nanoTime() - startTime;

            return makeTally(lastCoinTaken, value);
        }
    }

    /**
     * Finds the corresponding index in the denominations array where the given
     * currency value appears.
     *
     * @param currencyValue
     * @return
     */
    private int denominationIndex(int currencyValue)
        throws DenominationNotFoundException
    {
        int index = 0;
        for (int coin : denominations)
        {
            if (coin == currencyValue)
            {
                break;
            }
            else
            {
                index++;
                
                if (index == denominations.length)
                {
                    String err = "Denomination not found: " + currencyValue;
                    throw new DenominationNotFoundException(err);
                }
            }
        }
        return index;
    }

    /**
     * Makes a tally from a lastCoinTake array and a value.
     *
     * @param lastCoinTakenArray an array of the last coin taken for each
     * problem at index i
     * @param value the value for which we're finding a tally
     * @return the tally of coins that build the optimal solution
     */
    private int[] makeTally(int[] lastCoinTakenArray, int value)
        throws DenominationNotFoundException
    {
        // "tally" is the data structure used to keep track of the number of
        // coins used in our solution. With it we can figure out how many coins
        // there are total and what the total value of the tally is.
        int[] tally = new int[denominations.length];

        int i = value;
        while (i > 0)
        {
            // We increment the index of the tally to the corresponding last
            // coin taken. i.e. if currency value #2 is a 7, then we increment
            // index 1 in the tally to indicate we found a coin of that type.
            tally[denominationIndex(lastCoinTakenArray[i])]++;
            // The next location to index in the lastCoinTaken array is the
            // current index minus the last-coin's value.
            i -= lastCoinTakenArray[i];
        }

        return tally;
    }

    /**
     * Finds the index (based on an array of integer denominations) which
     * indexes to a denomination that is less than the value.
     *
     * @param value value of currency for which we are finding the highest
     * denomination index
     * @return the index of the highest denomination less than the value
     */
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

    public int makeChangeWithMemoization(int value)
    {
        coinCount = new int[value + 1];
        int answer = makeChangeWithMemoization(value, coinCount);
        
        int i = 0;
        System.out.println("\n\n------------------------");
        for (int count : coinCount)
        {
            System.out.print(count + (++i % 10 == 0 && i != 0 ? "\n" : ","));
        }
        
        return answer;
    }
    
    private int makeChangeWithMemoization(int value, int[] coinCount)
    {
        if (coinCount[value] != 0)
        {
           return coinCount[value];
        }
                    
        // If we are only working with our 1-value "penny" currency, we 
        // need exactly as many coins as our current index.
        if (value < denominations[1])
        {
            coinCount[value] = value;
            return value;
        }
        else
        {
            // Finds the index of the largest denomination under i.
            // This index is used to select currencies from the 
            // denomination array.
            int maxDenomIndex = findMaxDenominationIndex(value);

            int possibleSolutions[] = new int[maxDenomIndex + 1];

            // For each currency value, subtract that currency then
            // store the result in an array. These will be used to 
            // index back into the newCoinCount array so that the values
            // there can be compared.
            for (int k = 0; k <= maxDenomIndex; k++)
            {
                int j = value - denominations[k];
                possibleSolutions[k] = makeChangeWithMemoization(j, coinCount);
            }
            
            int min = minValue(possibleSolutions) + 1;
            
            coinCount[value] = min;

            return min;
        }
    }

    public long getRuntime()
    {
        return runtime;
    }

    /**
     * Sums the amount of coins in an integer array.
     *
     * @param tally the integer array to sum
     * @return the sum of the integers in the array
     */
    private int sumCoins(int[] tally)
    {
        int sum = 0;

        for (int i : tally)
        {
            sum += i;
        }

        return sum;
    }
    
        /**
     * Sums the amount of coins in an integer array.
     *
     * @param tally the integer array to sum
     * @return the sum of the integers in the array
     */
    private static int sum(int[] tally)
    {
        int sum = 0;

        for (int i : tally)
        {
            sum += i;
        }

        return sum;
    }

    /**
     * Sums the values of the coins in an array that has the same length as the
     * denomination array.
     *
     * @param tally the tally which contains a number of coins per denomination
     * as specified in the denominations array
     * @return the sum of the currency values
     */
    private int sumValues(int[] tally)
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

    /**
     * Prints information from
     *
     * @param tally
     * @param cm
     */
    public int printInfo(int[] tally, ChangeMaker cm)
    {
        System.out.println("Runtime: " + cm.getRuntime() + "ns");
        System.out.println("Answer: " + sumCoins(tally));
        for (int i = 0; i < denominations.length; i++)
        {
            System.out.print(denominations[i] + ":" + tally[i]);
            System.out.print((i == denominations.length - 1) ? (" = " + sumValues(tally) + "\n") : " + ");
        }
        
        return sumCoins(tally);
    }

    public static void main(String[] args)
    {
        try
        {
            File file = new File("make_change_input.txt");

            parseInputFile(file);

            ChangeMaker chg = new ChangeMaker(denominations);
            
            int value = 252;
            
            //for (int value = 1; value <= max; value++)
            {
                int i = chg.makeChangeWithMemoization(value);

                int[] j = chg.makeChangeDynamically(value);
                
                if(i != sum(j))
                {
                    System.out.println("fail at " + value + ": " + i + " != " + sum(j));
                }
            }
            //chg.printInfo(chg.makeChangeDynamically(8));
            //chg.printInfo(chg.makeChangeDynamically(22));
        }
        catch (InvalidInputFileException |
            FileNotFoundException |
            ChangeMakerException |
            InvalidProblemException |
            DenominationNotFoundException ex)
        {
            System.out.println(ex.getMessage());
        }

    }
}
