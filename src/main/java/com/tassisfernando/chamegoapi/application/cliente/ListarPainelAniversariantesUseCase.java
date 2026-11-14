package com.tassisfernando.chamegoapi.application.cliente;

import com.tassisfernando.chamegoapi.domain.cliente.Cliente;
import com.tassisfernando.chamegoapi.domain.cliente.ClienteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

@Service
public class ListarPainelAniversariantesUseCase {

    private static final int JANELA_PROXIMOS = 30;
    private static final int JANELA_ULTIMOS = 15;

    private final ClienteRepository clienteRepository;

    public ListarPainelAniversariantesUseCase(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    @Transactional(readOnly = true)
    public PainelAniversariantes executar() {
        return executar(LocalDate.now());
    }

    @Transactional(readOnly = true)
    public PainelAniversariantes executar(LocalDate referencia) {
        List<Cliente> clientes = clienteRepository.listarAtivosComNascimento();

        List<AniversarianteResumo> proximos = clientes.stream()
                .map(c -> AniversarianteResumo.de(c, referencia))
                .filter(a -> a.diasAteAniversario() >= 0 && a.diasAteAniversario() <= JANELA_PROXIMOS)
                .sorted(Comparator.comparingInt(AniversarianteResumo::diasAteAniversario)
                        .thenComparing(AniversarianteResumo::nome))
                .toList();

        List<AniversarianteResumo> ultimos = clientes.stream()
                .map(c -> AniversarianteResumo.recente(c, referencia))
                .filter(a -> a.diasAteAniversario() < 0 && a.diasAteAniversario() >= -JANELA_ULTIMOS)
                .sorted(Comparator.comparingInt(AniversarianteResumo::diasAteAniversario).reversed()
                        .thenComparing(AniversarianteResumo::nome))
                .toList();

        return new PainelAniversariantes(proximos, ultimos);
    }
}
