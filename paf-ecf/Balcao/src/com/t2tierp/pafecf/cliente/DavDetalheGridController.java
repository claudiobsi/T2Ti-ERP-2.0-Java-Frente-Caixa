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

import com.t2tierp.pafecf.java.DavDetalheAlteracaoVO;
import com.t2tierp.pafecf.java.DavDetalheVO;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.swing.JOptionPane;
import org.openswing.swing.client.GridControl;
import org.openswing.swing.message.receive.java.Response;
import org.openswing.swing.message.receive.java.VOListResponse;
import org.openswing.swing.table.client.GridController;
import org.openswing.swing.table.java.GridDataLocator;

public class DavDetalheGridController extends GridController implements GridDataLocator {

    private BigDecimal valorTotal = BigDecimal.ZERO;
    private DavDetalhe grid;
    private Integer item;
    private List<DavDetalheVO> listaDav;

    public DavDetalheGridController(DavDetalhe grid) {
        this.grid = grid;
        item = 0;
        listaDav = new ArrayList<>();
    }

    public Response loadData(int action, int startIndex, Map filteredColumns, ArrayList currentSortedColumns, ArrayList currentSortedVersusColumns, Class valueObjectType, Map otherGridParams) {
        return new VOListResponse(listaDav, false, listaDav.size());
    }

    @Override
    public void loadDataCompleted(boolean error) {
        atualizaTotais();
    }
    
    @Override
    public void afterReloadGrid() {
        
    }
    
    @Override
    public Response insertRecords(int[] rowNumbers, ArrayList newValueObjects) throws Exception {
        BigDecimal valorTotalProduto;
        DavDetalheVO davDetalhe;
        for (int i = 0; i < newValueObjects.size(); i++) {
            item++;
            davDetalhe = (DavDetalheVO) newValueObjects.get(i);
            davDetalhe.setListaDavDetalheAlteracao(new ArrayList<DavDetalheAlteracaoVO>());
            davDetalhe.setItem(item);
            valorTotalProduto = davDetalhe.getValorUnitario().multiply(davDetalhe.getQuantidade(), MathContext.DECIMAL64);
            valorTotalProduto = valorTotalProduto.setScale(2, RoundingMode.DOWN);
            davDetalhe.setValorTotal(valorTotalProduto);
            davDetalhe.setCancelado("N");

            davDetalhe.getListaDavDetalheAlteracao().add(registroAlteracao(davDetalhe, "I"));

            listaDav.add(davDetalhe);
        }
        return new VOListResponse(newValueObjects, false, newValueObjects.size());
    }

    @Override
    public void afterInsertGrid(GridControl grid) {
        this.grid.getGrid1().reloadData();
    }

    @Override
    public boolean beforeEditGrid(GridControl grid) {
        DavDetalheVO davDetalhe = listaDav.get(grid.getSelectedRow());
        if (davDetalhe.getCancelado().equals("S")) {
            JOptionPane.showMessageDialog(null, "Este item não pode ser alterado.", "Informação do Sistema", JOptionPane.WARNING_MESSAGE);
            return false;
        } else {
            return true;
        }
    }

    @Override
    public Response updateRecords(int[] rowNumbers, ArrayList oldPersistentObjects, ArrayList persistentObjects) throws Exception {
        BigDecimal valorTotalProduto;
        DavDetalheVO davDetalhe;
        for (int i = 0; i < persistentObjects.size(); i++) {
            davDetalhe = (DavDetalheVO) persistentObjects.get(i);
            valorTotalProduto = davDetalhe.getValorUnitario().multiply(davDetalhe.getQuantidade(), MathContext.DECIMAL64);
            valorTotalProduto = valorTotalProduto.setScale(2, RoundingMode.DOWN);
            davDetalhe.setValorTotal(valorTotalProduto);
            davDetalhe.getListaDavDetalheAlteracao().add(registroAlteracao(davDetalhe, "A"));
        }

        return new VOListResponse(persistentObjects, false, persistentObjects.size());
    }

    @Override
    public void afterEditGrid(GridControl grid) {
        this.grid.getGrid1().reloadData();
    }
    
    @Override
    public Response deleteRecords(ArrayList persistentObjects) throws Exception {
        for (int i = 0; i < persistentObjects.size(); i++) {
            valorTotal = valorTotal.subtract(((DavDetalheVO) persistentObjects.get(i)).getValorTotal(), MathContext.DECIMAL64);
            valorTotal = valorTotal.setScale(2, RoundingMode.DOWN);
        }
        DecimalFormat formato = new DecimalFormat("#,##0.00");
        grid.setValorTotal(formato.format(valorTotal));
        DavDetalheVO davDetalhe = listaDav.get(grid.getGrid1().getSelectedRow());
        davDetalhe.setCancelado("S");
        davDetalhe.getListaDavDetalheAlteracao().add(registroAlteracao(davDetalhe, "E"));
        return new VOListResponse(persistentObjects, false, persistentObjects.size());
    }

    @Override
    public void afterDeleteGrid() {
        this.grid.getGrid1().reloadData();
    }

    private DavDetalheAlteracaoVO registroAlteracao(DavDetalheVO davDetalhe, String tipo) {
        Date dataAtual = new Date();
        DavDetalheAlteracaoVO davAlteracao = new DavDetalheAlteracaoVO();
        davAlteracao.setDavDetalhe(davDetalhe);
        davAlteracao.setTipoAlteracao(tipo);
        davAlteracao.setDataAlteracao(dataAtual);
        davAlteracao.setHoraAlteracao(new SimpleDateFormat("HH:mm:ss").format(dataAtual));

        return davAlteracao;
    }

    public void setListaDav(List<DavDetalheVO> listaDav) {
        this.listaDav = listaDav;
        this.item = listaDav.size();
        this.grid.getGrid1().reloadData();
    }
    
    private void atualizaTotais() {
        valorTotal = BigDecimal.ZERO;
        for (DavDetalheVO d : listaDav) {
            if (d.getCancelado().equals("N")) {
                valorTotal = valorTotal.add(d.getValorTotal(), MathContext.DECIMAL64);
                valorTotal = valorTotal.setScale(2, RoundingMode.DOWN);
            }
        }
        DecimalFormat formato = new DecimalFormat("#,##0.00");
        grid.setValorTotal(formato.format(valorTotal));
    }
    
}
