package ngodingkuy.tech.classroomservice.consumer;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.data.redis.connection.stream.ObjectRecord;
import org.springframework.data.redis.connection.stream.ReadOffset;
import org.springframework.data.redis.connection.stream.StreamOffset;
import org.springframework.data.redis.connection.stream.StreamReadOptions;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import lombok.extern.slf4j.Slf4j;
import ngodingkuy.tech.classroomservice.dto.Constants;
import ngodingkuy.tech.classroomservice.model.Classroom;

@Component
@Slf4j
public class XReadNonBlockConsumer01 implements InitializingBean, DisposableBean{

	private ThreadPoolExecutor threadPoolExecutor;

	@Resource
	private RedisTemplate<String, Object> redisTemplate;

	private volatile boolean stop = false;

	@Override
	public void destroy() throws Exception {
		stop= true;
		threadPoolExecutor.shutdown();
		threadPoolExecutor.awaitTermination(3, TimeUnit.SECONDS);
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		
		threadPoolExecutor = new ThreadPoolExecutor(1,1,0, TimeUnit.SECONDS,
			new LinkedBlockingDeque<>(), r->{
				Thread thread = new Thread(r);
				thread.setDaemon(true);
				thread.setName("xread-nonblock-01");
				return thread;
			}
		);

		StreamReadOptions streamReadOptions = StreamReadOptions.empty()
			.block(Duration.ofMillis(1000))
			.count(10);

		StringBuilder readOffset = new StringBuilder("0-0");

		threadPoolExecutor.execute(()->{
			while(!stop){
				List<ObjectRecord<String, Classroom>> objectRecords = redisTemplate.opsForStream()
					.read(Classroom.class, streamReadOptions, StreamOffset.create(Constants.STREAM_KEY_001, ReadOffset.from(readOffset.toString())));
				if (CollectionUtils.isEmpty(objectRecords)) {
					log.warn("No data obtained");
					continue;
				}
				for (ObjectRecord<String, Classroom> objectRecord: objectRecords) {
					log.info("Obtained data information ID: [{}] classroom: [{}]", objectRecord.getId(), objectRecord.getValue());
					readOffset.setLength(0);
					readOffset.append(objectRecord.getId());
				}
			}
		});
	}

}

