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
package com.t2tierp.controller.nfce;

import java.io.StringWriter;
import java.math.BigDecimal;
import java.security.KeyStore;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Set;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Marshaller;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;

import br.inf.portalfiscal.nfe.envinfe.ObjectFactory;
import br.inf.portalfiscal.nfe.envinfe.TEnderEmi;
import br.inf.portalfiscal.nfe.envinfe.TEndereco;
import br.inf.portalfiscal.nfe.envinfe.TEnviNFe;
import br.inf.portalfiscal.nfe.envinfe.TNFe;
import br.inf.portalfiscal.nfe.envinfe.TUf;
import br.inf.portalfiscal.nfe.envinfe.TUfEmi;

import com.t2tierp.model.bean.cadastros.Empresa;
import com.t2tierp.model.bean.cadastros.EmpresaEndereco;
import com.t2tierp.model.bean.nfe.NfeCabecalho;
import com.t2tierp.model.bean.nfe.NfeDestinatario;
import com.t2tierp.model.bean.nfe.NfeDetalhe;
import com.t2tierp.model.bean.nfe.NfeFormaPagamento;
import com.t2tierp.util.Biblioteca;

public class GeraXmlEnvio {

	private SimpleDateFormat formatoDataHora = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");
	
    public String gerarXmlEnvio(Empresa empresa, NfeCabecalho nfeCabecalho, String alias, KeyStore ks, char[] senha, String idToken, String token) throws Exception {
        NfeDestinatario destinatario = nfeCabecalho.getDestinatario();
        List<NfeDetalhe> listaNfeDetalhe = nfeCabecalho.getListaNfeDetalhe();
        Set<NfeFormaPagamento> listaNfeFormaPagamento = nfeCabecalho.getListaNfeFormaPagamento();

        EmpresaEndereco empresaEndereco = new EmpresaEndereco();
        for (EmpresaEndereco e : empresa.getListaEndereco()) {
        	if (e.getPrincipal().equals("S")) {
        		empresaEndereco = e;
        		break;
        	}
        }
        
        DecimalFormatSymbols simboloDecimal = DecimalFormatSymbols.getInstance();
        simboloDecimal.setDecimalSeparator('.');
        DecimalFormat formatoQuantidade = new DecimalFormat("0.0000", simboloDecimal);
        DecimalFormat formatoValor = new DecimalFormat("0.00", simboloDecimal);

        ObjectFactory factory = new ObjectFactory();

        TNFe nfe = factory.createTNFe();

        TNFe.InfNFe infNfe = new TNFe.InfNFe();
        infNfe.setId("NFe" + nfeCabecalho.getChaveAcesso() + nfeCabecalho.getDigitoChaveAcesso());
        infNfe.setVersao("3.10");
        nfe.setInfNFe(infNfe);

        TNFe.InfNFe.Ide ide = new TNFe.InfNFe.Ide();
        nfe.getInfNFe().setIde(ide);

        //cabecalho
        ide.setCUF(empresa.getCodigoIbgeUf().toString());
        ide.setCNF(nfeCabecalho.getCodigoNumerico());
        ide.setNatOp(nfeCabecalho.getNaturezaOperacao());
        ide.setIndPag(String.valueOf(nfeCabecalho.getIndicadorFormaPagamento()));
        ide.setMod(nfeCabecalho.getCodigoModelo());
        ide.setSerie(Integer.valueOf(nfeCabecalho.getSerie()).toString());
        ide.setNNF(Integer.valueOf(nfeCabecalho.getNumero()).toString());
        ide.setDhEmi(formatoDataHora.format(nfeCabecalho.getDataHoraEmissao()));
        ide.setTpNF(String.valueOf(nfeCabecalho.getTipoOperacao()));
        ide.setCMunFG(nfeCabecalho.getCodigoMunicipio().toString());
        ide.setTpImp(String.valueOf(nfeCabecalho.getFormatoImpressaoDanfe()));
        ide.setTpEmis(String.valueOf(nfeCabecalho.getTipoEmissao()));
        ide.setVerProc(nfeCabecalho.getVersaoProcessoEmissao());
        ide.setTpAmb(String.valueOf(nfeCabecalho.getAmbiente()));
        ide.setFinNFe(String.valueOf(nfeCabecalho.getFinalidadeEmissao()));
        ide.setProcEmi(String.valueOf(nfeCabecalho.getProcessoEmissao()));
        ide.setCDV(nfeCabecalho.getDigitoChaveAcesso());
        ide.setIdDest(String.valueOf(nfeCabecalho.getLocalDestino()));
        ide.setIndFinal(String.valueOf(nfeCabecalho.getConsumidorOperacao()));
        ide.setIndPres(String.valueOf(nfeCabecalho.getConsumidorPresenca()));

        //NFe Cabecalho -- Totais
        TNFe.InfNFe.Total total = new TNFe.InfNFe.Total();
        nfe.getInfNFe().setTotal(total);

        TNFe.InfNFe.Total.ICMSTot icmsTot = new TNFe.InfNFe.Total.ICMSTot();
        nfe.getInfNFe().getTotal().setICMSTot(icmsTot);

        total.getICMSTot().setVBC(formatoValor.format(nfeCabecalho.getBaseCalculoIcms()));
        total.getICMSTot().setVICMS(formatoValor.format(nfeCabecalho.getValorIcms()));
        total.getICMSTot().setVBCST(formatoValor.format(nfeCabecalho.getBaseCalculoIcmsSt()));
        total.getICMSTot().setVST(formatoValor.format(nfeCabecalho.getValorIcmsSt()));
        total.getICMSTot().setVICMSDeson(formatoValor.format(nfeCabecalho.getValorIcmsDesonerado()));
        total.getICMSTot().setVProd(formatoValor.format(nfeCabecalho.getValorTotalProdutos()));
        total.getICMSTot().setVFrete(formatoValor.format(nfeCabecalho.getValorFrete()));
        total.getICMSTot().setVSeg(formatoValor.format(nfeCabecalho.getValorSeguro()));
        total.getICMSTot().setVDesc(formatoValor.format(nfeCabecalho.getValorDesconto()));
        total.getICMSTot().setVII(formatoValor.format(nfeCabecalho.getValorImpostoImportacao()));
        total.getICMSTot().setVIPI(formatoValor.format(nfeCabecalho.getValorIpi()));
        total.getICMSTot().setVPIS(formatoValor.format(nfeCabecalho.getValorPis()));
        total.getICMSTot().setVCOFINS(formatoValor.format(nfeCabecalho.getValorCofins()));
        total.getICMSTot().setVOutro(formatoValor.format(nfeCabecalho.getValorDespesasAcessorias()));
        total.getICMSTot().setVNF(formatoValor.format(nfeCabecalho.getValorTotal()));

        // lei da transparencia de impostos
        // Exerc�cio: avalie se essa informa��o est� sendo repassada corretamente
        total.getICMSTot().setVTotTrib(formatoValor.format(nfeCabecalho.getValorIcms()));

        if (nfeCabecalho.getValorServicos().compareTo(BigDecimal.ZERO) > 0) {
            TNFe.InfNFe.Total.ISSQNtot issqnTot = new TNFe.InfNFe.Total.ISSQNtot();
            nfe.getInfNFe().getTotal().setISSQNtot(issqnTot);

            total.getISSQNtot().setVServ(formatoValor.format(nfeCabecalho.getValorServicos()));
            total.getISSQNtot().setVBC(formatoValor.format(nfeCabecalho.getBaseCalculoIssqn()));
            total.getISSQNtot().setVISS(formatoValor.format(nfeCabecalho.getValorIssqn()));
            total.getISSQNtot().setVPIS(formatoValor.format(nfeCabecalho.getValorPisIssqn()));
            total.getISSQNtot().setVCOFINS(formatoValor.format(nfeCabecalho.getValorCofinsIssqn()));
        }

        if (nfeCabecalho.getValorRetidoPis().compareTo(BigDecimal.ZERO) > 0) {
            TNFe.InfNFe.Total.RetTrib retTrib = new TNFe.InfNFe.Total.RetTrib();
            nfe.getInfNFe().getTotal().setRetTrib(retTrib);

            total.getRetTrib().setVRetPIS(formatoValor.format(nfeCabecalho.getValorRetidoPis()));
            total.getRetTrib().setVRetCOFINS(formatoValor.format(nfeCabecalho.getValorRetidoCofins()));
            total.getRetTrib().setVRetCSLL(formatoValor.format(nfeCabecalho.getValorRetidoCsll()));
            total.getRetTrib().setVBCIRRF(formatoValor.format(nfeCabecalho.getBaseCalculoIrrf()));
            total.getRetTrib().setVIRRF(formatoValor.format(nfeCabecalho.getValorRetidoIrrf()));
            total.getRetTrib().setVBCRetPrev(formatoValor.format(nfeCabecalho.getBaseCalculoPrevidencia()));
            total.getRetTrib().setVRetPrev(formatoValor.format(nfeCabecalho.getValorRetidoPrevidencia()));
        }

        //emitente
        TNFe.InfNFe.Emit emit = new TNFe.InfNFe.Emit();
        nfe.getInfNFe().setEmit(emit);
        TEnderEmi enderecoEmi = new TEnderEmi();
        nfe.getInfNFe().getEmit().setEnderEmit(enderecoEmi);

        emit.setCNPJ(empresa.getCnpj());
        emit.setXNome(empresa.getRazaoSocial());
        emit.setXFant(empresa.getNomeFantasia());
        emit.getEnderEmit().setXLgr(empresaEndereco.getLogradouro());
        emit.getEnderEmit().setNro(empresaEndereco.getNumero());
        emit.getEnderEmit().setXCpl(empresaEndereco.getComplemento());
        emit.getEnderEmit().setXBairro(empresaEndereco.getBairro());
        emit.getEnderEmit().setCMun(empresa.getCodigoIbgeCidade().toString());
        emit.getEnderEmit().setXMun(empresaEndereco.getCidade());
        emit.getEnderEmit().setUF(TUfEmi.fromValue(empresaEndereco.getUf()));
        emit.getEnderEmit().setCEP(empresaEndereco.getCep());
        emit.getEnderEmit().setCPais("1058");
        emit.getEnderEmit().setXPais("BRASIL");
        emit.getEnderEmit().setFone(empresaEndereco.getFone());
        emit.setIE(empresa.getInscricaoEstadual());
        emit.setIM(empresa.getInscricaoMunicipal());
        emit.setCRT(empresa.getCrt());
        emit.setCNAE(empresa.getCodigoCnaePrincipal());

        //destinatario
        if (destinatario != null && destinatario.getCpfCnpj() != null) {
            TNFe.InfNFe.Dest dest = new TNFe.InfNFe.Dest();
            nfe.getInfNFe().setDest(dest);

            if (destinatario.getCpfCnpj().length() == 14) {
                dest.setCNPJ(destinatario.getCpfCnpj());
            } else {
                dest.setCPF(destinatario.getCpfCnpj());
            }
            if (nfeCabecalho.getAmbiente() == 2) {
                dest.setXNome("NF-E EMITIDA EM AMBIENTE DE HOMOLOGACAO - SEM VALOR FISCAL");
            } else {
                dest.setXNome(destinatario.getNome());
            }
            if (destinatario.getLogradouro() != null) {
                TEndereco enderecoDest = new TEndereco();
                nfe.getInfNFe().getDest().setEnderDest(enderecoDest);

                dest.getEnderDest().setXLgr(destinatario.getLogradouro());
                dest.getEnderDest().setNro(destinatario.getNumero());
                if (destinatario.getComplemento() != null) {
                    dest.getEnderDest().setXCpl(destinatario.getComplemento().equals("") ? null : destinatario.getComplemento());
                }
                dest.getEnderDest().setXBairro(destinatario.getBairro());
                dest.getEnderDest().setCMun(destinatario.getCodigoMunicipio() == null ? null : destinatario.getCodigoMunicipio().toString());
                dest.getEnderDest().setXMun(destinatario.getNomeMunicipio());
                dest.getEnderDest().setUF(destinatario.getUf() == null ? null : TUf.fromValue(destinatario.getUf()));
                dest.getEnderDest().setCEP(destinatario.getCep());
                dest.getEnderDest().setCPais(destinatario.getCodigoPais() == null ? null : destinatario.getCodigoPais().toString());
                dest.getEnderDest().setXPais(destinatario.getNomePais());
                if (destinatario.getTelefone() != null) {
                    dest.getEnderDest().setFone(destinatario.getTelefone().equals("") ? null : destinatario.getTelefone());
                }
            }
            //NFC-e n�o pode ser emitida para contribuinte do ICMS
            dest.setIndIEDest("9");
            if (destinatario.getEmail() != null) {
                dest.setEmail(destinatario.getEmail().equals("") ? null : destinatario.getEmail());
            }
        }

        //Transporte
        TNFe.InfNFe.Transp transp = new TNFe.InfNFe.Transp();
        infNfe.setTransp(transp);

        // NFC-e n�o pode ter FRETE
        transp.setModFrete("9");//sem frete

        //formas de pagamento
        for (NfeFormaPagamento p : listaNfeFormaPagamento) {
            TNFe.InfNFe.Pag pag = factory.createTNFeInfNFePag();
            pag.setTPag(p.getForma());
            pag.setVPag(formatoValor.format(p.getValor()));

            infNfe.getPag().add(pag);
        }

        //detalhes
        List<TNFe.InfNFe.Det> listaDet = nfe.getInfNFe().getDet();
        TNFe.InfNFe.Det det;
        NfeDetalhe nfeDetalhe;
        for (int i = 0; i < listaNfeDetalhe.size(); i++) {
            nfeDetalhe = listaNfeDetalhe.get(i);

            det = factory.createTNFeInfNFeDet();
            TNFe.InfNFe.Det.Prod prod = factory.createTNFeInfNFeDetProd();
            det.setNItem(nfeDetalhe.getNumeroItem().toString());
            det.setProd(prod);

            det.getProd().setCProd(nfeDetalhe.getGtin());
            det.getProd().setCEAN(nfeDetalhe.getGtin());
            if (nfeCabecalho.getAmbiente() == 2) {
            	det.getProd().setXProd("NOTA FISCAL EMITIDA EM AMBIENTE DE HOMOLOGACAO - SEM VALOR FISCAL");
            } else {
            	det.getProd().setXProd(nfeDetalhe.getNomeProduto());
            }
            det.getProd().setNCM(nfeDetalhe.getNcm());
            det.getProd().setCFOP(nfeDetalhe.getCfop().toString());
            det.getProd().setUCom(nfeDetalhe.getUnidadeComercial());
            det.getProd().setQCom(formatoQuantidade.format(nfeDetalhe.getQuantidadeComercial()));
            det.getProd().setVUnCom(nfeDetalhe.getValorUnitarioComercial().toPlainString());
            det.getProd().setVProd(formatoValor.format(nfeDetalhe.getValorTotal()));
            det.getProd().setCEANTrib(nfeDetalhe.getGtinUnidadeTributavel());
            det.getProd().setUTrib(nfeDetalhe.getUnidadeTributavel());
            det.getProd().setQTrib(formatoQuantidade.format(nfeDetalhe.getQuantidadeTributavel()));
            det.getProd().setVUnTrib(nfeDetalhe.getValorUnitarioTributavel().toPlainString());
            det.getProd().setVFrete(nfeDetalhe.getValorFrete().compareTo(BigDecimal.ZERO) == 0 ? null : formatoValor.format(nfeDetalhe.getValorFrete()));
            det.getProd().setVSeg(nfeDetalhe.getValorSeguro().compareTo(BigDecimal.ZERO) == 0 ? null : formatoValor.format(nfeDetalhe.getValorSeguro()));
            det.getProd().setVDesc(nfeDetalhe.getValorDesconto().compareTo(BigDecimal.ZERO) == 0 ? null : formatoValor.format(nfeDetalhe.getValorDesconto()));
            det.getProd().setVOutro(nfeDetalhe.getValorOutrasDespesas().compareTo(BigDecimal.ZERO) == 0 ? null : formatoValor.format(nfeDetalhe.getValorOutrasDespesas()));
            det.getProd().setIndTot(String.valueOf(nfeDetalhe.getEntraTotal()));
            det.setInfAdProd(nfeDetalhe.getInformacoesAdicionais());

            //Detalhes -- Impostos
            TNFe.InfNFe.Det.Imposto imposto = new TNFe.InfNFe.Det.Imposto();
            det.setImposto(imposto);

            // lei da transparencia nos impostos
            // Exerc�cio: avalie se essa informa��o est� sendo repassada corretamente
            det.getImposto().getContent().add(factory.createTNFeInfNFeDetImpostoVTotTrib(formatoValor.format(nfeDetalhe.getNfeDetalheImpostoIcms().getValorIcms())));

            TNFe.InfNFe.Det.Imposto.ICMS icms = new TNFe.InfNFe.Det.Imposto.ICMS();
            det.getImposto().getContent().add(factory.createTNFeInfNFeDetImpostoICMS(icms));

            if (empresa.getCrt().equals("1")) {//Simples Nacional
                String csosn = nfeDetalhe.getNfeDetalheImpostoIcms().getCsosn();

                if (csosn != null && csosn.equals("900")) {
                    TNFe.InfNFe.Det.Imposto.ICMS.ICMSSN900 icms900 = new TNFe.InfNFe.Det.Imposto.ICMS.ICMSSN900();
                    icms.setICMSSN900(icms900);

                    icms900.setCSOSN(csosn);
                    icms900.setOrig(String.valueOf(nfeDetalhe.getNfeDetalheImpostoIcms().getOrigemMercadoria()));
                    icms900.setModBC(String.valueOf(nfeDetalhe.getNfeDetalheImpostoIcms().getModalidadeBcIcms()));
                    icms900.setVBC(formatoValor.format(nfeDetalhe.getNfeDetalheImpostoIcms().getBaseCalculoIcms()));
                    icms900.setPRedBC(formatoValor.format(nfeDetalhe.getNfeDetalheImpostoIcms().getTaxaReducaoBcIcms()));
                    icms900.setPICMS(formatoValor.format(nfeDetalhe.getNfeDetalheImpostoIcms().getAliquotaIcms()));
                    icms900.setVICMS(formatoValor.format(nfeDetalhe.getNfeDetalheImpostoIcms().getValorIcms()));
                    icms900.setModBCST(String.valueOf(nfeDetalhe.getNfeDetalheImpostoIcms().getModalidadeBcIcmsSt()));
                    icms900.setPMVAST(formatoValor.format(nfeDetalhe.getNfeDetalheImpostoIcms().getPercentualMvaIcmsSt()));
                    icms900.setPRedBCST(formatoValor.format(nfeDetalhe.getNfeDetalheImpostoIcms().getPercentualReducaoBcIcmsSt()));
                    icms900.setVBCST(formatoValor.format(nfeDetalhe.getNfeDetalheImpostoIcms().getValorBaseCalculoIcmsSt()));
                    icms900.setPICMSST(formatoValor.format(nfeDetalhe.getNfeDetalheImpostoIcms().getAliquotaIcmsSt()));
                    icms900.setVICMSST(formatoValor.format(nfeDetalhe.getNfeDetalheImpostoIcms().getValorIcmsSt()));
                    icms900.setPCredSN(formatoValor.format(nfeDetalhe.getNfeDetalheImpostoIcms().getAliquotaCreditoIcmsSn()));
                    icms900.setVCredICMSSN(formatoValor.format(nfeDetalhe.getNfeDetalheImpostoIcms().getValorCreditoIcmsSn()));
                }
            } else {
                String cst = nfeDetalhe.getNfeDetalheImpostoIcms().getCstIcms();

                if (cst.equals("00")) {
                    TNFe.InfNFe.Det.Imposto.ICMS.ICMS00 icms00 = new TNFe.InfNFe.Det.Imposto.ICMS.ICMS00();
                    icms.setICMS00(icms00);

                    icms00.setCST(nfeDetalhe.getNfeDetalheImpostoIcms().getCstIcms());
                    icms00.setOrig(String.valueOf(nfeDetalhe.getNfeDetalheImpostoIcms().getOrigemMercadoria()));
                    icms00.setModBC(String.valueOf(nfeDetalhe.getNfeDetalheImpostoIcms().getModalidadeBcIcms()));
                    icms00.setVBC(formatoValor.format(nfeDetalhe.getNfeDetalheImpostoIcms().getBaseCalculoIcms()));
                    icms00.setPICMS(formatoValor.format(nfeDetalhe.getNfeDetalheImpostoIcms().getAliquotaIcms()));
                    icms00.setVICMS(formatoValor.format(nfeDetalhe.getNfeDetalheImpostoIcms().getValorIcms()));
                }
            }

            listaDet.add(det);
        }

        TEnviNFe enviNfe = new TEnviNFe();
        enviNfe.setIdLote("1");
        enviNfe.setVersao("3.10");
        enviNfe.setIndSinc("0");
        enviNfe.getNFe().add(nfe);

        JAXBContext jc = JAXBContext.newInstance("br.inf.portalfiscal.nfe.envinfe");
        Marshaller marshaller = jc.createMarshaller();

        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, false);
        JAXBElement<TEnviNFe> element = factory.createEnviNFe(enviNfe);

