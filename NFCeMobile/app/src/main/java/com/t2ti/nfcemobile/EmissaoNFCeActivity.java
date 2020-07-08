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
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.t2ti.nfcemobile.dao.ProdutoDAO;
import com.t2tierp.model.bean.cadastros.Produto;
import com.t2tierp.model.bean.nfce.NfceTipoPagamento;
import com.t2tierp.model.bean.nfe.NfeCabecalho;
import com.t2tierp.model.bean.nfe.NfeDestinatario;
import com.t2tierp.model.bean.nfe.NfeDetalhe;
import com.t2tierp.model.bean.nfe.NfeFormaPagamento;
import com.t2tierp.util.Biblioteca;

import java.io.File;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class EmissaoNFCeActivity extends AppCompatActivity {

    private final String TAG = "EmissaoNFCE";
    private static final int ADICIONA_ITEM = 1;
    private ListaItensAdapter itensAdapter;
    private Set<NfeFormaPagamento> pagamentos;
    private NfeDestinatario destinatario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emissao_nfce);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_emissao_nfce);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(getString(R.string.app_name));
        }

        itensAdapter = new ListaItensAdapter();
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.lista_produto_nfce);
        recyclerView.setAdapter(itensAdapter);

        pagamentos = new HashSet<>();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_emissao, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_item_incluir_pagamento) {
            incluiPagamento();
            return true;
        }

        if (id == R.id.menu_item_identifica_consumidor) {
            identificaConsumidor();
            return true;
        }

        if (id == R.id.menu_item_finaliza_venda) {
            finalizaVenda();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ADICIONA_ITEM) {
            if (resultCode == RESULT_OK) {
                int idProduto = data.getIntExtra("idProduto", 0);
                ProdutoDAO dao = new ProdutoDAO(this);
                try {
                    Produto produto = dao.getProduto(idProduto);
                    itensAdapter.adicionaItem(produto);
                } catch(Exception e) {
                    Log.e(TAG, e.toString());
                }
            }
        }
    }

    public void adicionaItem(View view) {
        Intent intent = new Intent(this, ListaProdutoActivity.class);
        startActivityForResult(intent, ADICIONA_ITEM);
    }

    public void incluiPagamento() {
        try {
            final CharSequence tiposPagamento[] = new CharSequence[] {"Dinheiro", "Cheque", "Cartão Crédito", "Cartão Débito"};

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(getString(R.string.txt_forma_pagamento));
            builder.setItems(tiposPagamento, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int pagamentoSelecionado) {
                    //Exercicio: buscar os tipos de pagamento através do webservice e armazenar no banco de dados do dispositivo.
                    NfceTipoPagamento tipoPagamento = new NfceTipoPagamento();
                    tipoPagamento.setId(pagamentoSelecionado + 1);
                    tipoPagamento.setCodigo("0" + (pagamentoSelecionado + 1));
                    tipoPagamento.setDescricao(tiposPagamento[pagamentoSelecionado].toString());
                    tipoPagamento.setGeraParcelas(pagamentoSelecionado == 1 ? "S" : "N");

                    NfeFormaPagamento formaPagamento = new NfeFormaPagamento();
                    formaPagamento.setForma(tipoPagamento.getCodigo());
                    formaPagamento.setValor(itensAdapter.getTotalGeral());
                    formaPagamento.setNfceTipoPagamento(tipoPagamento);
                    pagamentos.add(formaPagamento);
                    Toast.makeText(EmissaoNFCeActivity.this, getString(R.string.msg_pagamento) + tipoPagamento.getDescricao(), Toast.LENGTH_LONG).show();
                }
            });
            builder.show();
        } catch(Exception e) {
            Log.e(TAG, e.toString());
        }
    }

    private void identificaConsumidor() {
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        final View viewIdenficaConsumidor = layoutInflater.inflate(R.layout.layout_dados_consumidor, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(viewIdenficaConsumidor);
        builder.setTitle(getResources().getString(R.string.menu_item_identifica_consumidor));
        builder.setPositiveButton(R.string.txt_ok, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                EditText editTextNome = (EditText) viewIdenficaConsumidor.findViewById(R.id.txt_nome);
                String nome = editTextNome.getText().toString();
                EditText editTextCpfCnpj = (EditText) viewIdenficaConsumidor.findViewById(R.id.txt_cpf_cnpj);
                String cpfCnpj = editTextCpfCnpj.getText().toString();

                if (Biblioteca.cpfValido(cpfCnpj) || Biblioteca.cnpjValido(cpfCnpj)) {
                    destinatario = new NfeDestinatario();
                    destinatario.setNome(nome.isEmpty() ? null : nome);
                    destinatario.setCpfCnpj(cpfCnpj);

                    Toast.makeText(EmissaoNFCeActivity.this, getString(R.string.msg_consumidor_identificado) + cpfCnpj, Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(EmissaoNFCeActivity.this, getString(R.string.erro_cpf_cnpj), Toast.LENGTH_LONG).show();
                }
            }
        });

        builder.setNegativeButton(R.string.txt_cancelar, null);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void finalizaVenda() {
        try {
            if (itensAdapter.getItens().isEmpty()) {
                Toast.makeText(this, getString(R.string.erro_nenhum_item), Toast.LENGTH_LONG).show();
            } else if (pagamentos.isEmpty()) {
                Toast.makeText(this, getString(R.string.erro_nenhum_pagamento), Toast.LENGTH_LONG).show();
            } else {
                NfeCabecalho vendaAtual = new NfeCabecalho();
                vendaAtual.setDestinatario(destinatario);
                vendaAtual.setListaNfeDetalhe(itensAdapter.getItens());
                vendaAtual.setListaNfeFormaPagamento(pagamentos);

                new FinalizaVendaTask(this).execute(vendaAtual);
            }
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
    }

    public class ListaItensAdapter extends RecyclerView.Adapter<ListaItensAdapter.ViewHolder> {

        private List<NfeDetalhe> listaItens;
        private BigDecimal totalGeral;
        private DecimalFormat decimalFormat;

        public ListaItensAdapter() {
            decimalFormat = new DecimalFormat("0.00");
            listaItens = new ArrayList<>();
            totalGeral = BigDecimal.ZERO;
        }

        public void adicionaItem(Produto produto) {
            NfeDetalhe vendaDetalhe = new NfeDetalhe();
            vendaDetalhe.setProduto(produto);
            vendaDetalhe.setCodigoProduto(produto.getGtin());
            vendaDetalhe.setGtin(produto.getGtin());
            vendaDetalhe.setValorUnitarioComercial(produto.getValorVenda());
            vendaDetalhe.setNomeProduto(produto.getNome());
            vendaDetalhe.setValorTotal(Biblioteca.multiplica(BigDecimal.ONE, produto.getValorVenda()));

            listaItens.add(vendaDetalhe);

            totalGeral = Biblioteca.soma(totalGeral, produto.getValorVenda());

            atualizaTotais();

            notifyItemInserted(listaItens.size() - 1);
        }

        private void atualizaTotais() {
            TextView txtValorTotal = (TextView) findViewById(R.id.txt_valor_total);
            txtValorTotal.setText("Vlr. Total: R$ " + decimalFormat.format(totalGeral));
        }

        public BigDecimal getTotalGeral() {
            return totalGeral;
        }

        public List<NfeDetalhe> getItens() {
            return listaItens;
        }

        @Override
        public ListaItensAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.emissao_nfce_lista_itens, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ListaItensAdapter.ViewHolder holder, int position) {
            NfeDetalhe detalhe = listaItens.get(position);

            holder.textViewLinha1.setText("1,000 x UN    " + detalhe.getGtin());
            holder.textViewvalorUnitario.setText(decimalFormat.format(detalhe.getValorUnitarioComercial()));
            holder.textViewNomeProduto.setText(detalhe.getNomeProduto());
            holder.textViewValorTotal.setText(decimalFormat.format(detalhe.getValorTotal()));
        }

        @Override
        public int getItemCount() {
            return listaItens.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            final public TextView textViewLinha1;
            final public TextView textViewvalorUnitario;
            final public TextView textViewNomeProduto;
            final public TextView textViewValorTotal;

            public ViewHolder(View itemView) {
                super(itemView);
                textViewLinha1 = (TextView) itemView.findViewById(R.id.txt_linha_1);
                textViewvalorUnitario = (TextView) itemView.findViewById(R.id.txt_valor_unitario);
                textViewNomeProduto = (TextView) itemView.findViewById(R.id.txt_nome_produto);
                textViewValorTotal = (TextView) itemView.findViewById(R.id.txt_valor_total_item);
            }
        }
    }

    private class FinalizaVendaTask extends AsyncTask<NfeCabecalho, Void, NfeCabecalho> {

        private ProgressDialog dialog;
        private Context context;
        private String mensagem;

        public FinalizaVendaTask(Context context) {
            this.context = context;
            dialog = new ProgressDialog(context);
        }

        @Override
        protected void onPreExecute() {
            dialog.setMessage(getResources().getString(R.string.txt_aguarde));
            dialog.show();
        }

        @Override
        protected NfeCabecalho doInBackground(NfeCabecalho... params) {
            NfeCabecalho nfe = params[0];
            try {
                RequisicaoWebService requisicao = new RequisicaoWebService();
                nfe = requisicao.emissao(nfe);
            } catch(Exception e) {
                Log.e(TAG, e.toString());
                mensagem = e.getMessage();
            }
            return nfe;
        }

        @Override
        protected void onPostExecute(NfeCabecalho nfe) {
            dialog.dismiss();
            if(mensagem == null) {
                try {
                    File file = salvaDanfe(nfe.getDanfe(), nfe.getNumero());
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setDataAndType(Uri.fromFile(file), "application/pdf");
                    startActivity(intent);
                } catch (Exception e) {
                    Log.e(TAG, e.toString());
                    Toast.makeText(EmissaoNFCeActivity.this, e.toString(), Toast.LENGTH_LONG).show();
                }
                finish();
            } else {
                Toast.makeText(EmissaoNFCeActivity.this, mensagem, Toast.LENGTH_LONG).show();
            }
        }

        private File salvaDanfe(String danfe, String nome) {
            try {
                nome = "nfe-"+ nome + ".pdf";
                byte[] arquivo = Base64.decode(danfe, Base64.NO_WRAP);

                File file = new File(getExternalFilesDir(null), nome);

                FileOutputStream out = new FileOutputStream(file);
                out.write(arquivo);
                out.close();

                return file;
            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }
            return null;
        }

    }

}
