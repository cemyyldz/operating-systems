public class MatrixMultiplication {
    private static final int SIZE = 5;
    private static final int[][] A = {
        {1, 3, 5, 7, 9},
        {11, 13, 15, 17, 19},
        {21, 23, 25, 27, 29},
        {31, 33, 35, 37, 39},
        {41, 43, 45, 47, 49}
    };
    private static final int[][] B = {
        {2, 4, 6, 8, 10},
        {12, 14, 16, 18, 20},
        {22, 24, 26, 28, 30},
        {32, 34, 36, 38, 40},
        {42, 44, 46, 48, 50}
    };
    private static final int[][] C = new int[SIZE][SIZE];

    public static void main(String[] args) throws InterruptedException {
        Thread[] threads = new Thread[SIZE];

        for (int i = 0; i < SIZE; i++) {
            final int row = i;
            threads[i] = new Thread(() -> hesaplama(row));
            threads[i].start();
        }

        for (Thread thread : threads) {
            thread.join();
        }

        matrix_yazdirma(C);
    }


    private static void hesaplama(int row) {
        for (int j = 0; j < SIZE; j++) {
            C[row][j] = 0;
            for (int k = 0; k < SIZE; k++) {
                C[row][j] += A[row][k] * B[k][j];
            }
        }
    }


    private static void matrix_yazdirma(int[][] matrix) {
        for (int[] row : matrix) {
            for (int value : row) {
                System.out.print(value + " ");
            }
            System.out.println();
        }
    }
}
