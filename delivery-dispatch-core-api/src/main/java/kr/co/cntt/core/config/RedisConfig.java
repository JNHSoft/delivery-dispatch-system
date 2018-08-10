package kr.co.cntt.core.config;

import kr.co.cntt.core.model.redis.Content;
import kr.co.cntt.core.redis.RedisMessageListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

    private @Value("${spring.redis.host}")
    String redisHost;
    private @Value("${spring.redis.port}")
    int redisPort;

    @Bean
    public JedisConnectionFactory connectionFactory() {
        JedisConnectionFactory jedisConnectionFactory = new JedisConnectionFactory();

        jedisConnectionFactory.setHostName(redisHost);
        jedisConnectionFactory.setPort(redisPort);
        jedisConnectionFactory.setUsePool(true);

        return jedisConnectionFactory;
    }

    @Bean
    public RedisTemplate redisTemplate() {
        RedisTemplate<Object, Object> redisTemplate = new RedisTemplate<>();

        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new Jackson2JsonRedisSerializer(Content.class));
        redisTemplate.setConnectionFactory(connectionFactory());

        return redisTemplate;
    }

    @Bean
    MessageListenerAdapter messageListener() {
        return new MessageListenerAdapter(new RedisMessageListener());
    }

    @Bean
    RedisMessageListenerContainer redisContainer() {
        final RedisMessageListenerContainer container = new RedisMessageListenerContainer();

        container.setConnectionFactory(connectionFactory());
        container.addMessageListener(messageListener(), topic());

        return container;
    }

    @Bean
    ChannelTopic topic() {
        return new ChannelTopic("pubsub:web-notification");
    }

}
