package com.syscxp.utils.message;


import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;

public class VOAddAllOfMsg {

    public static <T> void addAll(T msg, T ob){

        Field[] declaredFields = msg.getClass().getDeclaredFields();

        Arrays.asList(declaredFields).stream().forEach((field)->{
            field.setAccessible(true);
            String name = field.getName();
            name = name.substring(0, 1).toUpperCase() + name.substring(1);
            Method MsgGet = null ;
            try{
                if (field.getType().getName().equals("boolean")){
                    MsgGet = msg.getClass().getMethod("is"+ name);
                }else{
                    MsgGet = msg.getClass().getMethod("get"+ name);
                }
            }catch (NoSuchMethodException e) {
                e.printStackTrace();
            }

            try{
                if(MsgGet != null && MsgGet.invoke(msg) != null){
                    Method obSet = null;
                    try{
                        obSet = ob.getClass().getMethod("set"+ name,field.getType());
                    }catch (Exception e){
                    }

                    if(obSet != null){
                        obSet.invoke(ob,MsgGet.invoke(msg));
                    }
                }
            }catch (IllegalAccessException e){
                e.printStackTrace();
            }catch (InvocationTargetException e){
                e.printStackTrace();
            }

        });

    }


}
