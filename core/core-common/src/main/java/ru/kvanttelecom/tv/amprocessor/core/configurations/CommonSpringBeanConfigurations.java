package ru.kvanttelecom.tv.amprocessor.core.configurations;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.web.client.RestTemplate;
import ru.dreamworkerln.spring.utils.common.rest.RestClient;
import ru.dreamworkerln.spring.utils.common.rest.RestClientBuilder;

import javax.validation.Validation;
import javax.validation.ValidatorFactory;
import javax.validation.Validator;
import java.time.Duration;


@Configuration
@Slf4j
public class CommonSpringBeanConfigurations {

    private final int HTTP_TIMEOUT = 4000;

    //public static final String OBJECT_MAPPER_SMILE = "mapperSmile";
    public static final String OBJECT_MAPPER_WITH_NULL = "mapperWithNull";

    public static final String ALERT_LIST_MARKUP_FACTORY_MAIL = "alertListMarkupFactoryMail";
    public static final String ALERT_LIST_MARKUP_FACTORY_TELEGRAM = "alertListMarkupFactoryTelegram";


    @Primary
    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return
            builder.setConnectTimeout(Duration.ofMillis(HTTP_TIMEOUT))
                .setReadTimeout(Duration.ofMillis(HTTP_TIMEOUT))
                .build();
    }

    @Primary
    @Bean
    public RestClient restClient(RestTemplate restTemplate) {
        RestClientBuilder builder = new RestClientBuilder();
        return builder.restTemplate(restTemplate).build();
    }


    @Primary
    @Bean
    public ObjectMapper objectMapper() {
        // allow convertation to/from Instant
        ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());
        // will write as string ISO 8601
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, true);
        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        mapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.PROTECTED_AND_PUBLIC);
        //mapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
        mapper.registerModule(new ParameterNamesModule());

        //mapper.registerSubtypes(Stream.class, DefaultSubject.class, Subject.class);

//        PolymorphicTypeValidator ptv = BasicPolymorphicTypeValidator.builder().build();
//        mapper.activateDefaultTyping(ptv); // default to using DefaultTyping.OBJECT_AND_NON_CONCRETE
//        mapper.activateDefaultTyping(ptv, ObjectMapper.DefaultTyping.NON_FINAL);

        //mapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
        //mapper.setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);
        //mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        //mapper.configure(MapperFeature.DEFAULT_VIEW_INCLUSION, true);
        return mapper;
    }


    @Bean(OBJECT_MAPPER_WITH_NULL)
    public ObjectMapper objectMapperIncludeNull() {

        // ObjectMapper is threadsafe

        // allow convertation to/from Instant
        ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());
        // will write as string ISO 8601
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, true);
        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        mapper.registerModule(new ParameterNamesModule());
        //mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        //mapper.setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);
        //mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        //mapper.configure(MapperFeature.DEFAULT_VIEW_INCLUSION, true);
        return mapper;
    }

//    /**
//     *
//     * @return
//     */
//    @Bean(OBJECT_MAPPER_SMILE)
//    public ObjectMapper objectMapperSmile() {
//
//        // ObjectMapper is threadsafe
//
//        // allow convertation to/from Instant
//        ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());
//        // will write as string ISO 8601
//        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
//        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, true);
//        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
//        mapper.registerModule(new ParameterNamesModule());
//        //mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
//        //mapper.setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);
//        //mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
//        //mapper.configure(MapperFeature.DEFAULT_VIEW_INCLUSION, true);
//        return mapper;
//    }


    @Bean
    public Validator validator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        return factory.getValidator();
    }
}
