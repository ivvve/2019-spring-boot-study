package com.community.rest.domain;

import com.community.rest.domain.enums.BoardType;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@Entity
public class Board implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // IDENTITY: 키 생성을 DB에 위임
    private Long index;
    private String title;
    private String subTitle;
    private String content;
    @Enumerated(EnumType.STRING) //EnumType.ORDINAL은 enum의 순번으로 저장, String은 해당 enum 그대로 저장
    private BoardType boardType;
    // LocalDateTime: 1.8부터 추가된 API로 Date, Calendar에서 부실한 날짜 연산기능을 추가로 제공
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;

    @OneToOne(fetch = FetchType.EAGER) // <- EAGER 사용해야 한다
    private User user; // 실제 User 객체가 DB에 저장되는 것이 아닌 User의 index가 record에 저장된다.

    /************
     * 추가 됨  *
     ************/
    public void setCreatedDateNow() {
        this.createdDate = LocalDateTime.now();
    }

    /************
     * 추가 됨  *
     ************/
    public void update(Board board) {
        this.title = board.getTitle();
        this.subTitle = board.getSubTitle();
        this.content = board.getContent();
        this.boardType = board.getBoardType();
        this.updatedDate = LocalDateTime.now();
    }

    @Builder
    public Board(String title, String subTitle, String content, BoardType boardType, LocalDateTime createdDate, LocalDateTime updatedDate, User user) {
        this.title = title;
        this.subTitle = subTitle;
        this.content = content;
        this.boardType = boardType;
        this.createdDate = createdDate;
        this.updatedDate = updatedDate;
        this.user = user;
    }
}