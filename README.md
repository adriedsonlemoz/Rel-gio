# Relógio Flutuante

Aplicativo Android nativo em Kotlin + Jetpack Compose com relógio ajustável, contagem regressiva e janela de sobreposição sobre outros aplicativos.

## Recursos

- Relógio principal em `HH:mm:ss`.
- Ajuste interno do horário sem modificar o relógio do Android.
- Botão para voltar ao horário do sistema.
- Contagem regressiva com horas, minutos e segundos.
- Iniciar, pausar, continuar e zerar; após zerar, o tempo configurado permanece disponível para um novo início.
- Aviso ao chegar a zero.
- Estado persistente compartilhado entre app e overlay.
- Overlay compacto, arrastável e com fechamento rápido.
- Permissão `SYSTEM_ALERT_WINDOW` solicitada pela tela oficial do Android.
- Serviço em primeiro plano do tipo `specialUse` enquanto o overlay está ativo.
- Layout adaptável para celulares e telas maiores.

## Requisitos de build

- Android Studio compatível com API 37.
- Android SDK 37.
- JDK 17 ou superior compatível com a versão do Android Gradle Plugin utilizada.
- AGP 9.4.0 / Gradle 9.6.0.

## Comportamento do overlay

A janela usa `TYPE_APPLICATION_OVERLAY`. Os toques fora dela continuam chegando ao aplicativo que estiver embaixo. A própria janela recebe toques apenas para arrastar e fechar.

No Android 13+, a permissão de notificações também é solicitada ao ativar o overlay. A recusa não altera a permissão de sobreposição, mas pode limitar a visibilidade da notificação do serviço de acordo com o comportamento do sistema.

## Observação

Nenhuma imagem, mockup ou recurso gráfico externo foi criado ou incluído neste projeto.


## GitHub Actions e GitHub Manager

O projeto inclui `github-manager.json` com nome, versão, `versionName`, `versionCode`, `applicationId`, `namespace`, linguagem, tipo e fonte da versão. A fonte principal da versão é `app/build.gradle.kts`.

O workflow `.github/workflows/android-kotlin-apk.yml` pode ser executado manualmente ou em pushes para `main`/`master`. Ele valida a versão, instala o SDK necessário, compila o app e publica diretamente `Relogio-Flutuante.apk` em uma GitHub Release. Não publica AAB nem ZIP de artifact.

### Versão atual

- `versionName`: `1.0.1`
- `versionCode`: `2`
- APK: `Relogio-Flutuante.apk`
