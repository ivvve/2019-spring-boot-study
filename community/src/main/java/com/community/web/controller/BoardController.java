package com.community.web.controller;

import com.community.web.domain.Board;
import com.community.web.service.BoardService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

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
    public String list(@PageableDefault Pageable pageable, Model model) {
        Page<Board> boardList = boardService.findBoardList(pageable);
        model.addAttribute("boardList", boardList);

        return "board/list"; // resources/templates/ + board/list + .html
    }
}