package tuproject.libraryproject.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import tuproject.libraryproject.domain.entities.Book;
import tuproject.libraryproject.domain.entities.User;
import tuproject.libraryproject.domain.models.view.BookViewModel;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookRepository extends JpaRepository<Book, String> {

    Optional<Book> findByTitle(String title);

    List<Book> findAllByTitleContains(String partOfTitle);

    List<Book> findAllByAuthorNameContains(String partOfAuthorName);

    List<Book> findAllByReleaseDateBefore(LocalDate dateBefore);

    List<Book> findAllByReleaseDateAfter(LocalDate dateAfter);


    List<Book> findAllByTitleContainsAndAuthorName(String title, String authorName);
}
