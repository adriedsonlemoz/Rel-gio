# Changelog

## 1.0.1

- Adicionado `github-manager.json` compatível com o padrão do GitHub Manager.
- Sincronizados `versionName` 1.0.1 e `versionCode` 2 com os metadados do projeto.
- Adicionado workflow GitHub Actions para compilar o APK automaticamente.
- Configurado Android SDK 37, Build Tools 36.0.0, JDK 17 e Gradle 9.6.0 no build remoto.
- Configurada publicação direta de `Relogio-Flutuante.apk` em GitHub Release.
- Adicionada validação automática da versão antes do build.
- Mantido o projeto sem imagens ou mockups adicionados.

## 1.0.0

- Implementado relógio principal com horas, minutos e segundos.
- Implementado ajuste de horário apenas dentro do aplicativo.
- Implementada restauração para o horário do sistema.
- Implementada contagem regressiva configurável em horas, minutos e segundos.
- Implementados controles iniciar, pausar, continuar e zerar.
- Implementado alerta visual de tempo esgotado.
- Implementado estado persistente compartilhado entre tela principal e overlay.
- Implementada sobreposição Android arrastável com relógio ou contagem regressiva.
- Implementado serviço em primeiro plano enquanto o overlay está ativo.
- Implementado fechamento rápido do overlay e persistência da última posição.
- Implementada interface responsiva em Jetpack Compose.
