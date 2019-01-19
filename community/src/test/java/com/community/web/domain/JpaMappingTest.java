package com.community.web.domain;

import com.community.web.domain.enums.BoardType;
import com.community.web.repository.BoardRepository;
import com.community.web.repository.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDateTime;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.core.Is.is;

@RunWith(SpringRunner.class)
@DataJpaTest
public class JpaMappingTest {
    private final String titleForTest = "게시판 테스트";
    private final String emailForTest = "tester@gmail.com";

    @Autowired UserRepository userRepository;
    @Autowired BoardRepository boardRepository;

    @Before
    public void init() {
        User chris = userRepository.save(User.builder()
                .name("Chris")
                .password("test1234!@")
                .email(emailForTest)
                .createdDate(LocalDateTime.now())
                .build());

        boardRepository.save(Board.builder()
                .title(titleForTest)
                .subTitle("부제 : 게시판 테스트")
                .content("테스트 중 입니다.")
                .boardType(BoardType.free)
                .createdDate(LocalDateTime.now())
                .user(chris)
                .build());
    }

    @Test
    public void User_와_Board_DB_저장_확인() {
        User user = userRepository.findByEmail(emailForTest).orElse(null);

        assertThat(user, notNullValue());
        assertThat(user.getName(), is("Chris"));
        assertThat(user.getPassword(), is("test1234!@"));
        assertThat(user.getEmail(), is(emailForTest));

        Board board = boardRepository.findByUser(user).orElse(null);

        assertThat(board, notNullValue());
        assertThat(board.getTitle(), is(titleForTest));
        assertThat(board.getSubTitle(), is("부제 : 게시판 테스트"));
        assertThat(board.getContent(), is("테스트 중 입니다."));
        assertThat(board.getBoardType(), is(BoardType.free));
    }
}