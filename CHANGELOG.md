# Changelog

## 1.1.4

- Aplicativo transformado em modo tela inteira/imersivo, com barras de status e navegação ocultas durante o uso.
- Barras do sistema podem reaparecer temporariamente por gesto e são ocultadas novamente ao retornar o foco ao app.
- Adicionado suporte edge-to-edge com uso das áreas próximas às bordas e proteção para recortes/notches.
- Removidos paddings fixos de status/navigation bars que deixavam áreas mortas na interface.
- Tela Relógio deixa de centralizar verticalmente o conteúdo e passa a começar no topo, reduzindo o grande espaço vazio percebido.
- Relógio, Contagem, Sobrepor e Configuração passam a compartilhar `AppScreenColumn` para espaçamento e rolagem consistentes.
- Card principal de tempo recebeu acabamento mais compacto, cantos maiores, borda sutil e hierarquia tipográfica refinada.
- Navegação inferior foi compactada e passou a dividir igualmente a largura entre as três áreas, sem animações pesadas.
- Cabeçalho ficou mais compacto para aproveitar melhor a tela cheia.
- Ações de horário foram extraídas para `ClockTimeActions`; ações de contagem e diálogo de tempo esgotado também foram separados para evitar crescimento das telas.
- Capacidade de overlay agora é lida uma vez por `permissionRefresh` no controlador e compartilhada entre Relógio, Sobrepor e Configuração, reduzindo consultas repetidas ao sistema durante a navegação.
- Adicionado `ScreenLayoutRules` com teste unitário para larguras compacta, normal e ampla, incluindo padding e tamanho do relógio.
- Mantida a regra de nenhum arquivo Kotlin ultrapassar 250 linhas.
- Atualizados README, CHANGELOG, `github-manager.json`, `versionName` e `versionCode`.

## 1.1.3

- Adicionado botão de ativação do relógio flutuante diretamente na tela Relógio.
- A ativação não exige mais entrar primeiro na aba Sobrepor.
- O botão principal muda automaticamente conforme o estado: configurações restritas, confirmação do passo 1, Acessibilidade, ativar e desativar.
- Ao abrir Informações do app, o progresso do passo fica salvo para que o retorno mostre a próxima ação correta.
- Ao voltar da Acessibilidade, o estado é reavaliado pelo `onResume` e a ativação pendente é concluída automaticamente quando possível.
- O guia completo continua disponível pelo menu, mas deixou de bloquear a entrada inicial do aplicativo.
- Adicionado `ClockOverlayActionResolver` para manter a regra de ativação fora da interface.
- Adicionado `ClockOverlayActionCard` como componente isolado, evitando crescimento da tela principal.
- Adicionado `ClockOverlayActionResolverTest` cobrindo os estados da ativação direta.
- Atualizados README, CHANGELOG, `github-manager.json`, `versionName` e `versionCode`.

## 1.1.2 (13)
- Simplifica a ativação para um assistente objetivo de dois passos: configurações restritas e Acessibilidade.
- Após a Acessibilidade ser detectada, oferece um único botão para ativar a janela e concluir.
- No passo de Acessibilidade, permite voltar às Informações do app caso a configuração restrita ainda não tenha sido liberada.
- A tela Sobrepor deixa de exibir métodos alternativos antes da configuração principal estar pronta.
- O botão principal passa a usar textos diretos: “Configurar em 2 passos” e “Ativar relógio sobre o jogo”.
- Substitui a NavigationBar animada por navegação inferior leve e imediata, reduzindo a sensação de atraso entre telas.
- Evita atualizar o estado visual do overlay quando o valor lido não mudou.
- Mantém estado da aba com rememberSaveable.
- Adiciona testes unitários para a resolução das etapas do assistente de ativação.
- Mantém a regra de arquivos Kotlin abaixo de 250 linhas.

## 1.1.1 - 2026-09-17

- Corrigido erro de compilação Kotlin em `CountdownScreen.kt` detectado antes da execução dos testes unitários.
- As ações `Pausar` e `Continuar` agora passam `modifier` e `onClick` explicitamente para `Button`, evitando conflito com a ordem dos parâmetros da API Compose.
- Revisado o projeto inteiro para ocorrências do mesmo padrão posicional; não foram encontradas outras chamadas incorretas.
- Mantidos os testes unitários e a validação de arquivos Kotlin abaixo de 250 linhas no GitHub Actions.
- Versão atualizada para `1.1.1` (`versionCode 12`).

