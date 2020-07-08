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

import com.t2tierp.pafecf.java.PreVendaDetalheVO;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.openswing.swing.client.GridControl;
import org.openswing.swing.message.receive.java.Response;
import org.openswing.swing.message.receive.java.VOListResponse;
import org.openswing.swing.table.client.GridController;
import org.openswing.swing.table.java.GridDataLocator;

public class PreVendaDetalheGridController extends GridController implements GridDataLocator {

    private BigDecimal valorTotal = BigDecimal.ZERO;
    private PreVendaDetalhe grid;
    private Integer item;
    private List<PreVendaDetalheVO> listaPreVenda;

    public PreVendaDetalheGridController(PreVendaDetalhe grid) {
        this.grid = grid;
        item = 0;
        listaPreVenda = new ArrayList<>();
    }

    public Response loadData(int action, int startIndex, Map filteredColumns, ArrayList currentSortedColumns, ArrayList currentSortedVersusColumns, Class valueObjectType, Map otherGridParams) {
        return new VOListResponse(listaPreVenda, false, listaPreVenda.size());
    }

    @Override
    public void loadDataCompleted(boolean error) {
        atualizaTotais();
    }
   
    @Override
    public Response insertRecords(int[] rowNumbers, ArrayList newValueObjects) throws Exception {
        BigDecimal valorTotalProduto;
        PreVendaDetalheVO preVendaDetalhe;
        for (int i = 0; i < newValueObjects.size(); i++) {
            item++;
            preVendaDetalhe = (PreVendaDetalheVO) newValueObjects.get(i);
            preVendaDetalhe.setItem(item);
            valorTotalProduto = preVendaDetalhe.getValorUnitario().multiply(preVendaDetalhe.getQuantidade(), MathContext.DECIMAL64);
            valorTotalProduto = valorTotalProduto.setScale(2, RoundingMode.DOWN);
            preVendaDetalhe.setValorTotal(valorTotalProduto);
            preVendaDetalhe.setCancelado("N");

            listaPreVenda.add(preVendaDetalhe);
        }
        return new VOListResponse(newValueObjects, false, newValueObjects.size());
    }

    @Override
    public void afterInsertGrid(GridControl grid) {
        this.grid.getGrid1().reloadData();
    }
    
    @Override
    public Response deleteRecords(ArrayList persistentObjects) throws Exception {
        for (int i = 0; i < persistentObjects.size(); i++) {
            valorTotal = valorTotal.subtract(((PreVendaDetalheVO) persistentObjects.get(i)).getValorTotal(), MathContext.DECIMAL64);
            valorTotal = valorTotal.setScale(2, RoundingMode.DOWN);
        }
        DecimalFormat formato = new DecimalFormat("#,##0.00");
        grid.setValorTotal(formato.format(valorTotal));
        PreVendaDetalheVO preVendaDetalhe = listaPreVenda.get(grid.getGrid1().getSelectedRow());
        preVendaDetalhe.setCancelado("S");
        return new VOListResponse(persistentObjects, false, persistentObjects.size());
    }

    @Override
    public void afterDeleteGrid() {
        this.grid.getGrid1().reloadData();
    }

    public void setListaPreVenda(List<PreVendaDetalheVO> listaPreVenda) {
        this.listaPreVenda = listaPreVenda;
        this.item = listaPreVenda.size();
        this.grid.getGrid1().reloadData();
    }
    
    private void atualizaTotais() {
        valorTotal = BigDecimal.ZERO;
        for (PreVendaDetalheVO d : listaPreVenda) {
            if (d.getCancelado().equals("N")) {
                valorTotal = valorTotal.add(d.getValorTotal(), MathContext.DECIMAL64);
                valorTotal = valorTotal.setScale(2, RoundingMode.DOWN);
            }
        }
        DecimalFormat formato = new DecimalFormat("#,##0.00");
        grid.setValorTotal(formato.format(valorTotal));
    }
    
}
