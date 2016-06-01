package org.lijingpeng.algo.DoubleExpSmoothing;

import org.lijingpeng.algo.utils.Constants;
import java.util.ArrayList;

public class DoubleExpSmoothingParams implements Parameters
{
    private double[] params;
    private ArrayList<double[]> bounds;

    public DoubleExpSmoothingParams()
    {
        this.params    = new double[2];
        this.params[0] = Constants.DOUBLE_EXP_SMTH_INIT_ALPHA;
        this.params[1] = Constants.DOUBLE_EXP_SMTH_INIT_BETA;

        this.bounds    = new ArrayList<double[]>();
        this.bounds.add(new double[] {0.0, 1.0});
        this.bounds.add(new double[] {0.0, 1.0});
    }

    public int numParameters()
    {
        return this.params.length;
    }

    public double[] getParameters()
    {
        return this.params;
    }

    public double getParameter(int parameterNo)
    {
        return this.params[parameterNo];
    }

    public double[] bounds(int parameterNo)
    {
        return this.bounds.get(parameterNo);
    }

    public void setParameter(int parameterNo, double value) {
        this.params[parameterNo] = value;
    }
}
