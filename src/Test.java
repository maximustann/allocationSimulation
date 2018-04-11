import java.util.HashMap;
import java.util.*;
public class Test {
    public static void main(String[] args){

        Map map = new HashMap();
        int[] key = {1, 2, 3, 4, 5};
        int[] value = {6, 7, 8, 9, 10};

        for(int i = 0; i < 5; i++){
            map.put(key[i], value[i]);
        }

        Iterator entries = map.entrySet().iterator();
        while(entries.hasNext()){
            Map.Entry entry = (Map.Entry) entries.next();
            Integer newKey = (Integer) entry.getKey();
            Integer newValue = (Integer) entry.getValue();


            System.out.println("key: " + newKey + ", value: " + newValue);
            if(newValue >= 6){
                newValue += 1;
                entry.setValue(newValue);
            }
        }

        System.out.println();
        Iterator entries2 = map.entrySet().iterator();
        while(entries2.hasNext()){
            Map.Entry entry = (Map.Entry) entries2.next();
            Integer newKey = (Integer) entry.getKey();
            Integer newValue = (Integer) entry.getValue();
            System.out.println("key: " + newKey + ", value: " + newValue);
        }

    }
}
