# Relógio Flutuante

Aplicativo Android nativo em Kotlin + Jetpack Compose com relógio ajustável, contagem regressiva, alarmes, fusos horários e janela compacta sobre outros aplicativos.

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
- Menu principal refinado em folha inferior, com acesso a `Permissões e configuração`, `Sobre` e atalho de apoio via PIX.
- Ativação direta pela tela Relógio, sem precisar entrar primeiro na aba Sobrepor.
- Guia completo de configuração disponível pelo menu para permissões e restrições de APK instalado fora da Play Store.
- Reavaliação automática de permissões ao voltar das Configurações do Android.
- Interface em tela inteira/imersiva, com barras do sistema ocultas e reaparecimento temporário por gesto.
- Layout edge-to-edge com proteção para recortes/notches e largura responsiva.
- Fusos horários salvos na própria tela Relógio, com catálogo pesquisável de cidades, reordenação e remoção.
- Cada fuso mostra `HH:mm:ss`, UTC, diferença para o fuso local e mudança de dia quando aplicável.
- Aba Alarmes com criação, edição, ativação/desativação, exclusão, nome opcional e repetição por dias da semana.
- Alarmes usam horário real do Android, podem tocar com áudio/vibração e são restaurados após reinicialização ou mudanças de horário/fuso.
- Ícone próprio do aplicativo com variantes normal, redonda, adaptativa e monocromática, mantendo os elementos principais dentro da área segura dos launchers Android.
- Card opcional de apoio ao projeto com chave PIX copiável (`adriedson@outlook.com`) e feedback visual ao copiar.

## Primeiro uso e permissões

Em APKs instalados fora da Play Store, o Android 13 ou superior pode exigir a liberação manual de **Permitir configurações restritas** antes de ativar um serviço de Acessibilidade. No Android 12 e anteriores, o assistente pula esse passo e segue diretamente para Acessibilidade. A própria tela Relógio mostra o estado e conduz o processo: abre `Informações do app` quando necessário, depois leva à Acessibilidade e, ao voltar, revalida o estado automaticamente. O usuário ainda precisa tocar no menu de três pontos e confirmar a opção, pois o Android não fornece API pública para concedê-la automaticamente.

Em alguns aparelhos, a opção **Permitir configurações restritas** pode não aparecer ou responder imediatamente ao abrir as Informações do app. O guia orienta aguardar alguns segundos, tentar o menu novamente e oferece um atalho para reabrir essa tela sem perder o fluxo.

Depois, o app pode abrir diretamente a tela de Acessibilidade. Se a ativação tiver sido iniciada pelo botão principal do overlay, o app verifica o novo estado ao voltar e ativa a janela automaticamente quando possível.

A tela de sobreposição normal também pode ser aberta pelo app. Em alguns Androids, o sistema pode exibir a lista geral de aplicativos em vez da página específica.

## Fusos horários

Na tela Relógio, `+ Adicionar` abre uma lista pronta de cidades e fusos. É possível pesquisar por cidade, país ou identificador IANA, adicionar vários horários, remover e reordenar a lista. Os fusos usam `ZoneId`, portanto acompanham automaticamente regras de horário de verão quando existentes.

Os fusos mundiais usam o instante real fornecido pelo Android. O ajuste manual do relógio principal não altera os fusos internacionais; ele continua afetando apenas o horário principal do aplicativo e o overlay.

## Alarmes

A aba `Alarmes` permite criar alarmes únicos ou recorrentes por dias da semana. O nome é opcional, cada item pode ser ativado/desativado e os alarmes salvos permanecem no aparelho. Alarmes de uma única vez são desativados automaticamente depois de tocar; os recorrentes são reagendados.

No Android 12 ou superior, o aplicativo verifica o acesso especial **Alarmes e lembretes**. Com o acesso liberado, usa alarmes exatos para maior precisão. Sem esse acesso, mantém um agendamento compatível menos preciso, sujeito aos atrasos que o Android pode aplicar. No Android 13 ou superior, a tela também solicita a permissão de notificações quando necessária.

Quando o alarme dispara, o app inicia um serviço de toque com o som de alarme padrão do sistema em loop, vibração e uma notificação com a ação `Parar`. O toque contínuo é encerrado automaticamente após 5 minutos. Se o sistema bloquear a inicialização contínua em segundo plano, existe um fallback por notificação sonora.

## Overlay para jogo

O mostrador usa fonte monoespaçada para evitar deslocamento dos números a cada segundo. O usuário pode escolher:

- `HH:MM:SS` — horário completo;
- `MM:SS` — minutos e segundos;
- `:SS` — somente os segundos, ideal para acompanhar janelas curtas dentro do jogo.

Quando a posição está desbloqueada, a janela pode ser arrastada e fechada. Depois de posicioná-la, `Bloquear posição` remove os controles e torna a janela não tocável, evitando interferência no jogo.

## Desempenho

