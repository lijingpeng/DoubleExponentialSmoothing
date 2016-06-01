package org.lijingpeng.algo.DoubleExpSmoothing;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.lijingpeng.algo.utils.Constants;

public class DoubleExpSmoothingSolver
{
    private static Log log = LogFactory.getLog(DoubleExpSmoothingSolver.class);

    private static DoubleExpSmoothingParams parameters = 
        new DoubleExpSmoothingParams();

    public static double[] solve(double[] y) {

        DoubleExpSmoothing fun = new DoubleExpSmoothing(y);
        int numParams  = parameters.numParameters();

        LBFGSB algo = new LBFGSB(
                Constants.DOUBLE_EXP_SMTH_MAX_ROUNDS, 
                Constants.DOUBLE_EXP_SMTH_CONV_THRES,
                numParams, fun);

        for (int i = 0; i< numParams; i++)
        {
            algo.setBoundSpec(i + 1, 2);
        }

        for (int i = 0; i < numParams; i++)
        {
            double[] bounds = parameters.bounds(i);

            int boundCode = -1;
            if (bounds[0] == Double.NEGATIVE_INFINITY
                && bounds[1] == Double.POSITIVE_INFINITY)
            {
                boundCode = 0;
            }
            else if (bounds[0] != Double.NEGATIVE_INFINITY
                     && bounds[1] == Double.POSITIVE_INFINITY)
            {
                boundCode = 1;
            }
            else if (bounds[0] != Double.NEGATIVE_INFINITY
                     && bounds[1] != Double.POSITIVE_INFINITY)
            {
                boundCode = 2;
            }
            else if (bounds[0] == Double.NEGATIVE_INFINITY
                     && bounds[1] != Double.POSITIVE_INFINITY)
            {
                boundCode = 3;
            }

            algo.setBoundSpec(i + 1, boundCode);

            if (boundCode == 1 || boundCode == 2)
            {
                algo.setLowerBound(i + 1, bounds[0]);
            }
            
            if (boundCode == 3 || boundCode == 2)
            {
                algo.setUpperBound(i + 1, bounds[1]);
            }
        }

        int[] iter      = {0};
        boolean[] error = {false};
        double[] params = new double[numParams + 1];
        // dummy weight
        params[0] = 0;
        // alpha
        params[1] = Constants.DOUBLE_EXP_SMTH_INIT_ALPHA;
        // beta
        params[2] = Constants.DOUBLE_EXP_SMTH_INIT_BETA;

        algo.minimize(params, iter, error);

        if (error[0])
        {
            //throw new RuntimeException("LBFGSB returned with an error.");
            log.error("LBFGSB returned with an error.");
        }

        return new double[] {params[1], params[2]};
    }
}
