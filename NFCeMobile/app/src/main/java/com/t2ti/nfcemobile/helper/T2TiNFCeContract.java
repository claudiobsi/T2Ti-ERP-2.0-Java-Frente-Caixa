/*
 * The MIT License
 *
 * Copyright: Copyright (C) 2014 T2Ti.COM
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 *
 * The author may be contacted at: t2ti.com@gmail.com
 *
 * @author Claudio de Barros (T2Ti.com)
 * @version 2.0
 */
package com.t2ti.nfcemobile.helper;

import android.provider.BaseColumns;

public class T2TiNFCeContract {

    public static String DATABASE_NAME = "t2ti_nfce_mobile";
    public static Integer DATABASE_VERSION = 1;

    public static abstract class Produto implements BaseColumns {

        public final static String TABLE_NAME = "PRODUTO";
        public final static String COLUMN_NAME_ID_SUBGRUPO = "ID_SUBGRUPO";
        public final static String COLUMN_NAME_ID_TRIBUT_ICMS_CUSTOM_CAB = "ID_TRIBUT_ICMS_CUSTOM_CAB";
        public final static String COLUMN_NAME_ID_UNIDADE_PRODUTO = "ID_UNIDADE_PRODUTO";
        public final static String COLUMN_NAME_ID_ALMOXARIFADO = "ID_ALMOXARIFADO";
        public final static String COLUMN_NAME_ID_GRUPO_TRIBUTARIO = "ID_GRUPO_TRIBUTARIO";
        public final static String COLUMN_NAME_ID_MARCA_PRODUTO = "ID_MARCA_PRODUTO";
        public final static String COLUMN_NAME_GTIN = "GTIN";
        public final static String COLUMN_NAME_CODIGO_INTERNO = "CODIGO_INTERNO";
        public final static String COLUMN_NAME_NCM = "NCM";
        public final static String COLUMN_NAME_NOME = "NOME";
        public final static String COLUMN_NAME_DESCRICAO = "DESCRICAO";
        public final static String COLUMN_NAME_DESCRICAO_PDV = "DESCRICAO_PDV";
        public final static String COLUMN_NAME_VALOR_COMPRA = "VALOR_COMPRA";
        public final static String COLUMN_NAME_VALOR_VENDA = "VALOR_VENDA";
        public final static String COLUMN_NAME_PRECO_VENDA_MINIMO = "PRECO_VENDA_MINIMO";
        public final static String COLUMN_NAME_PRECO_SUGERIDO = "PRECO_SUGERIDO";
        public final static String COLUMN_NAME_CUSTO_UNITARIO = "CUSTO_UNITARIO";
        public final static String COLUMN_NAME_CUSTO_PRODUCAO = "CUSTO_PRODUCAO";
        public final static String COLUMN_NAME_CUSTO_MEDIO_LIQUIDO = "CUSTO_MEDIO_LIQUIDO";
        public final static String COLUMN_NAME_PRECO_LUCRO_ZERO = "PRECO_LUCRO_ZERO";
        public final static String COLUMN_NAME_PRECO_LUCRO_MINIMO = "PRECO_LUCRO_MINIMO";
        public final static String COLUMN_NAME_PRECO_LUCRO_MAXIMO = "PRECO_LUCRO_MAXIMO";
        public final static String COLUMN_NAME_MARKUP = "MARKUP";
        public final static String COLUMN_NAME_QUANTIDADE_ESTOQUE = "QUANTIDADE_ESTOQUE";
        public final static String COLUMN_NAME_QUANTIDADE_ESTOQUE_ANTERIOR = "QUANTIDADE_ESTOQUE_ANTERIOR";
        public final static String COLUMN_NAME_ESTOQUE_MINIMO = "ESTOQUE_MINIMO";
        public final static String COLUMN_NAME_ESTOQUE_MAXIMO = "ESTOQUE_MAXIMO";
        public final static String COLUMN_NAME_ESTOQUE_IDEAL = "ESTOQUE_IDEAL";
        public final static String COLUMN_NAME_EXCLUIDO = "EXCLUIDO";
        public final static String COLUMN_NAME_INATIVO = "INATIVO";
        public final static String COLUMN_NAME_DATA_CADASTRO = "DATA_CADASTRO";
        public final static String COLUMN_NAME_IMAGEM = "IMAGEM";
        public final static String COLUMN_NAME_EX_TIPI = "EX_TIPI";
        public final static String COLUMN_NAME_CODIGO_LST = "CODIGO_LST";
        public final static String COLUMN_NAME_CLASSE_ABC = "CLASSE_ABC";
        public final static String COLUMN_NAME_IAT = "IAT";
        public final static String COLUMN_NAME_IPPT = "IPPT";
        public final static String COLUMN_NAME_TIPO_ITEM_SPED = "TIPO_ITEM_SPED";
        public final static String COLUMN_NAME_PESO = "PESO";
        public final static String COLUMN_NAME_PORCENTO_COMISSAO = "PORCENTO_COMISSAO";
        public final static String COLUMN_NAME_PONTO_PEDIDO = "PONTO_PEDIDO";
        public final static String COLUMN_NAME_LOTE_ECONOMICO_COMPRA = "LOTE_ECONOMICO_COMPRA";
        public final static String COLUMN_NAME_ALIQUOTA_ICMS_PAF = "ALIQUOTA_ICMS_PAF";
        public final static String COLUMN_NAME_ALIQUOTA_ISSQN_PAF = "ALIQUOTA_ISSQN_PAF";
        public final static String COLUMN_NAME_TOTALIZADOR_PARCIAL = "TOTALIZADOR_PARCIAL";
        public final static String COLUMN_NAME_CODIGO_BALANCA = "CODIGO_BALANCA";
        public final static String COLUMN_NAME_DATA_ALTERACAO = "DATA_ALTERACAO";
        public final static String COLUMN_NAME_TIPO = "TIPO";
        public final static String COLUMN_NAME_SERVICO = "SERVICO";

