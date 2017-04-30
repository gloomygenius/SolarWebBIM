package edu.spbpu.util;

/**
 * Created by Vasiliy Bobkov on 10.03.2017.
 */
public class ExtendedMath {
    public static double getDecDet (double [][] a) {
        int n = a.length - 1;
        if (n < 0) return 0;
        double M [][][] = new double [n+1][][];

        M[n] = a;  // init first, largest, M to a

        // create working arrays
        for (int i = 0; i < n; i++)
            M[i] = new double [i+1][i+1];

        return getDecDet (M, n);
    } // end method getDecDet double [][] parameter

    public static double getDecDet (double [][][] M, int m) {
        if (m == 0) return M[0][0][0];
        int e = 1;

        // init subarray to upper left mxm submatrix
        for (int i = 0; i < m; i++)
            for (int j = 0; j < m; j++)
                M[m-1][i][j] = M[m][i][j];
        double sum = M[m][m][m] * getDecDet (M, m-1);

        // walk through rest of rows of M
        for (int i = m-1; i >= 0; i--) {
            for (int j = 0; j < m; j++)
                M[m-1][i][j] = M[m][i+1][j];
            e = -e;
            sum += e * M[m][i][m] * getDecDet (M, m-1);
        } // end for each row of matrix

        return sum;
    } // end getDecDet double [][][], int

    public static void main(String[] args) {
        double[][] matrix={
                {1,7,1,4,5},
                {6,7,8,9,10},
                {9,8,7,4,5},
                {4,3,2,3,2},
                {3,5,2,3,8}
        };
        System.out.println(getDecDet(matrix));
    }
}
