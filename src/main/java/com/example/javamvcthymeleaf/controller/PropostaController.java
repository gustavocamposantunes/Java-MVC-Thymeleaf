package com.example.javamvcthymeleaf.controller;

import com.example.javamvcthymeleaf.controller.dto.PropostaForm;
import com.example.javamvcthymeleaf.model.Proposta;
import com.example.javamvcthymeleaf.service.PropostaService;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Controller
@RequestMapping("/propostas")
public class PropostaController {

    private static final List<String> STATUS_OPTIONS = Arrays.asList(
            "aprovado",
            "aguardando aprovação",
            "reijeitado"
    );

    private final PropostaService propostaService;

    public PropostaController(PropostaService propostaService) {
        this.propostaService = propostaService;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("propostas", propostaService.listarTodas());
        return "propostas/lista";
    }

    @GetMapping("/nova")
    public String nova(Model model) {
        model.addAttribute("propostaForm", new PropostaForm());
        model.addAttribute("statusOptions", STATUS_OPTIONS);
        model.addAttribute("modoEdicao", false);
        return "propostas/form";
    }

    @PostMapping
    public String criar(@Valid @ModelAttribute("propostaForm") PropostaForm form,
                        BindingResult bindingResult,
                        Model model) throws IOException {
        validarStatus(form, bindingResult);

        if (bindingResult.hasErrors()) {
            model.addAttribute("statusOptions", STATUS_OPTIONS);
            model.addAttribute("modoEdicao", false);
            return "propostas/form";
        }

        Proposta proposta = new Proposta();
        proposta.setTitulo(form.getTitulo());
        proposta.setDescricao(form.getDescricao());
        proposta.setDataSubmissao(form.getDataSubmissao());
        proposta.setStatus(form.getStatus());

        if (form.getAnexo() != null && !form.getAnexo().isEmpty()) {
            proposta.setAnexos(form.getAnexo().getBytes());
        }

        propostaService.salvar(proposta);
        return "redirect:/propostas";
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Integer id, Model model) {
        Proposta proposta = propostaService.buscarPorId(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        model.addAttribute("propostaForm", PropostaForm.fromEntity(proposta));
        model.addAttribute("statusOptions", STATUS_OPTIONS);
        model.addAttribute("modoEdicao", true);
        return "propostas/form";
    }

    @PostMapping("/{id}")
    public String atualizar(@PathVariable Integer id,
                            @Valid @ModelAttribute("propostaForm") PropostaForm form,
                            BindingResult bindingResult,
                            Model model) throws IOException {
        Proposta proposta = propostaService.buscarPorId(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        validarStatus(form, bindingResult);

        if (bindingResult.hasErrors()) {
            form.setIdProposta(id);
            model.addAttribute("statusOptions", STATUS_OPTIONS);
            model.addAttribute("modoEdicao", true);
            return "propostas/form";
        }

        proposta.setTitulo(form.getTitulo());
        proposta.setDescricao(form.getDescricao());
        proposta.setDataSubmissao(form.getDataSubmissao());
        proposta.setStatus(form.getStatus());

        if (form.getAnexo() != null && !form.getAnexo().isEmpty()) {
            proposta.setAnexos(form.getAnexo().getBytes());
        }

        propostaService.salvar(proposta);
        return "redirect:/propostas";
    }

    @PostMapping("/{id}/excluir")
    public String excluir(@PathVariable Integer id) {
        propostaService.excluir(id);
        return "redirect:/propostas";
    }

    @GetMapping("/{id}/anexo")
    public ResponseEntity<ByteArrayResource> baixarAnexo(@PathVariable Integer id) {
        Proposta proposta = propostaService.buscarPorId(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        if (proposta.getAnexos() == null || proposta.getAnexos().length == 0) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Anexo não encontrado");
        }

        ByteArrayResource resource = new ByteArrayResource(proposta.getAnexos());

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"proposta-" + id + "-anexo.bin\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .contentLength(proposta.getAnexos().length)
                .body(resource);
    }

    private void validarStatus(PropostaForm form, BindingResult bindingResult) {
        if (form.getStatus() != null && !STATUS_OPTIONS.contains(form.getStatus())) {
            bindingResult.rejectValue("status", "status.invalido", "Status inválido");
        }
    }
}
