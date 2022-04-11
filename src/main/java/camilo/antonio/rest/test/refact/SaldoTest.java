package camilo.antonio.rest.test.refact;

import camilo.antonio.rest.core.BaseTest;
import camilo.antonio.utils.BarrigaUtils;
import io.restassured.RestAssured;
import org.junit.Test;

import static org.hamcrest.Matchers.is;

public class SaldoTest extends BaseTest {

    @Test
    public void deveCalcularSaldoDeContas() {
        Integer CONTA_ID = BarrigaUtils.getIdContaPeloNome("Conta para saldo");
        RestAssured.given()
                .when()
                .get("/saldo")
                .then()
                .statusCode(200)
                .body("find{it.conta_id == " + CONTA_ID + "}.saldo", is("534.00"))
        ;
    }
}
