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
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.t2ti.nfcemobile.dao.ProdutoDAO;
import com.t2tierp.model.bean.cadastros.Produto;

import java.util.ArrayList;
import java.util.List;

public class ListaProdutoActivity extends AppCompatActivity {

    private static final String TAG = "ListaProduto";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_produto);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_lista_produto);
        setSupportActionBar(toolbar);

        new ProdutoBDTask(this).execute();
    }

    private void mostraItens(List<Produto> produtos) {
        ListaProdutoAdapter adapter = new ListaProdutoAdapter(produtos);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.lista_produto);
        recyclerView.setAdapter(adapter);
    }

    private void selecionaProduto(int idProduto) {
        Intent intent = new Intent();
        intent.putExtra("idProduto", idProduto);
        setResult(RESULT_OK, intent);
        finish();
    }

    private class ProdutoBDTask extends AsyncTask<Void, Void, List<Produto>> {

        private ProgressDialog dialog;
        private Context context;

        public ProdutoBDTask(Context context) {
            this.context = context;
            dialog = new ProgressDialog(context);
        }

        @Override
        protected void onPreExecute() {
            dialog.setMessage(getResources().getString(R.string.txt_aguarde));
            dialog.show();
        }

        @Override
        protected List<Produto> doInBackground(Void... params) {
            List<Produto> produtos = new ArrayList<>();
            try {
                ProdutoDAO dao = new ProdutoDAO(context);
                produtos = dao.getProdutos();
            } catch(Exception e) {
                Log.e(TAG, e.toString());
            }
            return produtos;
        }

        @Override
        protected void onPostExecute(List<Produto> produtos) {
            dialog.dismiss();
            mostraItens(produtos);
        }
    }

    public class ListaProdutoAdapter extends RecyclerView.Adapter<ListaProdutoAdapter.ViewHolder> {

        private List<Produto> listaItens;

        public ListaProdutoAdapter(List<Produto> listaItens) {
            this.listaItens = listaItens;
        }

        @Override
        public ListaProdutoAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.lista_produto_itens, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ListaProdutoAdapter.ViewHolder holder, int position) {
            Produto produto = listaItens.get(position);

            holder.textViewIdProduto.setText(produto.getId().toString());
            holder.textViewNomeProduto.setText(produto.getNome());
            holder.textViewGtinProduto.setText(produto.getGtin());
        }

        @Override
        public int getItemCount() {
            return listaItens.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            final public TextView textViewIdProduto;
            final public TextView textViewNomeProduto;
            final public TextView textViewGtinProduto;

            public ViewHolder(View itemView) {
                super(itemView);
                textViewIdProduto = (TextView) itemView.findViewById(R.id.txt_id_produto);
                textViewNomeProduto = (TextView) itemView.findViewById(R.id.txt_nome_produto);
                textViewGtinProduto = (TextView) itemView.findViewById(R.id.txt_gtin_produto);
                itemView.setOnClickListener(this);
            }

            @Override
            public void onClick(View v) {
                selecionaProduto(Integer.valueOf(textViewIdProduto.getText().toString()));
            }
        }
    }

}
