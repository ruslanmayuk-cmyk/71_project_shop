package app.repository;

import app.domain.Product;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

// Репозитории - это второй слой приложения
//Задача репозиториев - обеспечить простейший доступ к данным
//посредством реализации CRUD операций
//CRUD - Create(создать), Read(прочитать), Update(обновить), Delete(удалить)
public class ProductRepository {
    // Файл, к-рый является базой данных
    private final File database;

    // Маппер для чтения и записи обьектов в файл
    private  final ObjectMapper mapper;

    // Поле, к-рое хранит максимальный идентификатор, сохранённый в БД
    private  int maxId;


    // Конструктор
    public  ProductRepository() throws IOException {
        database = new File("database/product.txt");
        mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);

        // Выясняем какой идентификатор в БД на данный момент максимальный
        List<Product> products = findAll();

        if(!products.isEmpty()){
            Product lastProduct = products.get(products.size() - 1);
            maxId = lastProduct.getId();
        }
    }
// Сохранение нового продукта в БД
    public  Product save(Product product) throws IOException {
        product.setId(++maxId);
        List<Product> products = findAll();
        products.add(product);
        mapper.writeValue(database, products);
        return product;
    }
// Чтение всех продуктов из БД
public List<Product> findAll() throws IOException {
    try {
        Product[] products = mapper.readValue(database, Product[].class);
        return new ArrayList<>(Arrays.asList(products));
    } catch (MismatchedInputException e) {
        return new ArrayList<>();
    }
}

// Чтение одного продукта по id
    public Product findById(int id) throws IOException {
        return findAll()
                .stream()
                .filter(x -> x.getId() == id)
                .findFirst()
                .orElse(null);
    }
// Обновление существующего продукта
    // этот метод будет менять только цену продуктов
    public  void update (Product product) throws IOException {
        int id = product.getId();
        double newPrice = product.getPrice();

        List<Product> products = findAll();
        products.stream()
                .filter(x-> x.getId() == id)
                .forEach(x -> x.setPrice(newPrice) );

        mapper.writeValue(database, products);

    }
// Удаление продукта
    public void  deleteById(int id) throws IOException {
        List<Product> products = findAll();
        products.removeIf(x-> x.getId() == id);
        mapper.writeValue(database, products);

    }


}
