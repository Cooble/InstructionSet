package cs.cooble;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.function.BiConsumer;

/**
 * Created by Matej on 29.6.2018.
 */
public class Saver {
    public void saveToWholeFile(Map<Integer,Integer> map,File f) {
        try (PrintWriter writer = new PrintWriter(f)) {
            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            Date date = new Date();
            writer.println("#Content of whole EEPROM " + dateFormat.format(date));
            writer.println();
            writer.println();
            map.forEach(new BiConsumer<Integer, Integer>() {
                @Override
                public void accept(Integer integer, Integer integer2) {
                    writer.println(BinaryEngine.toBinString(integer, 13) + " : " + BinaryEngine.toBinString(integer2, 24));
                }
            });

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
    public void saveOneRom(int romIndex,Map<Integer,Integer> map,File folder) {
        final int romIndexx=romIndex*8;
        System.out.println("saving to "+new File(folder.getAbsolutePath()+"/rom_"+romIndex+".txt"));
        try (PrintWriter writer = new PrintWriter(new File(folder.getAbsolutePath()+"/rom_"+romIndex+".txt"))) {
            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            Date date = new Date();
            writer.println("#Content of part of EEPROM["+romIndex+"]   "+ dateFormat.format(date));
            writer.println();
            writer.println();
            map.forEach(new BiConsumer<Integer, Integer>() {
                @Override
                public void accept(Integer integer, Integer integer2) {
                    writer.println(BinaryEngine.toBinString(integer, 13) + " : " + flip(flip(BinaryEngine.toBinString(integer2, 24)).substring(romIndexx, romIndexx + 8)));
                }
            });

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private String flip(String s) {
        char[] out = new char[s.length()];
        for (int i = 0; i < s.length(); i++) {
            out[i]=s.charAt(s.length()-1-i);
        }
        return new String(out);
    }
}
