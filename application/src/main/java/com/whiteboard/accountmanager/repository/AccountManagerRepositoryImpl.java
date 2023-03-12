package com.whiteboard.accountmanager.repository;

import co.elastic.clients.elasticsearch.core.IndexRequest;
import com.google.gson.Gson;
import com.whiteboard.accountmanager.configuration.AppConnections;
import com.whiteboard.accountmanager.connections.elasticsearch.Connecting;
import com.whiteboard.accountmanager.dto.AccountDTO;
import com.whiteboard.accountmanager.dto.FiltrosRequestDTO;
import com.whiteboard.accountmanager.enums.CodigoErroEnum;
import com.whiteboard.accountmanager.exceptions.CadastroException;
import com.whiteboard.accountmanager.search.SearchBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Repository
public class AccountManagerRepositoryImpl implements AccountManagerRepository {

    private final RestTemplate template;
    private final AppConnections connections;
    @Autowired
    private Gson gson;
    public AccountManagerRepositoryImpl(RestTemplate template, AppConnections conections) {
        this.template = template;
        this.connections = conections;
    }

    private void logStashCall() {
        //var entity = new HttpEntity<>(toSaveAccountMapper);
        //UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(connections.getLogStash());
        //var response = template.exchange(builder.toUriString(), HttpMethod.POST, entity, String.class);
        /*(if(ObjectUtils.isEmpty(response) || !response.getStatusCode().is2xxSuccessful()) {
            log.error("Erro ao cadastrar conta no ElasticSearch");
            throw new CadastroNaoEfetivadoException(CodigoErroEnum.ERRO_CADASTRO_NAO_EFETIVADO);
        }*/
    }

    @Override
    public AccountDTO createAccount(AccountDTO dadosConta) throws CadastroException {
        try {
            log.info("Chamando a base para registrar nova conta");
            IndexRequest<AccountDTO> request = IndexRequest.of(i -> i
                    .index("white-board-accounts")
                    .id(dadosConta.getCpf())
                    .document(dadosConta));
            Connecting.getClient().index(request);
            log.info("Nova conta registrada, id: {}", dadosConta.getId());
        } catch (IOException e) {
            log.error("Erro ao registrar conta. Erro: {}", e.getLocalizedMessage());
            throw new CadastroException(CodigoErroEnum.ERRO_CADASTRO_NAO_EFETIVADO);
        }
        return dadosConta;
    }

    @Override
    public AccountDTO getAccount(String usuarioId) throws IOException {
        log.info("Inicio consulta dados da conta {} no Elastic", usuarioId);
        var pesquisa = SearchBuilder.get(usuarioId);
        if (!pesquisa.isEmpty()) {
            Gson gson = new Gson();
            AccountDTO emp = gson.fromJson(pesquisa.toString(), AccountDTO.class);
            log.info("Conta encontrada. Fim de consulta de dados");
            return emp;
        } else {
            throw new IOException(CodigoErroEnum.ERRO_INTERO.getDescricaoCodigo());
        }
    }

    @Override
    public List<AccountDTO> findAccountByFilter(FiltrosRequestDTO filtroRequestDTO) throws CadastroException {
        try {
            log.info("Inicio consulta contas");

            var pesquisa = SearchBuilder.search(filtroRequestDTO);
            List<AccountDTO> contas = pesquisa.hits().hits().stream()
                    .map(hit -> gson.fromJson(hit.source().toString(), AccountDTO.class))
                    .collect(Collectors.toList());

            log.info("Fim de consulta de contas, total de contas encontradas: {}", contas.size());
            return contas;
        } catch (IOException e) {
            throw new CadastroException(CodigoErroEnum.ERRO_INTERO);
        }
    }

    public boolean verifyDocExists(String usuarioId) throws IOException {
        try {
            return Connecting.getClient().exists(builder -> builder
                    .index("white-board-accounts")
                    .id(usuarioId)
                    .storedFields("_none_")).value();
        } catch (Exception e) {
            throw new IOException(CodigoErroEnum.ERRO_INTERO.getDescricaoCodigo());
        }
    }

}
