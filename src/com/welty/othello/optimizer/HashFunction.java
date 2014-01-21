package com.welty.othello.optimizer;

import java.util.*;

/**
 * <PRE>
 * User: chris
 * Date: 3/16/11
 * Time: 2:29 PM
 * </PRE>
 */
class HashFunction {
    private final HashMap<Integer, Double> f = new HashMap<Integer, Double>();

    void add(Integer key, double value) {
        final double originalValue = get(key);
        f.put(key, originalValue + value);
    }

    void add(HashFunction b) {
        HashSet<Integer> keys = combinedKeys(b);

        for (Integer key : keys) {
            add(key, b.get(key));
        }
    }

    HashSet<Integer> combinedKeys(HashFunction b) {
        HashSet<Integer> keys = new HashSet<Integer>(f.keySet());
        keys.addAll(b.f.keySet());
        return keys;
    }

    double get(Integer key) {
        final Double aDouble = f.get(key);
        return aDouble == null ? 0 : aDouble;
    }

    void put(Integer key, double value) {
        f.put(key, value);
    }

    HashFunction dividedBy(HashFunction b) {
        HashFunction result = new HashFunction();
        for (Integer key : f.keySet()) {
            result.put(key, get(key) / b.get(key));
        }
        return result;
    }

    void dump() {
        final ArrayList<Integer> keys = new ArrayList<Integer>(f.keySet());
        Collections.sort(keys);
        for (Integer key : keys) {
            System.out.format("%3d: %6.1f%n", key, f.get(key));
        }
    }

    public void dumpAsArray(String name, int length) {
        System.out.println("\t"+"double " + name + "["+length+"] = {");
        final ArrayList<Integer> keys = new ArrayList<Integer>(f.keySet());
        Collections.sort(keys);
        boolean hasComma = false;
        for (Integer key : keys) {
            if (hasComma) {
                System.out.print(",");
            }
            else {
                System.out.print("\t");
            }
            System.out.format("%6.1f", f.get(key));
            hasComma=true;
        }
        System.out.println();
        System.out.println("\t};");
    }
}
