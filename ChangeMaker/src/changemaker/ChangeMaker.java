
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
     * @throws ChangeMakerException thrown when the denomination array has
     * values that don't make sense
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
            // denominations must be in sorted order for selection logic to
            // work properly
            Arrays.sort(denominations);

            System.out.print("Denominations: ");
            int i = 0;
            for (int coin : denominations)
            {
                System.out.print(
                    coin + (++i == denominations.length ? "\n" : ", ")
                );
            }
        }
    }

    /**
     * Finds denomination and problem information in a file and stores it in
     * static internal variables.
     *
     * @param inputFile file that contains denomination information
     * @return the array of denominations
     * @throws InvalidDenominationException thrown when there are not enough
     * denominations specified in the file
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
                String err
                    = "Not enough denomination info found in input file!";
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

    /**
     * Overloaded function allows the user to iterate the function a large
     * amount of times so that non-noisy data can be collected.
     *
     * @param value value for which to make change
     * @param iterations amount of times to solve the problem
     * @return the tally of coins
     * @throws InvalidProblemException if the value is less than 1
     * @throws DenominationNotFoundException if a denomination is not found
     */
    public int[] makeChangeDynamically(int value, int iterations)
        throws InvalidProblemException, DenominationNotFoundException
    {
        long avgRuntime = 0;
        int answer[] = new int[0];
        runtime = 0;

        for (int i = 0; i < iterations; i++)
        {
            answer = makeChangeDynamically(value);
            avgRuntime = (avgRuntime + runtime) / (i + 1);
        }

        runtime = avgRuntime;

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
                    lastCoinTaken[i] = (i == 0 ? 0 : 1);
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
                    dynamicCoinCount[i]
                        = minValue(possibleSolutions) + 1;

                    lastCoinTaken[i]
                        = denominations[minIndex(possibleSolutions)];
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
     * @param currencyValue the value for which we are finding a corresponding
     * index in the denomination array
     * @return the index of the currencyValue specified
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
                    String err = "Denomination of value " + currencyValue;
                    err += " not found.";
                    throw new DenominationNotFoundException(err);
                }
            }
        }
        return index;
    }

    /**
     * Makes a tally from a lastCoinTaken array and a value.
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
     * number of times so that an average runtime can be taken for more reliable
     * data.
     *
     * @param value value for which to make change
     * @param iterations number of times to run this problem
     * @return optimal solution
     * @throws changemaker.DenominationNotFoundException
     */
    public int[] makeChangeRecursively(int value, int iterations)
        throws DenominationNotFoundException
    {
        long avgRuntime = 0;
        int tally[] = new int[0];
        runtime = 0;

        for (int i = 0; i < iterations; i++)
        {
            tally = makeChangeRecursively(value);
            avgRuntime = (avgRuntime + runtime) / (i + 1);
        }

        runtime = avgRuntime;

        return tally;
    }

    /**
     * This overloaded function abstracts the recursion so that the user doesn't
     * have to worry about constructing and passing their own coinCount array,
     * and also manages runtime much more easily.
     *
     * @param value value of which we are making change
     * @return the tally of coins used to make up the optimal solution
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

    /**
     * Recursive function which solves the making change problem.
     *
     * @param value value for which we are making change
     * @return the minimum coin amount found for the solution of value
     */
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
     * @param values array of values, the smallest of which we will return the
     * index
     * @return the index of the smallest value
     */
    private int minIndex(int[] values)
    {
        int least = 0;

        for (int i = 0; i < values.length; i++)
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
     * number of times so that an average runtime can be taken for more reliable
     * data.
     *
     * @param value value for which to make change
     * @param iterations number of times to run this problem
     * @return optimal solution
     * @throws changemaker.DenominationNotFoundException
     */
    public int[] makeChangeWithMemoization(int value, int iterations)
        throws DenominationNotFoundException
    {
        long avgRuntime = 0;
        int tally[] = new int[0];
        runtime = 0;

        for (int i = 0; i < iterations; i++)
        {
            tally = makeChangeWithMemoization(value);
            avgRuntime = (avgRuntime + runtime) / (i + 1);
        }

        runtime = avgRuntime;

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

    /**
     * Recursive function which is used to solve the making change problem,
     * except this recursive algorithm memoizes optimal solutions.
     *
     * @param value the value for which we are making change
     * @param coinCount the array which holds the optimal solutions
     * @return the optimal solution for value
     */
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

    /**
     * Returns the runtime of the last making change algorithm.
     *
     * @return the runtime of the last making change algorithm
     */
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
     * Prints relevant information to the screen from the last run. This
     * function is meant to have a call to an algorithm as the tally argument so
     * that it gets printed directly after running.
     *
     * @param tally a tally of an optimal solution
     * @param cm the ChangeMaker object we are interested in
     */
    public int printInfo(int[] tally, ChangeMaker cm)
    {
        System.out.println("  Optimal number of coins: " + sumCoins(tally));
        System.out.print("  " + sumValues(tally) + " = ");
        for (int i = 0; i < denominations.length; i++)
        {
            if (tally[i] != 0)
            {
                System.out.print(denominations[i] + ":" + tally[i] + " ");
            }
        }
        System.out.println("");

        return sumCoins(tally);
    }

    public static void main(String[] args)
    {
        try
        {
            File inputFile = new File("make_change_input.txt");
            PrintWriter outputFile = new PrintWriter(new File("output.csv"));
            outputFile.println("type,problem,answer,time");

            parseInputFile(inputFile);

            ChangeMaker chg = new ChangeMaker(denominations);

            int max = 5000;
            int timesToSolve = 100;

            //for (int problem : problems)
            /*
             for (int problem = 1; problem <= max; problem++)
             {
             //int problem = 252;
            
             System.out.println("\nPROBLEM " + problem);
             System.out.println("--------------");
             System.out.println("Dynamic");
             int dyn[] = chg.makeChangeDynamically(problem);
             chg.printInfo(dyn, chg);

             // Problems start to take massive amounts of time around 50
             if (problem < 50)
             {
             System.out.println("Recursive");
             chg.printInfo(
             chg.makeChangeRecursively(problem, timesToSolve), chg);
             }

             System.out.println("Memoized");
             int mem[] = chg.makeChangeWithMemoization(problem);
             chg.printInfo(mem, chg);
                
             assert (sum(mem) == sum(dyn));
             assert (chg.sumCoins(mem) == chg.sumCoins(dyn));
             assert (chg.sumValues(mem) == chg.sumValues(dyn));
             }*/
            boolean generateOutput = true;

            if (generateOutput)
            {
                for (int problem = 1; problem <= max; problem++)
                {
                    if (problem % 100 == 0)
                    {
                        System.out.println(problem);
                    }
                    int[] answer
                        = chg.makeChangeDynamically(problem, timesToSolve);
                    outputFile.println(
                        "d," + problem + "," + sum(answer) + ","
                        + chg.getRuntime());

                    // Problems start to take massive amounts of time around 50
                    if (problem < 50)
                    {
                        answer = chg.makeChangeRecursively(
                            problem, timesToSolve);
                        outputFile.println("r," + problem + "," + sum(answer)
                            + "," + (chg.getRuntime()));
                    }

                    answer = chg.makeChangeWithMemoization(
                        problem, timesToSolve);
                    outputFile.println("m," + problem + "," + sum(answer)
                        + "," + (chg.getRuntime()));
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
