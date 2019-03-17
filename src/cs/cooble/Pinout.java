package cs.cooble;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

/**
 * Created by Matej on 28.6.2018.
 */
public class Pinout {
    private Map<String,Integer> pins = new HashMap<>();
    private Map<String,Boolean> pinsInverted = new HashMap<>();

    public void setPin(String name,int index){
        pins.put(name,index);
    }
    public void setPin(String name,boolean inverted){
        pinsInverted.put(name,inverted);
    }

    public void translate(Map<Integer,Integer> map){
        Map<String,Integer> newMap = new HashMap<>();
        pins.forEach(new BiConsumer<String, Integer>() {
            @Override
            public void accept(String s, Integer integer) {
                newMap.put(s,map.get(integer));
            }
        });
        pins=newMap;
    }
    public List<String> getNames(){
        List<String> out = new ArrayList<>();
        pins.forEach(new BiConsumer<String, Integer>() {
            @Override
            public void accept(String s, Integer integer) {
                out.add(s);
            }
        });
        return out;
    }

    public int getPin(String name){
        return pins.get(name);
    }
    public boolean isInverted(String name){
        return pinsInverted.get(name);
    }
}
