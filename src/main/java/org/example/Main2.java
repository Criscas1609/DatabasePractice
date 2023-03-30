package org.example;

import org.example.model.Category;
import org.example.model.Product;
import org.example.repository.Repository;
import org.example.repository.impl.RepositoryImpl;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Scanner;

public class Main2 {
    public static void main(String[] args) {
        try(Connection conn = ConexionBD.getInstance()){
            Repository<Product> repository = new RepositoryImpl();
            listProducts(repository);
            getProductById(repository);
            addProduct(repository);
            updateProduct(repository);
            deleteProduct(repository);
        }catch (SQLException e) {
            e.printStackTrace();
        }

        
    }

    private static void updateProduct(Repository<Product> repository) {
        Scanner lectura = new Scanner (System.in);
        System.out.println("Ingrese el id para actualizar: ");
        Long id = Long.valueOf(lectura.next());
        System.out.println("Ingrese el nombre: ");
        String name = lectura.next();
        System.out.println("Ingrese el precio: ");
        Double price = Double.valueOf(lectura.next());
        System.out.println("Ingrese la fecha de registro (YY-MM-DD) : ");
        LocalDate date = LocalDate.parse(lectura.next());
        repository.update(id,new Product(id,name,date,price,null));
    }

    private static void addProduct(Repository<Product> repository) {
        Scanner lectura = new Scanner (System.in);
        System.out.println("Ingrese el nombre del nuevo producto: ");
        String name = lectura.next();
        System.out.println("Ingrese el precio del nuevo producto: ");
        Double price = Double.valueOf(lectura.next());
        System.out.println("Ingrese la fecha de registro del nuevo producto (YY-MM-DD) : ");
        LocalDate date = LocalDate.parse(lectura.next());
        System.out.println("Ingrese el ID de la categoria : ");
        Long categoryID = Long.valueOf(lectura.next());
        repository.save(new Product(1L,name,date,price,new Category(categoryID,null)));

    }

    private static void deleteProduct(Repository<Product> repository) throws SQLException {
        Scanner lectura = new Scanner (System.in);
        System.out.println("Ingrese el id a borrar: ");
        Long id = Long.valueOf(lectura.next());
        repository.delete(id);
        listProducts(repository);
    }

    private static void getProductById(Repository<Product> repository) {
        Scanner lectura = new Scanner (System.in);
        System.out.println("Ingrese el id a buscar: ");
        Long id = Long.valueOf(lectura.next());
        Product product = repository.byId(id);
         if(product != null) { System.out.println(product.toString()); }

    }

    private static void listProducts(Repository<Product> repository) throws SQLException {
        System.out.println("----------------LISTA DE PRODUCTOS COMPLETA --------------------");
        repository.list().forEach(System.out::println);
    }
}
