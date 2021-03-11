package com.example.crudbooks;

import com.example.crudbooks.books.Book;
import com.example.crudbooks.books.BookRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import javax.transaction.Status;
import javax.transaction.Transactional;
import java.util.Date;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class BookControllerTests
{
    @Autowired
    MockMvc mvc;

    @Autowired
    BookRepository repo;

    @Transactional
    @Rollback
    @Test
    public void postBook() throws Exception
    {
        Book newBook = new Book();
        newBook.setName("sorcerer's stone");
        newBook.setPublishDate(new Date());
        ObjectMapper objectMapper = new ObjectMapper();

        MockHttpServletRequestBuilder postRequest = post("/books")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newBook));

        mvc.perform(postRequest)
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.name", is(newBook.getName())));
    }

    @Transactional
    @Rollback
    @Test
    public void postBookThenGetById() throws Exception
    {
        Book newBook = new Book();
        newBook.setName("sorcerer's stone");
        newBook.setPublishDate(new Date());
        ObjectMapper objectMapper = new ObjectMapper();

        MockHttpServletRequestBuilder postRequest = post("/books")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newBook));

        mvc.perform(postRequest)
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.name", is(newBook.getName())));

        Book newBook2 = new Book();
        newBook2.setName("chamber of secrets");
        newBook2.setPublishDate(new Date());
        repo.save(newBook2);

        Book newBook3 = new Book();
        newBook3.setName("prisoner of azkaban");
        newBook3.setPublishDate(new Date());
        repo.save(newBook3);

        MockHttpServletRequestBuilder getId2 = get("/books/2")
                .contentType(MediaType.TEXT_PLAIN);

        mvc.perform(getId2)
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.name", is(newBook2.getName())));

    }

    @Transactional
    @Rollback
    @Test
    public void saveBookThenPatchIt() throws Exception
    {
        Book newBook = new Book();
        newBook.setName("sorcerer's stone");
        newBook.setPublishDate(new Date());
        repo.save(newBook);

        var patches = Map.of("name", "philosopher's stone");
        ObjectMapper mapper = new ObjectMapper();

        MockHttpServletRequestBuilder patchRequest = patch("/books/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(patches));

        mvc.perform(patchRequest)
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.name", is(patches.get("name"))));
    }

    @Transactional
    @Rollback
    @Test
    public void tryToPatchIfDoesntExist() throws Exception
    {
        var patches = Map.of("name", "philosopher's stone",
                            "publishDate", new Date());
        ObjectMapper mapper = new ObjectMapper();

        MockHttpServletRequestBuilder patchRequest = patch("/books/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(patches));

        mvc.perform(patchRequest)
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.name", is(patches.get("name"))));
    }

    @Transactional
    @Rollback
    @Test
    public void saveThreeBooksThenDeleteOne() throws Exception
    {
        Book newBook = new Book();
        newBook.setName("sorcerer's stone");
        newBook.setPublishDate(new Date());
        repo.save(newBook);

        Book newBook2 = new Book();
        newBook2.setName("chamber of secrets");
        newBook2.setPublishDate(new Date());
        repo.save(newBook2);

        Book newBook3 = new Book();
        newBook3.setName("prisoner of azkaban");
        newBook3.setPublishDate(new Date());
        repo.save(newBook3);

        MockHttpServletRequestBuilder deleteRequest = delete("/books/2")
                .contentType(MediaType.TEXT_PLAIN);

        mvc.perform(deleteRequest)
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().string("Book 2 has been deleted - 2 remaining"));
    }

    @Transactional
    @Rollback
    @Test
    public void postThreeBooksThenGetAll() throws Exception
    {
        Book newBook = new Book();
        newBook.setName("sorcerer's stone");
        newBook.setPublishDate(new Date());
        repo.save(newBook);

        Book newBook2 = new Book();
        newBook2.setName("chamber of secrets");
        newBook2.setPublishDate(new Date());
        repo.save(newBook2);

        Book newBook3 = new Book();
        newBook3.setName("prisoner of azkaban");
        newBook3.setPublishDate(new Date());
        repo.save(newBook3);

        MockHttpServletRequestBuilder getId2 = get("/books")
                .contentType(MediaType.TEXT_PLAIN);

        mvc.perform(getId2)
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$[1].name", is(newBook2.getName())));
    }

    @Transactional
    @Rollback
    @Test
    public void saveThreeBooksThenFailToDeleteOne() throws Exception
    {
        Book newBook = new Book();
        newBook.setName("sorcerer's stone");
        newBook.setPublishDate(new Date());
        repo.save(newBook);

        Book newBook2 = new Book();
        newBook2.setName("chamber of secrets");
        newBook2.setPublishDate(new Date());
        repo.save(newBook2);

        Book newBook3 = new Book();
        newBook3.setName("prisoner of azkaban");
        newBook3.setPublishDate(new Date());
        repo.save(newBook3);

        MockHttpServletRequestBuilder deleteRequest = delete("/books/20")
                .contentType(MediaType.TEXT_PLAIN);

        mvc.perform(deleteRequest)
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().string("There is no book with id 20"));
    }

    @Transactional
    @Rollback
    @Test
    public void tryToGetBookThatDoesntExist() throws Exception
    {
        MockHttpServletRequestBuilder failingGetRequest = get("/books/20")
                .contentType(MediaType.TEXT_PLAIN);

        mvc.perform(failingGetRequest)
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().string("There is no book with id 20"));
    }

}
