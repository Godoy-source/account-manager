package com.whiteboard.accountmanager.utils;

import com.whiteboard.accountmanager.enums.CodigoErroEnum;
import com.whiteboard.accountmanager.exceptions.CadastroException;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

@UtilityClass
public class ValidationCamposUtils {
    private static final List<String> charactersBloqueados = List.of("<", ">", "/", "[", "]", "'", "^", "!");

    public void validarSomenteNumeros(String campo, String validar) throws CadastroException {
        if (!StringUtils.isNumeric(validar)) {
            throw new CadastroException(CodigoErroEnum.ERRO_DADOS_ENTRADA_VALOR_INVALIDO,
                    CodigoErroEnum.ERRO_DADOS_ENTRADA_VALOR_INVALIDO.getDescricaoCodigo()
                            .replace("@campo", campo));
        }
    }

    public void validarTamanho(String campo, String validar, int minTamanho, int maxTamanho) throws CadastroException {
        if (validar.length() > maxTamanho || validar.length() < minTamanho) {
            throw new CadastroException(CodigoErroEnum.ERRO_DADOS_ENTRADA_TAMANHO_CAMPO,
                    CodigoErroEnum.ERRO_DADOS_ENTRADA_TAMANHO_CAMPO.getDescricaoCodigo()
                            .replace("@campo", campo)
                            .replace("@limiteMin", minTamanho + "")
                            .replace("@limiteMax", maxTamanho + ""));
        }
    }

    public void validarCharacters(String campo, String validar) throws CadastroException {
        for (int i = 0; i < validar.length(); i++) {
            var strChar = Character.toString(validar.charAt(i));
            if (charactersBloqueados.contains(strChar)) {
                throw new CadastroException(CodigoErroEnum.ERRO_DADOS_ENTRADA_NAO_PERMITIDOS,
                        CodigoErroEnum.ERRO_DADOS_ENTRADA_NAO_PERMITIDOS.getDescricaoCodigo()
                                .replace("@character", strChar)
                                .replace("@campo", campo));
            }
        }
    }
}
