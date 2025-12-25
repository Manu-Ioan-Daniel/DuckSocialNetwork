package repo;
import domain.Entity;
import exceptions.RepoException;
import exceptions.ValidationException;

import java.util.Optional;



public interface Repository<ID, E extends Entity<ID>> {

    Optional<E> findOne(ID id);

    Iterable<E> findAll();

    void save(E entity);

    void delete(ID id);

    void update(E entity);

}


