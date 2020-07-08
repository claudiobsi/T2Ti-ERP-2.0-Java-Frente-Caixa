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

import com.t2tierp.padrao.cliente.HibernateUtil;
import com.t2tierp.pafecf.java.PreVendaCabecalhoVO;
import java.beans.PropertyVetoException;
import java.util.ArrayList;
import java.util.Map;
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

public class PreVendaGridController extends GridController implements GridDataLocator {

    private PreVendaGrid grid;

    public PreVendaGridController() {
        grid = new PreVendaGrid(this);
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
            String baseSQL = "select PRE_VENDA from com.t2tierp.pafecf.java.PreVendaCabecalhoVO as PRE_VENDA";

            session = HibernateUtil.getSessionFactory().openSession();
            Response res = HibernateUtils.getBlockFromQuery(
                    pars.getAction(),
                    pars.getStartPos(),
                    50, // block size...
                    pars.getFilteredColumns(),
                    pars.getCurrentSortedColumns(),
                    pars.getCurrentSortedVersusColumns(),
                    com.t2tierp.pafecf.java.PreVendaCabecalhoVO.class,
                    baseSQL,
                    new Object[0],
                    new Type[0],
                    "PRE_VENDA",
                    HibernateUtil.getSessionFactory(),
                    session);
            return res;
        } catch (Exception e) {
            e.printStackTrace();
            return new ErrorResponse("Erro ao listar as Pre-Vendas.\n" + e.getMessage());
        } finally {
            session.close();
        }
    }

    @Override
    public boolean beforeInsertGrid(GridControl grid) {
        new PreVendaDetalheController(null);
        return false;
    }

    @Override
    public void doubleClick(int rowNumber, ValueObject persistentObject) {
        PreVendaCabecalhoVO preVendaCabecalho = (PreVendaCabecalhoVO) persistentObject;
        new PreVendaDetalheController(preVendaCabecalho);
    }
}
