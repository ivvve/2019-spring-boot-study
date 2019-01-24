package com.community.rest.controller;

import com.community.rest.domain.Board;
import com.community.rest.repository.BoardRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.hateoas.PagedResources;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@RestController
@RequestMapping("/api/boards")
public class BoardRestController {
    private BoardRepository boardRepository;

    public BoardRestController(BoardRepository boardRepository) {
        this.boardRepository = boardRepository;
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PagedResources<Board>> getBoards(@PageableDefault Pageable pageable) {
        Page<Board> boardList = boardRepository.findAll(pageable);

        //페이징 처리에 관한 리소스를 만드는 PagedResources 객체를 생성
        PagedResources.PageMetadata pageMetadata =
                new PagedResources.PageMetadata(pageable.getPageSize(), boardList.getNumber(), boardList.getTotalElements());

        // PagedResources 객체를 생성
        // HATEOAS가 적용되며 페이징값까지 생성된 REST 형의 데이터를 만들어 줌
        PagedResources<Board> resources = new PagedResources<>(boardList.getContent(), pageMetadata);

        // PagedResources 객체 생성 시 따로 링크를 설정하지 않았을 경우 아래와 같이 링크를 추가
        // 여기서는 Board 마다 상세정보를 불러올 수 있는 링크를 추가함
        resources.add(linkTo(methodOn(BoardRestController.class)
                .getBoards(pageable))
                .withSelfRel());

        return ResponseEntity.ok(resources);
    }
}