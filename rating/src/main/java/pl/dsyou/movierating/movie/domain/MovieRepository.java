package pl.dsyou.movierating.movie.domain;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MovieRepository extends CrudRepository<Movie, Long> {
    void deleteBy(String uuid);

    Optional<Movie> findBy(String uuid);
}
