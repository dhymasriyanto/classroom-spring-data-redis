package ngodingkuy.tech.classroomservice.config;

import java.time.Duration;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.Resource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.stream.Consumer;
import org.springframework.data.redis.connection.stream.ObjectRecord;
import org.springframework.data.redis.connection.stream.ReadOffset;
import org.springframework.data.redis.connection.stream.StreamOffset;
import org.springframework.data.redis.hash.ObjectHashMapper;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.stream.StreamMessageListenerContainer;

import ngodingkuy.tech.classroomservice.consumer.AsyncConsumeStreamListener;
import ngodingkuy.tech.classroomservice.dto.Constants;
import ngodingkuy.tech.classroomservice.dto.CustomErrorHandler;
import ngodingkuy.tech.classroomservice.model.Classroom;

@Configuration
public class RedisStreamConfiguration {

	@Resource
	private RedisConnectionFactory redisConnectionFactory;

	@Bean(initMethod="start", destroyMethod="stop")
	public StreamMessageListenerContainer<String, ObjectRecord<String, Classroom>> streamMessageListenerContainer(){
		AtomicInteger index = new AtomicInteger(1);

		int processors = Runtime.getRuntime().availableProcessors();

		ThreadPoolExecutor executor = new ThreadPoolExecutor(processors, processors, 0, TimeUnit.SECONDS, 
			new LinkedBlockingDeque<>(), r -> {
				Thread thread = new Thread(r);
				thread.setName("async-stream-consumer-" + index.getAndIncrement());
				thread.setDaemon(true);
				return thread;
		});
		StreamMessageListenerContainer.StreamMessageListenerContainerOptions<String, ObjectRecord<String, Classroom>> options =
			StreamMessageListenerContainer.StreamMessageListenerContainerOptions
				.builder()
				.batchSize(10)
				.executor(executor)
				.keySerializer(RedisSerializer.string())
				.hashKeySerializer(RedisSerializer.string())
				.hashValueSerializer(RedisSerializer.string())
				.pollTimeout(Duration.ofSeconds(1))
				.objectMapper(new ObjectHashMapper())
				.errorHandler(new CustomErrorHandler())
				.targetType(Classroom.class)
				.build();

		StreamMessageListenerContainer<String, ObjectRecord<String, Classroom>> streamMessageListenerContainer = 
			StreamMessageListenerContainer.create(redisConnectionFactory, options);

		String streamKey = Constants.STREAM_KEY_001;

		streamMessageListenerContainer.receive(StreamOffset.fromStart(streamKey),
			new AsyncConsumeStreamListener("Independent consumption", null, null)
		);

		streamMessageListenerContainer.receive(Consumer.from("group-a", "consumer-a"),
			StreamOffset.create(streamKey, ReadOffset.lastConsumed()), new AsyncConsumeStreamListener("Consumption Group A", "group-a", "consumer-a"));

		streamMessageListenerContainer.receive(Consumer.from("group-a", "consumer-b"),
			StreamOffset.create(streamKey, ReadOffset.lastConsumed()), new AsyncConsumeStreamListener("Consumption Group A", "group-a", "consumer-b"));

		streamMessageListenerContainer.receiveAutoAck(Consumer.from("group-b", "consumer-a"),
			StreamOffset.create(streamKey, ReadOffset.lastConsumed()), new AsyncConsumeStreamListener("Consumption Group B", "group-b", "consumer-a"));

		return streamMessageListenerContainer;
	}
}

