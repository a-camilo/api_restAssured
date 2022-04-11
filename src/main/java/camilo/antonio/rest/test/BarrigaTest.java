package camilo.antonio.rest.test;

import camilo.antonio.rest.core.BaseTest;
import camilo.antonio.utils.DateUtils;
import io.restassured.RestAssured;
import io.restassured.specification.FilterableRequestSpecification;
import org.junit.jupiter.api.*;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;


@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class BarrigaTest extends BaseTest {

    private static final String CONTA_NAME = "conta " + System.nanoTime();
    private static Integer CONTA_ID;
    private static Integer MOV_ID;

    @BeforeAll
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
    }

    @Test
    @Order(2)
    public void deveIncluirContaComSucesso() {
        CONTA_ID = given()
                .body("{\"nome\": \"" + CONTA_NAME + "\"}")
                .when()
                .post("/contas")
                .then()
                .statusCode(201)
                .extract().path("id")
        ;
    }

    @Test
    @Order(3)
    public void deveAlterarContaComSucesso() {
        given()
                .body("{\"nome\": \"" + CONTA_NAME + " alterada\"}")
                .pathParam("id", CONTA_ID)
                .when()
                .put("/contas/{id}")
                .then()
                .statusCode(200)
                .body("nome", is("" + CONTA_NAME + " alterada"))
        ;
    }

    @Test
    @Order(4)
    public void naoDeveInserirContaNoMesmoNome() {
        given()
                .body("{\"nome\": \"" + CONTA_NAME + " alterada\"}")
                .when()
                .post("/contas")
                .then()
                .statusCode(400)
                .body("error", is("Já existe uma conta com esse nome!"))
        ;
    }

    @Test
    @Order(5)
    public void deveInserirMovimentacaoComSucesso() {
        Movimentacao mov = getMovimentacaoValida();
        MOV_ID = given()
                .body(mov)
                .when()
                .post("/transacoes")
                .then()
                .statusCode(201)
                .extract().path("id")
        ;
    }

    @Test
    @Order(6)
    public void deveValidarCamposObrigatorioValidacao() {
        given()
                .body("{}")
                .when()
                .post("/transacoes")
                .then()
                .statusCode(400)
                .body("$", hasSize(8))
                .body("msg", hasItems(
                        "Data da Movimentação é obrigatório",
                        "Data do pagamento é obrigatório",
                        "Descrição é obrigatório",
                        "Interessado é obrigatório",
                        "Valor é obrigatório",
                        "Valor deve ser um número",
                        "Conta é obrigatório",
                        "Situação é obrigatório"
                ))
        ;
    }

    @Test
    @Order(7)
    public void naoDeveInserirMovimentacaoComDataFutura() {
        Movimentacao mov = getMovimentacaoValida();
        mov.setData_transacao(DateUtils.getDataDiferencaDias(2));
        given()
                .body(mov)
                .when()
                .post("/transacoes")
                .then()
                .statusCode(400)
                .body("$", hasSize(1))
                .body("msg", hasItem("Data da Movimentação deve ser menor ou igual à data atual"))
        ;
    }

    @Test
    @Order(8)
    public void naoDeveRemoverContaMovimentacao() {
        given()
                .pathParam("id", CONTA_ID)
                .when()
                .delete("/contas/{id}")
                .then()
                .statusCode(500)
                .body("constraint", is("transacoes_conta_id_foreign"))
        ;
    }

    @Test
    @Order(9)
    public void deveCalcularSaldoContas() {
        given()
                .when()
                .get("/saldo")
                .then()
                .statusCode(200)
                .body("find{it.conta_id == " + CONTA_ID + "}.saldo", is("100.00"))
        ;
    }

    @Test
    @Order(10)
    public void deveRemoverMovimentacao() {
        given()
                .pathParam("id", MOV_ID)
                .when()
                .delete("/transacoes/{id}")
                .then()
                .statusCode(204)
        ;
    }

    @Test
    @Order(11)
    public void naoAcessarApiSemToken() {
        FilterableRequestSpecification req = (FilterableRequestSpecification) RestAssured.requestSpecification;
        req.removeHeader("Authorization");
        given()
                .when()
                .get("/contas")
                .then()
                .statusCode(401)
        ;
    }

    private Movimentacao getMovimentacaoValida() {
        Movimentacao mov = new Movimentacao();
        mov.setConta_id(CONTA_ID);
        mov.setDescricao("Descrevendo a movimentacao");
        mov.setEnvolvido("Envolvendo a movimentacao");
        mov.setTipo("REC");
        mov.setData_transacao(DateUtils.getDataDiferencaDias(-10));
        mov.setData_pagamento(DateUtils.getDataDiferencaDias(1));
        mov.setValor(100f);
        mov.setStatus(true);
        return mov;
    }

}

