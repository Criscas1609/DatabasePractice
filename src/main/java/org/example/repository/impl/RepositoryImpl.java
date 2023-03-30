package org.example.repository.impl;

import org.example.ConexionBD;
import org.example.model.Category;
import org.example.model.Product;
import org.example.repository.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class RepositoryImpl implements Repository {
    private Connection getConnection() throws SQLException {
        return ConexionBD.getInstance();
    }

    private Product createProduct(ResultSet resultSet) throws
            SQLException {
        Product producto = new Product();
        producto.setId(resultSet.getLong("id"));

        producto.setName(resultSet.getString("nombre"));
        producto.setPrice(resultSet.getDouble("precio"));
        producto.setRegisterDate(resultSet.getDate("fecha_registro").toLocalDate());
        Category categoria = new Category();
        categoria.setId(resultSet.getLong("categoria_id"));
        categoria.setName(resultSet.getString("categoria_nombre"));
        producto.setCategory(categoria);
        return producto;
    }

    public List<Product> list() throws SQLException {
        List<Product> productoList = new ArrayList<>();
        try (Statement statement = getConnection().createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT p.*,c.nombre as categoria_nombre from products as p inner join category as c ON (p.categoria_id=c.id)")) {
            while (resultSet.next()) {
                Product producto = createProduct(resultSet);
                productoList.add(producto);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return productoList;
    }

    public Product byId(Long id) {
        Product producto = null;
        try (PreparedStatement preparedStatement = getConnection()
                .prepareStatement("SELECT p.*,c.nombre as categoria_nombre from products as p inner join category as c ON (p.categoria_id=c.id) WHERE p.id=?")) {
            preparedStatement.setLong(1,id);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                producto = createProduct(resultSet);
            }else {
                System.out.println("No existe ese id");
            }
            resultSet.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return producto;
    }

    @Override
    public void save(Product product) {
        try (PreparedStatement preparedStatement=getConnection().prepareStatement("INSERT INTO products(nombre,precio,fecha_registro,categoria_id) VALUES (?,?,?,?)")){
            preparedStatement.setString(1,product.getName());
            preparedStatement.setLong(2,product.getPrice().longValue());
            preparedStatement.setDate(3,Date.valueOf(product.getRegisterDate()));
            validateCategory(preparedStatement,product);
            preparedStatement.executeUpdate();
        }catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(Long valueId) {
        try {
            PreparedStatement preparedStatement = getConnection()
                    .prepareStatement("DELETE FROM products WHERE id ='"+valueId+"'");
                preparedStatement.executeUpdate();
                System.out.println("Producto eliminado");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void update(Long id, Product product) {
        try (PreparedStatement preparedStatement = getConnection().prepareStatement("UPDATE products SET nombre=? ,precio=?,fecha_registro=? where id=?")) {
            preparedStatement.setString(1, product.getName());
            preparedStatement.setLong(2, product.getPrice().longValue());
            preparedStatement.setDate(3, Date.valueOf(product.getRegisterDate()));
            preparedStatement.setLong(4,product.getId());
            preparedStatement.executeUpdate();
            System.out.println("Producto actualizado");
            System.out.println("----------" + product.toString() + "----------------");
        } catch (SQLException e){
            e.printStackTrace();
        }

    }

    //Validación de la existencia de la nueva categoria
    public boolean checkCategory(Long id) {
        try (PreparedStatement preparedStatement = getConnection()
                .prepareStatement("SELECT p.*,c.nombre as categoria_nombre from products as p inner join category as c ON (p.categoria_id=c.id) WHERE c.id=?")) {
            preparedStatement.setLong(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return true;
            }else return false;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    public Category createCategory(Long id) {
        try (PreparedStatement preparedStatement=getConnection().prepareStatement("INSERT INTO category(id,nombre) VALUES (?,?)")){
            Scanner lectura = new Scanner (System.in);
            System.out.println("Ingrese el nombre de la nueva categoria: ");
            String name = lectura.next();
            preparedStatement.setLong(1,id);
            preparedStatement.setString(2,name);
            preparedStatement.executeUpdate();
            return new Category(id,name);
        }catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    public void validateCategory(PreparedStatement preparedStatement,Product product) throws SQLException{
        boolean a = checkCategory(product.getCategory().getId());
        if(a){
          preparedStatement.setLong(4, product.getCategory().getId());
        }else{
            Category category = createCategory(product.getCategory().getId());
            preparedStatement.setLong(4,category.getId());
            product.setCategory(category);
        }
        System.out.println(product.toString());
        System.out.println("Producto agregado");
    }



}
