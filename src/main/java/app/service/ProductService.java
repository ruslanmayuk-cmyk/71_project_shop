package app.service;
// Сервисы - это третий слой приложения
//Сервисы содержат всю бизнес логику, то есть
//тот код, который и решает бизнес задачи, ради
//которых приложение и создавалось
//Сервис не должен обращаться в базу данных напрямую, вместо
//этого он должен вызывать соответствуюшие методы репозитория

import app.domain.Product;
import app.exceptions.ProductNotFoundException;
import app.exceptions.ProductSaveException;
import app.exceptions.ProductUpdateException;
import app.repository.ProductRepository;

import java.io.IOException;
import java.util.List;

public class ProductService {
    private  final ProductRepository repository;

    public ProductService() throws IOException {
        repository = new ProductRepository();
    }
    //   Сохранить продукт в базе данных (при сохранении продукт автоматически считается активным).
    public Product save(Product product) throws ProductSaveException, IOException {
    if (product == null) {
      throw new ProductSaveException("Product must not be null");
    }
    String title = product.getTitle();
    if (title == null || title.trim().isEmpty()){
        throw new ProductSaveException("Наименование продукта не может быть пустым");
    }
    if (product.getPrice() <= 0){
        throw new ProductSaveException("Цена продукта не  должна быть отрицательной");
    }
    product.setActive(true);
    return repository.save(product);
    }


//   Вернуть все продукты из базы данных (активные).

    public List<Product> getAllActiveProducts() throws IOException {
        return repository.findAll()
                .stream()
                .filter(Product::isActive)
                //.filter(x->x.isActive())
                .toList();
    }
//   Вернуть один продукт из базы данных по его идентификатору (если он активен).

    public Product getActiveProductById(int id) throws IOException, ProductNotFoundException {
        Product product = repository.findById(id);

        if(product == null || !product.isActive()) {
            throw new ProductNotFoundException(id);
        }
        return product;
    }

//   Изменить один продукт в базе данных по его идентификатору.
    public  void update(Product product) throws ProductUpdateException, IOException {
        if (product == null){
            throw new ProductUpdateException("Продукт не может быть null");
        }
        if (product.getPrice() <= 0){
            throw new ProductUpdateException("Цена продукта должна быть положительна");
        }
        repository.update(product);
    }

//   Удалить продукт из базы данных по его идентификатору.
//   По требованию должно происходить soft удаление - изменение статуса продукта
    public void deleteById (int id) throws IOException, ProductNotFoundException {
        getActiveProductById(id).setActive(false);
    }

//   Удалить продукт из базы данных по его наименованию.
    public void deleteByTitle(String title) throws IOException {
        getAllActiveProducts().stream()
                .filter(x-> x.getTitle().equals(title))
                .forEach(x-> x.setActive(false));
    }

//   Восстановить удалённый продукт в базе данных по его идентификатору.
    public  void  restoreById(int id) throws ProductNotFoundException, IOException {
        Product product = repository.findById(id);
        if (product != null){
            product.setActive(true);
        } else {
            throw new ProductNotFoundException(id);
        }
    }
//   Вернуть общее количество продуктов в базе данных (активных).
    public int getActiveProductsCount() throws IOException{
        return getAllActiveProducts().size();
    }

//   Вернуть суммарную стоимость всех продуктов в базе данных (активных).
    public double getActiveProductsTotalCost() throws IOException{
        return getAllActiveProducts().stream()
                .mapToDouble(Product::getPrice)
                .sum();
    }

//   Вернуть среднюю стоимость продукта в базе данных (из активных)
    public double getActiveProductsAveragePrice() throws IOException{
        int productCount = getActiveProductsCount();
        if (productCount == 0){
            return 0.0;
        }
        return getActiveProductsCount()/productCount;
    }
}