        public final static String ON_CREATE = "CREATE TABLE " + TABLE_NAME + " ("
                + _ID + " INTEGER PRIMARY KEY, "
                + COLUMN_NAME_ID_SUBGRUPO + " INTEGER, "
                + COLUMN_NAME_ID_TRIBUT_ICMS_CUSTOM_CAB + " INTEGER, "
                + COLUMN_NAME_ID_UNIDADE_PRODUTO + " INTEGER, "
                + COLUMN_NAME_ID_ALMOXARIFADO + " INTEGER, "
                + COLUMN_NAME_ID_GRUPO_TRIBUTARIO + " INTEGER, "
                + COLUMN_NAME_ID_MARCA_PRODUTO + " INTEGER, "
                + COLUMN_NAME_GTIN + " TEXT, "
                + COLUMN_NAME_CODIGO_INTERNO + " TEXT, "
                + COLUMN_NAME_NCM + " TEXT, "
                + COLUMN_NAME_NOME + " TEXT, "
                + COLUMN_NAME_DESCRICAO + " TEXT, "
                + COLUMN_NAME_DESCRICAO_PDV + " TEXT, "
                + COLUMN_NAME_VALOR_COMPRA + " REAL, "
                + COLUMN_NAME_VALOR_VENDA + " REAL, "
                + COLUMN_NAME_PRECO_VENDA_MINIMO + " REAL, "
                + COLUMN_NAME_PRECO_SUGERIDO + " REAL, "
                + COLUMN_NAME_CUSTO_UNITARIO + " REAL, "
                + COLUMN_NAME_CUSTO_PRODUCAO + " REAL, "
                + COLUMN_NAME_CUSTO_MEDIO_LIQUIDO + " REAL, "
                + COLUMN_NAME_PRECO_LUCRO_ZERO + " REAL, "
                + COLUMN_NAME_PRECO_LUCRO_MINIMO + " REAL, "
                + COLUMN_NAME_PRECO_LUCRO_MAXIMO + " REAL, "
                + COLUMN_NAME_MARKUP + " REAL, "
                + COLUMN_NAME_QUANTIDADE_ESTOQUE + " REAL, "
                + COLUMN_NAME_QUANTIDADE_ESTOQUE_ANTERIOR + " REAL, "
                + COLUMN_NAME_ESTOQUE_MINIMO + " REAL, "
                + COLUMN_NAME_ESTOQUE_MAXIMO + " REAL, "
                + COLUMN_NAME_ESTOQUE_IDEAL + " REAL, "
                + COLUMN_NAME_EXCLUIDO + " TEXT, "
                + COLUMN_NAME_INATIVO + " TEXT, "
                + COLUMN_NAME_DATA_CADASTRO + " TEXT, "
                + COLUMN_NAME_IMAGEM + " TEXT, "
                + COLUMN_NAME_EX_TIPI + " TEXT, "
                + COLUMN_NAME_CODIGO_LST + " TEXT, "
                + COLUMN_NAME_CLASSE_ABC + " TEXT, "
                + COLUMN_NAME_IAT + " TEXT, "
                + COLUMN_NAME_IPPT + " TEXT, "
                + COLUMN_NAME_TIPO_ITEM_SPED + " TEXT, "
                + COLUMN_NAME_PESO + " REAL, "
                + COLUMN_NAME_PORCENTO_COMISSAO + " REAL, "
                + COLUMN_NAME_PONTO_PEDIDO + " REAL, "
                + COLUMN_NAME_LOTE_ECONOMICO_COMPRA + " REAL, "
                + COLUMN_NAME_ALIQUOTA_ICMS_PAF + " REAL, "
                + COLUMN_NAME_ALIQUOTA_ISSQN_PAF + " REAL, "
                + COLUMN_NAME_TOTALIZADOR_PARCIAL + " TEXT, "
                + COLUMN_NAME_CODIGO_BALANCA + " INTEGER, "
                + COLUMN_NAME_DATA_ALTERACAO + " TEXT, "
                + COLUMN_NAME_TIPO + " TEXT, "
                + COLUMN_NAME_SERVICO + " TEXT "
                + ")";

        public final static String ON_DELETE = "DROP TABLE IF EXISTS " + TABLE_NAME;
    }

    public static abstract class UnidadeProduto implements BaseColumns {

        public final static String TABLE_NAME = "UNIDADE_PRODUTO";
        public final static String COLUMN_NAME_SIGLA = "SIGLA";
        public final static String COLUMN_NAME_DESCRICAO = "DESCRICAO";
        public final static String COLUMN_NAME_PODE_FRACIONAR = "PODE_FRACIONAR";

        public final static String ON_CREATE = "CREATE TABLE " + TABLE_NAME + " ("
                + _ID + " INTEGER PRIMARY KEY, "
                + COLUMN_NAME_SIGLA + " TEXT, "
                + COLUMN_NAME_DESCRICAO + " TEXT, "
                + COLUMN_NAME_PODE_FRACIONAR + " TEXT "
                + ")";

        public final static String ON_DELETE = "DROP TABLE IF EXISTS " + TABLE_NAME;
    }
}
