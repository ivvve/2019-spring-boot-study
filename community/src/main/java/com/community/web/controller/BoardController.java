package com.community.web.controller;

import com.community.web.domain.Board;
import com.community.web.service.BoardService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Slf4j
@Controller
@RequestMapping("/board")
public class BoardController {
    private final BoardService boardService;

    public BoardController(BoardService boardService) {
        this.boardService = boardService;
    }

    @GetMapping({"", "/"})
    public String board(@RequestParam(value = "index", defaultValue = "0") Long index,
                        Model model) {
        Board board = boardService.findBoardByIndex(index).orElse(null);
        model.addAttribute("board", board);

        return "board/form";
    }

    @GetMapping("/list")
    public String list(@PageableDefault Pageable pageable, Model model) { // Pageable 필드 디버깅으로 확인
        log.debug("요청받은 page 정보 : {}", pageable);

        Page<Board> boardList = boardService.findBoardList(pageable);
        model.addAttribute("boardList", boardList);

        log.debug("총 element 수 : {}, 전체 page 수 : {}, 페이지에 표시할 element 수 : {}, 현재 페이지 index : {}, 현재 페이지의 element 수 : {}",
                boardList.getTotalElements(), boardList.getTotalPages(), boardList.getSize(),
                boardList.getNumber(), boardList.getNumberOfElements());

        return "board/list"; // resources/templates/ + board/list + .html
    }
}