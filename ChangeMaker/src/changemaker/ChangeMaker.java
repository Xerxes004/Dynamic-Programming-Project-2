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

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class ChangeMaker
{
    private int[] denominations;
    
    /**
     * Searches dictionary to determine if key is present 
     * @param denominations_ an array of integer denominations for our money
     * system
     * @throws InvalidDenominationException
     */
    public ChangeMaker(int denominations_[])
        throws InvalidDenominationException
    {
        if (denominations_ == null)
        {
            String err = "";
            err += "Denomination array passed was null. Check your input file";
            throw new InvalidDenominationException(err);
        }
        
        denominations = denominations_;
    }
    
    /**
     * Parses an input file for
     * @param inputFile file that contains denomination information
     * @return the array of denominations
     * @throws InvalidDenominationException 
     */
    private static int[] parseInputFile(Scanner inputFile)
        throws InvalidDenominationException
    {
        int numOfDenominations = 0;
        // first integer is the number of denominations
        if (inputFile.hasNextInt())
        {
            numOfDenominations = inputFile.nextInt();
        }
        else
        {
            String err = "No denomination info found in file, check file.";
            throw new InvalidDenominationException(err);
        }
        
        // make an array large enough to hold all of the denominations
        int[] denominations = new int[numOfDenominations];  
        
        // get each denomination
        for (int i = 0; i < numOfDenominations; i++)
        {
            if (inputFile.hasNextInt())
            {
                denominations[i] = inputFile.nextInt();
            }
            else
            {
                String err = "No denomination info found in file, check file.";
                throw new InvalidDenominationException(err);
            }
        }
            
        //TODO: get more numbers
        
        return denominations;
    }
    
    public static void main(String[] args)
    {
        try
        {
            Scanner fileScanner = new Scanner(new File("make_change_input.txt"));
            
            int[] denominations = parseInputFile(fileScanner);

            ChangeMaker chg = new ChangeMaker(denominations);
        }
        catch (InvalidDenominationException | FileNotFoundException ex)
        {
            System.out.println(ex.getMessage());
        }
        
    }
}
