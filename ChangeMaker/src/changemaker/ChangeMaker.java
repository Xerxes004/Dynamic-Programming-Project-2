/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package changemaker;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

/**
 *
 * @author wes
 */
public class ChangeMaker
{
    private int[] denominations;
    
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
    
    private static int[] getDenominationsFromFile(Scanner inputFile)
        throws InvalidDenominationException
    {
        int numOfDenominations = 0;
        if (inputFile.hasNextInt())
        {
            numOfDenominations = inputFile.nextInt();
        }
        else
        {
            String err = "No denomination info found in file, check file.";
            throw new InvalidDenominationException(err);
        }
        
        int[] denominations = new int[numOfDenominations];  
        
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
        
        return null;
    }
    
    public static void main(String[] args)
    {
        try
        {
            Scanner fileScanner = new Scanner(new File("make_change_input.txt"));
            
            int[] denominations = getDenominationsFromFile(fileScanner);

            ChangeMaker chg = new ChangeMaker(denominations);
        }
        catch (InvalidDenominationException | FileNotFoundException ex)
        {
            System.out.println(ex.getMessage());
        }
        
    }
}