        StringWriter writer = new StringWriter();
        marshaller.marshal(element, writer);

        String xmlEnviNfe = writer.toString();
        xmlEnviNfe = xmlEnviNfe.replaceAll("xmlns:ns2=\"http://www.w3.org/2000/09/xmldsig#\"", "");
        xmlEnviNfe = xmlEnviNfe.replaceAll("<NFe>", "<NFe xmlns=\"http://www.portalfiscal.inf.br/nfe\">");

        String xmlAssinado = Biblioteca.assinaXML(xmlEnviNfe, alias, ks, senha, "#NFe" + nfeCabecalho.getChaveAcesso() + nfeCabecalho.getDigitoChaveAcesso(), "NFe", "infNFe", "Id");
        
        //<DigestValue>I1nSK4A82NpJXh4oYnMjKGiKGHE=</DigestValue>
        String digestValue = xmlAssinado.split("DigestValue>")[1];//I1nSK4A82NpJXh4oYnMjKGiKGHE=</
        digestValue = digestValue.substring(0, digestValue.length() - 2);//I1nSK4A82NpJXh4oYnMjKGiKGHE=
        
        String tagQrCode = geraTagQrCode(digestValue, nfeCabecalho, empresaEndereco.getUf(), idToken, token);
        xmlAssinado = xmlAssinado.replaceAll("</infNFe>", "</infNFe><infNFeSupl><qrCode><![CDATA[" + tagQrCode + "]]></qrCode></infNFeSupl>");

