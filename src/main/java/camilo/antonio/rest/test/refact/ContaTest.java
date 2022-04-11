package camilo.antonio.rest.test.refact;

import camilo.antonio.rest.core.BaseTest;
import io.restassured.RestAssured;
import org.junit.Test;


import static org.hamcrest.Matchers.is;

public class ContaTest extends BaseTest {

   @Test
    public void deveIncluirContaComSucesso() {
        RestAssured.given()
                .body("{\"nome\":\"Conta inserida\"}")
                .when()
                .post("/contas")
                .then()
                .statusCode(201)
        ;
    }

    @Test
    public void deveAlterarContaComSucesso() {
        Integer CONTA_ID = getIdContaPeloNome("Conta para alterar");

        RestAssured.given()
                .body("{\"nome\":\"Conta alterada\"}")
                .pathParam("id", CONTA_ID)
                .when()
                .put("/contas/{id}")
                .then()
                .statusCode(200)
                .body("nome", is("Conta alterada"))
        ;
    }

    private Integer getIdContaPeloNome(String nome) {
        return RestAssured.get("/contas?=nome" + nome).then().extract().path("id[0]");

    }

    @Test
    public void naoDeveInserirContaMesmoNome() {
        RestAssured.given()
                .body("{\"nome\":\"Conta mesmo nome\"}")
                .when()
                .post("/contas")
                .then()
                .statusCode(400)
                .body("error", is("Já existe uma conta com esse nome!"))
        ;
    }


}
