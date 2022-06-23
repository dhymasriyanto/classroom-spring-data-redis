package ngodingkuy.tech.classroomservice.dto;

import org.springframework.lang.Nullable;
import org.springframework.util.ErrorHandler;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CustomErrorHandler implements ErrorHandler{
	@Override
	public void handleError(@Nullable Throwable t) {
		log.info("Error exception occured: ", t);
	}
}

