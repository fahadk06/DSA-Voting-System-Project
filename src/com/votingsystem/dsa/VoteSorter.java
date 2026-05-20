package com.votingsystem.dsa;

public class VoteSorter {

    // Bubble Sort — descending by vote_count
    // Called by UserDashboard doViewResults()
    public static void bubbleSort(Object[][] arr) {
        int n = arr.length;
        for (int i = 0; i < n - 1; i++) {
            for (int j = 0; j < n - i - 1; j++) {
                if ((int) arr[j][4] < (int) arr[j + 1][4]) {
                    Object[] temp = arr[j];
                    arr[j]        = arr[j + 1];
                    arr[j + 1]    = temp;
                }
            }
        }
    }
}