package edu.handong.week12.book;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/books")
public class BookController {
    private final BookService service;

    public BookController(BookService service) {
        this.service = service;
    }

    @GetMapping
    public List<BookResponse> list() {
        return service.findAll();
    } // TODO: Pageable

    @GetMapping("/{id}")
    public BookResponse get(@PathVariable Long id) {
        return service.findById(id);
    }

    @PostMapping
    public BookResponse create(@Valid @RequestBody BookRequest input) {
        return service.create(input);
    } // TODO: @Valid, ResponseEntity 201

    @PutMapping("/{id}")
    public BookResponse update(@PathVariable Long id, @Valid @RequestBody BookRequest input) {
        return service.update(id, input);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    } // TODO: 204
}
