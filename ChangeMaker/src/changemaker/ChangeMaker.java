/**
 * Title      : Project 2, CS3410
 * Description: A dynamic-programming and naive solution to the "making change"
 *              problem. 
 * Copyright  : Copyright (c) 2015, Wesley Kelly, James Von Eiff, 
 *                                  Cedarville University
 * @author    : Wesley Kelly, James Von Eiff
 * @version 1.0
 */

package changemaker;

import com.sun.media.sound.InvalidDataException;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class ChangeMaker
{
    private static int[] denominations;
    private static int[] problems;
    
    /**
     * Searches dictionary to determine if key is present 
     * @param denominations_ an array of integer denominations for our money
     * system
     * @throws ChangeMakerException
     */
    public ChangeMaker(int denominations_[])
        throws ChangeMakerException
    {
        String err = "";
        if (denominations_ == null)
        {
            err = "Denomination array passed was null. Check your input file";
            throw new ChangeMakerException(err);
        }
        else if (denominations_.length == 1)
        {
            err = "No denominations specified, denomination array length 0";
            throw new ChangeMakerException(err);
        }
        else
        {
            denominations = denominations_;
        }
    }
    
    /**
     * Finds denomination information in a file.
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
        System.out.println("Denominations found: " + denominations.length);
        
        // get each denomination
        for (int i = 0; i < denominations.length; i++)
        {
            if (inputFile.hasNextInt())
            {
                denominations[i] = inputFile.nextInt();
                System.out.println("Denomination "+ (i + 1) + ": " + denominations[i]);
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
        System.out.println("Problems found: " + problems.length);
        
        // get each denomination
        for (int i = 0; i < problems.length; i++)
        {
            if (inputFile.hasNextInt())
            {
                problems[i] = inputFile.nextInt();
                System.out.println("Problem " + (i + 1) + ": " + problems[i]);
            }
            else
            {
                String err = "No problem info found in file, check file.";
                throw new InvalidInputFileException(err);
            }
        }
    }
    
    private int makeChangeRecursively(int value)
        throws InvalidProblemException
    {
        return -1;
    }
    
    private int makeChangeDynamically(int value)
        throws InvalidProblemException
    {
        return -1;
    }
    
    private int makeChangeWithMemoization(int value)
        throws InvalidProblemException
    {
        return -1;
    }
    
    public static void main(String[] args)
    {
        try
        {
            File file = new File("make_change_input.txt");
            
            parseInputFile(file);
            
            ChangeMaker chg = new ChangeMaker(denominations);
            
        }
        catch (
            InvalidInputFileException | 
            FileNotFoundException | 
            ChangeMakerException ex
        )
        {
            System.out.println(ex.getMessage());
        }
        
    }
}
