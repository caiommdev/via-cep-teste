package org.example.test;

import org.example.model.ViaCepResponse;
import org.example.service.ViaCepService;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ViaCepIntegrationTest {

    private static ViaCepService viaCepService;

    @BeforeAll
    static void setUp() {
        viaCepService = new ViaCepService();
        viaCepService.setUseMock(true);
        System.out.println("Testes executando com dados mockados devido à indisponibilidade da API ViaCEP");
    }

    @AfterAll
    static void tearDown() {
        if (viaCepService != null) {
            viaCepService.close();
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {"01310100", "22071900", "30112000"})
    @Order(1)
    @DisplayName("Teste CEPs Válidos - Validação Completa")
    void testCepsValidosValidacaoCompleta(String cep) throws Exception {
        ViaCepResponse response = viaCepService.consultarCep(cep);

        assertNotNull(response);
        assertFalse(response.isErro());
        assertNotNull(response.getCep());
        assertNotNull(response.getLogradouro());
        assertNotNull(response.getLocalidade());
        assertNotNull(response.getUf());
        assertEquals(2, response.getUf().length());
    }

    @ParameterizedTest
    @ValueSource(strings = {"abcd1234", "1234567", "123456789", "12345@78"})
    @Order(2)
    @DisplayName("Teste CEPs Inválidos - Validação de Erro")
    void testCepsInvalidosValidacaoErro(String cepInvalido) throws Exception {
        ViaCepResponse response = viaCepService.consultarCep(cepInvalido);

        assertNotNull(response);
        assertTrue(response.isErro());
    }

    @Test
    @Order(3)
    @DisplayName("Teste Consulta por Endereço Válido")
    void testConsultaPorEnderecoValido() throws Exception {
        ViaCepResponse[] responses = viaCepService.consultarEndereco("SP", "São Paulo", "Avenida Paulista");

        assertNotNull(responses);
        assertTrue(responses.length > 0);
        assertEquals("SP", responses[0].getUf());
        assertTrue(responses[0].getLocalidade().contains("São Paulo"));
    }

    @Test
    @Order(4)
    @DisplayName("Teste Consulta por Endereço Inválido")
    void testConsultaPorEnderecoInvalido() throws Exception {
        ViaCepResponse[] responses = viaCepService.consultarEndereco("XX", "CidadeInexistente", "RuaInexistente");

        assertNotNull(responses);
        assertEquals(0, responses.length);
    }

    @Test
    @Order(5)
    @DisplayName("Teste Acentuação em Nomes")
    void testAcentuacaoNomes() throws Exception {
        ViaCepResponse[] responsesComAcento = viaCepService.consultarEndereco("SP", "São Paulo", "Avenida Paulista");
        ViaCepResponse[] responsesSemAcento = viaCepService.consultarEndereco("SP", "Sao Paulo", "Avenida Paulista");

        assertNotNull(responsesComAcento);
        assertNotNull(responsesSemAcento);
        assertTrue(responsesComAcento.length > 0 || responsesSemAcento.length > 0);
    }

    @Test
    @Order(6)
    @DisplayName("Teste Performance com Mock")
    void testPerformanceComMock() throws Exception {
        long startTime = System.currentTimeMillis();

        for (int i = 0; i < 10; i++) {
            viaCepService.consultarCep("01310100");
        }
        
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        
        System.out.println("Tempo total para 10 consultas mockadas: " + duration + "ms");
        assertTrue(duration < 1000, "10 consultas mockadas devem ser muito rápidas");
    }

    @Test
    @Order(7)
    @DisplayName("Teste Estrutura de Dados")
    void testEstruturaDados() throws Exception {
        ViaCepResponse response = viaCepService.consultarCep("01310100");

        assertNotNull(response);
        assertFalse(response.isErro());
        assertNotNull(response.getCep());
        assertNotNull(response.getLogradouro());
        assertNotNull(response.getBairro());
        assertNotNull(response.getLocalidade());
        assertNotNull(response.getUf());
        assertNotNull(response.getDdd());
    }

    @Test
    @Order(8)
    @DisplayName("Teste Strings Vazias")
    void testStringsVazias() {
        assertThrows(IOException.class, () -> {
            viaCepService.consultarEndereco("", "São Paulo", "Avenida Paulista");
        });

        assertThrows(IOException.class, () -> {
            viaCepService.consultarEndereco("SP", "", "Avenida Paulista");
        });

        assertThrows(IOException.class, () -> {
            viaCepService.consultarEndereco("SP", "São Paulo", "");
        });
    }

    @Test
    @Order(9)
    @DisplayName("Teste Status do Mock")
    void testStatusMock() {
        assertTrue(viaCepService.isUsingMock());
        System.out.println("Confirmado: testes executando com dados mockados");
    }

    @Test
    @Order(10)
    @DisplayName("Teste CEP com Formatação")
    void testCepComFormatacao() throws Exception {
        ViaCepResponse response1 = viaCepService.consultarCep("01310100");
        ViaCepResponse response2 = viaCepService.consultarCep("01310-100");

        assertNotNull(response1);
        assertNotNull(response2);
        assertFalse(response1.isErro());
        assertFalse(response2.isErro());
        assertEquals(response1.getLogradouro(), response2.getLogradouro());
    }
}
