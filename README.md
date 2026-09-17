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
- Segundo método de sobreposição por Serviço de Acessibilidade usando `TYPE_ACCESSIBILITY_OVERLAY`, pensado para aparelhos que bloqueiam `SYSTEM_ALERT_WINDOW`.
- O serviço de Acessibilidade não solicita conteúdo das janelas e não executa cliques ou gestos; ele apenas desenha o relógio/contagem.
- Modo por notificação mantido como último fallback.
- Controles rápidos da contagem pela notificação quando esse fallback é usado.
- Permissão `SYSTEM_ALERT_WINDOW` solicitada pela tela oficial do Android.
- Serviço em primeiro plano do tipo `specialUse` para o overlay normal e o fallback por notificação; o modo por Acessibilidade usa o serviço do sistema correspondente.
- Layout adaptável para celulares e telas maiores.

## Requisitos de build

- Android Studio compatível com API 36.
- Android SDK 36.
- JDK 17 ou superior compatível com a versão do Android Gradle Plugin utilizada.
- AGP 9.4.0 / Gradle 9.6.0.
- Jetpack Compose BOM 2026.06.00 (linha Compose 1.11.x, compatível com compileSdk 36).
- AndroidX Core KTX 1.17.0 para manter compatibilidade com API 36.

## Comportamento do overlay

Quando o aparelho permite sobreposição, a janela usa `TYPE_APPLICATION_OVERLAY`. Os toques fora dela continuam chegando ao aplicativo que estiver embaixo. A própria janela recebe toques apenas para arrastar e fechar.

Em aparelhos low-RAM/Android Go que bloqueiam `SYSTEM_ALERT_WINDOW`, o app oferece a sobreposição por Acessibilidade. Depois de o usuário ativar o serviço nas configurações do Android, a janela usa `TYPE_ACCESSIBILITY_OVERLAY`, sem precisar da permissão tradicional de “Sobrepor a outros apps”. O serviço está configurado com `canRetrieveWindowContent=false`, ignora eventos de acessibilidade e não implementa gestos ou cliques.

O modo por notificação continua disponível apenas como fallback. No Android 13+, `POST_NOTIFICATIONS` pode ser necessário para esse modo.

## Desempenho

Os atualizadores trabalham no máximo uma vez por segundo. Os serviços de sobreposição agora realinham a próxima atualização à virada do segundo do relógio do sistema, reduzindo deriva visual. O serviço também evita redesenhar texto e notificação quando o conteúdo exibido não mudou.

## Observação

Nenhuma imagem, mockup ou recurso gráfico externo foi criado ou incluído neste projeto.


## GitHub Actions e GitHub Manager

O projeto inclui `github-manager.json` com nome, versão, `versionName`, `versionCode`, `applicationId`, `namespace`, linguagem, tipo e fonte da versão. A fonte principal da versão é `app/build.gradle.kts`.

O workflow `.github/workflows/android-kotlin-apk.yml` pode ser executado manualmente ou em pushes para `main`/`master`. Ele valida a versão, instala o SDK necessário, compila o app e publica diretamente `Relogio-Flutuante.apk` em uma GitHub Release. Não publica AAB nem ZIP de artifact.

### Versão atual

- `versionName`: `1.0.7`
- `versionCode`: `8`
- APK: `Relogio-Flutuante.apk`

## Organização do código

O projeto foi fatorado desde a base para evitar arquivos monolíticos conforme novas funções forem adicionadas:

- `ui/screens/`: telas de Relógio, Contagem e Sobreposição.
- `ui/components/`: componentes reutilizáveis, cartões, campos, controles de overlay e diálogos.
- `ui/theme/`: tema e paleta visual.
- `state/`: estado persistente do relógio, contagem e overlay.
- `overlay/`: serviços de overlay normal e por Acessibilidade, detecção de compatibilidade, controle da janela, arraste e notificações.
- `MainActivity.kt`: apenas ponto de entrada do aplicativo.

A recomendação para novas funções é manter cada responsabilidade no pacote correspondente e evitar concentrar lógica de estado diretamente nas telas.

## GitHub Actions

O workflow usa `android-actions/setup-android@v4` com a instalação automática de pacotes desativada no próprio action (`packages: ""`). Os pacotes necessários são instalados explicitamente pelo `sdkmanager`, evitando a tentativa de instalar o pacote Android legado `tools`.
