package com.example.javamvcthymeleaf.controller;

import com.example.javamvcthymeleaf.controller.dto.CursoForm;
import com.example.javamvcthymeleaf.model.Curso;
import com.example.javamvcthymeleaf.service.CursoService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;

@Controller
@RequestMapping("/cursos")
public class CursoController {

    private final CursoService cursoService;

    public CursoController(CursoService cursoService) {
        this.cursoService = cursoService;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("cursos", cursoService.listarTodas());
        return "cursos/lista";
    }

    @GetMapping("/novo")
    public String novo(Model model) {
        model.addAttribute("cursoForm", new CursoForm());
        model.addAttribute("modoEdicao", false);
        return "cursos/form";
    }

    @PostMapping
    public String criar(@Valid @ModelAttribute("cursoForm") CursoForm form,
                        BindingResult bindingResult,
                        Model model,
                        RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("modoEdicao", false);
            model.addAttribute("mensagemErro", "Corrija os campos destacados.");
            return "cursos/form";
        }

        Curso curso = new Curso();
        curso.setNome(form.getNome());
        curso.setDescricao(form.getDescricao());
        cursoService.salvar(curso);
        redirectAttributes.addFlashAttribute("mensagemSucesso", "Curso cadastrado com sucesso.");
        return "redirect:/cursos";
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Integer id, Model model) {
        Curso curso = cursoService.buscarPorId(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        model.addAttribute("cursoForm", CursoForm.fromEntity(curso));
        model.addAttribute("modoEdicao", true);
        return "cursos/form";
    }

    @PostMapping("/{id}")
    public String atualizar(@PathVariable Integer id,
                            @Valid @ModelAttribute("cursoForm") CursoForm form,
                            BindingResult bindingResult,
                    Model model,
                    RedirectAttributes redirectAttributes) {
        Curso curso = cursoService.buscarPorId(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        if (bindingResult.hasErrors()) {
            form.setIdCurso(id);
            model.addAttribute("modoEdicao", true);
            model.addAttribute("mensagemErro", "Corrija os campos destacados.");
            return "cursos/form";
        }

        curso.setNome(form.getNome());
        curso.setDescricao(form.getDescricao());
        cursoService.salvar(curso);
        redirectAttributes.addFlashAttribute("mensagemSucesso", "Curso atualizado com sucesso.");
        return "redirect:/cursos";
    }

    @PostMapping("/{id}/excluir")
    public String excluir(@PathVariable Integer id,
                          RedirectAttributes redirectAttributes) {
        if (cursoService.buscarPorId(id).isEmpty()) {
            redirectAttributes.addFlashAttribute("mensagemErro", "Curso não encontrado para exclusão.");
            return "redirect:/cursos";
        }

        cursoService.excluir(id);
        redirectAttributes.addFlashAttribute("mensagemSucesso", "Curso excluído com sucesso.");
        return "redirect:/cursos";
    }
}