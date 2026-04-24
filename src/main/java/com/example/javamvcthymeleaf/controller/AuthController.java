package com.example.javamvcthymeleaf.controller;

import com.example.javamvcthymeleaf.config.AuthSession;
import com.example.javamvcthymeleaf.controller.dto.LoginForm;
import com.example.javamvcthymeleaf.model.Usuario;
import com.example.javamvcthymeleaf.service.AuthService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.Optional;

@Controller
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping("/login")
    public String loginPage(Model model, HttpSession session) {
        if (session.getAttribute(AuthSession.USER_ID) != null) {
            return "redirect:/";
        }

        if (!model.containsAttribute("loginForm")) {
            model.addAttribute("loginForm", new LoginForm());
        }

        return "login";
    }

    @PostMapping("/login")
    public String login(@Valid @ModelAttribute("loginForm") LoginForm loginForm,
                        BindingResult bindingResult,
                        Model model,
                        HttpSession session) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("mensagemErro", "Usuário e senha são obrigatórios.");
            return "login";
        }

        Optional<Usuario> usuario = authService.autenticar(loginForm.getUsername(), loginForm.getSenha());
        if (usuario.isEmpty()) {
            model.addAttribute("mensagemErro", "Credenciais inválidas.");
            return "login";
        }

        session.setAttribute(AuthSession.USER_ID, usuario.get().getIdUsuario());
        session.setAttribute(AuthSession.USERNAME, usuario.get().getUsername());
        session.setAttribute(AuthSession.ROLE, usuario.get().getPerfil());

        return "redirect:/";
    }

    @PostMapping("/logout")
    public String logout(HttpSession session, RedirectAttributes redirectAttributes) {
        session.invalidate();
        redirectAttributes.addFlashAttribute("mensagemSucesso", "Sessão encerrada com sucesso.");
        return "redirect:/login";
    }
}
