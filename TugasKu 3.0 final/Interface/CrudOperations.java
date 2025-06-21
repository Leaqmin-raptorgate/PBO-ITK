package Interface;

import java.util.List;
import java.util.Optional;

public interface CrudOperations<T, ID> {
    // Create
    boolean create(T entity); // Returns true if successful

    // Read
    Optional<T> findById(ID id);    // Find a single entity by its ID
    List<T> findAll();              // Find all entities


    // Update
    boolean update(T entity);       // Returns true if successful

    // Delete
    boolean deleteById(ID id);      // Returns true if successful
}
