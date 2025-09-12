package app.repository;

import app.domain.Customer;

import app.domain.Product;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CustomerRepository {
    // Файл, к-рый является базой данных
    private final File database;

    // Маппер для чтения и записи обьектов в файл
    private  final ObjectMapper mapper;

    // Поле, к-рое хранит максимальный идентификатор, сохранённый в БД
    private  int maxId;


    // Конструктор
    public  CustomerRepository() throws IOException {
        database = new File("database/customer.txt");
        mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);

        // Выясняем какой идентификатор в БД на данный момент максимальный
        List<Customer> customers = findAll();

        if(!customers.isEmpty()){
            Customer lastCustomert = customers.get(customers.size() - 1);
            maxId = lastCustomert.getId();
        }
    }
    // Сохранение нового продукта в БД
    public  Customer save(Customer customer) throws IOException {
        customer.setId(++maxId);
        List<Customer> customers = findAll();
        customers.add(customer);
        mapper.writeValue(database, customers);
        return customer;
    }
    // Чтение всех продуктов из БД
    public List<Customer> findAll() throws IOException {
        try {
            Customer[] customers = mapper.readValue(database, Customer[].class);
            return new ArrayList<>(Arrays.asList(customers));
        } catch (MismatchedInputException e) {
            return new ArrayList<>();
        }
    }

    // Чтение одного продукта по id
    public Customer findById(int id) throws IOException {
        return findAll()
                .stream()
                .filter(x -> x.getId() == id)
                .findFirst()
                .orElse(null);
    }

    public void update(Customer customer) throws IOException {
        int id = customer.getId();
        String newName = customer.getName();
        boolean active = customer.isActive();
        List<Product> products = customer.getProducts();

        List<Customer> customers = findAll();
        customers
                .stream()
                .filter(x -> x.getId() == id)
                .forEach(x -> {
                    x.setName(newName);
                    x.setActive(active);
                    x.setProducts(products);
                });

        mapper.writeValue(database, customers);
    }
    // Удаление
    public void  deleteById(int id) throws IOException {
        List<Customer> customers = findAll();
        customers.removeIf(x-> x.getId() == id);
        mapper.writeValue(database, customers);

    }
}
