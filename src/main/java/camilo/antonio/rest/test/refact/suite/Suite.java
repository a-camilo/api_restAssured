package camilo.antonio.rest.test.refact.suite;


import camilo.antonio.rest.core.BaseTest;
import camilo.antonio.rest.test.refact.AuthTest;
import camilo.antonio.rest.test.refact.ContaTest;
import camilo.antonio.rest.test.refact.MovimentacaoTest;
import camilo.antonio.rest.test.refact.SaldoTest;
import io.restassured.RestAssured;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite.SuiteClasses;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;

@RunWith(org.junit.runners.Suite.class)
@SuiteClasses({
        ContaTest.class,
        MovimentacaoTest.class,
        SaldoTest.class,
        AuthTest.class
})
public class Suite extends BaseTest {

    @BeforeClass
    public static void login() {
        Map<String, String> login = new HashMap<>();
        login.put("email", "antonio@email.com");
        login.put("senha", "antonio");

        String TOKEN = given()
                .body(login)
                .when()
                .post("/signin")
                .then()
                .statusCode(200)
                .extract().path("token");
        RestAssured.requestSpecification.header("Authorization", "JWT " + TOKEN);
        RestAssured.get("/reset").then().statusCode(200);
    }
}
