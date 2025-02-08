# QR Code Generator

[![en](https://img.shields.io/badge/lang-en-red.svg)](./README.md) [![pt-br](https://img.shields.io/badge/lang-pt--br-green.svg)](./README.pt-br.md)

## Descrição

Esta é uma aplicação Spring Boot que fornece uma API para a geração de QR codes. Ela utiliza a biblioteca ZXing para criar QR codes baseados em parâmetros definidos pelo usuário.

## Uso

Para gerar um QR code, deve-se fazer uma requisição GET para `/api/qrcode`, que aceita os seguintes parâmetros:

- `contents`: O conteúdo a ser codificado no QR code.
- `size`: Tamanho da imagem do QR code (em pixels).
- `correction`: Nível de correção de erro (L, M, Q, H).
- `type`: Formato da imagem (PNG, JPEG, GIF).

Apenas o parâmetro `contents` é obrigatório. Se os outros parâmetros não forem especificados, default values serão utilizados.

Os parâmetros `size`, `type` e `correction` são validados para garantir que estejam dentro da faixa aceitável de valores.

Exemplo de requisição:

```
GET /api/qrcode?contents=HelloWorld&size=250&correction=L&type=png
```

Para requisições bem-sucedidas, a API retorna a imagem do QR code no formato especificado, juntamente com OK/200. Caso contrário, retornará um json contendo uma mensagem de erro e um código de status HTTP apropriado.

## Testes

Foram desenvolvidos testes automatizados com o objetivo de validar as funcionalidades da aplicação, assegurar a confiabilidade do sistema em seu estado atual e facilitar a manutenção e a evolução do código. Adicionalmente, servem como uma forma de documentação sobre o comportamento esperado da aplicação.
