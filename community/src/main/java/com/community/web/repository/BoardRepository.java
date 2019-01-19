package com.community.web.repository;

import com.community.web.domain.Board;
import com.community.web.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BoardRepository extends JpaRepository<Board, Long> {
    public Optional<Board> findByUser(User user);
}