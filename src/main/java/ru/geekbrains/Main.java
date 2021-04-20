package ru.geekbrains;

import com.github.javafaker.Faker;
import ru.geekbrains.java4.lesson6.db.dao.CategoriesMapper;
import ru.geekbrains.java4.lesson6.db.dao.ProductsMapper;
import ru.geekbrains.java4.lesson6.db.model.Categories;
import ru.geekbrains.java4.lesson6.db.model.CategoriesExample;
import ru.geekbrains.java4.lesson6.db.model.Products;
import ru.geekbrains.java4.lesson6.db.model.ProductsExample;
import ru.geekbrains.util.DbUtils;

public class Main {
    static Faker faker = new Faker();

    private static String resource = "mybatisConfig.xml";
    public static void main(String[] args) {
        CategoriesMapper categoriesMapper = DbUtils.getCategoriesMapper();
        ProductsMapper productsMapper = DbUtils.getProductsMapper();


//        long categoriesNumber = countCategoriesNumber(categoriesMapper);
//
//        countProductsNumber(productsMapper);


        Products product3101 = productsMapper.selectByPrimaryKey(-1L);
        System.out.println(product3101);


//        deleteProductById(productsMapper);

//        Categories newCategory = new Categories();
//        newCategory.setTitle(faker.artist().name());
//        long categoryNumber = (categoriesNumber + 1);
//        newCategory.setId((int) categoryNumber);
//        categoriesMapper.insert(newCategory);
//
//        productsMapper.insert(new Products(faker.commerce().productName(), 7777, categoryNumber));
//        productsMapper.insert(new Products(faker.commerce().productName(), 7117, categoryNumber));

    }

    private static long countProductsNumber(ProductsMapper productsMapper) {
        long count = productsMapper.countByExample(new ProductsExample());
        System.out.println("Количество товаров: " + count);
        return count;
    }


    private static long countCategoriesNumber(CategoriesMapper categoriesMapper) {
        long categoriesCount = categoriesMapper.countByExample(new CategoriesExample());
        System.out.println("Количество категорий: " + categoriesCount);
        return categoriesCount;
    }

    private static void deleteProductById(ProductsMapper productsMapper) {
        Products product3101 = productsMapper.selectByPrimaryKey(3200L);
        productsMapper.deleteByPrimaryKey(product3101.getId());
    }

}
