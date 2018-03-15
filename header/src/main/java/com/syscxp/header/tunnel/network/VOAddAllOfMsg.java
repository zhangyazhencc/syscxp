package com.syscxp.header.tunnel.network;

import com.syscxp.header.message.APIMessage;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class VOAddAllOfMsg<T> {

    private void handle(APIMessage msg, T ob)
            throws NoSuchMethodException, InvocationTargetException, IllegalAccessException{

        Field[] declaredFields = msg.getClass().getDeclaredFields();
        Field[] obField = ob.getClass().getDeclaredFields();


        for(Field field : declaredFields){
            field.setAccessible(true);
            String name = field.getName();
            name = name.substring(0, 1).toUpperCase() + name.substring(1);
            Method MsgGet;
            if (field.getType().getName().equals("boolean")){
                MsgGet = msg.getClass().getMethod("is"+ name);
            }else{
                MsgGet = msg.getClass().getMethod("get"+ name);
            }

            if(MsgGet.invoke(msg) != null){
                Method obSet = ob.getClass().getMethod("set"+ name,field.getType());
                obSet.invoke(ob,MsgGet.invoke(msg));
            }
        }

    }

    public void addAll(APIMessage msg, T ob) {
        try {
            handle(msg, ob);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

    }

    /*public static void main(String[] args)  {
        APIUpdateL3NetworkMsg msg = new APIUpdateL3NetworkMsg();
        msg.setName("test");
        L3NetworkVO vo = new L3NetworkVO();
        vo.setType("typepe");
        UpdateMsgUtil util = new UpdateMsgUtil();
        util.updateMsg(msg, vo);
        System.out.println(vo.getName());
    }*/


}
