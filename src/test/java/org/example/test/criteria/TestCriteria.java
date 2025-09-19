package org.example.test.criteria;

public class TestCriteria {
    
    public static final String[] VALID_CEPS = {
        "01310-100",
        "22071900",
        "30112000",
        "00000000",
        "99999999"
    };
    
    public static final String[] INVALID_CEPS = {
        "1234567",
        "123456789",
        "abcd1234",
        "12345-abc",
        "",
        "   ",
        "12.345.678",
        "12345/6789",
        "00000001",
    };
    
    public static final String[] VALID_UFS = {
        "SP", "RJ", "MG", "RS", "PR", "SC", "BA", "GO", "PE", "CE"
    };
    
    public static final String[] INVALID_UFS = {
        "XX", "YY", "123", "", "sp", "ABC", "Z"
    };
    
    public static final String[] VALID_CITIES = {
        "São Paulo", "Sao Paulo", "Rio de Janeiro", "Belo Horizonte", 
        "Porto Alegre", "Curitiba", "Salvador", "Brasília"
    };
    
    public static final String[] INVALID_CITIES = {
        "CidadeInexistente123", "", "São@Paulo", "Cidade#Especial"
    };
    
    public static final String[] VALID_STREETS = {
        "Avenida Paulista", "Rua Augusta", "Copacabana", "Rua da Bahia",
        "Avenida Ipiranga", "Rua das Flores"
    };
    
    public static final String[] INVALID_STREETS = {
        "RuaInexistente9999", "", "Rua@Especial", "Logradouro#Inválido"
    };
}
