package com.tassisfernando.chamegoapi.infrastructure.web.cliente;

import com.tassisfernando.chamegoapi.application.cliente.AtualizarClienteComando;
import com.tassisfernando.chamegoapi.application.cliente.AtualizarClienteUseCase;
import com.tassisfernando.chamegoapi.application.cliente.CadastrarClienteComando;
import com.tassisfernando.chamegoapi.application.cliente.CadastrarClienteUseCase;
import com.tassisfernando.chamegoapi.application.cliente.DesativarClienteUseCase;
import com.tassisfernando.chamegoapi.application.cliente.ListarAniversariantesUseCase;
import com.tassisfernando.chamegoapi.application.cliente.ListarPainelAniversariantesUseCase;
import com.tassisfernando.chamegoapi.application.cliente.PainelAniversariantes;
import com.tassisfernando.chamegoapi.domain.cliente.Cliente;
import com.tassisfernando.chamegoapi.domain.cliente.ClienteRepository;
import com.tassisfernando.chamegoapi.domain.compartilhado.Pagina;
import com.tassisfernando.chamegoapi.infrastructure.web.compartilhado.PaginaResposta;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.time.MonthDay;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/clientes")
public class ClienteController {

    private final CadastrarClienteUseCase cadastrarClienteUseCase;
    private final AtualizarClienteUseCase atualizarClienteUseCase;
    private final DesativarClienteUseCase desativarClienteUseCase;
    private final ListarAniversariantesUseCase listarAniversariantesUseCase;
    private final ListarPainelAniversariantesUseCase listarPainelAniversariantesUseCase;
    private final ClienteRepository clienteRepository;
    private final ClienteWebMapper mapper;

    public ClienteController(
            CadastrarClienteUseCase cadastrarClienteUseCase,
            AtualizarClienteUseCase atualizarClienteUseCase,
            DesativarClienteUseCase desativarClienteUseCase,
            ListarAniversariantesUseCase listarAniversariantesUseCase,
            ListarPainelAniversariantesUseCase listarPainelAniversariantesUseCase,
            ClienteRepository clienteRepository,
            ClienteWebMapper mapper) {
        this.cadastrarClienteUseCase = cadastrarClienteUseCase;
        this.atualizarClienteUseCase = atualizarClienteUseCase;
        this.desativarClienteUseCase = desativarClienteUseCase;
        this.listarAniversariantesUseCase = listarAniversariantesUseCase;
        this.listarPainelAniversariantesUseCase = listarPainelAniversariantesUseCase;
        this.clienteRepository = clienteRepository;
        this.mapper = mapper;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ClienteResposta cadastrar(@Valid @RequestBody CadastrarClienteRequisicao requisicao) {
        Cliente cliente = cadastrarClienteUseCase.executar(new CadastrarClienteComando(
                requisicao.nome(),
                requisicao.telefone(),
                requisicao.cidade(),
                requisicao.estado(),
                requisicao.dataNascimento()));
        return mapper.paraResposta(cliente);
    }

    @PutMapping("/{id}")
    public ClienteResposta atualizar(
            @PathVariable UUID id, @Valid @RequestBody CadastrarClienteRequisicao requisicao) {
        Cliente cliente = atualizarClienteUseCase.executar(new AtualizarClienteComando(
                id,
                requisicao.nome(),
                requisicao.telefone(),
                requisicao.cidade(),
                requisicao.estado(),
                requisicao.dataNascimento()));
        return mapper.paraResposta(cliente);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void desativar(@PathVariable UUID id) {
        desativarClienteUseCase.executar(id);
    }

    @GetMapping
    public PaginaResposta<ClienteResposta> listar(
            @RequestParam(required = false) String busca,
            @PageableDefault(size = 20, sort = "nome") Pageable pageable) {
        Pagina<Cliente> pagina = clienteRepository.buscarAtivos(
                busca, pageable.getPageNumber(), pageable.getPageSize());
        return PaginaResposta.mapear(pagina, mapper::paraResposta);
    }

    @GetMapping("/opcoes")
    public List<ClienteResposta> opcoes() {
        return clienteRepository.listarAtivos().stream().map(mapper::paraResposta).toList();
    }

    @GetMapping("/aniversariantes")
    public List<ClienteResposta> aniversariantes() {
        return listarAniversariantesUseCase.executar(MonthDay.now()).stream()
                .map(mapper::paraResposta)
                .toList();
    }

    @GetMapping("/aniversariantes/painel")
    public PainelAniversariantes painelAniversariantes() {
        return listarPainelAniversariantesUseCase.executar();
    }
}
