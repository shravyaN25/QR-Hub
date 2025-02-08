# QR Code Generator

[![en](https://img.shields.io/badge/lang-en-red.svg)](./README.md) [![pt-br](https://img.shields.io/badge/lang-pt--br-green.svg)](./README.pt-br.md)

## Description

This is a Spring Boot application that provides an API for generating QR codes. It leverages Google's ZXing library to create QR codes based on user-defined parameters.

## Usage

To generate a QR code, one should make a GET request to `/api/qrcode`, which accepts the following parameters:

- `contents`: The content to be encoded in the QR code.
- `size`: Size of the QR code image (in pixels).
- `correction`: Error correction level (L, M, Q, H).
- `type`: Image format (PNG, JPEG, GIF).

Only the `contents` parameter is required. If the other parameters are not specified, default values will be used.

The `size`, `type` and `correction` parameters are validated to ensure that they are within the acceptable range of values.

Example request:

```
GET /api/qrcode?contents=HelloWorld&size=250&correction=L&type=png
```

For successful requests, the API returns the QR code image in the specified format, along with OK/200. Otherwise, the API returns a json containing an error message and an appropriate HTTP status code.

## Tests

Automated tests are featured so we can test the functionalities of the application, ensure the reliability of the system in its current state and make code maintenance and evolution easier. Furthermore, they serve as a form of documentation on the expected behavior of the application.
