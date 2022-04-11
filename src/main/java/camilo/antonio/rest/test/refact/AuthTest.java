package camilo.antonio.rest.test.refact;

import camilo.antonio.rest.core.BaseTest;
import io.restassured.RestAssured;
import io.restassured.specification.FilterableRequestSpecification;
import org.junit.Test;


import static io.restassured.RestAssured.given;

public class AuthTest extends BaseTest {



    @Test
    public void naoDeveAcessarAPISemToken(){
        FilterableRequestSpecification req = (FilterableRequestSpecification) RestAssured.requestSpecification;
        req.removeHeader("Authorization");
        given()
                .when()
                .get("/contas")
                .then()
                .statusCode(401)
        ;
    }




}