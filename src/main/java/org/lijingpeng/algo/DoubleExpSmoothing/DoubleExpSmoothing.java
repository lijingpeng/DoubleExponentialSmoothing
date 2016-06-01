package org.lijingpeng.algo.DoubleExpSmoothing;
public class DoubleExpSmoothing implements ConvexFunc {

    private double[] y;
    private double mse;
    private double mse_alpha;
    private double mse_beta;

    public DoubleExpSmoothing(double[] y) {
        if (y == null || y.length < 3) {
            throw new IllegalArgumentException("Illegal length of CPMs!");
        }

        this.y = y;

    }

    public double getValueAndGradient(double[] g, final double[] wts) {
        double alpha = wts[1];
        double beta  = wts[2];

        this.calcMseAndPartDiff(alpha, beta);

        g[1] = this.mse_alpha;
        g[2] = this.mse_beta;
        g[0] = 0;

        return this.mse;
    }

    private void calcMseAndPartDiff(double alpha, double beta) {
        this.mse       = 0.0;
        this.mse_alpha = 0.0;
        this.mse_beta  = 0.0;

        double y_tdl_2, y_tdl_0 = 0.0, y_tdl_1 = 0.0;
        double c;

        double mse_alpha_2, mse_alpha_0 = 0.0, mse_alpha_1 = 0.0;
        double mse_beta_2, mse_beta_0 = 0.0, mse_beta_1 = 0.0;
        for (int i = 2; i < y.length; i++) {
            // calculate MSE
            c = y[i - 1] * 2 - y[i] - y[i - 2];

            y_tdl_2 = (2 - alpha - alpha * beta) * y_tdl_1
                + (alpha - 1) * y_tdl_0 + c;

            this.mse += Math.pow(y_tdl_2, 2.0);


            // calculate the partial diffs w.r.t alpha and beta
            mse_alpha_2 = (-1) * (1 + beta) * y_tdl_1 
                + (2 - alpha - alpha * beta) * mse_alpha_1
                + y_tdl_0 + (alpha - 1) * mse_alpha_0;

            this.mse_alpha += 2 * y_tdl_2 * mse_alpha_2;

            mse_beta_2 = (-1) * alpha * y_tdl_1 
                + (2 - alpha - alpha * beta) * mse_beta_1
                + (alpha - 1) * mse_beta_0;

            this.mse_beta += 2 * y_tdl_2 * mse_beta_2;


            //update params
            y_tdl_0 = y_tdl_1;
            y_tdl_1 = y_tdl_2;

            mse_alpha_0 = mse_alpha_1;
            mse_alpha_1 = mse_alpha_2;

            mse_beta_0 = mse_beta_1;
            mse_beta_1 = mse_beta_2;

        }
    }

    // class utils of forcasting
    public static double[] forecast(double[] y, double alpha, double beta)
        throws IllegalArgumentException {

        if (y == null || y.length < 3) {
            throw new IllegalArgumentException("Illegal length of CPMs!");
        }

        if (alpha < 0 || alpha > 1 || beta < 0 || beta > 1) {
            throw new IllegalArgumentException("Illegal Smoothing params!");
        }

        double initLevel = setInitLevel(y);
        double initTrend = setInitTrend(y);

        double[] forecast = calcDoubleExpSmoothing(y, initLevel, initTrend, 
                alpha, beta);

        return forecast;
    }

    private static double setInitLevel(double[] y) {
        return y[0];
    }

    private static double setInitTrend(double[] y) {
        int len = y.length;

        return y[1] - y[0];
    }

    private static double[] calcDoubleExpSmoothing(double[] y,
                            double initLevel, double initTrend, 
                            double alpha, double beta) {

        // res[0]: predicted mean
        // res[1]: 95% prediction interval
        double[] res = new double[2];

        double s_ = initLevel;
        double b_ = initTrend;

        double s  = 0.0;
        double b  = 0.0;

        double y_hat   = 0.0;
        double acc_err = 0.0;

        for (int i = 1; i < y.length; i++) {
            s = alpha * y[i] + (1 - alpha) * (s_ + b_);
            b = beta * (s - s_) + (1 - beta) * b_;

            s_ = s;
            b_ = b;

            y_hat = s_ + b_;

            acc_err += Math.abs(y_hat - y[i]);
        }

        res[0] = y_hat;
        res[1] = (acc_err / y.length) * 1.96 * 1.25;

        return res;
    }
}