## 1.1.0 - 2026-09-17

- Adicionada configuração inicial guiada para APK instalado fora da Play Store.
- Adicionado atalho direto para Informações do app, Acessibilidade e Sobrepor a outros apps.
- Permissões são reavaliadas automaticamente ao retornar das Configurações do Android.
- Ativação iniciada pelo app agora continua automaticamente quando a permissão correspondente é concedida.
- Tela Sobrepor reorganizada para destacar um único estado principal e mover métodos alternativos para “Outros métodos”.
- Adicionados formatos `HH:MM:SS`, `MM:SS` e `:SS` para a janela flutuante.
- Adicionados tamanho, opacidade e bloqueio de posição do overlay.
- Quando bloqueado, o overlay deixa de capturar toques e esconde controles de mover/fechar.
- Posição do overlay agora é salva separadamente para retrato e paisagem e limitada à área visível.
- Visual da janela flutuante ficou mais compacto, com números monoespaçados, grip discreto e borda reduzida.
- Navegação inferior recebeu ícones vetoriais consistentes e indicador alinhado à cor principal do app.
- Adicionado menu com “Permissões e configuração” e “Sobre”, incluindo versão instalada.
- Tela de Relógio recebeu melhor distribuição vertical e contraste de textos secundários.
- Tela de Contagem esconde os campos durante execução/pausa e separa as ações em componente próprio.
- Adicionados testes unitários para relógio, contagem, formatação e estratégia de overlay.
- Serviço de Acessibilidade deixa de atualizar a cada segundo quando o overlay está desativado, reduzindo trabalho em aparelhos low-RAM.
- GitHub Actions agora executa testes antes do APK e bloqueia arquivos Kotlin acima de 250 linhas.
- Versão atualizada para `1.1.0` (`versionCode 11`).

## 1.0.9

- Confirmado pelo GitHub Actions que o APK compila com sucesso (`BUILD SUCCESSFUL`).
- Corrigida a etapa de publicação que falhava depois do build com `Error creating asset temp dir`.
- Removido `softprops/action-gh-release@v2` da publicação do APK; a Release agora usa o GitHub CLI (`gh release`).
- A publicação agora suporta reexecução do mesmo workflow: se a Release já existir, o APK é reenviado com `--clobber`.
- Atualizadas as actions para runtimes Node 24 (`actions/checkout@v6`, `actions/setup-java@v6` e `gradle/actions/setup-gradle@v6`).
- Mantida a saída direta `Relogio-Flutuante.apk` na GitHub Release, sem AAB e sem ZIP de artifact.
- Atualizados README, `github-manager.json`, `versionName` e `versionCode`.

## 1.0.8

- Corrigido erro de compilação Kotlin no `OverlayService.kt` após a introdução do `OverlayWindowController` configurável.
- A callback de fechamento agora é passada explicitamente como `onClose`, evitando que a lambda seja interpretada como `canShow`.
- Revisadas as chamadas do `OverlayWindowController`; o serviço de Acessibilidade já utilizava argumentos nomeados corretamente.
- Atualizados README, `github-manager.json`, `versionName` e `versionCode`.

## 1.0.7

- Adicionada sobreposição alternativa por `AccessibilityService` com `TYPE_ACCESSIBILITY_OVERLAY`.
- O novo modo permite manter relógio ou contagem sobre o jogo em aparelhos que bloqueiam `SYSTEM_ALERT_WINDOW`.
- O serviço de Acessibilidade não solicita conteúdo das janelas (`canRetrieveWindowContent=false`), ignora eventos recebidos e não executa cliques ou gestos.
- A tela Sobrepor agora mostra separadamente disponibilidade da sobreposição normal, Acessibilidade e notificações.
- Em aparelhos low-RAM com a permissão tradicional bloqueada, o botão principal direciona para configurar a Acessibilidade em vez da tela indisponível de sobreposição.
- Depois de ativado o serviço de Acessibilidade, a janela pode ser ligada e desligada pelo próprio app sem reabrir as configurações.
- A janela por Acessibilidade reutiliza o mesmo estado, modo, posição, arraste e fechamento rápido do overlay normal.
- O modo por notificação foi mantido como fallback secundário.
- Janela flutuante ligeiramente compactada para ocupar menos espaço sobre o jogo.
- Tickers dos serviços realinhados à próxima virada de segundo para reduzir deriva visual.
- Atualizados README, `github-manager.json`, `versionName` e `versionCode`.

