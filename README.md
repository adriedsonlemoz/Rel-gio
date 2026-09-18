# Relógio Flutuante

Aplicativo Android nativo em Kotlin + Jetpack Compose com relógio ajustável, contagem regressiva e janela compacta sobre outros aplicativos.

## Recursos

- Relógio principal em `HH:mm:ss` com ajuste interno independente do relógio do Android.
- Contagem regressiva com horas, minutos e segundos; iniciar, pausar, continuar e zerar.
- Overlay normal com `TYPE_APPLICATION_OVERLAY` quando o aparelho permite.
- Overlay alternativo por Acessibilidade com `TYPE_ACCESSIBILITY_OVERLAY` para aparelhos que bloqueiam a permissão tradicional.
- O serviço de Acessibilidade não lê conteúdo de janelas e não executa cliques ou gestos.
- Modo por notificação mantido como último fallback.
- Janela com três formatos: `HH:MM:SS`, `MM:SS` e somente segundos `:SS`.
- Três tamanhos, opacidade ajustável e posição bloqueável.
- Ao bloquear a posição, a janela usa `FLAG_NOT_TOUCHABLE`, deixando os toques passarem integralmente para o jogo.
- Posição lembrada separadamente em retrato e paisagem.
- Menu principal com `Permissões e configuração` e `Sobre`.
- Ativação direta pela tela Relógio, sem precisar entrar primeiro na aba Sobrepor.
- Guia completo de configuração disponível pelo menu para permissões e restrições de APK instalado fora da Play Store.
- Reavaliação automática de permissões ao voltar das Configurações do Android.
- Interface em tela inteira/imersiva, com barras do sistema ocultas e reaparecimento temporário por gesto.
- Layout edge-to-edge com proteção para recortes/notches e largura responsiva.
- Fusos horários salvos na própria tela Relógio, com catálogo pesquisável de cidades, reordenação e remoção.
- Cada fuso mostra `HH:mm:ss`, UTC, diferença para o fuso local e mudança de dia quando aplicável.

## Primeiro uso e permissões

Em APKs instalados fora da Play Store, algumas versões do Android exigem liberar manualmente **Permitir configurações restritas** antes de ativar um serviço de Acessibilidade. A própria tela Relógio mostra o estado e conduz o processo: primeiro abre `Informações do app`, depois leva à Acessibilidade e, ao voltar, revalida o estado automaticamente. O usuário ainda precisa tocar no menu de três pontos e confirmar a opção, pois o Android não fornece API pública para concedê-la automaticamente.

Depois, o app pode abrir diretamente a tela de Acessibilidade. Se a ativação tiver sido iniciada pelo botão principal do overlay, o app verifica o novo estado ao voltar e ativa a janela automaticamente quando possível.

A tela de sobreposição normal também pode ser aberta pelo app. Em alguns Androids, o sistema pode exibir a lista geral de aplicativos em vez da página específica.

## Fusos horários

Na tela Relógio, `+ Adicionar` abre uma lista pronta de cidades e fusos. É possível pesquisar por cidade, país ou identificador IANA, adicionar vários horários, remover e reordenar a lista. Os fusos usam `ZoneId`, portanto acompanham automaticamente regras de horário de verão quando existentes.

Os fusos mundiais usam o instante real fornecido pelo Android. O ajuste manual do relógio principal não altera os fusos internacionais; ele continua afetando apenas o horário principal do aplicativo e o overlay.

## Overlay para jogo

O mostrador usa fonte monoespaçada para evitar deslocamento dos números a cada segundo. O usuário pode escolher:

- `HH:MM:SS` — horário completo;
- `MM:SS` — minutos e segundos;
- `:SS` — somente os segundos, ideal para acompanhar janelas curtas dentro do jogo.

Quando a posição está desbloqueada, a janela pode ser arrastada e fechada. Depois de posicioná-la, `Bloquear posição` remove os controles e torna a janela não tocável, evitando interferência no jogo.

## Desempenho

Relógio, contagem e overlays atualizam apenas na virada do segundo. O serviço evita redesenhar conteúdo que não mudou. A aparência do overlay é aplicada somente quando uma configuração é alterada. O serviço de Acessibilidade deixa de manter ticker ativo quando a janela está desativada e acorda por mudanças nas preferências. A capacidade de sobreposição é lida uma única vez por ciclo de retorno das Configurações e compartilhada entre as telas, reduzindo consultas repetidas ao sistema durante a troca de abas.

## Testes

O projeto inclui testes unitários para:

- cálculo da contagem regressiva;
- alinhamento da atualização ao próximo segundo;
- cálculo do deslocamento do relógio;
- formatação da duração;
- formatos do overlay;
- escolha do método preferencial de sobreposição;
- fluxo do botão de ativação direta na tela Relógio;
- regras responsivas de largura, padding e tamanho do relógio;
- validade e pesquisa do catálogo de fusos;
- adição, remoção e reordenação dos fusos;
- cálculo de offsets UTC e diferença relativa entre zonas.

O GitHub Actions executa `:app:testDebugUnitTest` antes do APK. O workflow também impede arquivos Kotlin com mais de **250 linhas**, ajudando a evitar componentes e classes monolíticas.

## Organização do código

- `ui/screens/`: Relógio, Contagem, Sobreposição e Configuração inicial.
- `ui/components/`: componentes pequenos e reutilizáveis.
- `ui/dialogs/`: diálogo Sobre.
- `ui/theme/`: tema e paleta.
- `ui/layout/`: regras responsivas testáveis para diferentes larguras de tela.
- `state/`: estados persistentes, aparência, posição e cálculos.
- `timezones/`: catálogo, persistência, regras da lista e cálculos de fusos horários.
- `ui/components/timezones/`: componentes pequenos da lista de fusos.
- `ui/dialogs/timezones/`: seletor pesquisável de cidades.
- `overlay/`: serviços, janela, estilo, arraste, formatação e notificações.
- `app/src/test/`: testes unitários.
- `MainActivity.kt`: ponto de entrada e atualização de estado ao retornar das Configurações.

## Build

- Android SDK 36.
- JDK 17.
- AGP 9.4.0.
- Gradle 9.6.0.
- Jetpack Compose BOM 2026.06.00.
- AndroidX Core KTX 1.17.0.

O workflow `.github/workflows/android-kotlin-apk.yml` executa testes, valida metadados e organização do código, compila o APK e publica `Relogio-Flutuante.apk` diretamente em uma GitHub Release.

### Versão atual

- `versionName`: `1.1.5`
- `versionCode`: `16`
- APK: `Relogio-Flutuante.apk`

## Ativação simplificada
A ativação principal agora fica na própria tela Relógio. O botão muda conforme o estado: libera configurações restritas, leva à Acessibilidade e, quando tudo está pronto, ativa ou desativa a janela sobre o jogo. A aba Sobrepor fica focada nas opções avançadas do overlay.


## Tela inteira e refinamento visual
A interface principal agora usa modo imersivo edge-to-edge. As barras do Android ficam ocultas durante o uso e podem aparecer temporariamente por gesto. O conteúdo respeita recortes de tela, a navegação inferior foi compactada e as telas usam espaçamento unificado. Relógio e Contagem começam no topo em vez de centralizar conteúdo com grandes áreas vazias.

## Fusos horários salvos
A versão 1.1.5 adiciona relógios mundiais dentro da tela Relógio. A seleção fica salva localmente e pode ser reorganizada com os controles de posição, sem criar uma nova aba na navegação inferior.
