package org.example.repository;
import org.example.model.Product;

import java.sql.SQLException;
import java.util.List;
public interface Repository <T>{
    List<T> list() throws SQLException;
    T byId(Long id);
    void save(Product product);
    void delete(Long id);
    void update(Long id, Product product);

}
