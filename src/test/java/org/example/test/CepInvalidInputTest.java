package org.example.test;

import org.example.model.ViaCepResponse;
import org.example.service.ViaCepService;
import org.example.test.criteria.TestCriteria;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class CepInvalidInputTest {

    private static ViaCepService viaCepService;

    @BeforeAll
    static void setUp() {
        viaCepService = new ViaCepService();
    }

    @AfterAll
    static void tearDown() {
        if (viaCepService != null) {
            viaCepService.close();
        }
    }

    @Test
    @Order(1)
    @DisplayName("Teste CEP com letras - Partição Inválida")
    void testCepComLetras() {
        assertAll(
            () -> {
                ViaCepResponse response = viaCepService.consultarCep("abcd1234");
                assertTrue(response.isErro(), "CEP com letras deve retornar erro");
            },
            () -> {
                ViaCepResponse response = viaCepService.consultarCep("12345abc");
                assertTrue(response.isErro(), "CEP com letras no final deve retornar erro");
            },
            () -> {
                ViaCepResponse response = viaCepService.consultarCep("ab12cd34");
                assertTrue(response.isErro(), "CEP com letras intercaladas deve retornar erro");
            }
        );
    }

    @Test
    @Order(2)
    @DisplayName("Teste CEP com tamanho incorreto - Valor Limite")
    void testCepTamanhoIncorreto() {
        assertAll(
            () -> {
                ViaCepResponse response = viaCepService.consultarCep("1234567");
                assertTrue(response.isErro(), "CEP com 7 dígitos deve retornar erro");
            },
            () -> {
                ViaCepResponse response = viaCepService.consultarCep("123456789");
                assertTrue(response.isErro(), "CEP com 9 dígitos deve retornar erro");
            },
            () -> {
                ViaCepResponse response = viaCepService.consultarCep("1");
                assertTrue(response.isErro(), "CEP com 1 dígito deve retornar erro");
            },
            () -> {
                ViaCepResponse response = viaCepService.consultarCep("12345678901234567890");
                assertTrue(response.isErro(), "CEP muito longo deve retornar erro");
            }
        );
    }

    @Test
    @Order(3)
    @DisplayName("Teste CEP vazio ou nulo")
    void testCepVazioOuNulo() {
        assertAll(
            () -> {
                ViaCepResponse response = viaCepService.consultarCep("");
                assertTrue(response.isErro(), "CEP vazio deve retornar erro");
            },
            () -> {
                ViaCepResponse response = viaCepService.consultarCep("   ");
                assertTrue(response.isErro(), "CEP com apenas espaços deve retornar erro");
            },
            () -> {
                assertThrows(Exception.class, () -> {
                    viaCepService.consultarCep(null);
                }, "CEP nulo deve lançar exceção");
            }
        );
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "12.345.678", "12345-abc", "12345/6789", "12345@678", 
        "12345 678", "abc-defg", "########", "12345ABC"
    })
    @Order(4)
    @DisplayName("Teste CEPs com caracteres especiais - Partição Inválida")
    void testCepComCaracteresEspeciais(String cepInvalido) throws IOException {
        ViaCepResponse response = viaCepService.consultarCep(cepInvalido);
        assertTrue(response.isErro(), 
            "CEP '" + cepInvalido + "' deve retornar erro por conter caracteres inválidos");
    }

    @Test
    @Order(5)
    @DisplayName("Teste CEPs inexistentes mas numericamente válidos")
    void testCepInexistente() {
        assertAll(
            () -> {
                ViaCepResponse response = viaCepService.consultarCep("99999999");
                assertTrue(response.isErro() ||
                          (response.getLogradouro() == null || response.getLogradouro().isEmpty()),
                          "CEP inexistente deve retornar erro ou dados vazios");
            },
            () -> {
                ViaCepResponse response = viaCepService.consultarCep("00000001");
                assertTrue(response.isErro() || 
                          (response.getLogradouro() == null || response.getLogradouro().isEmpty()),
                          "CEP inexistente deve retornar erro ou dados vazios");
            }
        );
    }

    @Test
    @Order(6)
    @DisplayName("Teste valores limite válidos para CEP")
    void testValoresLimiteValidos() throws IOException {
        ViaCepResponse response1 = viaCepService.consultarCep("01000000");
        assertFalse(response1.isErro(), "CEP 01000000 deve ser válido");
        
        ViaCepResponse response2 = viaCepService.consultarCep("01310-100");
        assertFalse(response2.isErro(), "CEP formatado deve ser aceito");
        assertNotNull(response2.getLogradouro(), "Deve retornar logradouro válido");
    }

    @Test
    @Order(7)
    @DisplayName("Teste de performance com CEPs inválidos")
    void testPerformanceCepsInvalidos() {
        long startTime = System.currentTimeMillis();
        
        for (String cepInvalido : TestCriteria.INVALID_CEPS) {
            try {
                ViaCepResponse response = viaCepService.consultarCep(cepInvalido);
                assertTrue(response.isErro() || 
                          (response.getLogradouro() == null || response.getLogradouro().isEmpty()),
                          "CEP inválido: " + cepInvalido);
            } catch (IOException e) {
            }
        }
        
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        
        assertTrue(duration < 30000,
            "Teste de performance não deve demorar mais que 30 segundos");
        
        System.out.println("Tempo total para testar CEPs inválidos: " + duration + "ms");
    }
}
