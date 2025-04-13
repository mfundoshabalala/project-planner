package ch.planner.plannersvc.config;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StringDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.openapitools.jackson.nullable.JsonNullableModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Copied from https://stackoverflow.com/a/77485120 and slightly modified to not return null values for empty strings
 */
@Configuration
public class JacksonConfiguration {

  /**
   * Configures module with a {@link StringDeserializer} that will trim all {@link String} fields in the request body
   *
   * @return a {@link SimpleModule} bean that will be registered to the global {@link ObjectMapper}
   */
  @Bean
  public SimpleModule stringDeserializerModule() {
    final var module = new SimpleModule();

    module.addDeserializer(
      String.class,
      new StringDeserializer() {
        @Override
        public String deserialize(JsonParser p, DeserializationContext ctx) throws java.io.IOException {
          final var value = super.deserialize(p, ctx);
          if (value == null) {
            return null;
          }

          return value.trim();
        }
      }
    );

    return module;
  }

  @Bean
  public JsonNullableModule jsonNullableModule() {
    return new JsonNullableModule();
  }

  @Bean
  public JavaTimeModule javaTimeModule() {
    return new JavaTimeModule();
  }
}
