package ngodingkuy.tech.classroomservice;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ngodingkuy.tech.classroomservice.producer.StreamProducer;

import static ngodingkuy.tech.classroomservice.dto.Constants.STREAM_KEY_001;

@Component
@AllArgsConstructor
@Slf4j
public class CycleGeneratorStreamMessageRunner implements ApplicationRunner{

	private final StreamProducer streamProducer;

	@Override
	public void run(ApplicationArguments args){
		Executors.newSingleThreadScheduledExecutor()
			.scheduleAtFixedRate(()->streamProducer
				.sendRecord(STREAM_KEY_001),
			0,
			5,
			TimeUnit.SECONDS);
		log.info(STREAM_KEY_001);
	}
}

