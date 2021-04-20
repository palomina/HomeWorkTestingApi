package geekbrains;

import com.github.javafaker.Faker;
import lombok.SneakyThrows;
import okhttp3.ResponseBody;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import retrofit2.Converter;
import ru.geekbrains.base.enums.CategoryType;
import ru.geekbrains.base.enums.ResponseCode;
import ru.geekbrains.dto.ErrorBody;
import ru.geekbrains.dto.Product;
import ru.geekbrains.java4.lesson6.db.dao.CategoriesMapper;
import ru.geekbrains.java4.lesson6.db.dao.ProductsMapper;
import ru.geekbrains.java4.lesson6.db.model.Categories;
import ru.geekbrains.java4.lesson6.db.model.Products;
import ru.geekbrains.service.ProductService;
import ru.geekbrains.util.DbUtils;
import ru.geekbrains.util.RetrofitUtils;

import java.io.IOException;
import java.lang.annotation.Annotation;

import static org.assertj.core.api.Assertions.assertThat;

public class ProductTests {
    Integer productId;
    Faker faker = new Faker();
    static ProductService productService;
    Product product;

    static CategoriesMapper categoriesMapper;
    static ProductsMapper productsMapper;

    @SneakyThrows
    @BeforeAll
    static void beforeAll() {
        productService = RetrofitUtils.getRetrofit()
                .create(ProductService.class);

        categoriesMapper = DbUtils.getCategoriesMapper();
        productsMapper = DbUtils.getProductsMapper();
    }

    @BeforeEach
    void setUp() {
        product = new Product()
                .withCategoryTitle(CategoryType.FOOD.getTitle())
                .withPrice((int) (Math.random() * 1000 + 1))
                .withTitle(faker.food().ingredient());
    }

    @AfterEach
    void tearDown() {
        if (productId!=null) {
            try {
                retrofit2.Response<ResponseBody> response =
                        productService.deleteProduct(productId)
                                .execute();
                assertThat(response.isSuccessful()).isTrue();
            } catch (IOException e) {

            }
        }
    }

    @SneakyThrows
    @Test
    void getProductsTest() {
        retrofit2.Response<ResponseBody> response =
                productService.list()
                        .execute();
        assertThat(response.isSuccessful()).isTrue();
    }

    @SneakyThrows
    @Test
    void createNewProductTest() {
        retrofit2.Response<Product> response =
                productService.createProduct(product)
                        .execute();
        productId = response.body().getId();
        assertThat(response.isSuccessful()).isTrue();

        Products dbProduct = productsMapper.selectByPrimaryKey((long) productId);
        assertThat(dbProduct.getTitle()).isEqualTo(product.getTitle());
        assertThat((double) dbProduct.getPrice()).isEqualTo(product.getPrice());
    }

    @SneakyThrows
    @Test
    void createNewProductNegativeTest() {
        retrofit2.Response<Product> response =
                productService.createProduct(product.withId(555))
                        .execute();
        assertThat(response.code()).isEqualTo(ResponseCode.BAD_REQUEST);
        if (response != null && !response.isSuccessful() && response.errorBody() != null) {
            ResponseBody body = response.errorBody();
            Converter<ResponseBody, ErrorBody> converter = RetrofitUtils.getRetrofit().responseBodyConverter(ErrorBody.class, new Annotation[0]);
            ErrorBody errorBody = converter.convert(body);
            assertThat(errorBody.getMessage()).isEqualTo("Id must be null for new entity");
        }
    }

    @SneakyThrows
    @ParameterizedTest
    @ValueSource(doubles = {100, -3, 0, 0.5d,  Integer.MAX_VALUE})
    void createProductWithNegativePriceTest(double price) {
        Product p = product.withPrice(price);
        retrofit2.Response<Product> response =
                productService.createProduct(p)
                        .execute();
        productId = response.body().getId();
        assertThat(response.isSuccessful()).isTrue();

        retrofit2.Response<Product> getProductResponse =
                productService.getProduct(productId)
                        .execute();
        assertThat(getProductResponse.code()).isEqualTo(ResponseCode.OK);
        assertThat(getProductResponse.isSuccessful()).isTrue();
        assertThat(getProductResponse.body().getTitle()).isEqualTo(p.getTitle());
        assertThat(getProductResponse.body().getPrice()).isEqualTo(p.getPrice());
        assertThat(getProductResponse.body().getCategoryTitle()).isEqualTo(p.getCategoryTitle());


        Products dbProduct = productsMapper.selectByPrimaryKey((long) productId);
        Categories dbCategory = categoriesMapper.selectByPrimaryKey(dbProduct.getCategory_id());

        assertThat(dbProduct.getTitle()).isEqualTo(p.getTitle());
        assertThat((double) dbProduct.getPrice()).isEqualTo(p.getPrice());
        assertThat(dbCategory.getTitle()).isEqualTo(p.getCategoryTitle());
    }

