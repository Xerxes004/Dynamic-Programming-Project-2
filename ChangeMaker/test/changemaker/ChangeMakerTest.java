/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package changemaker;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author jjvoneiff
 */
public class ChangeMakerTest {
    
   
    public ChangeMakerTest() {
        
    }
    @Test
    public void testResultsEqual() {
        int[] denoms = {1, 7, 17, 24, 53};
        try{
            ChangeMaker testing = new ChangeMaker(denoms);
            int[] dynamicSolve = null;
            int[] memoizedSolve = null;
            int[] recursiveSolve = null;
            for(int i = 0; i < 100; i ++) {
                dynamicSolve = testing.makeChangeDynamically(i);
                memoizedSolve = testing.makeChangeWithMemoization(i);
                recursiveSolve = testing.makeChangeRecursively(i);
                assertArrayEquals(dynamicSolve, memoizedSolve);
                assertArrayEquals(dynamicSolve, recursiveSolve);
                assertArrayEquals(recursiveSolve, memoizedSolve);
            }
            
        } catch(ChangeMakerException e) {
            System.out.println("Something went wrong");
        } catch (InvalidProblemException ex) {
            
        } catch (DenominationNotFoundException ex) {
            
        }
    }

    
    
}
