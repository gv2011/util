package com.github.gv2011.jacksonadapter;

import static org.slf4j.LoggerFactory.getLogger;

/*-
 * #%L
 * jacksonadapter
 * %%
 * Copyright (C) 2018 Vinz (https://github.com/gv2011)
 * %%
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * #L%
 */
import java.io.IOException;

import org.slf4j.Logger;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.deser.Deserializers;
import com.fasterxml.jackson.databind.ser.Serializers;
import com.github.gv2011.util.beans.imp.AbstractType;
import com.github.gv2011.util.beans.imp.DefaultTypeRegistry;
import com.github.gv2011.util.json.JsonNode;
import com.github.gv2011.util.json.imp.Adapter;
import com.github.gv2011.util.json.imp.JsonFactoryImp;


public final class ImmutableBeansModule extends Module{

    private static final Logger LOG = getLogger(ImmutableBeansModule.class);

    private static final Version VERSION = Version.unknownVersion();

    private JsonFactoryImp jsonFactory;

    private DefaultTypeRegistry typeRegistry;

    @Override
    public String getModuleName() {
        return getClass().getSimpleName();
    }

    @Override
    public Version version() {
        return VERSION;
    }

    @Override
    public void setupModule(final SetupContext context) {
        final JsonFactory jacksonFactory = context.getOwner().getFactory();
        final Adapter adapter = new JacksonAdapter(jacksonFactory);
        jsonFactory = new JsonFactoryImp(adapter);
        typeRegistry = new DefaultTypeRegistry(jsonFactory);
        context.addSerializers(new SerializersImp());
        context.addDeserializers(new DeserializersImp());
    }

    class SerializersImp extends Serializers.Base {

        @Override
        public JsonSerializer<?> findSerializer(
            final SerializationConfig config, final JavaType type, final BeanDescription beanDesc
        ) {
            final Class<?> clazz = type.getRawClass();
            return typeRegistry.findTypeForInstanceClass(clazz).map(this::createSerializer).orElse(null);
        }

        private <T> JsonSerializer<T> createSerializer(final AbstractType<T> type) {
            return new JsonSerializer<T>() {
                @Override
                public void serialize(
                    final T value, final JsonGenerator gen, final SerializerProvider serializers
                ) throws IOException {
                    final JsonNode json = type.toJson(value);
                    json.write(new JacksonJsonWriter(gen));
                }
            };
        }
    }

    class DeserializersImp extends Deserializers.Base{

        @Override
        public JsonDeserializer<?> findBeanDeserializer(
            final JavaType type, final DeserializationConfig config, final BeanDescription beanDesc
        ) throws JsonMappingException {
            final Class<?> clazz = type.getRawClass();
            if(typeRegistry.isSupported(clazz)) {
                final JsonDeserializer<?> createDeserializer = createDeserializer(clazz);
                LOG.trace("Created deserializer for {}.", clazz);
                return createDeserializer;
            }
            else {
                LOG.trace("{} is not supported.", clazz);
                return null;
            }
        }

        private <T> JsonDeserializer<T> createDeserializer(final Class<T> clazz) {
            final AbstractType<T> type = typeRegistry.type(clazz);
            return new JsonDeserializer<T>() {
                @Override
                public T deserialize(
                    final JsonParser p, final DeserializationContext ctxt
                ) throws IOException, JsonProcessingException {
                    return type.parse(JacksonAdapter.deserialize(jsonFactory, p));
                }
            };
        }
    }

}
