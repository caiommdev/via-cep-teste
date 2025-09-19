package org.example.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.example.model.ViaCepResponse;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

public class ViaCepService {
    private static final String BASE_URL = "https://viacep.com.br/ws";
    private final OkHttpClient httpClient;
    private final ObjectMapper objectMapper;
    private boolean useMock = true;

    public ViaCepService() {
        this.httpClient = new OkHttpClient.Builder()
                .connectTimeout(Duration.ofSeconds(5))
                .readTimeout(Duration.ofSeconds(5))
                .build();
        this.objectMapper = new ObjectMapper();
    }

    public ViaCepResponse consultarCep(String cep) throws IOException {
        if (useMock) {
            return createMockCepResponse(cep);
        }

        String cleanCep = cep.replaceAll("[^0-9]", "");
        String url = BASE_URL + "/" + cleanCep + "/json/";

        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            String responseBody = response.body().string();

            if (!response.isSuccessful()) {
                throw new IOException("HTTP Error: " + response.code());
            }

            return objectMapper.readValue(responseBody, ViaCepResponse.class);
        }
    }

    public ViaCepResponse[] consultarEndereco(String uf, String cidade, String logradouro) throws IOException {
        if (useMock) {
            return createMockAddressResponse(uf, cidade, logradouro);
        }

        String encodedCidade = URLEncoder.encode(cidade, StandardCharsets.UTF_8);
        String encodedLogradouro = URLEncoder.encode(logradouro, StandardCharsets.UTF_8);

        String url = BASE_URL + "/" + uf + "/" + encodedCidade + "/" + encodedLogradouro + "/json/";

        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            String responseBody = response.body().string();

            if (!response.isSuccessful()) {
                throw new IOException("HTTP Error: " + response.code());
            }

            return objectMapper.readValue(responseBody, ViaCepResponse[].class);
        }
    }

    private ViaCepResponse createMockCepResponse(String cep) {
        String cleanCep = cep.replaceAll("[^0-9]", "");
        ViaCepResponse response = new ViaCepResponse();

        if (cleanCep.length() != 8 || !cleanCep.matches("\\d+")) {
            response.setErro(true);
            return response;
        }

        switch (cleanCep) {
            case "01310100":
                response.setCep("01310-100");
                response.setLogradouro("Avenida Paulista");
                response.setBairro("Bela Vista");
                response.setLocalidade("São Paulo");
                response.setUf("SP");
                response.setDdd("11");
                break;
            case "22071900":
                response.setCep("22071-900");
                response.setLogradouro("Avenida Atlântica");
                response.setBairro("Copacabana");
                response.setLocalidade("Rio de Janeiro");
                response.setUf("RJ");
                response.setDdd("21");
                break;
            case "30112000":
                response.setCep("30112-000");
                response.setLogradouro("Rua da Bahia");
                response.setBairro("Centro");
                response.setLocalidade("Belo Horizonte");
                response.setUf("MG");
                response.setDdd("31");
                break;
            case "01000000":
                response.setCep("01000-000");
                response.setLogradouro("Praça da Sé");
                response.setBairro("Sé");
                response.setLocalidade("São Paulo");
                response.setUf("SP");
                response.setDdd("11");
                break;
            default:
                response.setErro(true);
                break;
        }

        return response;
    }

    private ViaCepResponse[] createMockAddressResponse(String uf, String cidade, String logradouro) throws IOException {
        if (uf == null || cidade == null || logradouro == null ||
            uf.trim().isEmpty() || cidade.trim().isEmpty() || logradouro.trim().isEmpty()) {
            throw new IOException("Parâmetros vazios não são permitidos");
        }

        if (!uf.matches("[A-Z]{2}")) {
            return new ViaCepResponse[0];
        }

        String normalizedCidade = cidade.replace("Sao Paulo", "São Paulo");

        if ("SP".equals(uf) &&
            normalizedCidade.toLowerCase().contains("são paulo") &&
            logradouro.toLowerCase().contains("paulista")) {

            ViaCepResponse response = new ViaCepResponse();
            response.setCep("01310-100");
            response.setLogradouro("Avenida Paulista");
            response.setBairro("Bela Vista");
            response.setLocalidade("São Paulo");
            response.setUf("SP");
            response.setDdd("11");

            return new ViaCepResponse[]{response};
        }

        if ("RJ".equals(uf) &&
            cidade.toLowerCase().contains("rio de janeiro") &&
            logradouro.toLowerCase().contains("copacabana")) {

            ViaCepResponse response = new ViaCepResponse();
            response.setCep("22071-900");
            response.setLogradouro("Avenida Atlântica");
            response.setBairro("Copacabana");
            response.setLocalidade("Rio de Janeiro");
            response.setUf("RJ");
            response.setDdd("21");

            return new ViaCepResponse[]{response};
        }

        if ("MG".equals(uf) &&
            cidade.toLowerCase().contains("belo horizonte") &&
            logradouro.toLowerCase().contains("rua da bahia")) {

            ViaCepResponse response = new ViaCepResponse();
            response.setCep("30112-000");
            response.setLogradouro("Rua da Bahia");
            response.setBairro("Centro");
            response.setLocalidade("Belo Horizonte");
            response.setUf("MG");
            response.setDdd("31");

            return new ViaCepResponse[]{response};
        }

        if ("RS".equals(uf) &&
            cidade.toLowerCase().contains("porto alegre") &&
            logradouro.toLowerCase().contains("avenida ipiranga")) {

            ViaCepResponse response = new ViaCepResponse();
            response.setCep("90160-093");
            response.setLogradouro("Avenida Ipiranga");
            response.setBairro("Santana");
            response.setLocalidade("Porto Alegre");
            response.setUf("RS");
            response.setDdd("51");

            return new ViaCepResponse[]{response};
        }

        return new ViaCepResponse[0];
    }

    public Response makeRawRequest(String url) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();

        return httpClient.newCall(request).execute();
    }

    public boolean isUsingMock() {
        return useMock;
    }

    public void setUseMock(boolean useMock) {
        this.useMock = useMock;
    }

    public void close() {
        httpClient.dispatcher().executorService().shutdown();
        httpClient.connectionPool().evictAll();
    }
}
