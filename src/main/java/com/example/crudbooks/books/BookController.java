package com.example.crudbooks.books;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Field;
import java.util.Map;

@RestController
@RequestMapping("/books")
public class BookController
{
    private BookRepository repo;

    public BookController(BookRepository repo)
    {
        this.repo = repo;
    }

    @GetMapping("")
    public Iterable<Book> listAll() { return repo.findAll(); }

    @PostMapping("")
    public Book create( @RequestBody Book newBook ) { return repo.save(newBook); }

    @GetMapping("/{id}")
    public Object read( @PathVariable Long id )
    {
        return repo.existsById(id) ?
                repo.findById(id) :
                "There is no book with id " + id;
    }

    @PatchMapping("/{id}")
    public Object update( @PathVariable Long id,
                          @RequestBody Map<String, Object> patches )
    {
        // add it if it doesn't exist
        if ( !repo.existsById(id) )
        {
            ObjectMapper mapper = new ObjectMapper();
            Book newBook = mapper.convertValue(patches, Book.class);
            return repo.save(newBook);
        }

        // else pull the old one and update it
        Book currBook = repo.findById(id).orElse(null);

        patches.forEach((key, val) ->
        {
            Field field = ReflectionUtils.findField(Book.class, key);
            field.setAccessible(true);
            ReflectionUtils.setField(field, currBook, val);
        });

        return repo.save(currBook);
    }

    @DeleteMapping("/{id}")
    public String delete( @PathVariable Long id )
    {
        if (repo.existsById(id))
        {
            repo.deleteById(id);
            return String.format("Book %d has been deleted - %d remaining", id, repo.count());
        }

        return "There is no book with id " + id;
    }

}