        return xmlAssinado;
    }

    public String geraTagQrCode(String digestValue, NfeCabecalho vendaAtual, String uf, String idToken, String valorToken) throws Exception {
        try {
            DecimalFormatSymbols simboloDecimal = DecimalFormatSymbols.getInstance();
            simboloDecimal.setDecimalSeparator('.');
            DecimalFormat formatoValor = new DecimalFormat("0.00", simboloDecimal);

            String enderecoSefaz = "http://dec.fazenda.df.gov.br/ConsultarNFCe.aspx?";

            //parametros
            String chNFe = "chNFe=" + vendaAtual.getChaveAcesso() + vendaAtual.getDigitoChaveAcesso();
            String nVersao = "nVersao=100";
            String tpAmb = "tpAmb=" + vendaAtual.getAmbiente();
            String cDest = vendaAtual.getDestinatario() != null ? "cDest=" + vendaAtual.getDestinatario().getCpfCnpj() : null;
            String dhEmi = "dhEmi=" + Hex.encodeHexString(formatoDataHora.format(vendaAtual.getDataHoraEmissao()).getBytes("UTF-8"));
            String vNF = "vNF=" + formatoValor.format(vendaAtual.getValorTotalProdutos());
            String vICMS = "vICMS=" + formatoValor.format(vendaAtual.getValorIcms());
            String digVal = "digVal=" + Hex.encodeHexString(digestValue.getBytes("UTF-8"));
            String cIdToken = "cIdToken=" + idToken;

            String parametros = chNFe + "&" + nVersao + "&" + tpAmb + (cDest == null ? "" : "&" + cDest) + "&" + dhEmi + "&" + vNF + "&" + vICMS + "&" + digVal + "&" + cIdToken + valorToken;

            String cHashQRCode = "cHashQRCode=" + DigestUtils.sha1Hex(parametros);

            parametros = chNFe + "&" + nVersao + "&" + tpAmb
                    + (cDest == null ? "" : "&" + cDest) + "&" + dhEmi
                    + "&" + vNF + "&" + vICMS + "&" + digVal + "&" + cIdToken
                    + "&" + cHashQRCode;

            return enderecoSefaz + parametros;
        } catch (Exception e) {
            throw e;
        }
    }
    
}
