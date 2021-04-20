package geekbrains;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import retrofit2.Response;
import ru.geekbrains.dto.Category;
import ru.geekbrains.service.CategoryService;
import ru.geekbrains.util.RetrofitUtils;

import java.io.IOException;
import java.net.MalformedURLException;

import static org.assertj.core.api.Assertions.assertThat;
import static ru.geekbrains.base.enums.CategoryType.FOOD;

public class CategoryTests {
   static CategoryService categoryService;

    @BeforeAll
    static void beforeAll() throws MalformedURLException {
        categoryService = RetrofitUtils.getRetrofit().create(CategoryService.class);
    }

    @Test
    void getFoodCategoryPositiveTest() throws IOException {
        Response<Category> response = categoryService
                .getCategory(FOOD.getId())
                .execute();
        assertThat(response.isSuccessful()).isTrue();
        assertThat(response.body().getId()).as("Id is not equal to 1!").isEqualTo(1);
        assertThat(response.body().getTitle()).isEqualTo(FOOD.getTitle());
    }

    @Test
    void negativeGetWithZeroIdTest() throws IOException {
        Response<Category> response = categoryService
                .getCategory(0)
                .execute();
        assertThat(response.code()).as("Expect code 404").isEqualTo(404);
        assertThat(response.body()).isNull();
        assertThat(response.errorBody()).isNotNull();
    }

    @Test
    void negativeGetWithNegativeIdTest() throws IOException {
        Response<Category> response = categoryService
                .getCategory(-1)
                .execute();
        assertThat(response.code()).as("Expect code 404").isEqualTo(404);
        assertThat(response.body()).isNull();
        assertThat(response.errorBody()).isNotNull();
    }


}
