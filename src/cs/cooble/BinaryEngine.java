package cs.cooble;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

/**
 * Created by Matej on 28.6.2018.
 */
public class BinaryEngine {
    private InstructionList instructionList;
    private Pinout pinout;
    private Map<Integer, Integer> translateAddress;

    public BinaryEngine(InstructionList instructionList, Pinout pinout) {
        this.instructionList = instructionList;
        this.pinout = pinout;
        loadTranslation();


    }

    private void loadTranslation() {
        translateAddress = new HashMap<>();

        translateAddress.put(0, 7);//lsb
        translateAddress.put(1, 6);
        translateAddress.put(2, 5);
        translateAddress.put(3, 4);
        translateAddress.put(4, 3);
        translateAddress.put(5, 2);//T2
        translateAddress.put(6, 1);//T1
        translateAddress.put(7, 0);//T0 msb

        translateAddress.put(8, 11);//T5
        translateAddress.put(9, 12);//T4
        translateAddress.put(10, 8);//T3

        translateAddress.put(11, 9);//jc
        translateAddress.put(12, 10);//jz
    }

    private String translateAddress(String s) {
        char[] out = new char[s.length()];
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            out[translateAddress.get(i)] = c;
        }
        return new String(out);
    }

    public Map<Integer, Integer> process() {
        Map<Integer, Integer> out = new HashMap<>();
        int nop = getBoolVal(instructionList.getInstruction("NOP"),0);
        for (int address = 0; address < 8192; address++)
            out.put(address,nop);//set all other values to nop

        instructionList.getSrc().forEach(new BiConsumer<String, InstructionList.Instruction>() {
            @Override
            public void accept(String s, InstructionList.Instruction instruction) {
                if (s.equals("T0-T2")) {
                    String t0 = "10000000000XX";
                    String t1 = "01000000000XX";
                    String t2 = "00100000000XX";

                    t0 = translateAddress(t0);
                    List<Integer> t0Addresses = generateAllAddress(flip(t0));
                    int val = getBoolVal(instruction, 0);
                    for (Integer adres : t0Addresses)
                        out.put(adres, val);

                    t1 = translateAddress(t1);
                    List<Integer> t1Addresses = generateAllAddress(flip(t1));
                    val = getBoolVal(instruction, 1);
                    for (Integer adres : t1Addresses)
                        out.put(adres, val);

                    t2 = translateAddress(t2);
                    List<Integer> t2Addresses = generateAllAddress(flip(t2));
                    val = getBoolVal(instruction, 2);
                    for (Integer adres : t2Addresses)
                        out.put(adres, val);

                    return;
                }
                int instructionIndex = instruction.getIndex();
                for (int t = 0; t < instruction.getCycleNumber(); t++) {
                    String addressTemplate = toBinString(flipBitOrder(instructionIndex, 8), 8);
                    addressTemplate += t == 2 ? "1" : "0";
                    addressTemplate += t == 1 ? "1" : "0";
                    addressTemplate += t == 0 ? "1" : "0";
                    String string;
                    Boolean jc = instruction.getFlag(t, "JC");
                    if (jc == null)
                        string = "X";
                    else if (jc)
                        string = "1";
                    else string = "0";

                    addressTemplate += string;

                    Boolean jz = instruction.getFlag(t, "JZ");
                    if (jz == null)
                        string = "X";
                    else if (jz)
                        string = "1";
                    else string = "0";

                    addressTemplate += string;
                    int boolVal = getBoolVal(instruction, t);
                    addressTemplate = translateAddress(addressTemplate);
                    List<Integer> addresses = generateAllAddress(flip(addressTemplate));
                    for (int adres : addresses) {
                        out.put(adres, boolVal);
                    }
                }


            }
        });
        return out;
    }

    /**
     * @param i
     * @param bits number of active bits
     * @return
     */
    private int flipBitOrder(int i, int bits) {
        int out = 0;
        for (int j = 0; j < bits; j++) {
            out = setBit(out, bits - 1 - j, getBit(i, j));
        }
        return out;
    }

    /**
     * @return
     */

    private String flip(String s) {
        char[] out = new char[s.length()];
        for (int i = 0; i < s.length(); i++) {
            out[i] = s.charAt(s.length() - 1 - i);
        }
        return new String(out);
    }

    private int getBoolVal(InstructionList.Instruction instruction, int t) {
        final int[] out = {0};
        for (String pinName : pinout.getNames()) {
            Boolean bit = instruction.getBool(t, pinName);
            if (bit == null)
                bit = false;
            if (pinout.isInverted(pinName))
                bit = !bit;
            out[0] = setBit(out[0], pinout.getPin(pinName), bit);
        }

        return out[0];
    }

    private List<Integer> generateAllAddress(String temple) {
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
                i = setBit(i, temple.length() - 1 - xIndexes.get(j), getBit(counter, j));
            }
            if (!out.contains(i))
                out.add(i);
        }
        return out;


    }

    private boolean getBit(int number, int index) {
        return (number & (1 << index)) != 0;
    }

    private int setBit(int number, int index, boolean bit) {
        if (bit) {
            return number | (1 << index);
        }
        return number & (~(1 << index));
    }

    public static String toBinString(int integer, int length) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < length; i++) {
            builder.append(((integer & (1 << (length - 1 - i))) != 0) ? "1" : "0");
        }
        return builder.toString();
    }
}
