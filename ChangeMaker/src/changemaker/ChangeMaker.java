
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
import java.io.PrintWriter;
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
                String err = "Not enough denomination info found in input file!";
                throw new InvalidInputFileException(err);
            }
        }

        // fill problems array
        int numOfProblems = 0;

        // next integer is the number of problems
        if (inputFile.hasNextInt())
        {
            numOfProblems = inputFile.nextInt();
        }
        else
        {
            String err = "No problems info found in file, check file.";
            throw new InvalidInputFileException(err);
        }

        // make an array large enough to hold all of the problems
        problems = new int[numOfProblems];

        // get each problem and store it
        for (int i = 0; i < problems.length; i++)
        {
            if (inputFile.hasNextInt())
            {
                problems[i] = inputFile.nextInt();
            }
            else
            {
                String err = "Not enough problem info found in input file!";
                throw new InvalidInputFileException(err);
            }
        }

        inputFile.close();
    }
    
    public int[] makeChangeDynamically(int value, int iterations)
        throws InvalidProblemException, DenominationNotFoundException
    {
        int avgRuntime = 0;
        int answer[] = new int[0];
        runtime = 0;
        
        for (int i = 0; i < iterations; i++)
        {
            answer = makeChangeDynamically(value);
            avgRuntime += runtime;
        }
        
        runtime = avgRuntime / iterations;
        
        return answer;
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
            // used to store the optimal # of coins
            int dynamicCoinCount[] = new int[value + 1];
            // used to store the last coin taken for each solution
            int lastCoinTaken[] = new int[dynamicCoinCount.length];

            // start timer
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

                    lastCoinTaken[i] = denominations[minIndex(possibleSolutions)];
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

    /**
     * This overloaded function allows the user to run this problem a specified
     * number of times so that an average runtime can be taken for more
     * reliable data.
     * 
     * @param value value for which to make change
     * @param iterations number of times to run this problem
     * @return optimal solution
     * @throws changemaker.DenominationNotFoundException
     */
    public int[] makeChangeRecursively(int value, int iterations)
        throws DenominationNotFoundException
    {
        int avgRuntime = 0;
        int tally[] = new int[0];
        runtime = 0;
        
        for (int i = 0; i < iterations; i++)
        {
            tally = makeChangeRecursively(value);
            avgRuntime += runtime;
        }
        
        runtime = avgRuntime / iterations;
        
        return tally;
    }
    
    /**
     * This overloaded function abstracts the recursion so that the user doesn't
     * have to worry about constructing and passing their own coinCount array,
     * and also manages runtime much more easily.
     * 
     * @param value
     * @return 
     * @throws changemaker.DenominationNotFoundException 
     */
    public int[] makeChangeRecursively(int value)
        throws DenominationNotFoundException
    {
        coinCount = new int[value + 1];
        lastCoin = new int[value + 1];
        
        long startTime = System.nanoTime();
        
        recurse(value);
        
        runtime = System.nanoTime() - startTime;
                
        return makeTally(lastCoin, value);
    }
    
    private int recurse(int value)
    {
        // If we are only working with our 1-value "penny" currency, we 
        // need exactly as many coins as our current index.
        if (value < denominations[1])
        {
            // if index is zero, the last coin is zero, otherwise it's 1
            for (int i = 0; i <= value; i++)
            {
                lastCoin[i] = (i != 0 ? 1 : 0);
            }
            return value;
        }
        else
        {
            // Finds the index of the largest denomination under i.
            // This index is used to select currencies from the 
            // denomination array.
            int maxDenomIndex = findMaxDenominationIndex(value);

            // stores the possible solutions to each sub-problem, on each of
            // which we will recurse
            int possibleSolutions[] = new int[maxDenomIndex + 1];

            // For each currency value, subtract that currency then
            // store the result in an array. These will be used to 
            // index back into the newCoinCount array so that the values
            // there can be compared.
            for (int k = 0; k <= maxDenomIndex; k++)
            {
                int j = value - denominations[k];
                possibleSolutions[k] = recurse(j);
            }
            
            // the sub-problem with the least coins
            int min = minValue(possibleSolutions) + 1;
            
            lastCoin[value] = denominations[minIndex(possibleSolutions)];
            
            return min;
        }
    }

    /**
     * Returns the index to the smallest element in an array.
     * 
     * @param values
     * @return 
     */
    private int minIndex(int[] values)
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

    /**
     * Finds the minimum value contained in an array.
     * 
     * @param values array to search for smallest value
     * @return the smallest value
     */
    private int minValue(int[] values)
    {
        return values[minIndex(values)];
    }

    /**
     * This overloaded function allows the user to run this problem a specified
     * number of times so that an average runtime can be taken for more
     * reliable data.
     * 
     * @param value value for which to make change
     * @param iterations number of times to run this problem
     * @return optimal solution
     * @throws changemaker.DenominationNotFoundException
     */
    public int[] makeChangeWithMemoization(int value, int iterations)
        throws DenominationNotFoundException
    {
        int avgRuntime = 0;
        int tally[] = new int[0];
        runtime = 0;
        
        for (int i = 0; i < iterations; i++)
        {
            tally = makeChangeWithMemoization(value);
            avgRuntime += runtime;
        }
        
        runtime = avgRuntime / iterations;
        
        return tally;
    }
    
    /**
     * This overloaded function abstracts the recursion so that the user doesn't
     * have to worry about constructing and passing their own coinCount array,
     * and also manages runtime much more easily.
     * 
     * @param value
     * @return 
     * @throws changemaker.DenominationNotFoundException 
     */
    public int[] makeChangeWithMemoization(int value)
        throws DenominationNotFoundException
    {
        coinCount = new int[value + 1];
        lastCoin = new int[value + 1];
        
        long startTime = System.nanoTime();
        
        makeChangeWithMemoization(value, coinCount);
        
        runtime = System.nanoTime() - startTime;
                
        return makeTally(lastCoin, value);
    }
    
    private int makeChangeWithMemoization(int value, int[] coinCount)
    {
        if (this.coinCount[value] != 0)
        {
           return coinCount[value];
        }
                    
        // If we are only working with our 1-value "penny" currency, we 
        // need exactly as many coins as our current index.
        if (value < denominations[1])
        {
            coinCount[value] = value;
            
            // if index is zero, the last coin is zero, otherwise it's 1
            for (int i = 0; i <= value; i++)
            {
                lastCoin[i] = (i != 0 ? 1 : 0);
            }
            return value;
        }
        else
        {
            // Finds the index of the largest denomination under i.
            // This index is used to select currencies from the 
            // denomination array.
            int maxDenomIndex = findMaxDenominationIndex(value);

            // stores the possible solutions to each sub-problem, on each of
            // which we will recurse
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
            
            // the sub-problem with the least coins
            int min = minValue(possibleSolutions) + 1;
            
            coinCount[value] = min;
            
            lastCoin[value] = denominations[minIndex(possibleSolutions)];
            
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
        System.out.println("\tRuntime: " + cm.getRuntime() + "ns");
        System.out.println("\tAnswer: " + sumCoins(tally));
        System.out.print("\tCoin tally: ");
        for (int i = 0; i < denominations.length; i++)
        {
            System.out.print(denominations[i] + ":" + tally[i]);
            System.out.print((i == denominations.length - 1) ? (" = " + sumValues(tally) + "\n") : " + ");
        }
        System.out.println("--------------");
        
        return sumCoins(tally);
    }

    public static void main(String[] args)
    {
        try
        {
            File inputFile = new File("make_change_input.txt");
            PrintWriter outputFile = new PrintWriter(new File("output.csv"));
            outputFile.println("type,problem,time");

            parseInputFile(inputFile);

            ChangeMaker chg = new ChangeMaker(denominations);
            
            for (int problem : problems)
            {
                System.out.println("**************\nSolving " + problem);
                System.out.println("\tDynamic:");
                chg.printInfo(chg.makeChangeDynamically(problem, 10000), chg);
                outputFile.println("d," + problem + "," + (chg.getRuntime()));
                
                // Problems start to take massive amounts of time around 80
                if (problem < 80)
                {
                    System.out.println("\tRecursive:");
                    chg.printInfo(chg.makeChangeRecursively(problem, 100), chg);
                    outputFile.println("r," + problem + "," + (chg.getRuntime()));
                }
                
                // for some reason memoization breaks at 252... no idea why.
                if (problem < 252)
                {
                    System.out.println("\tRecursive w/memo:");
                    chg.printInfo(chg.makeChangeWithMemoization(problem, 1000), chg);
                    outputFile.println("m," + problem + "," + (chg.getRuntime()));
                }
            }
            
            outputFile.close();
            
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
