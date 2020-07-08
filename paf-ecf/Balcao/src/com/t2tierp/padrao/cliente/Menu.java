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
package com.t2tierp.padrao.cliente;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Map;
import javax.swing.tree.DefaultTreeModel;
import org.openswing.swing.mdi.client.*;
import org.openswing.swing.permissions.client.*;
import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import org.openswing.swing.domains.java.Domain;
import org.openswing.swing.internationalization.java.Language;
import org.openswing.swing.internationalization.java.XMLResourcesFactory;
import org.openswing.swing.mdi.java.ApplicationFunction;
import org.openswing.swing.tree.java.OpenSwingTreeNode;
import org.openswing.swing.util.client.ClientSettings;

public class Menu implements LoginController, MDIController {

    private String nomeUsuario = null;
    private String senha = null;
    private Fachada fachada = new Fachada();
    private Hashtable domains = new Hashtable();

    public Menu() {
        LoginDialog loginDialog = new LoginDialog(null, false, this);
    }

    public static void main(String args[]) {
        new Menu();
    }

    public boolean authenticateUser(Map loginInfo) throws Exception {
        nomeUsuario = (String) loginInfo.get("username");
        senha = (String) loginInfo.get("password");

        if (nomeUsuario == null || senha == null) {
            return false;
        }

        Hashtable xmlFiles = new Hashtable();
        xmlFiles.put("EN", "Resources_en.xml");
        xmlFiles.put("PT_BR", "Resources_pt_br.xml");
        ClientSettings clientSettings = new ClientSettings(new XMLResourcesFactory(xmlFiles, true), domains);
        ClientSettings.PERC_TREE_FOLDER = "folder3.gif";
        ClientSettings.BACKGROUND = "background4.jpg";
        ClientSettings.TREE_BACK = "treeback2.jpg";
        ClientSettings.getInstance().setLanguage("PT_BR");
        ClientSettings.MAX_NR_OF_LOOPS_IN_ANALYZE_VO = 4;

        try {
            // Exercício: Implemente a rotina de login
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public int getMaxAttempts() {
        return 3;
    }

    public void loginSuccessful(Map loginInfo) {
        Domain tipoDAV = new Domain("tipoDAV");
        tipoDAV.addDomainPair("0", "Normal");
        tipoDAV.addDomainPair("1", "Fármacia Manipulação");
        tipoDAV.addDomainPair("2", "DAV-OS");
        
        domains.clear();
        domains.put(tipoDAV.getDomainId(), tipoDAV);
        
        MDIFrame mdi = new MDIFrame(this);

        mdi.addButtonToToolBar("telaDocumento04.png", "Emissão de DAV").addActionListener(new DavAction());
        mdi.addButtonToToolBar("telaDocumento07.png", "Emissão de Pré-Venda").addActionListener(new PreVendaAction());
        mdi.addSeparatorToToolBar();
        mdi.addButtonToToolBar("telaSair01.png", "Sair do Sistema").addActionListener(new SairAction());
    }

    class DavAction implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            fachada.getDav();
        }
    }

    class PreVendaAction implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            fachada.getPreVenda();
        }
    }

    class SairAction implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            stopApplication();
        }
    }

    public void stopApplication() {
        System.exit(0);
    }

    public void afterMDIcreation(MDIFrame frame) {
        GenericStatusPanel userPanel = new GenericStatusPanel();
        userPanel.setColumns(12);
        MDIFrame.addStatusComponent(userPanel);
        userPanel.setText(nomeUsuario);
        MDIFrame.addStatusComponent(new Clock());
    }

    public String getAboutImage() {
        return "about.jpg";
    }

    public String getAboutText() {
        return "T2TiPDV - Balcão";
    }

    public ClientFacade getClientFacade() {
        return fachada;
    }

    public int getExtendedState() {
        return JFrame.MAXIMIZED_BOTH;
    }

    public ArrayList getLanguages() {
        ArrayList list = new ArrayList();
        list.add(new Language("EN", "English"));
        list.add(new Language("PT_BR", "Português do Brasil"));
        return list;
    }

    public String getMDIFrameTitle() {
        return "T2TiPDV - Balcão";
    }

    public boolean viewChangeLanguageInMenuBar() {
        return true;
    }

    public boolean viewFileMenu() {
        return true;
    }

    public boolean viewFunctionsInMenuBar() {
        return true;
    }

    public boolean viewFunctionsInTreePanel() {
        return false;
    }

    public JDialog viewLoginDialog(JFrame parentFrame) {
        JDialog loginDialog = new LoginDialog(null, false, this);
        return loginDialog;
    }

    public boolean viewLoginInMenuBar() {
        return true;
    }

    public boolean viewOpenedWindowIcons() {
        return true;
    }

    public DefaultTreeModel getApplicationFunctions() {
        DefaultMutableTreeNode root = new OpenSwingTreeNode();
        DefaultTreeModel model = new DefaultTreeModel(root);

        ApplicationFunction n1 = new ApplicationFunction("Balcão - Opções", null);
        ApplicationFunction n11 = new ApplicationFunction("DAV", "dav", null, "getDav");
        ApplicationFunction n12 = new ApplicationFunction("Pré-Venda", "preVenda", null, "getPreVenda");
        //ApplicationFunction n13 = new ApplicationFunction("Produtos", "produto", null, "getProduto");
        //ApplicationFunction n14 = new ApplicationFunction("Carga PDV", "cargaPdv", null, "getCargaPdv");

        n1.add(n11);
        n1.add(n12);
        //n1.add(n13);
        //n1.add(n14);

        root.add(n1);

        return model;
    }
}