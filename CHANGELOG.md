# Changelog

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
