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
package com.t2ti.nfcemobile.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.t2ti.nfcemobile.helper.T2TiNFCeContract;
import com.t2ti.nfcemobile.helper.T2TiNFCeDatabaseOpenHelper;
import com.t2tierp.model.bean.cadastros.Produto;
import com.t2tierp.model.bean.cadastros.UnidadeProduto;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class ProdutoDAO {

    T2TiNFCeDatabaseOpenHelper databaseOpenHelper;
    private Context context;

    private String colunas[] = {T2TiNFCeContract.Produto._ID,
            T2TiNFCeContract.Produto.COLUMN_NAME_GTIN,
            T2TiNFCeContract.Produto.COLUMN_NAME_NCM,
            T2TiNFCeContract.Produto.COLUMN_NAME_NOME,
            T2TiNFCeContract.Produto.COLUMN_NAME_VALOR_VENDA,
            T2TiNFCeContract.Produto.COLUMN_NAME_ALIQUOTA_ICMS_PAF};


    public ProdutoDAO(Context context) {
        this.context = context;
        databaseOpenHelper = new T2TiNFCeDatabaseOpenHelper(context);
    }

    public List<Produto> getProdutos() throws Exception {
        SQLiteDatabase db = databaseOpenHelper.getReadableDatabase();
        List<Produto> listaProduto = new ArrayList<>();

        Cursor cursor = db.query(T2TiNFCeContract.Produto.TABLE_NAME, colunas, null, null, null, null, null);

        while (cursor.moveToNext()) {
            Produto produto = new Produto();
            setDadosProduto(produto, cursor);

            listaProduto.add(produto);
        }
        db.close();
        return listaProduto;
    }

    public Produto getProduto(int idProduto) throws Exception {
        SQLiteDatabase db = databaseOpenHelper.getReadableDatabase();
        Produto produto = null;

        String whereColumns = T2TiNFCeContract.Produto._ID + " = ?";
        String whereValues[] = {String.valueOf(idProduto)};

        Cursor cursor = db.query(T2TiNFCeContract.Produto.TABLE_NAME, colunas, whereColumns, whereValues, null, null, null);

        while (cursor.moveToNext()) {
            produto = new Produto();
            setDadosProduto(produto, cursor);
        }

        db.close();
        return produto;
    }

    private void setDadosProduto(Produto produto, Cursor cursor) {
        produto.setId(cursor.getInt(0));
        produto.setGtin(cursor.getString(1));
        produto.setNcm(cursor.getString(2));
        produto.setNome(cursor.getString(3));
        produto.setValorVenda(BigDecimal.valueOf(cursor.getDouble(4)));
        produto.setAliquotaIcmsPaf(BigDecimal.valueOf(cursor.getDouble(5)));

        //Exercício: Persistir os dados de unidade na tabela UnidadeProduto
        UnidadeProduto unidade = new UnidadeProduto();
        unidade.setSigla("UN");

        produto.setUnidadeProduto(unidade);
    }

    public void inclui(Produto produto) throws Exception {
        SQLiteDatabase db = databaseOpenHelper.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(T2TiNFCeContract.Produto.COLUMN_NAME_GTIN, produto.getGtin());
        contentValues.put(T2TiNFCeContract.Produto.COLUMN_NAME_NCM, produto.getNcm());
        contentValues.put(T2TiNFCeContract.Produto.COLUMN_NAME_NOME, produto.getNome());
        contentValues.put(T2TiNFCeContract.Produto.COLUMN_NAME_VALOR_VENDA, produto.getValorVenda().doubleValue());
        contentValues.put(T2TiNFCeContract.Produto.COLUMN_NAME_ALIQUOTA_ICMS_PAF, produto.getAliquotaIcmsPaf().doubleValue());

        db.insertOrThrow(T2TiNFCeContract.Produto.TABLE_NAME, null, contentValues);
        db.close();
    }

    public void importaProduto() {
        try {
            //Exercício: importar os dados através do webservice

            Produto produto = new Produto();
            produto.setGtin("7896019606226");
            produto.setNcm("17049010");
            produto.setNome("Produto 01");
            produto.setValorVenda(BigDecimal.valueOf(5));
            produto.setAliquotaIcmsPaf(BigDecimal.valueOf(17));

            inclui(produto);

            produto = new Produto();
            produto.setGtin("7897975018016");
            produto.setNcm("48234000");
            produto.setNome("Produto 02");
            produto.setValorVenda(BigDecimal.valueOf(10));
            produto.setAliquotaIcmsPaf(BigDecimal.valueOf(15));

            inclui(produto);

            produto = new Produto();
            produto.setGtin("7896019606226");
            produto.setNcm("17049010");
            produto.setNome("Produto 03");
            produto.setValorVenda(BigDecimal.valueOf(5));
            produto.setAliquotaIcmsPaf(BigDecimal.valueOf(17));

            inclui(produto);

            produto = new Produto();
            produto.setGtin("7897975018016");
            produto.setNcm("48234000");
            produto.setNome("Produto 04");
            produto.setValorVenda(BigDecimal.valueOf(10));
            produto.setAliquotaIcmsPaf(BigDecimal.valueOf(15));

            inclui(produto);

            produto = new Produto();
            produto.setGtin("7896019606226");
            produto.setNcm("17049010");
            produto.setNome("Produto 05");
            produto.setValorVenda(BigDecimal.valueOf(5));
            produto.setAliquotaIcmsPaf(BigDecimal.valueOf(17));

            inclui(produto);

            produto = new Produto();
            produto.setGtin("7897975018016");
            produto.setNcm("48234000");
            produto.setNome("Produto 06");
            produto.setValorVenda(BigDecimal.valueOf(10));
            produto.setAliquotaIcmsPaf(BigDecimal.valueOf(15));

            inclui(produto);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
