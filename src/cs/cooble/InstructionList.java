package cs.cooble;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Matej on 28.6.2018.
 */
public class InstructionList {
    private Map<String,Instruction> map = new HashMap<>();


    public Instruction addInstruction(String name){
        Instruction i = new Instruction(name);
        map.put(name,i);
        return i;
    }
    public Instruction getInstruction(String name){
        return map.get(name);
    }

    public Map<String, Instruction> getSrc() {
        return map;
    }

    public class Instruction{
        public final String NAME;
        private int index;
        private Map<Integer,Map<String,Boolean>> src;
        private Map<Integer,Map<String,Boolean>> flags;

        private Instruction(String name){
            NAME = name;
            src = new HashMap<>();
            flags=new HashMap<>();
        }

        public void set(int t,String name,Boolean b){
            Map<String,Boolean> map = getBoolMap(t);
            map.put(name,b);
        }
        public Boolean getBool(int t,String name){
            return getBoolMap(t).get(name);
        }

        public void setFlag(int t,String name,Boolean b){
            Map<String,Boolean> map = getFlagMap(t);
            map.put(name,b);
        }
        public Boolean getFlag(int t,String name){
            return getFlagMap(t).get(name);
        }

        public Map<String,Boolean> getBoolMap(int t){
            if(src.get(t)==null)
                src.put(t,new HashMap<>());
            return src.get(t);
        }
        private Map<String,Boolean> getFlagMap(int t){
            Map<String,Boolean> map = flags.get(t);
            if(map==null){
                map = new HashMap<>();
                flags.put(t,map);
            }
            return map;
        }

        public int getIndex() {
            return index;
        }
        public int getCycleNumber(){//how many tees
            return src.size();
        }

        public void setIndex(int index) {
            this.index = index;
        }
    }

}
