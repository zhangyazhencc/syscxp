import com.google.gson.*;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class Test {

    String jsonnew = "{\"id\":1,\"name\":\"eric\",\"named\":\"aaaaeric\",\"ida\":{\"name\":\"erica\",\"idddd\":100,\"qqqq\":{\"bb\":\"mmm\"}}, \"newobj\":{\"nn\":5555}}";
    String jsonold = "{\"name\":\"ericdddd\",\"id\":55,\"ida\":{\"name\":\"eric\",\"id\":1,\"ida\":{\"bb\":\"mmm\"}}, \"wo\":\"sdfsf\"}";

    @org.junit.Test
    public void test5() throws Exception {
        JsonParser parser = new JsonParser();
        JsonObject objnew = (JsonObject) parser.parse(jsonnew);
        JsonObject objold = (JsonObject) parser.parse(jsonold);

        JsonObject obj = mergeJson(objnew, objold);


        System.out.println(obj.toString());
    }

    private JsonObject mergeJson(JsonObject objNew, JsonObject objOld){
        Iterator<Map.Entry<String, JsonElement>> newIter =  objNew.entrySet().iterator();
        while (newIter.hasNext()) {
            Map.Entry<String, JsonElement> entry = newIter.next();
            String key = entry.getKey();
            JsonElement value = entry.getValue();

            if (value.isJsonObject()){
                if (objOld.has(key)){
                    mergeJson(value.getAsJsonObject(), objOld.get(key).getAsJsonObject());
                }else{
                    objOld.add(key, value);
                }
            }else{
                objOld.add(key, value);
            }

        }

        return objOld;
    }
}
