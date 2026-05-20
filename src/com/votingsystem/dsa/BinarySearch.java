package com.votingsystem.dsa;

import java.util.ArrayList;
import java.util.List;

public class BinarySearch {

    // Search candidates by name (binary) + area (linear fallback)
    // candidates[][] format: {id, name, party, area, votes}
    public static Object[][] search(Object[][] candidates, String query) {

        List<Object[]> results = new ArrayList<>();

        // Step 1 — sort copy by name for binary search
        Object[][] sorted = candidates.clone();
        sortByName(sorted);

        // Step 2 — binary search by name
        int lo = 0, hi = sorted.length - 1;

        while (lo <= hi) {
            int mid = (lo + hi) / 2;
            String midName = ((String) sorted[mid][1]).toLowerCase();

            if (midName.contains(query)) {
                // collect this match
                results.add(sorted[mid]);

                // collect neighbours that also match
                int left = mid - 1;
                while (left >= 0
                        && ((String) sorted[left][1]).toLowerCase().contains(query)) {
                    results.add(sorted[left--]);
                }
                int right = mid + 1;
                while (right < sorted.length
                        && ((String) sorted[right][1]).toLowerCase().contains(query)) {
                    results.add(sorted[right++]);
                }
                break;

            } else if (midName.compareTo(query) < 0) {
                lo = mid + 1;
            } else {
                hi = mid - 1;
            }
        }

        // Step 3 — linear search by area (catches area queries binary miss)
        for (Object[] c : candidates) {
            String area = ((String) c[3]).toLowerCase();
            if (area.contains(query) && !results.contains(c)) {
                results.add(c);
            }
        }

        return results.toArray(new Object[0][]);
    }

    // Insertion sort by name A→Z (stable, needed before binary search)
    private static void sortByName(Object[][] arr) {
        for (int i = 1; i < arr.length; i++) {
            Object[] key     = arr[i];
            String   keyName = ((String) key[1]).toLowerCase();
            int j = i - 1;
            while (j >= 0
                    && ((String) arr[j][1]).toLowerCase().compareTo(keyName) > 0) {
                arr[j + 1] = arr[j];
                j--;
            }
            arr[j + 1] = key;
        }
    }
}