package com.syscxp.core.rest;

import com.google.gson.*;
import com.syscxp.header.message.Message;
import com.syscxp.header.rest.APINoSee;
import com.syscxp.header.rest.APINoSession;
import com.syscxp.header.rest.APIWithSession;
import com.syscxp.utils.Utils;
import com.syscxp.utils.gson.GsonTypeCoder;
import com.syscxp.utils.gson.GsonUtil;
import com.syscxp.utils.logging.CLogger;

import java.lang.reflect.Type;
import java.util.Map;

public class RESTApiDecoder {
    private static CLogger logger = Utils.getLogger(RESTApiDecoder.class);
    
    private final Gson gsonEncoder;
    private final Gson gsonDecoder;
    private final Gson gsonEncoderWithSession;
    private static final RESTApiDecoder self;
    
    private class Encoder implements GsonTypeCoder<Message>, ExclusionStrategy {
        private Gson gson;
        
        void setGson(Gson gson) {
            this.gson = gson;
        }
        
        @Override
        public Message deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jObj = json.getAsJsonObject();
            Map.Entry<String, JsonElement> entry = jObj.entrySet().iterator().next();
            String className = entry.getKey();
            Class<?> clazz;
            try {
                clazz = Class.forName(className);
            } catch (ClassNotFoundException e) {
                throw new JsonParseException("Unable to deserialize class " + className, e);
            }
            return (Message) this.gson.fromJson(entry.getValue(), clazz);
        }

        @Override
        public JsonElement serialize(Message msg, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject jObj = new JsonObject();
            jObj.add(msg.getClass().getName(), this.gson.toJsonTree(msg));
            return jObj;
        }

        @Override
        public boolean shouldSkipField(FieldAttributes f) {
            return f.getAnnotation(APINoSee.class) != null;
        }

        @Override
        public boolean shouldSkipClass(Class<?> clazz) {
            return false;
        }
    }

    private class EncoderWithSession extends Encoder {
        @Override
        public boolean shouldSkipField(FieldAttributes f) {
            return f.getAnnotation(APIWithSession.class) == null
                    && (f.getAnnotation(APINoSee.class) != null
                    || f.getAnnotation(APINoSession.class) != null);
        }
    }

    private class Decoder implements GsonTypeCoder<Message> {
        private Gson gson;
        
        void setGson(Gson gson) {
            this.gson = gson;
        }
        
        @Override
        public Message deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jObj = json.getAsJsonObject();
            Map.Entry<String, JsonElement> entry = jObj.entrySet().iterator().next();
            String className = entry.getKey();
            Class<?> clazz;
            try {
                clazz = Class.forName(className);
            } catch (ClassNotFoundException e) {
                throw new JsonParseException("Unable to deserialize class " + className, e);
            }
            Message msg = (Message) this.gson.fromJson(entry.getValue(), clazz);
            return msg;
        }

        @Override
        public JsonElement serialize(Message msg, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject jObj = new JsonObject();
            jObj.add(msg.getClass().getName(), this.gson.toJsonTree(msg));
            return jObj;
        }
        
    }
    
    static {
        self = new RESTApiDecoder();
    }
    
    private RESTApiDecoder() {
        Encoder encoder = new Encoder();
        gsonEncoder = new GsonUtil().setCoder(Message.class, encoder).setExclusionStrategies(new ExclusionStrategy[]{encoder}).create();
        encoder.setGson(gsonEncoder);

        EncoderWithSession encoder1 = new EncoderWithSession();
        gsonEncoderWithSession = new GsonUtil().setCoder(Message.class, encoder1).setExclusionStrategies(new ExclusionStrategy[]{encoder1}).create();
        encoder1.setGson(gsonEncoderWithSession);

        Decoder decoder = new Decoder();
        gsonDecoder = new GsonUtil().setCoder(Message.class, decoder).create();
        decoder.setGson(gsonDecoder);
    }
    
    public static Message loads(String jsonStr) {
        return self.gsonDecoder.fromJson(jsonStr, Message.class);
    }
    
    public static String dump(Message msg) {
        return self.gsonEncoder.toJson(msg, Message.class);
    }

    public static String dumpWithSession(Message msg) {
        return self.gsonEncoderWithSession.toJson(msg, Message.class);
    }

}
