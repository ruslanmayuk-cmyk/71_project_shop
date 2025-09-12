package app.service;

import app.domain.Customer;
import app.domain.Product;
import app.exceptions.CustomerNotFoundException;
import app.exceptions.CustomerSaveException;
import app.exceptions.CustomerUpdateException;
import app.exceptions.ProductNotFoundException;
import app.repository.CustomerRepository;

import java.io.IOException;
import java.util.List;

public class CustomerService {
    private final CustomerRepository repository;
    private final ProductService productService;

    // Конструктор
    public CustomerService() throws IOException {
        repository = new CustomerRepository();
        productService = new ProductService();
    }

    //   Сохранить покупателя в базе данных (при сохранении покупатель автоматически считается активным).
    public Customer save(Customer customer) throws IOException, CustomerSaveException {
        if (customer == null) {
            throw new CustomerSaveException("Покупатель не может быть null");
        }

        String name = customer.getName();
        if (name == null || name.trim().isEmpty()) {
            throw new CustomerSaveException("Имя покупателя не может быть пустым");
        }

        customer.setActive(true);
        return repository.save(customer);
    }

    //   Вернуть всех покупателей из базы данных (активных).
    public List<Customer> getAllActiveCustomers() throws IOException {
        return repository.findAll()
                .stream()
                .filter(Customer::isActive)
                .toList();
    }
//    Вернуть одного покупателя из базы данных по его идентификатору (если он активен).
    public Customer getActiveCustomerById(int id) throws IOException, CustomerNotFoundException {
        Customer customer = repository.findById(id);
        if (customer == null || !customer.isActive()){
            throw new CustomerNotFoundException(id);
        }
        return  customer;
    }
//    Изменить одного покупателя в базе данных по его идентификатору.
public void update(Customer customer) throws CustomerUpdateException, IOException {
    if (customer == null) {
        throw new CustomerUpdateException("Покупатель не может быть null");
    }

    String name = customer.getName();
    if (name == null || name.trim().isEmpty()) {
        throw new CustomerUpdateException("Имя покупателя не может быть пустым");
    }
    customer.setActive(true);
    repository.update(customer);
}
//    Удалить покупателя из базы данных по его идентификатору.
    public void deleteById(int id) throws IOException, CustomerNotFoundException {
        Customer customer = getActiveCustomerById(id);
        customer.setActive(false);
        repository.update(customer);

    }

//    Удалить покупателя из базы данных по его имени.
    public void deleteByName(String name) throws IOException, CustomerNotFoundException {
        Customer customer = getAllActiveCustomers()
                .stream()
                .filter(x -> x.getName().equals(name))
                .peek(x -> x.setActive(false))
                .findFirst()
                .orElseThrow(
                        () -> new CustomerNotFoundException(name)
                );
        repository.update(customer);
    }

//    Восстановить удалённого покупателя в базе данных по его идентификатору.
    public  void restoreById( int id) throws IOException, CustomerNotFoundException {
        Customer customer = repository.findById(id);
        if(customer != null){
            customer.setActive(true);
            repository.update(customer);
        } else {
            throw new CustomerNotFoundException(id);
        }

    }

//    Вернуть общее количество покупателей в базе данных (активных).

    public int getActiveCustomerCount() throws IOException {
        return getAllActiveCustomers().size();
    }
//    Вернуть стоимость корзины покупателя по его идентификатору (если он активен).
    public double getCustomerCartTotalPrice(int id) throws IOException, CustomerNotFoundException {
        return getActiveCustomerById(id).getProducts()
                .stream()
                .filter(Product::isActive)
                .mapToDouble(Product::getPrice)
                .sum();
    }

//    Вернуть среднюю стоимость продукта в корзине покупателя по его идентификатору (если он активен)
public double getCustomerCartAveragePrice(int id) throws IOException, CustomerNotFoundException {
    return getActiveCustomerById(id).getProducts()
            .stream()
            .filter(Product::isActive)
            .mapToDouble(Product::getPrice)
            .average()
            .orElse(0.0);
}

//    Добавить товар в корзину покупателя по их идентификаторам (если оба активны)
    public void addProductToCustomerCart(int customerId, int productId) throws IOException, CustomerNotFoundException, ProductNotFoundException {
        Customer customer = getActiveCustomerById(customerId);
        Product product = productService.getActiveProductById(productId);
        customer.getProducts().add(product);
        repository.update(customer);
    }

//    Удалить товар из корзины покупателя по их идентификатору
    public void removeProductFromCustomerCart(int customerId, int productId) throws IOException, CustomerNotFoundException, ProductNotFoundException {
        Customer customer = getActiveCustomerById(customerId);
        Product product = productService.getActiveProductById(productId);
        customer.getProducts().remove(product);
        repository.update(customer);
    }

//    Полностью очистить корзину покупателя по его идентификатору (если он активен)

    public void clearCustomerCart(int id) throws IOException, CustomerNotFoundException {
        Customer customer = getActiveCustomerById(id);
        customer.getProducts().clear();
        repository.update(customer);
    }


}
