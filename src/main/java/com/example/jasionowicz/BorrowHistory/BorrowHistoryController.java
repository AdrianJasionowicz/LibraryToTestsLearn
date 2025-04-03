package com.example.jasionowicz.BorrowHistory;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class BorrowHistoryController {

    private final BorrowHistoryService borrowHistoryService;

    public BorrowHistoryController(BorrowHistoryService borrowHistoryService) {
        this.borrowHistoryService = borrowHistoryService;

    }

    @GetMapping("/getBorrowHistory/{id}")
    public List<BorrowHistoryDTO> getBorrowHistory(@PathVariable int id) {
        return borrowHistoryService.getAllLibraryUserBorrowedHistory(id);

    }

}
