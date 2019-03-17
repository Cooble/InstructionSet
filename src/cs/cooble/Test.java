package cs.cooble;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.List;

/**
 * Created by Matej on 29.6.2018.
 */
public class Test {
    public static void main(String[] agrs){
        List<Integer> list = generateAllAddress("X01X1");
       System.out.println("list");
        for (int i = 0; i < list.size(); i++) {
            System.out.println(BinaryEngine.toBinString(list.get(i), 5));
        }
    }
    private static List<Integer> generateAllAddress(String temple) {
        int intTemple = 0;
        List<Integer> xIndexes = new ArrayList<>();
        List<Integer> out = new ArrayList<>();
        for (int i = 0; i < temple.length(); i++) {
            char c = temple.charAt(i);
            if (c == '1')
                intTemple |= (1 << (temple.length() - 1 - i));
            if (c == 'X' || c == 'x')
                xIndexes.add(i);
        }
        int max = Integer.MAX_VALUE;
        for (int i = 0; i < (30 - xIndexes.size()); i++) {
            max /= 2;
        }

        for (int counter = 0; counter < max; counter++) {
            int i = intTemple;
            for (int j = 0; j < xIndexes.size(); j++) {
                i = setBit(i, temple.length()-1-xIndexes.get(j), getBit(counter, j));
            }
            if (!out.contains(i))
                out.add(i);
        }
        return out;


    }
    private static boolean getBit(int number, int index) {
        return (number & (1 << index)) != 0;
    }

    private static int setBit(int number, int index, boolean bit) {
        if (bit) {
            return number | (1 << index);
        }
        return number & (~(1 << index));
    }


}
