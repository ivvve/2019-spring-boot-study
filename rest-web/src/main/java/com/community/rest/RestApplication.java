package com.community.rest;


import com.community.rest.domain.Board;
import com.community.rest.domain.User;
import com.community.rest.domain.enums.BoardType;
import com.community.rest.repository.BoardRepository;
import com.community.rest.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.time.LocalDateTime;
import java.util.stream.IntStream;

@SpringBootApplication
public class RestApplication {

	public static void main(String[] args) {
		SpringApplication.run(RestApplication.class, args);
	}

	@Bean
	public CommandLineRunner commandLineRunner(UserRepository userRepository, BoardRepository boardRepository) {
		return arg -> {
			User chris = userRepository.save(User.builder()
					.name("Chris")
					.password("test1234!@")
					.email("chris@naver.com")
					.createdDate(LocalDateTime.now())
					.build());

			// Paging을 위해 200개의 데이터를 넣는다.
			IntStream.rangeClosed(1, 154).forEach(index ->
				boardRepository.save(Board.builder()
						.title("게시글" + index)
						.subTitle("순서" + index)
						.content("테스트" + index)
						.boardType(BoardType.free)
						.createdDate(LocalDateTime.now())
						.updatedDate(LocalDateTime.now())
						.user(chris)
						.build())
			);
		};
	}
}

