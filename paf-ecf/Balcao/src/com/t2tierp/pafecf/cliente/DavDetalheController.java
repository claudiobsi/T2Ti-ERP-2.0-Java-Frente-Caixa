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
import com.t2tierp.pafecf.java.DavCabecalhoVO;
import com.t2tierp.pafecf.java.DavDetalheVO;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.text.DecimalFormat;
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

public class DavDetalheController extends FormController {

    private DavDetalhe janelaDetalhe;
    private DavCabecalhoVO davCabecalho;

    public DavDetalheController(DavCabecalhoVO davCabecalho) {
        janelaDetalhe = new DavDetalhe(this);
        this.davCabecalho = davCabecalho;
        MDIFrame.add(janelaDetalhe);
        if (davCabecalho == null) {
            janelaDetalhe.getForm1().setMode(Consts.INSERT);
            janelaDetalhe.getGrid1().reloadData();
        } else {
            janelaDetalhe.getForm1().reload();
        }
    }

    @Override
    public Response loadData(Class valueObjectClass) {
        return new VOResponse(davCabecalho);
    }

    @Override
    public void afterReloadData() {
        janelaDetalhe.getDavGridController().setListaDav(davCabecalho.getListaDavDetalhe());
        janelaDetalhe.getForm1().setMode(Consts.EDIT);
    }

    @Override
    public Response insertRecord(ValueObject newPersistentObject) throws Exception {
        davCabecalho = (DavCabecalhoVO) newPersistentObject;

        if (!Biblioteca.validaCpfCnpj(davCabecalho.getCpfCnpjDestinatario())) {
            return new ErrorResponse("CPF/CNPJ inválido!");
        }
        
        Date dataAtual = new Date();
        SimpleDateFormat formatoHora = new SimpleDateFormat("HH:mm:ss");
        String horaAtual = formatoHora.format(dataAtual);

        BigDecimal valorTotal = BigDecimal.ZERO;
        List<DavDetalheVO> listaDavDetalhe = janelaDetalhe.getGrid1().getVOListTableModel().getDataVector();
        if (listaDavDetalhe.isEmpty()) {
            return new ErrorResponse("Nenhum item na lista!");
        }
        for (int i = 0; i < listaDavDetalhe.size(); i++) {
            if (listaDavDetalhe.get(i).getCancelado().equals("N")) {
                valorTotal = valorTotal.add(listaDavDetalhe.get(i).getValorTotal(), MathContext.DECIMAL64);
                valorTotal = valorTotal.setScale(2, RoundingMode.DOWN);
            }
        }

        davCabecalho.setValor(valorTotal);
        davCabecalho.setDataEmissao(dataAtual);
        davCabecalho.setHoraEmissao(horaAtual);
        davCabecalho.setSituacao("P");
        davCabecalho.setTaxaAcrescimo(BigDecimal.ZERO);
        davCabecalho.setAcrescimo(BigDecimal.ZERO);
        davCabecalho.setTaxaDesconto(BigDecimal.ZERO);
        davCabecalho.setDesconto(BigDecimal.ZERO);
        davCabecalho.setSubtotal(valorTotal);

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

            davCabecalho.setEmpresa(empresa);

            String sql = "select NUMERO_DAV from DAV_CABECALHO where ID = (select max(ID) from DAV_CABECALHO)";

            String numeroUltimoDav = (String) session.createSQLQuery(sql).uniqueResult();
            if (numeroUltimoDav != null) {
                if (numeroUltimoDav.equals("9999999999")) {
                    davCabecalho.setNumeroDav("0000000001");
                } else {
                    int numeroNovoDav = Integer.valueOf(numeroUltimoDav) + 1;
                    DecimalFormat formatoDav = new DecimalFormat("0000000000");
                    davCabecalho.setNumeroDav(formatoDav.format(numeroNovoDav));
                }
            } else {
                davCabecalho.setNumeroDav("0000000001");
            }

            String formula = janelaDetalhe.getFormula();
            for (DavDetalheVO davDetalhe : listaDavDetalhe) {
                davDetalhe.setDavCabecalho(davCabecalho);
                davDetalhe.setNumeroDav(davCabecalho.getNumeroDav());
                davDetalhe.setDataEmissao(davCabecalho.getDataEmissao());
                davDetalhe.setMesclaProduto("N");
                davDetalhe.setServicoFormula(formula);
            }

            davCabecalho.setListaDavDetalhe(listaDavDetalhe);
            session.save(davCabecalho);

            session.getTransaction().commit();

            return new VOResponse(newPersistentObject);
        } catch (Exception e) {
            e.printStackTrace();
            session.getTransaction().rollback();
            return new ErrorResponse("Erro ao gerar o DAV.\n" + e.getMessage());
        } finally {
            session.close();
        }
    }

    @Override
    public void afterInsertData() {
        JOptionPane.showMessageDialog(janelaDetalhe, "DAV gerado com sucesso!", "Informacao do Sistema", JOptionPane.INFORMATION_MESSAGE);
        janelaDetalhe.dispose();
    }

    @Override
    public boolean beforeEditData(Form form) {
        if (davCabecalho.getSituacao().equals("P")) {
            return true;
        } else {
            JOptionPane.showMessageDialog(janelaDetalhe, "Situação do DAV não permite alteração.", "Informação do Sistema", JOptionPane.INFORMATION_MESSAGE);
            return false;
        }
    }

    @Override
    public Response updateRecord(ValueObject oldPersistentObject, ValueObject persistentObject) throws Exception {
        BigDecimal valorTotal = BigDecimal.ZERO;
        List<DavDetalheVO> listaDavDetalhe = janelaDetalhe.getGrid1().getVOListTableModel().getDataVector();
        for (DavDetalheVO d : listaDavDetalhe) {
            if (d.getCancelado().equals("N")) {
                valorTotal = valorTotal.add(d.getValorTotal(), MathContext.DECIMAL64);
                valorTotal = valorTotal.setScale(2, RoundingMode.DOWN);
            }
        }
        davCabecalho.setValor(valorTotal);
        davCabecalho.setSubtotal(valorTotal);

        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.getTransaction().begin();

            for (DavDetalheVO davDetalhe : listaDavDetalhe) {
                davDetalhe.setDavCabecalho(davCabecalho);
                davDetalhe.setNumeroDav(davCabecalho.getNumeroDav());
                davDetalhe.setDataEmissao(davCabecalho.getDataEmissao());
                davDetalhe.setMesclaProduto("N");
            }
            davCabecalho.setListaDavDetalhe(listaDavDetalhe);
            session.merge(davCabecalho);

            session.getTransaction().commit();

            return new VOResponse(davCabecalho);
        } catch (Exception e) {
            e.printStackTrace();
            session.getTransaction().rollback();
            return new ErrorResponse("Erro ao alterar o DAV.\n" + e.getMessage());
        } finally {
            session.close();
        }
    }

    @Override
    public void afterEditData() {
        JOptionPane.showMessageDialog(janelaDetalhe, "DAV alterado com sucesso!", "Informacao do Sistema", JOptionPane.INFORMATION_MESSAGE);
        janelaDetalhe.dispose();
    }

}