Relógio, contagem e overlays atualizam apenas na virada do segundo. O serviço evita redesenhar conteúdo que não mudou. A aparência do overlay é aplicada somente quando uma configuração é alterada. O serviço de Acessibilidade deixa de manter ticker ativo quando a janela está desativada e acorda por mudanças nas preferências. A capacidade de sobreposição é lida uma única vez por ciclo de retorno das Configurações e compartilhada entre as telas, reduzindo consultas repetidas ao sistema durante a troca de abas. A navegação do menu superior passou a usar uma folha inferior simples, evitando o popup lateral desalinhado e reduzindo recomposições visuais desnecessárias no cabeçalho.
Os resumos relativos de alarmes agora são atualizados na virada do minuto, em vez de duas vezes por minuto, e a verificação passiva do estado externo do overlay foi reduzida para intervalos de 2 segundos; ações locais continuam atualizando imediatamente seus próprios estados de interface.

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
- cálculo de offsets UTC e diferença relativa entre zonas;
- serialização/persistência dos alarmes;
- cálculo do próximo disparo de alarme único e repetido;
- resumos dos padrões de repetição.

O GitHub Actions executa `:app:testDebugUnitTest` antes do APK. O workflow também impede arquivos Kotlin com mais de **250 linhas**, ajudando a evitar componentes e classes monolíticas.

## Organização do código

- `ui/screens/`: Relógio, Contagem, Alarmes, Sobreposição e Configuração inicial.
- `ui/components/`: componentes pequenos e reutilizáveis.
- `ui/dialogs/`: diálogo Sobre.
- `ui/theme/`: tema e paleta.
- `ui/layout/`: regras responsivas testáveis para diferentes larguras de tela.
- `state/`: estados persistentes, aparência, posição e cálculos.
- `timezones/`: catálogo, persistência, regras da lista e cálculos de fusos horários.
- `ui/components/timezones/`: componentes pequenos da lista de fusos.
- `ui/dialogs/timezones/`: seletor pesquisável de cidades.
- `overlay/`: serviços, janela, estilo, arraste, formatação e notificações.
- `alarms/`: modelo, persistência, agendamento, receivers, serviço de toque e regras de horário.
- `ui/components/alarms/` e `ui/dialogs/alarms/`: lista, permissões e editor de alarmes.
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

- `versionName`: `1.3.2`
- `versionCode`: `22`
- APK: `Relogio-Flutuante.apk`

## Ativação simplificada
A ativação principal agora fica na própria tela Relógio. O botão muda conforme o estado: libera configurações restritas quando necessário, leva à Acessibilidade e, quando tudo está pronto, ativa ou desativa a janela sobre o jogo. A aba Sobrepor fica focada nas opções avançadas do overlay.


## Tela inteira e refinamento visual
A interface principal agora usa modo imersivo edge-to-edge. As barras do Android ficam ocultas durante o uso e podem aparecer temporariamente por gesto. O conteúdo respeita recortes de tela, a navegação inferior foi compactada e as telas usam espaçamento unificado. Relógio e Contagem começam no topo em vez de centralizar conteúdo com grandes áreas vazias.

## Fusos horários salvos
A versão 1.1.5 adiciona relógios mundiais dentro da tela Relógio. A seleção fica salva localmente e pode ser reorganizada com os controles de posição, sem criar uma nova aba na navegação inferior.


## Alarmes na versão 1.2.0
A versão 1.2.0 adiciona uma aba própria de alarmes, com alarmes únicos ou recorrentes, nome opcional, controle de ativação e integração com o agendamento do Android. O app orienta a liberação de alarmes exatos quando necessária e mantém fallback compatível quando essa permissão não está disponível.
## QA e permissões na versão 1.2.1
A versão 1.2.1 revisa o fluxo de configuração do overlay. Android 13+ mantém o passo de configurações restritas quando necessário, com instruções de recuperação caso a opção demore a aparecer. Android 12 e anteriores seguem diretamente para Acessibilidade, evitando um passo inexistente nessas versões.
## Ícone do aplicativo na versão 1.2.2
A versão 1.2.2 integra o novo ícone do Relógio Flutuante aos recursos nativos do Android. O projeto inclui variantes por densidade, ícone redondo, Adaptive Icon para Android 8.0+ e versão monocromática para ícones temáticos no Android 13+. A arte principal foi mantida com margem de segurança para evitar cortes em máscaras circulares, squircle e outros formatos de launcher.
## Refinamento visual na versão 1.3.0
A versão 1.3.0 reorganiza alarmes, fusos e opções de sobreposição a partir da análise de uso em aparelho real. Alarmes mostram a próxima ocorrência e o tempo restante; o editor ganhou seletor próprio de horário e dias sem rolagem. Fusos exibem UTC, diferença legível e horário atual tanto na lista quanto antes de adicionar. Feedbacks por snackbar confirmam ações importantes, os controles visuais usam uma paleta azul/ciano consistente e o overlay oculta automaticamente os botões de mover/fechar após alguns segundos para interferir menos no jogo.

