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
package com.t2ti.nfcemobile;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.t2ti.nfcemobile.dao.ProdutoDAO;

public class MainActivity extends AppCompatActivity {

    private final String TAG = "EmissaoNFCE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_item_novo) {
            Intent intent = new Intent(this, EmissaoNFCeActivity.class);
            startActivity(intent);
            return true;
        }

        if (id == R.id.menu_item_cancela_nfce) {
            cancelaNfce();
            return true;
        }

        if (id == R.id.menu_item_importa_produto) {
            ProdutoDAO dao = new ProdutoDAO(this);
            dao.importaProduto();

            Toast.makeText(MainActivity.this, getString(R.string.msg_produto_importado), Toast.LENGTH_LONG).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void cancelaNfce() {
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        final View viewCancelaNfce = layoutInflater.inflate(R.layout.layout_cancelamento, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(viewCancelaNfce);
        builder.setTitle(getResources().getString(R.string.txt_cancelar_nfce));
        builder.setPositiveButton(R.string.txt_ok, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    EditText editTextNumero = (EditText) viewCancelaNfce.findViewById(R.id.txt_numero_cancelar);
                    Integer numero = Integer.valueOf(editTextNumero.getText().toString());

                    EditText editTextJustificativa = (EditText) viewCancelaNfce.findViewById(R.id.txt_justificativa);
                    String justificativa = editTextJustificativa.getText().toString();

                    new CancelaNfceTask(MainActivity.this).execute(numero.toString(), justificativa);
                } catch (NumberFormatException e) {
                    Toast.makeText(MainActivity.this, getString(R.string.erro_numero), Toast.LENGTH_LONG).show();
                }
            }
        });

        builder.setNegativeButton(R.string.txt_cancelar, null);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private class CancelaNfceTask extends AsyncTask<String, Void, String> {

        private ProgressDialog dialog;

        public CancelaNfceTask(Context context) {
            dialog = new ProgressDialog(context);
        }

        @Override
        protected void onPreExecute() {
            dialog.setMessage(getResources().getString(R.string.txt_aguarde));
            dialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                RequisicaoWebService requisicao = new RequisicaoWebService();
                return requisicao.cancelamento(params[0], params[1]);
            } catch(Exception e) {
                Log.e(TAG, e.toString());
                return e.getMessage();
            }
        }

        @Override
        protected void onPostExecute(String resposta) {
            dialog.dismiss();
            Toast.makeText(MainActivity.this, resposta, Toast.LENGTH_LONG).show();
        }
    }


}