## 1.0.6

- Adicionado modo compatível para aparelhos low-RAM/Android Go que bloqueiam a permissão de sobreposição.
- O app evita abrir a tela de sobreposição indisponível quando detecta essa limitação e usa notificação persistente.
- Adicionado fallback manual “Usar modo compatível” para fabricantes que bloqueiam overlay mesmo fora do modo low-RAM.
- Relógio e contagem no modo compatível compartilham o mesmo estado do aplicativo.
- Adicionados controles rápidos de iniciar, pausar e continuar a contagem pela notificação.
- Reduzida a frequência do relógio de 5 atualizações/s para 1 atualização/s.
- Reduzida a frequência da contagem de 10 atualizações/s para atualização alinhada aos segundos.
- Reduzida a frequência do serviço de overlay de 5 atualizações/s para 1 atualização/s.
- O serviço agora evita redesenhar texto/notificação quando o conteúdo não mudou.
- Corrigida a exibição da contagem para arredondar o segundo restante corretamente, evitando perder um segundo logo após iniciar.
- Simplificado o `TimeCard`, removendo `BoxWithConstraints` da área atualizada a cada segundo.
- Navegação e cabeçalho ficaram mais compactos e leves, sem adicionar animações ou recursos gráficos externos.
- Mantida a fatoração: controle da janela flutuante e componentes de configuração/overlay foram separados para evitar arquivos monolíticos.
- Atualizados README, `github-manager.json`, `versionName` e `versionCode`.

## 1.0.5

- Corrigida incompatibilidade de AAR metadata encontrada no quarto build do GitHub Actions.
- Compose BOM ajustado de `2026.08.00` para `2026.06.00`, evitando Compose 1.12.x que exige `compileSdk 37`.
- `androidx.core:core-ktx` ajustado de `1.19.0` para `1.17.0`, mantendo compatibilidade com `compileSdk 36`.
- Mantidos `compileSdk 36`, `targetSdk 36`, AGP 9.4.0, Gradle 9.6.0 e JDK 17.
- Atualizados `github-manager.json`, README, `versionName` e `versionCode`.

## 1.0.4

- Corrigido erro de compilação Kotlin em `FloatingClockApp.kt` no layout responsivo.
- O valor de largura do `BoxWithConstraints` agora é capturado antes do `Box` interno, evitando uso inválido do receiver implícito.
- Confirmado pelo log que o workflow já passa pela instalação do Android SDK e chega à compilação do aplicativo.
- Corrigida a versão exibida no README para manter documentação e metadados sincronizados.
- Atualizados `github-manager.json`, `versionName` e `versionCode`.

## 1.0.3

- Corrigido o build do GitHub Actions que falhava ao tentar instalar `platforms;android-37`.
- Ajustados `compileSdk` e `targetSdk` para Android 16 / API 36 estável.
- Ajustado o workflow para instalar `platforms;android-36` com Build Tools 36.0.0.
- Mantidos AGP 9.4.0, Gradle 9.6.0 e JDK 17.
- Atualizados `github-manager.json`, `versionName` e `versionCode`.

## 1.0.2

- Refatorada a estrutura do projeto para evitar arquivos monolíticos.
- `MainActivity.kt` reduzido para apenas o ponto de entrada do aplicativo.
- Separadas as telas de Relógio, Contagem e Sobreposição.
- Separados componentes visuais reutilizáveis e tema.
- Separados os estados persistentes de relógio, contagem e overlay.
- Separado o serviço de overlay em serviço, criação de view, arraste e notificações.
- Atualizado o caminho do serviço no `AndroidManifest.xml`.
- Corrigido o workflow GitHub Actions para não tentar instalar o pacote Android legado `tools`.
- Atualizados `github-manager.json`, `versionName` e `versionCode`.

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
