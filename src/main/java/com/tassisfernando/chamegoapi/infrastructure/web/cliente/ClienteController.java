package com.tassisfernando.chamegoapi.infrastructure.web.cliente;

import com.tassisfernando.chamegoapi.application.cliente.CadastrarClienteComando;
import com.tassisfernando.chamegoapi.application.cliente.CadastrarClienteUseCase;
import com.tassisfernando.chamegoapi.application.cliente.ListarAniversariantesUseCase;
import com.tassisfernando.chamegoapi.domain.cliente.Cliente;
import com.tassisfernando.chamegoapi.domain.cliente.ClienteRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.time.MonthDay;
import java.util.List;

@RestController
@RequestMapping("/api/clientes")
public class ClienteController {

    private final CadastrarClienteUseCase cadastrarClienteUseCase;
    private final ListarAniversariantesUseCase listarAniversariantesUseCase;
    private final ClienteRepository clienteRepository;
    private final ClienteWebMapper mapper;

    public ClienteController(
            CadastrarClienteUseCase cadastrarClienteUseCase,
            ListarAniversariantesUseCase listarAniversariantesUseCase,
            ClienteRepository clienteRepository,
            ClienteWebMapper mapper) {
        this.cadastrarClienteUseCase = cadastrarClienteUseCase;
        this.listarAniversariantesUseCase = listarAniversariantesUseCase;
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

    @GetMapping
    public List<ClienteResposta> listar() {
        return clienteRepository.listarTodos().stream().map(mapper::paraResposta).toList();
    }

    @GetMapping("/aniversariantes")
    public List<ClienteResposta> aniversariantes() {
        return listarAniversariantesUseCase.executar(MonthDay.now()).stream()
                .map(mapper::paraResposta)
                .toList();
    }
}
