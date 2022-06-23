package ngodingkuy.tech.classroomservice.producer;

import org.springframework.data.redis.connection.stream.ObjectRecord;
import org.springframework.data.redis.connection.stream.RecordId;
import org.springframework.data.redis.connection.stream.StreamRecords;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ngodingkuy.tech.classroomservice.dto.ClassroomFaker;
import ngodingkuy.tech.classroomservice.model.Classroom;

@Component
@RequiredArgsConstructor
@Slf4j
public class StreamProducer {

	private final RedisTemplate<String, Object> redisTemplate;

	public void sendRecord(String streamKey) {
		Classroom classroom = ClassroomFaker.create();
		log.info("Information generating a classroom: [{}]", classroom);

		ObjectRecord<String, Classroom> record = StreamRecords.newRecord()
			.in(streamKey)
			.ofObject(classroom)
			.withId(RecordId.autoGenerate());

		RecordId recordId = redisTemplate.opsForStream()
			.add(record);

		log.info("Returned record ID: [{}]", recordId);
	}
}

