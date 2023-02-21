package com.whiteboard.accountmanager.pattern.impl;

import com.whiteboard.accountmanager.exceptions.ValidationException;
import com.whiteboard.accountmanager.pattern.CamposBuscaStrategy;
import com.whiteboard.accountmanager.utils.ValidationCamposUtils;

public class CampoCidadeRegraStrategy implements CamposBuscaStrategy {
    @Override
    public void validar(String campo, String valor) throws ValidationException {
        ValidationCamposUtils.validarTamanho(campo, valor, 5, 50);
        ValidationCamposUtils.validarCharacters(campo, valor);
    }
}
