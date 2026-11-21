package com.tassisfernando.chamegoapi.application.precificacao;

import com.tassisfernando.chamegoapi.domain.compartilhado.Dinheiro;
import com.tassisfernando.chamegoapi.domain.compartilhado.EntidadeNaoEncontrada;
import com.tassisfernando.chamegoapi.domain.compartilhado.RegraDeNegocioViolada;
import com.tassisfernando.chamegoapi.domain.insumo.Insumo;
import com.tassisfernando.chamegoapi.domain.insumo.InsumoRepository;
import com.tassisfernando.chamegoapi.domain.precificacao.ItemPrecificacao;
import com.tassisfernando.chamegoapi.domain.precificacao.PrecificacaoCalculos;
import com.tassisfernando.chamegoapi.domain.precificacao.TipoItemPrecificacao;
import com.tassisfernando.chamegoapi.domain.produto.ItemReceita;
import com.tassisfernando.chamegoapi.domain.produto.Produto;
import com.tassisfernando.chamegoapi.domain.produto.ProdutoRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class PrecificacaoMontagemService {

    private final ProdutoRepository produtoRepository;
    private final InsumoRepository insumoRepository;

    public PrecificacaoMontagemService(ProdutoRepository produtoRepository, InsumoRepository insumoRepository) {
        this.produtoRepository = produtoRepository;
        this.insumoRepository = insumoRepository;
    }

    public List<ItemPrecificacao> itensDaFichaTecnica(UUID produtoId) {
        Produto produto = produtoRepository
                .buscarPorId(produtoId)
                .orElseThrow(() -> EntidadeNaoEncontrada.de("Produto", produtoId));
        List<ItemPrecificacao> itens = new ArrayList<>();
        for (ItemReceita linha : produto.receita()) {
            Insumo insumo = insumoRepository
                    .buscarPorId(linha.insumoId())
                    .orElseThrow(() -> EntidadeNaoEncontrada.de("Insumo", linha.insumoId()));
            itens.add(ItemPrecificacao.deInsumo(
                    insumo.id(),
                    insumo.nome(),
                    linha.quantidadePorUnidade().valor(),
                    linha.quantidadePorUnidade().unidade(),
                    insumo.custoMedio()));
        }
        return itens;
    }

    public List<ItemPrecificacao> montarItens(List<LinhaPrecificacaoComando> comandos) {
        List<ItemPrecificacao> itens = new ArrayList<>();
        for (LinhaPrecificacaoComando comando : comandos) {
            itens.add(resolverLinha(comando));
        }
        return itens;
    }

    public Dinheiro custoTotal(List<ItemPrecificacao> itens) {
        return PrecificacaoCalculos.somarLinhas(itens.stream().map(ItemPrecificacao::subtotal).toList());
    }

    private ItemPrecificacao resolverLinha(LinhaPrecificacaoComando comando) {
        BigDecimal quantidade = comando.quantidade();
        if (quantidade == null || quantidade.signum() <= 0) {
            throw new RegraDeNegocioViolada("quantidade do item de precificação deve ser positiva");
        }
        return switch (comando.tipo()) {
            case INSUMO -> {
                Insumo insumo = insumoRepository
                        .buscarPorId(comando.insumoId())
                        .orElseThrow(() -> EntidadeNaoEncontrada.de("Insumo", comando.insumoId()));
                Dinheiro unitario = comando.custoUnitarioInformado() != null
                        ? Dinheiro.de(comando.custoUnitarioInformado())
                        : insumo.custoMedio();
                yield ItemPrecificacao.deInsumo(
                        insumo.id(),
                        insumo.nome(),
                        quantidade,
                        comando.unidadeMedida() != null ? comando.unidadeMedida() : insumo.unidadeMedida(),
                        unitario);
            }
            case PRODUTO -> {
                Produto produto = produtoRepository
                        .buscarPorId(comando.produtoId())
                        .orElseThrow(() -> EntidadeNaoEncontrada.de("Produto", comando.produtoId()));
                Dinheiro unitario = comando.custoUnitarioInformado() != null
                        ? Dinheiro.de(comando.custoUnitarioInformado())
                        : produto.custoPadrao();
                yield ItemPrecificacao.deProduto(produto.id(), produto.nome(), quantidade, unitario);
            }
            case EXTRA -> {
                if (comando.descricao() == null || comando.descricao().isBlank()) {
                    throw new RegraDeNegocioViolada("descrição do item extra é obrigatória");
                }
                Dinheiro unitario = comando.custoUnitarioInformado() != null
                        ? Dinheiro.de(comando.custoUnitarioInformado())
                        : Dinheiro.zero();
                yield ItemPrecificacao.extra(comando.descricao().trim(), quantidade, unitario);
            }
        };
    }
}