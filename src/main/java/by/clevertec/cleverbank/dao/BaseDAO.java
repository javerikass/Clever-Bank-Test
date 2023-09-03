package by.clevertec.cleverbank.dao;

import java.util.List;
import java.util.Optional;

public interface BaseDAO<E> {

  long save(E entity);

  boolean update(E entity);

  boolean delete(E entity);

  List<E> findAll();

  Optional<E> findById(long id);
}
