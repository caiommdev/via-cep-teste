# Relatório de Testes - API ViaCEP

## Resumo Executivo
**Testes realizados na API ViaCEP** (https://viacep.com.br)

**⚠️ IMPORTANTE: Durante a execução dos testes, a API ViaCEP estava caindo com fequencia retornando erro 404. Para garantir 
a continuidade dos testes, foi implementado um sistema de mock simples com respostas pré-definidas assim como o combinado 
com o professor Leo.**

## 🔧 Solução de Mock Implementada

### Solução Simples Implementada
Agora podendo utilizar respostas mockadas, com uma flag de controle por padrão ativa.
Simulei mesmo comportamento de erro para entradas inválidas

### CEPs Mockados
- **01310100**: Avenida Paulista, Bela Vista, São Paulo/SP
- **22071900**: Avenida Atlântica, Copacabana, Rio de Janeiro/RJ  
- **30112000**: Rua da Bahia, Centro, Belo Horizonte/MG
- **Inválidos**: Qualquer outro CEP retorna `{"erro": true}`

## 1. Técnicas de Teste Aplicadas

### 1.1 Partição de Equivalência

**Para CEP:**
- **Classe Válida**: CEPs com 8 dígitos numéricos (01310100, 22071900, 30112000)
- **Classe Inválida**: CEPs com letras, tamanho incorreto, vazios ou caracteres especiais

**Para UF:**
- **Classe Válida**: SP (único estado mockado)
- **Classe Inválida**: Qualquer outro código

**Para Consulta por Endereço:**
- **Classe Válida**: SP + São Paulo + Avenida Paulista
- **Classe Inválida**: Qualquer outra combinação

### 1.2 Análise de Valor Limite

**Limites para CEP:**
- Válidos: 8 dígitos exatos (01310100, 22071900, 30112000)
- Inválidos: 7 dígitos (1234567), 9 dígitos (123456789)

### 1.3 Tabela de Decisão

| Cenário | UF | Cidade | Logradouro | Resultado Mock |
|---------|----|---------|-----------|--------------------|
| 1 | SP | São Paulo | Avenida Paulista | ✅ Retorna dados |
| 2 | SP | Sao Paulo | Avenida Paulista | ✅ Normaliza acentos |
| 3 | XX | São Paulo | Avenida Paulista | ❌ Array vazio |
| 4 | SP | CidadeInexistente | Avenida Paulista | ❌ Array vazio |
| 5 | SP | São Paulo | RuaInexistente | ❌ Array vazio |
| 6 |    | São Paulo | Avenida Paulista | ❌ Array vazio |

## 2. Estrutura de Testes Implementada

### 2.1 CepInvalidInputTest
- **Status**: ✅ Funcional com mock
- **Cobertura**: Testa CEPs inválidos que retornam erro

### 2.2 AddressLookupTest  
- **Status**: ✅ Funcional com mock
- **Cobertura**: Testa consultas por endereço

### 2.3 ViaCepIntegrationTest
- **Status**: ✅ Simplificado para mock direto
- **Métodos**: 10 testes cobrindo funcionalidades principais

## 3. Casos de Teste com Mock

### 3.1 CEPs Válidos Mockados
```
01310100 → Avenida Paulista, Bela Vista, São Paulo/SP
22071900 → Avenida Atlântica, Copacabana, Rio de Janeiro/RJ
30112000 → Rua da Bahia, Centro, Belo Horizonte/MG
```

### 3.2 CEPs Inválidos
```
abcd1234 → {"erro": true}
1234567  → {"erro": true} (7 dígitos)
123456789 → {"erro": true} (9 dígitos)
```

### 3.3 Consulta por Endereço
```
SP + São Paulo + Avenida Paulista → Retorna dados da Avenida Paulista
Qualquer outra combinação → Array vazio
```

## 4. Performance com Mock

- **Velocidade**: < 1ms por consulta
- **10 consultas**: < 1000ms total
- **Sem limitações**: Não há rate limiting
- **Sempre disponível**: 100% uptime

## 5. Validações Mantidas

- **Estrutura JSON**: Campos cep, logradouro, bairro, localidade, uf, ddd
- **Tipos de Dados**: Strings para todos os campos
- **Flag de Erro**: Boolean para indicar CEP inválido
- **Formatação**: Aceita CEP com ou sem hífen

## 6. Limitações da Solução Mock

### ✅ Mantido:
- Estrutura de dados idêntica à API real
- Validações de entrada
- Comportamento de erro para entradas inválidas
- Suporte a acentuação básica

### ⚠️ Limitações:
- Apenas 3 CEPs válidos mockados
- Somente SP + São Paulo + Avenida Paulista para consulta por endereço
- Dados estáticos (não refletem mudanças reais)
- Sem rate limiting ou outros aspectos de rede

## 7. Execução dos Testes

```bash
mvn test
```

Os testes executam automaticamente com mock ativo (`useMock = true`), garantindo:
- ✅ Execução sem dependência externa
- ✅ Resultados previsíveis e consistentes  
- ✅ Cobertura completa das funcionalidades testadas
- ✅ Performance otimizada

## 8. Conclusão

A solução simplificada com mock permite:

1. **Testes Confiáveis**: Execução independente da disponibilidade da API
2. **Cobertura Mantida**: Todos os cenários de teste preservados
3. **Performance Superior**: Respostas instantâneas
4. **Facilidade de Manutenção**: Código simples e direto

**A implementação garante que os objetivos de teste sejam alcançados mesmo com a API ViaCEP indisponível, mantendo a validação das técnicas de partição de equivalência, análise de valor limite e tabela de decisão aplicadas ao projeto.**
