package org.scriptonbasestar.oauth.client.util;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.deser.BeanDeserializerModifier;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.google.gson.Gson;
import lombok.experimental.UtilityClass;

import java.io.IOException;

@UtilityClass
public class SBSingleInstances {

  private static class ObjectMapperHolder {

    public static final ObjectMapper INSTANCE = new ObjectMapper();

    static {
      INSTANCE.setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);
//      INSTANCE.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true);
//      INSTANCE.setPropertyNamingStrategy(PropertyNamingStrategy.CAMEL_CASE_TO_LOWER_CASE_WITH_UNDERSCORES);
      SimpleModule module = new SimpleModule();
      module.setDeserializerModifier(new BeanDeserializerModifier() {
        @Override
        public JsonDeserializer<Enum> modifyEnumDeserializer(
            DeserializationConfig config,
            final JavaType type,
            BeanDescription beanDesc,
            final JsonDeserializer<?> deserializer) {
          return new JsonDeserializer<Enum>() {
            @Override
            public Enum deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
              Class<? extends Enum> rawClass = (Class<Enum<?>>) type.getRawClass();
              return Enum.valueOf(rawClass, jp.getValueAsString().toUpperCase());
            }
          };
        }
      });
      module.addSerializer(Enum.class, new StdSerializer<Enum>(Enum.class) {
        @Override
        public void serialize(Enum value, JsonGenerator jgen, SerializerProvider provider) throws IOException {
//          jgen.writeString(value.name().toUpperCase());
          jgen.writeString(value.name().toLowerCase());
        }
      });
      INSTANCE.registerModule(module);
    }
  }

  public static ObjectMapper getObjectMapper() {
    return ObjectMapperHolder.INSTANCE;
  }

  private static class GsonHolder {

    public static final Gson INSTANCE = new Gson();
  }

  public static Gson getGson() {
    return GsonHolder.INSTANCE;
  }

//  private static ObjectMapper mapper = null;
//  public static ObjectMapper getObjectMapper(){
//    if(mapper == null){
//      synchronized (SBSingleInstances.class){
//        if(mapper == null){
//          mapper = new ObjectMapper();
//        }
//      }
//    }
//    return mapper;
//  }
//  private static Gson gson = null;
//  public static Gson getGson(){
//    synchronized (gson){
//      if(gson == null){
//        gson = new Gson();
//      }
//    }
//    return gson;
//  }

}
