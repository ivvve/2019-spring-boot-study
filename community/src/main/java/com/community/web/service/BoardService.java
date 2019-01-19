package com.community.web.service;

import com.community.web.domain.Board;
import com.community.web.repository.BoardRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class BoardService {

    private final BoardRepository boardRepository;

    public BoardService(BoardRepository boardRepository) {
        this.boardRepository = boardRepository;
    }

    /**
     * Pageable을 사용하여 해당 페이지에 해당하는 Board를 가져옴
     * @param pageable
     * @return
     */
    public Page<Board> findBoardList(Pageable pageable) {
        int pageNumber = pageable.getPageNumber() <= 0 ? 0 : pageable.getPageNumber() - 1; // page는 index 처럼 0부터 count
        pageable = PageRequest.of(pageNumber, pageable.getPageSize());

        return boardRepository.findAll(pageable);
    }

    public Optional<Board> findBoardByIndex(Long index) {
        return boardRepository.findById(index);
    }
}