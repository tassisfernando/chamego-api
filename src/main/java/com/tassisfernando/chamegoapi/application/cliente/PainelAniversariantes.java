package com.tassisfernando.chamegoapi.application.cliente;

import java.util.List;

public record PainelAniversariantes(
        List<AniversarianteResumo> proximos, List<AniversarianteResumo> ultimos) {
}
