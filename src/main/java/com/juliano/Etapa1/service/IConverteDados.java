/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.juliano.Etapa1.service;

/**
 *
 * @author juliano
 */
public interface IConverteDados {
    <T> T  obterDados(String json, Class<T> classe);
}