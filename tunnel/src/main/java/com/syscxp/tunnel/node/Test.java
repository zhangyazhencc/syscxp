package com.syscxp.tunnel.node;


import com.alibaba.fastjson.JSONObject;

public class Test {

    public static void main(String[] args)
    {
        String jsonnew = "{\"id\":1,\"name\":\"eric\",\"named\":\"aaaaeric\",\"ida\":{\"name\":\"erica\",\"idddd\":100,\"qqqq\":{\"bb\":\"mmm\"}}, \"newobj\":{\"nn\":5555}}";
        String jsonold = "{\"name\":\"ericdddd\",\"id\":55,\"ida\":{\"name\":\"eric\",\"id\":1,\"ida\":{\"bb\":\"mmm\"}}, \"wo\":\"sdfsf\",\"newobj\":3}";

        JSONObject jsonThree = new JSONObject();
        jsonThree.putAll(JSONObject.parseObject(jsonold));
        jsonThree.putAll(JSONObject.parseObject(jsonnew));

        System.out.println(jsonThree.toString());

    }
}
