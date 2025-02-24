package com.example.jasionowicz.BorrowHistory;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BorrowHistoryRepository extends JpaRepository<BorrowHistory, Long> {
List<BorrowHistory> findAllByUserId(long id);
}
