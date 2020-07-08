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
import com.t2tierp.padrao.cliente.HibernateUtil;
import com.t2tierp.pafecf.java.DavCabecalhoVO;
import java.beans.PropertyVetoException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JOptionPane;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.view.JasperViewer;
import org.hibernate.classic.Session;
import org.hibernate.type.Type;
import org.openswing.swing.client.GridControl;
import org.openswing.swing.mdi.client.MDIFrame;
import org.openswing.swing.message.receive.java.ErrorResponse;
import org.openswing.swing.message.receive.java.Response;
import org.openswing.swing.message.receive.java.ValueObject;
import org.openswing.swing.message.send.java.GridParams;
import org.openswing.swing.table.client.GridController;
import org.openswing.swing.table.java.GridDataLocator;
import org.openswing.swing.util.server.HibernateUtils;

public class DavGridController extends GridController implements GridDataLocator {

    private DavGrid grid;
    private EmpresaVO empresa;

    public DavGridController() {
        buscaEmpresa();
        grid = new DavGrid(this);
        MDIFrame.add(grid);
        try {
            grid.setMaximum(true);
        } catch (PropertyVetoException ex) {
        }
    }

    public Response loadData(int action, int startIndex, Map filteredColumns, ArrayList currentSortedColumns, ArrayList currentSortedVersusColumns, Class valueObjectType, Map otherGridParams) {
        Session session = null;
        GridParams pars = new GridParams(action, startIndex, filteredColumns, currentSortedColumns, currentSortedVersusColumns, otherGridParams);
        try {
            String baseSQL = "select DAV from com.t2tierp.pafecf.java.DavCabecalhoVO as DAV";

            session = HibernateUtil.getSessionFactory().openSession();
            Response res = HibernateUtils.getBlockFromQuery(
                    pars.getAction(),
                    pars.getStartPos(),
                    50, // block size...
                    pars.getFilteredColumns(),
                    pars.getCurrentSortedColumns(),
                    pars.getCurrentSortedVersusColumns(),
                    com.t2tierp.pafecf.java.DavCabecalhoVO.class,
                    baseSQL,
                    new Object[0],
                    new Type[0],
                    "DAV",
                    HibernateUtil.getSessionFactory(),
                    session);
            return res;
        } catch (Exception e) {
            e.printStackTrace();
            return new ErrorResponse("Erro ao listar os DAVs.\n" + e.getMessage());
        } finally {
            session.close();
        }
    }

    @Override
    public boolean beforeInsertGrid(GridControl grid) {
        new DavDetalheController(null);
        return false;
    }

    @Override
    public void doubleClick(int rowNumber, ValueObject persistentObject) {
        DavCabecalhoVO davCabecalho = (DavCabecalhoVO) persistentObject;
        new DavDetalheController(davCabecalho);
    }

    public void imprimeDav() {
        if (grid.getGrid1().getSelectedRow() == -1) {
            JOptionPane.showMessageDialog(grid, "Selecione um item da lista", "Informação do Sistema", JOptionPane.INFORMATION_MESSAGE);
        } else {
            DavCabecalhoVO davCabecalho = (DavCabecalhoVO) grid.getGrid1().getVOListTableModel().getObjectForRow(grid.getGrid1().getSelectedRow());
            try {
                if (empresa == null) {
                    JOptionPane.showMessageDialog(grid, "Empresa não cadastrada. Entre em contato com a Software House.", "Erro do Sistema", JOptionPane.ERROR_MESSAGE);
                } else {
                    HashMap parametros = new HashMap();
                    parametros.put("NOME_EMPRESA", empresa.getRazaoSocial());
                    parametros.put("CNPJ_EMPRESA", empresa.getCnpj());
                    parametros.put("CODIGO_DAV", davCabecalho.getId());
                    parametros.put("NUMERO_DAV", davCabecalho.getNumeroDav());
                    parametros.put("NOME_DESTINATARIO", davCabecalho.getNomeDestinatario());
                    parametros.put("CPF_CNPJ_DESTINATARIO", davCabecalho.getCpfCnpjDestinatario());

                    String nomeRelatorio = "/relatorios/DAV.jasper";
                    if ((davCabecalho.getTipo().equals("1"))) {
                        nomeRelatorio = "/relatorios/dav-farmacia.jasper";
                    }else if ((davCabecalho.getTipo().equals("2"))) {
                        nomeRelatorio = "/relatorios/dav-os.jasper";
                    }
                    InputStream ip = this.getClass().getResourceAsStream(nomeRelatorio);
                    JasperPrint jasperPrint = JasperFillManager.fillReport(ip, parametros, new JRBeanCollectionDataSource(davCabecalho.getListaDavDetalhe()));
                    JasperViewer.viewReport(jasperPrint, false);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(grid, "Erro ao imprimir o DAV.\n" + ex.getMessage(), "Erro do Sistema", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void buscaEmpresa() {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            empresa = (EmpresaVO) session.get(EmpresaVO.class, 1);
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(grid, "Erro ao imprimir o DAV.\n" + ex.getMessage(), "Erro do Sistema", JOptionPane.ERROR_MESSAGE);
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }
}
