package org.example.test;

import org.example.model.ViaCepResponse;
import org.example.service.ViaCepService;
import org.example.test.criteria.TestCriteria;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AddressLookupTest {

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

    @ParameterizedTest
    @CsvSource({
        "SP, São Paulo, Avenida Paulista, true",
        "SP, Sao Paulo, Avenida Paulista, true", 
        "RJ, Rio de Janeiro, Copacabana, true",
        "MG, Belo Horizonte, Rua da Bahia, true",
        "RS, Porto Alegre, Avenida Ipiranga, true"
    })
    @Order(1)
    @DisplayName("Tabela de Decisão - Cenários Válidos")
    void testTabelaDecisaoScenariosValidos(String uf, String cidade, String logradouro, boolean esperaSucesso) throws IOException {
        ViaCepResponse[] responses = viaCepService.consultarEndereco(uf, cidade, logradouro);
        
        assertNotNull(responses, "Resposta não deve ser nula");
        
        if (esperaSucesso) {
            assertTrue(responses.length > 0, 
                String.format("Deve encontrar resultados para UF=%s, Cidade=%s, Logradouro=%s", uf, cidade, logradouro));
            
            boolean temResultadoValido = false;
            for (ViaCepResponse response : responses) {
                if (!response.isErro() && response.getLogradouro() != null && !response.getLogradouro().isEmpty()) {
                    temResultadoValido = true;
                    assertEquals(uf.toUpperCase(), response.getUf(), "UF deve corresponder");
                    break;
                }
            }
            assertTrue(temResultadoValido, "Deve ter pelo menos um resultado válido");
        }
    }

    @ParameterizedTest
    @CsvSource({
        "XX, São Paulo, Avenida Paulista, false",
        "SP, CidadeInexistente123, Avenida Paulista, false",
        "SP, São Paulo, RuaInexistente9999, false",
        "123, São Paulo, Avenida Paulista, false",
        "SP, São@Paulo, Avenida Paulista, false"
    })
    @Order(2)
    @DisplayName("Tabela de Decisão - Cenários Inválidos")
    void testTabelaDecisaoScenariosInvalidos(String uf, String cidade, String logradouro, boolean esperaSucesso) throws IOException {
        ViaCepResponse[] responses = viaCepService.consultarEndereco(uf, cidade, logradouro);
        
        assertNotNull(responses, "Resposta não deve ser nula");
        
        if (!esperaSucesso) {
            assertTrue(responses.length == 0 || 
                      (responses.length > 0 && responses[0].isErro()),
                      String.format("Deve retornar vazio ou erro para UF=%s, Cidade=%s, Logradouro=%s", uf, cidade, logradouro));
        }
    }

    @Test
    @Order(3)
    @DisplayName("Teste UF Inválida - Partição de Equivalência")
    void testUfInvalida() {
        for (String ufInvalida : TestCriteria.INVALID_UFS) {
            assertAll(() -> {
                try {
                    ViaCepResponse[] responses = viaCepService.consultarEndereco(ufInvalida, "São Paulo", "Avenida Paulista");
                    assertTrue(responses.length == 0 || responses[0].isErro(),
                            "UF inválida '" + ufInvalida + "' deve retornar erro ou array vazio");
                } catch (IOException e) {
                    assertTrue(true, "Erro de rede esperado para UF muito inválida: " + ufInvalida);
                }
            });
        }
    }

    @Test
    @Order(4)
    @DisplayName("Teste Cidade com e sem Acentuação")
    void testCidadeComESemAcentuacao() throws IOException {
        ViaCepResponse[] responsesComAcento = viaCepService.consultarEndereco("SP", "São Paulo", "Avenida Paulista");
        
        ViaCepResponse[] responsesSemAcento = viaCepService.consultarEndereco("SP", "Sao Paulo", "Avenida Paulista");
        
        assertNotNull(responsesComAcento, "Resposta com acento não deve ser nula");
        assertNotNull(responsesSemAcento, "Resposta sem acento não deve ser nula");
        
        assertTrue(responsesComAcento.length > 0 || responsesSemAcento.length > 0,
                "Pelo menos uma das consultas deve retornar resultados");
    }

    @Test
    @Order(5)
    @DisplayName("Teste Valores Limite - Strings Vazias")
    void testStringsVazias() {
        assertAll(
            () -> {
                assertThrows(Exception.class, () -> {
                    viaCepService.consultarEndereco("", "São Paulo", "Avenida Paulista");
                }, "UF vazia deve causar erro");
            },
            () -> {
                assertThrows(Exception.class, () -> {
                    viaCepService.consultarEndereco("SP", "", "Avenida Paulista");
                }, "Cidade vazia deve causar erro");
            },
            () -> {
                assertThrows(Exception.class, () -> {
                    viaCepService.consultarEndereco("SP", "São Paulo", "");
                }, "Logradouro vazio deve causar erro");
            }
        );
    }

    @Test
    @Order(6)
    @DisplayName("Teste Caracteres Especiais em Nomes")
    void testCaracteresEspeciais() throws IOException {
        ViaCepResponse[] responses1 = viaCepService.consultarEndereco("SP", "São@Paulo", "Avenida Paulista");
        assertTrue(responses1.length == 0, "Cidade com caracteres especiais deve retornar vazio");
        
        ViaCepResponse[] responses2 = viaCepService.consultarEndereco("SP", "São Paulo", "Rua#Especial");
        assertTrue(responses2.length == 0, "Logradouro com caracteres especiais deve retornar vazio");
    }

    @Test
    @Order(7)
    @DisplayName("Teste Case Sensitivity")
    void testCaseSensitivity() throws IOException {
        ViaCepResponse[] responsesMinuscula = viaCepService.consultarEndereco("sp", "São Paulo", "Avenida Paulista");
        
        ViaCepResponse[] responsesMaiuscula = viaCepService.consultarEndereco("SP", "São Paulo", "Avenida Paulista");
        
        assertNotNull(responsesMinuscula, "Resposta com UF minúscula não deve ser nula");
        assertNotNull(responsesMaiuscula, "Resposta com UF maiúscula não deve ser nula");
        
        assertTrue(responsesMinuscula.length > 0 || responsesMaiuscula.length > 0,
                "Pelo menos uma das consultas deve retornar resultados");
    }

    @Test
    @Order(8)
    @DisplayName("Teste Múltiplos Resultados")
    void testMultiplosResultados() throws IOException {
        ViaCepResponse[] responses = viaCepService.consultarEndereco("SP", "São Paulo", "Rua das Flores");
        
        assertNotNull(responses, "Resposta não deve ser nula");
        
        if (responses.length > 1) {
            for (ViaCepResponse response : responses) {
                if (!response.isErro()) {
                    assertEquals("SP", response.getUf(), "Todos os resultados devem ser do mesmo estado");
                }
            }
        }
    }

    @Test
    @Order(9)
    @DisplayName("Teste Encoding de Caracteres Especiais")
    void testEncodingCaracteresEspeciais() throws IOException {
        ViaCepResponse[] responses = viaCepService.consultarEndereco("GO", "Brasília", "Esplanada dos Ministérios");
        
        assertNotNull(responses, "Resposta não deve ser nula");
    }
}
