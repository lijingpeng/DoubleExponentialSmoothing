import junit.framework.TestCase;
import org.lijingpeng.algo.DoubleExpSmoothing.DoubleExpSmoothing;
import org.lijingpeng.algo.DoubleExpSmoothing.DoubleExpSmoothingParams;
import org.lijingpeng.algo.DoubleExpSmoothing.LBFGSB;

import java.util.Arrays;

public class SampleRunTest extends TestCase {
    protected static void setUpBeforeClass() throws Exception {
    }

    protected static void tearDownAfterClass() throws Exception {
    }

    protected void setUp() throws Exception {
        super.setUp();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

	public void testSampleRun() {
        double[] y = new double[] {1.19, 1.2, 0.99, 1.2, 1.2, 1.206, 1.26,
                                 0.96, 0.78, 1.18};
        DoubleExpSmoothing func = new DoubleExpSmoothing(y);

        DoubleExpSmoothingParams parameters = new DoubleExpSmoothingParams();

        int numParams = parameters.numParameters();

        LBFGSB lbfgsb = new LBFGSB(100, 1.0E-4, numParams, func);

        for (int i = 0; i < parameters.numParameters(); i++) 
        /* Each parameter has upper and lower bounds (indicated by 2) */ 
            lbfgsb.setBoundSpec(i+1,2);  
 
        for (int i = 0; i < parameters.numParameters(); i++) 
        { 
            double[] bounds = parameters.bounds(i); 
 
            int boundCode = -1; 
            if (bounds[0] == Double.NEGATIVE_INFINITY && bounds[1] == Double.POSITIVE_INFINITY)        boundCode = 0; 
            else if (bounds[0] != Double.NEGATIVE_INFINITY && bounds[1] == Double.POSITIVE_INFINITY)   boundCode = 1; //lower bound 
            else if (bounds[0] != Double.NEGATIVE_INFINITY && bounds[1] != Double.POSITIVE_INFINITY)   boundCode = 2; //lower & upper bounds 
            else if (bounds[0] == Double.NEGATIVE_INFINITY && bounds[1] != Double.POSITIVE_INFINITY)   boundCode = 3; //upper bound 
 
            lbfgsb.setBoundSpec(i+1,boundCode); 
 
            if (boundCode == 1 || boundCode == 2) lbfgsb.setLowerBound(i+1, bounds[0]); 
            if (boundCode == 3 || boundCode == 2) lbfgsb.setUpperBound(i+1, bounds[1]); 
        } 
 
        int[]     iter   = {0};       //store number of iterations taken by L-BFGS-B 
        boolean[] error  = {false};   //indicate whether L-BFGS-B encountered an error 
        double[]  params = new double[numParams + 1]; //parameters (e.g., wts of formulas) found by L-BFGS-B 
        //double[]  paras  = parameters.getAllParameterValues(); 
        //double[]  paras  = new double[2]; 
   
        for (int i = 1; i < params.length; i++) 
            params[i] = 0.0; 
 
        lbfgsb.minimize(params, iter, error); 
 
        if (error[0]) 
        { 
            throw new RuntimeException("LBFGSB returned with an error!"); 
        } 
 
        //log.debug("LBFGSB number of iterations = " + iter[0]); 
        System.out.println("LBFGSB number of iterations = " + iter[0]); 
 
        /*
        for (int i = 1; i < params.length; i++) //NOTE: the params are indexed starting from 1! 
        { 
            paras[i-1] = params[i];
        }
        */
        System.out.println(Arrays.toString(params));
        //parameters.setAllParameters(paras); 
        //log.debug("Learned parameters: {}", Arrays.toString(paras)); 
	}
}


