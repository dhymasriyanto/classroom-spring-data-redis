package ngodingkuy.tech.classroomservice.consumer;


import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.connection.stream.ObjectRecord;
import org.springframework.data.redis.connection.stream.RecordId;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.stream.StreamListener;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import ngodingkuy.tech.classroomservice.model.Classroom;

@Slf4j
@Getter
@Setter
public class AsyncConsumeStreamListener implements StreamListener<String, ObjectRecord<String, Classroom>>{

	public AsyncConsumeStreamListener(String consumerType, String group, String consumerName) {
		this.consumerType = consumerType;
		this.group = group;
		this.consumerName = consumerName;
	}

	private String consumerType;

	private String group;

	private String consumerName;

	private RedisTemplate<String, Object> redisTemplate;

	@Override
	public void onMessage(ObjectRecord<String, Classroom> message) {
		String stream = message.getStream();
		RecordId id = message.getId();
		Classroom value = message.getValue();

		if (StringUtils.isBlank(group)) {
			log.info("[{}]: received message stream: [{}] ID: [{}], value: [{}]", consumerType, stream, id, value);
		}else{
			log.info("[{}] group : [{}], consumerName :[{}] received message stream: [{}] ID: [{}], value: [{}]", consumerType, group, consumerName, stream, id, value);
		}

		//redisTemplate.opsForStream()
			//.acknowledge("key", "group", "recordId");
	}

}