    @SneakyThrows
    @Test
    void createProductWithSqlInjectionTest() {
        Product p = new Product()
                .withCategoryTitle(CategoryType.FOOD.getTitle())
                .withPrice(1)
                .withTitle("(SELECT pg_sleep(60))");

        retrofit2.Response<Product> response =
                productService.createProduct(p)
                        .execute();
        productId = response.body().getId();
        assertThat(response.isSuccessful()).isTrue();

        retrofit2.Response<Product> getProductResponse =
                productService.getProduct(productId)
                        .execute();
        assertThat(response.code()).isEqualTo(ResponseCode.OK_CREATED);
        assertThat(response.isSuccessful()).isTrue();
        assertThat(getProductResponse.body().getTitle()).isEqualTo(p.getTitle());
        assertThat(getProductResponse.body().getPrice()).isEqualTo(p.getPrice());
        assertThat(getProductResponse.body().getCategoryTitle()).isEqualTo(p.getCategoryTitle());

        Products dbProduct = productsMapper.selectByPrimaryKey((long) productId);
        Categories dbCategory = categoriesMapper.selectByPrimaryKey(dbProduct.getCategory_id());

        assertThat(dbProduct.getTitle()).isEqualTo(p.getTitle());
        assertThat((double) dbProduct.getPrice()).isEqualTo(p.getPrice());
        assertThat(dbCategory.getTitle()).isEqualTo(p.getCategoryTitle());
    }

    @SneakyThrows
    @Test
    void updateProductTest() {
        retrofit2.Response<Product> response =
                productService.createProduct(product)
                        .execute();
        productId = response.body().getId();
        assertThat(response.isSuccessful()).isTrue();

        product = product
                .withId(productId)
                .withTitle(faker.food().ingredient())
                .withPrice((int) (Math.random() * 1000 + 1));

        retrofit2.Response<Product> updatedProduct =
                productService.updateProduct(product)
                        .execute();

        assertThat(updatedProduct.code()).isEqualTo(ResponseCode.OK);
        assertThat(updatedProduct.isSuccessful()).isTrue();
        assertThat(updatedProduct.body()).isEqualTo(product);

        Products dbProduct = productsMapper.selectByPrimaryKey((long) productId);
        assertThat((long) dbProduct.getId()).isEqualTo((long) product.getId());
        assertThat(dbProduct.getTitle()).isEqualTo(product.getTitle());
        assertThat((double)dbProduct.getPrice()).isEqualTo((double)product.getPrice());
    }

    @SneakyThrows
    @Test
    void getProductTest() {
        retrofit2.Response<Product> response =
                productService.createProduct(product)
                        .execute();
        productId = response.body().getId();
        assertThat(response.code()).isGreaterThanOrEqualTo(ResponseCode.OK);
        assertThat(response.code()).isLessThanOrEqualTo(ResponseCode.OK_LAST);
        assertThat(response.isSuccessful()).isTrue();

        retrofit2.Response<Product> getProductResponse =
                productService.getProduct(productId)
                        .execute();
        assertThat(getProductResponse.code()).isGreaterThanOrEqualTo(ResponseCode.OK);
        assertThat(getProductResponse.code()).isLessThanOrEqualTo(ResponseCode.OK_LAST);
        assertThat(getProductResponse.isSuccessful()).isTrue();
        assertThat(getProductResponse.body().getTitle()).isEqualTo(product.getTitle());
        assertThat(getProductResponse.body().getPrice()).isEqualTo(product.getPrice());
        assertThat(getProductResponse.body().getCategoryTitle()).isEqualTo(product.getCategoryTitle());

        Products dbProduct = productsMapper.selectByPrimaryKey((long) productId);
        Categories dbCategory = categoriesMapper.selectByPrimaryKey(dbProduct.getCategory_id());

        assertThat(dbProduct.getTitle()).isEqualTo(getProductResponse.body().getTitle());
        assertThat((double) dbProduct.getPrice()).isEqualTo((double) getProductResponse.body().getPrice());
        assertThat(dbCategory.getTitle()).isEqualTo(getProductResponse.body().getCategoryTitle());
    }

