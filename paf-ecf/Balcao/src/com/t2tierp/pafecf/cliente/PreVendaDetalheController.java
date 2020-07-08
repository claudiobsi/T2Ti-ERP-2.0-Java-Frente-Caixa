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
package com.t2tierp.pafecf.cliente;

import com.t2tierp.cadastros.java.EmpresaVO;
import com.t2tierp.padrao.cliente.Biblioteca;
import com.t2tierp.padrao.cliente.HibernateUtil;
import com.t2tierp.pafecf.java.PreVendaCabecalhoVO;
import com.t2tierp.pafecf.java.PreVendaDetalheVO;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import javax.swing.JOptionPane;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.openswing.swing.form.client.Form;
import org.openswing.swing.form.client.FormController;
import org.openswing.swing.mdi.client.MDIFrame;
import org.openswing.swing.message.receive.java.ErrorResponse;
import org.openswing.swing.message.receive.java.Response;
import org.openswing.swing.message.receive.java.VOResponse;
import org.openswing.swing.message.receive.java.ValueObject;
import org.openswing.swing.util.java.Consts;

public class PreVendaDetalheController extends FormController {

    private PreVendaDetalhe janelaDetalhe;
    private PreVendaCabecalhoVO preVendaCabecalho;

    public PreVendaDetalheController(PreVendaCabecalhoVO preVendaCabecalho) {
        janelaDetalhe = new PreVendaDetalhe(this);
        this.preVendaCabecalho = preVendaCabecalho;
        MDIFrame.add(janelaDetalhe);
        if (preVendaCabecalho == null) {
            janelaDetalhe.getForm1().setMode(Consts.INSERT);
            janelaDetalhe.getGrid1().reloadData();
        } else {
            janelaDetalhe.getForm1().reload();
        }
    }

    @Override
    public Response loadData(Class valueObjectClass) {
        return new VOResponse(preVendaCabecalho);
    }

    @Override
    public void afterReloadData() {
        janelaDetalhe.getPreVendaDetalheGridController().setListaPreVenda(preVendaCabecalho.getListaPreVendaDetalhe());
        janelaDetalhe.getForm1().setMode(Consts.EDIT);
    }

    @Override
    public Response insertRecord(ValueObject newPersistentObject) throws Exception {
        preVendaCabecalho = (PreVendaCabecalhoVO) newPersistentObject;

        if (!preVendaCabecalho.getCpfCnpjDestinatario().trim().equals("")) {
            if (!Biblioteca.validaCpfCnpj(preVendaCabecalho.getCpfCnpjDestinatario())) {
                return new ErrorResponse("CPF/CNPJ inválido!");
            }
        }

        Date dataAtual = new Date();
        SimpleDateFormat formatoHora = new SimpleDateFormat("HH:mm:ss");
        String horaAtual = formatoHora.format(dataAtual);

        BigDecimal valorTotal = BigDecimal.ZERO;
        List<PreVendaDetalheVO> listaPreVendaDetalhe = janelaDetalhe.getGrid1().getVOListTableModel().getDataVector();
        if (listaPreVendaDetalhe.isEmpty()) {
            return new ErrorResponse("Nenhum item na lista!");
        }
        for (int i = 0; i < listaPreVendaDetalhe.size(); i++) {
            if (listaPreVendaDetalhe.get(i).getCancelado().equals("N")) {
                valorTotal = valorTotal.add(listaPreVendaDetalhe.get(i).getValorTotal(), MathContext.DECIMAL64);
                valorTotal = valorTotal.setScale(2, RoundingMode.DOWN);
            }
        }

        preVendaCabecalho.setValor(valorTotal);
        preVendaCabecalho.setDataEmissao(dataAtual);
        preVendaCabecalho.setHoraEmissao(horaAtual);
        preVendaCabecalho.setSituacao("P");
        preVendaCabecalho.setTaxaAcrescimo(BigDecimal.ZERO);
        preVendaCabecalho.setAcrescimo(BigDecimal.ZERO);
        preVendaCabecalho.setTaxaDesconto(BigDecimal.ZERO);
        preVendaCabecalho.setDesconto(BigDecimal.ZERO);
        preVendaCabecalho.setSubtotal(valorTotal);

        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.getTransaction().begin();

            Criteria criteria = session.createCriteria(EmpresaVO.class);
            criteria.add(Restrictions.eq("id", 1));
            EmpresaVO empresa = (EmpresaVO) criteria.uniqueResult();
            if (empresa == null) {
                return new ErrorResponse("Empresa não cadastrada! Entre em contato com a Software House.");
            }

            preVendaCabecalho.setEmpresa(empresa);

            for (PreVendaDetalheVO davDetalhe : listaPreVendaDetalhe) {
                davDetalhe.setPreVendaCabecalho(preVendaCabecalho);
            }

            preVendaCabecalho.setListaPreVendaDetalhe(listaPreVendaDetalhe);
            session.save(preVendaCabecalho);

            session.getTransaction().commit();

            return new VOResponse(newPersistentObject);
        } catch (Exception e) {
            e.printStackTrace();
            session.getTransaction().rollback();
            return new ErrorResponse("Erro ao gerar a Pré-Venda.\n" + e.getMessage());
        } finally {
            session.close();
        }
    }

    @Override
    public void afterInsertData() {
        JOptionPane.showMessageDialog(janelaDetalhe, "Pré-Venda gerada com sucesso!\nNúmero: " + preVendaCabecalho.getId(), "Informacao do Sistema", JOptionPane.INFORMATION_MESSAGE);
        janelaDetalhe.dispose();
    }

    @Override
    public boolean beforeEditData(Form form) {
        if (preVendaCabecalho.getSituacao().equals("P")) {
            return true;
        } else {
            JOptionPane.showMessageDialog(janelaDetalhe, "Situação da Pré-Venda não permite alteração.", "Informação do Sistema", JOptionPane.INFORMATION_MESSAGE);
            return false;
        }
    }

    @Override
    public Response updateRecord(ValueObject oldPersistentObject, ValueObject persistentObject) throws Exception {
        BigDecimal valorTotal = BigDecimal.ZERO;
        List<PreVendaDetalheVO> listaPreVendaDetalhe = janelaDetalhe.getGrid1().getVOListTableModel().getDataVector();
        for (PreVendaDetalheVO d : listaPreVendaDetalhe) {
            if (d.getCancelado().equals("N")) {
                valorTotal = valorTotal.add(d.getValorTotal(), MathContext.DECIMAL64);
                valorTotal = valorTotal.setScale(2, RoundingMode.DOWN);
            }
        }
        preVendaCabecalho.setValor(valorTotal);
        preVendaCabecalho.setSubtotal(valorTotal);

        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.getTransaction().begin();

            for (PreVendaDetalheVO davDetalhe : listaPreVendaDetalhe) {
                davDetalhe.setPreVendaCabecalho(preVendaCabecalho);
            }
            preVendaCabecalho.setListaPreVendaDetalhe(listaPreVendaDetalhe);
            session.merge(preVendaCabecalho);

            session.getTransaction().commit();

            return new VOResponse(preVendaCabecalho);
        } catch (Exception e) {
            e.printStackTrace();
            session.getTransaction().rollback();
            return new ErrorResponse("Erro ao alterar a Pré-Venda.\n" + e.getMessage());
        } finally {
            session.close();
        }
    }

    @Override
    public void afterEditData() {
        JOptionPane.showMessageDialog(janelaDetalhe, "Pré-Venda alterada com sucesso!", "Informacao do Sistema", JOptionPane.INFORMATION_MESSAGE);
        janelaDetalhe.dispose();
    }

}
