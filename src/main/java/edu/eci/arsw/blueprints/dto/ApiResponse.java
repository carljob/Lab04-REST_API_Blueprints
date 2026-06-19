package edu.eci.arsw.blueprints.dto;

/**
 * Envoltorio uniforme de respuesta para todos los endpoints de la API.
 *
 * @param code    Código HTTP de la respuesta (200, 201, 202, 400, 404...)
 * @param message Mensaje descriptivo del resultado
 * @param data    Cuerpo de datos (puede ser null en errores)
 * @param <T>     Tipo del dato retornado
 */
public record ApiResponse<T>(int code, String message, T data) { }