    @SneakyThrows
    @Test
    void getProductWithNegativeIdTest() {
        int invalidId = -1;

        retrofit2.Response<Product> response =
                productService.getProduct(invalidId)
                        .execute();
        assertThat(response.code()).isEqualTo(ResponseCode.NOT_FOUND);
        assertThat(response.isSuccessful()).isFalse();
        if (response != null && !response.isSuccessful() && response.errorBody() != null) {
            ResponseBody body = response.errorBody();
            Converter<ResponseBody, ErrorBody> converter = RetrofitUtils.getRetrofit().responseBodyConverter(ErrorBody.class, new Annotation[0]);
            ErrorBody errorBody = converter.convert(body);
            assertThat(errorBody.getMessage()).isEqualTo("Unable to find product with id: " + invalidId);
        }

        Products dbProduct = productsMapper.selectByPrimaryKey((long) invalidId);
        assertThat(dbProduct).isNull();
    }

    @SneakyThrows
    @Test
    void getProductWithStringIdTest() {
        retrofit2.Response<Product> response =
                productService.getProduct("XXX")
                        .execute();
        assertThat(response.code()).isEqualTo(ResponseCode.BAD_REQUEST);
        assertThat(response.isSuccessful()).isFalse();
        if (response != null && !response.isSuccessful() && response.errorBody() != null) {
            ResponseBody body = response.errorBody();
            Converter<ResponseBody, ErrorBody> converter = RetrofitUtils.getRetrofit().responseBodyConverter(ErrorBody.class, new Annotation[0]);
            ErrorBody errorBody = converter.convert(body);
            assertThat(errorBody.getError()).isEqualTo("Bad Request");
        }
    }

    @SneakyThrows
    @Test
    void getProductWithVeryLongIdTest() {
        String longId = "999999999999999999999999999999999999";
        retrofit2.Response<Product> response =
                productService.getProduct(longId)
                        .execute();
        assertThat(response.code()).isEqualTo(ResponseCode.BAD_REQUEST);
        assertThat(response.isSuccessful()).isFalse();
        if (response != null && !response.isSuccessful() && response.errorBody() != null) {
            ResponseBody body = response.errorBody();
            Converter<ResponseBody, ErrorBody> converter = RetrofitUtils.getRetrofit().responseBodyConverter(ErrorBody.class, new Annotation[0]);
            ErrorBody errorBody = converter.convert(body);
            assertThat(errorBody.getError()).isEqualTo("Bad Request");
        }
    }



    @SneakyThrows
    @Test
    void deleteProductTest() {
        retrofit2.Response<Product> response =
                productService.createProduct(product)
                        .execute();
        productId = response.body().getId();
        assertThat(response.isSuccessful()).isTrue();

        retrofit2.Response<ResponseBody> deleteResponse =
                productService.deleteProduct(productId)
                        .execute();
        assertThat(deleteResponse.code()).isEqualTo(ResponseCode.OK);
        assertThat(deleteResponse.isSuccessful()).isTrue();

        Products dbProduct = productsMapper.selectByPrimaryKey((long) productId);
        assertThat(dbProduct).isNull();

        productId = null;
    }

    @SneakyThrows
    @Test
    void deleteUnknownProductTest() {
        int unknownId = -1;
        retrofit2.Response<ResponseBody> deleteResponse =
                productService.deleteProduct(unknownId)
                        .execute();
        assertThat(deleteResponse.code()).isEqualTo(ResponseCode.NOT_FOUND);
        assertThat(deleteResponse.isSuccessful()).isTrue();

        Products dbProduct = productsMapper.selectByPrimaryKey((long) unknownId);
        assertThat(dbProduct).isNull();
    }

    @SneakyThrows
    @Test
    void deleteAlreadyDeletedProductTest() {
        retrofit2.Response<Product> response =
                productService.createProduct(product)
                        .execute();
        productId = response.body().getId();
        assertThat(response.isSuccessful()).isTrue();

        Products dbProduct = productsMapper.selectByPrimaryKey((long) productId);
        assertThat(dbProduct).isNotNull();
        productsMapper.deleteByPrimaryKey(dbProduct.getId());

        retrofit2.Response<ResponseBody> deleteResponse2 =
                productService.deleteProduct(productId)
                        .execute();
        assertThat(deleteResponse2.code()).isEqualTo(ResponseCode.NOT_FOUND);
        assertThat(deleteResponse2.isSuccessful()).isTrue();

        productId = null;
    }


}
