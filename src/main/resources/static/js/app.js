document.addEventListener('DOMContentLoaded', () => {
  document.querySelectorAll('[data-auto-dismiss]').forEach((el) => {
    window.setTimeout(() => {
      el.style.transition = 'opacity 300ms ease, transform 300ms ease';
      el.style.opacity = '0';
      el.style.transform = 'translateY(-4px)';
      window.setTimeout(() => el.remove(), 350);
    }, 3800);
  });

  document.querySelectorAll('[data-confirm]').forEach((form) => {
    form.addEventListener('submit', (event) => {
      const message = form.getAttribute('data-confirm') || 'Deseja realmente excluir?';
      if (!window.confirm(message)) {
        event.preventDefault();
      }
    });
  });

  document.querySelectorAll('form[data-submit-loading]').forEach((form) => {
    form.addEventListener('submit', () => {
      const button = form.querySelector('button[type="submit"]');
      if (!button) return;

      const originalText = button.dataset.originalText || button.textContent.trim();
      button.dataset.originalText = originalText;
      button.disabled = true;
      button.classList.add('is-loading');
      button.innerHTML = '<i class="material-icons" style="margin-right: 8px;">autorenew</i>Enviando...';
    });
  });

  document.querySelectorAll('input[type="file"][data-file-label]').forEach((input) => {
    const label = document.querySelector(`[data-file-name-for="${input.id}"]`);
    const updateLabel = () => {
      if (!label) return;
      label.textContent = input.files && input.files.length ? input.files[0].name : 'Nenhum arquivo selecionado';
    };

    input.addEventListener('change', updateLabel);
    updateLabel();
  });

  const firstError = document.querySelector('.alert--danger, .mdl-textfield__error:not(:empty)');
  if (firstError) {
    firstError.scrollIntoView({ behavior: 'smooth', block: 'center' });
  }
});
