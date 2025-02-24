package com.example.jasionowicz.BorrowHistory;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class BorrowHistoryController {

    private BorrowHistoryService borrowHistoryService;

    public BorrowHistoryController(BorrowHistoryService borrowHistoryService) {
        this.borrowHistoryService = borrowHistoryService;

    }

    @GetMapping("/getBorrowHistory/{id}")
    @ResponseBody
    public List<BorrowHistoryDTO> getBorrowHistory(@PathVariable int id) {
        return borrowHistoryService.getAllLibraryUserBorrowedHistory(id);

    }

}
