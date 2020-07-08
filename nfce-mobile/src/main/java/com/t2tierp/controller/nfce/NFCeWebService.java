package com.t2tierp.controller.nfce;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.servlet.ServletContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

import com.t2ti.nfce.infra.NfceConstantes;
import com.t2tierp.model.bean.nfe.NfeCabecalho;
import com.t2tierp.model.bean.nfe.NfeDetalhe;
import com.t2tierp.model.bean.nfe.NfeFormaPagamento;

@RequestScoped
@Path("/emissao")
public class NFCeWebService  {

	@Inject
	private NfceController controller;
	
	@Context
	private ServletContext context;
	
	@POST
	@Path("/envio")
	@Consumes("application/json")
	@Produces("application/json")
	public Response emiteNFCe(NfeCabecalho nfe) {
		try {
			controller.setEmissaoWebService(true);
			controller.setContext(context);
			for (NfeDetalhe d : nfe.getListaNfeDetalhe()) {
				controller.setCodigoProduto(d.getCodigoProduto());
				controller.iniciaVendaDeItens();
			}
			
			if (nfe.getDestinatario() != null) {
				controller.getCliente().setNome(nfe.getDestinatario().getNome());
				controller.getCliente().setCpf(nfe.getDestinatario().getCpfCnpj());
				
				controller.identificaCliente();
			}
			
			controller.iniciaEncerramentoVenda();
			for (NfeFormaPagamento p : nfe.getListaNfeFormaPagamento()) {
				controller.setTipoPagamento(p.getNfceTipoPagamento());
				controller.setValorPagamento(p.getValor());
				controller.incluiPagamento();
			}
			nfe = controller.getNfeCabecalho();
			controller.finalizaVenda();
			nfe.setDanfe(getDanfeBase64(new File(context.getRealPath(controller.getDanfe()))));
			return Response.ok(nfe).build();
		} catch (Exception e) {
			e.printStackTrace();
			return Response.serverError().entity(e.getMessage()).build();
		}
	}

	@GET
	@Path("/cancela")
	@Produces("text/html; charset=UTF-8")
	public Response cancelaNFCe(@QueryParam("numero") Integer numero, @QueryParam("justificativa") String justificativa) {
		try {
			controller.setEmissaoWebService(true);
			controller.setContext(context);
			controller.setNumeroCancelar(numero);
			controller.setJustificativa(justificativa);
			controller.setOperacaoNfce(NfceConstantes.OP_CANCELA_NFCE);
			controller.cancelaInutiliza();
			return Response.ok().entity(controller.getRespostaCancelamento()).build();
		} catch (Exception e) {
			e.printStackTrace();
			return Response.serverError().entity(e.getMessage()).build();
		}
	}
	
	private String getDanfeBase64(File file) {
		try {
			return Base64.getEncoder().encodeToString(Files.readAllBytes(Paths.get(file.toURI())));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
}
