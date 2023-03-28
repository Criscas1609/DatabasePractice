package org.example.repository.impl;

import org.example.ConexionBD;
import org.example.model.Product;
import org.example.repository.Repository;

import javax.management.Query;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

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
        return producto;
    }

    public List<Product> list() throws SQLException {
        List<Product> productoList = new ArrayList<>();
        try (Statement statement = getConnection().createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT * from products")) {
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
                .prepareStatement("SELECT * FROM products WHERE id =?")) {
            preparedStatement.setLong(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                producto = createProduct(resultSet);
            }
            resultSet.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return producto;
    }

    @Override
    public void save(Product product) {
        try (PreparedStatement preparedStatement=getConnection().prepareStatement("INSERT INTO products(nombre,precio,fecha_registro) VALUES (?,?,?)")){
            preparedStatement.setString(1,product.getName());
            preparedStatement.setLong(2,product.getPrice().longValue());
            preparedStatement.setDate(3,Date.valueOf(product.getRegisterDate()));
            preparedStatement.executeUpdate();
            System.out.println(product.toString());
            System.out.println("Producto agregado");
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
        try (PreparedStatement preparedStatement=getConnection().prepareStatement("UPDATE products SET nombre=? ,precio=?,fecha_registro=? where id=?")){
            preparedStatement.setString(1,product.getName());
            preparedStatement.setLong(2,product.getPrice().longValue());
            preparedStatement.setDate(3,Date.valueOf(product.getRegisterDate()));
            preparedStatement.setLong(4,id);
            preparedStatement.executeUpdate();
            System.out.println("Producto actualizado");
            System.out.println("----------"+product.toString()+"----------------");
        }catch (SQLException e) {
            e.printStackTrace();
        }

    }



}
