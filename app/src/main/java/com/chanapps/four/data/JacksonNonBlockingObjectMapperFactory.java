package com.chanapps.four.data;



import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.DateDeserializers;
import com.fasterxml.jackson.databind.deser.std.NumberDeserializers;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.deser.std.StringDeserializer;

import java.io.IOException;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: johnarleyburns
 * Date: 2/25/13
 * Time: 12:15 PM
 * To change this template use File | Settings | File Templates.
 */
public class JacksonNonBlockingObjectMapperFactory {

    public JacksonNonBlockingObjectMapperFactory() {
    }

    public ObjectMapper createObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper;
    }

    public static class NonBlockingIntegerDeserializer extends JsonDeserializer<Integer> {
        private JsonDeserializer<?> delegate;

        public NonBlockingIntegerDeserializer() {
            this.delegate = new NumberDeserializers.IntegerDeserializer(Integer.class, 0);
        }

        @Override
        public Integer deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
            Object o = null;
            try {
                if (delegate != null) o = delegate.deserialize(jp, ctxt);
            } catch (JsonMappingException e) {
                try {
                    boolean b = jp.getBooleanValue();
                    o = b ? 1 : 0;
                } catch (Exception e2) {
                    if (delegate != null) o = delegate.getNullValue();
                }
            } catch (Exception e) {
                if (delegate != null) o = delegate.getNullValue();
            }
            return (Integer) o;
        }
    }

    public static class NonBlockingLongDeserializer extends JsonDeserializer<Long> {
        private JsonDeserializer<?> delegate;

        public NonBlockingLongDeserializer() {
            this.delegate = new NumberDeserializers.LongDeserializer(Long.class, 0L);
        }

        @Override
        public Long deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
            Object o = null;
            try {
                if (delegate != null) o = delegate.deserialize(jp, ctxt);
            } catch (Exception e) {
                if (delegate != null) o = delegate.getNullValue();
            }
            return (Long) o;
        }
    }

    public static class NonBlockingBooleanDeserializer extends JsonDeserializer<Boolean> {
        private JsonDeserializer<?> delegate;

        public NonBlockingBooleanDeserializer() {
            this.delegate = new NumberDeserializers.BooleanDeserializer(Boolean.class, false);
        }

        @Override
        public Boolean deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
            Object o = null;
            try {
                if (delegate != null) o = delegate.deserialize(jp, ctxt);
            } catch (Exception e) {
                if (delegate != null) o = delegate.getNullValue();
            }
            return (Boolean) o;
        }
    }

    public static class NonBlockingStringDeserializer extends JsonDeserializer<String> {
        private JsonDeserializer<?> delegate;

        public NonBlockingStringDeserializer() {
            this.delegate = new StringDeserializer();
        }

        @Override
        public String deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
            Object o = null;
            try {
                if (delegate != null) o = delegate.deserialize(jp, ctxt);
            } catch (Exception e) {
                if (delegate != null) o = delegate.getNullValue();
            }
            return (String) o;
        }
    }

    public static class NonBlockingDateDeserializer extends JsonDeserializer<Date> {
        private JsonDeserializer<?> delegate;

        public NonBlockingDateDeserializer() {
            this.delegate = new DateDeserializers.CalendarDeserializer();
        }

        @Override
        public Date deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
            Object o = null;
            try {
                if (delegate != null) o = delegate.deserialize(jp, ctxt);
            } catch (Exception e) {
                if (delegate != null) o = delegate.getNullValue();
            }
            return null;
        }
    }

}
