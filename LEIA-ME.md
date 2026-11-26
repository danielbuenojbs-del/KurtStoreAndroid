# Kurt Store Android — projeto fonte

Este projeto prepara um app Android nativo com WebView que abre primeiro `https://monetiza.link/KurtStore`, mantendo esse link como URL inicial para preservar o fluxo de monetização/redirecionamento configurado pelo proprietário.

Depois do redirecionamento, a loja carrega em tela cheia dentro do app. A barra inferior injetada cobre a área inferior da página (inclusive o selo flutuante do construtor, se estiver naquela região) e oferece atalho de início e busca que aciona a busca nativa do site.

## Ícone
O ícone Android foi gerado a partir da segunda imagem enviada pelo usuário.

## Downloads
O WebView usa o DownloadManager do Android para arquivos disponibilizados pela loja.

## Gerar APK
O ambiente de criação desta conversa não possui Android SDK/Gradle instalados, então não foi possível compilar ou assinar um APK binário aqui. O projeto inclui workflow GitHub Actions em `.github/workflows/build-apk.yml`:
1. Crie um repositório GitHub vazio.
2. Envie o conteúdo deste projeto para a branch `main`.
3. Abra a aba Actions e execute `Build Kurt Store APK` (ou aguarde o build após push).
4. Baixe o artefato `Kurt-Store-debug-apk`; dentro estará `app-debug.apk`.

O APK de debug serve para instalação e testes. Para distribuição pública, gere uma versão release assinada com uma chave própria e guarde essa chave em local seguro.

## Observações importantes
- O app precisa de internet; não é uma loja offline.
- O ponto de entrada é o link Monetiza informado. A monetização efetiva depende das regras, campanhas, redirecionamentos e condições da própria Monetiza Link; o app não consegue garantir ganhos por instalação ou abertura.
- A barra inferior é injetada no WebView e tenta usar o campo/botão de pesquisa existentes na Kurt Store. Se o Base44 mudar a estrutura/placeholder da busca, o seletor pode precisar ser atualizado.
- O selo do Base44 é coberto visualmente pelo painel inferior quando aparece na área inferior. Isso não remove a marcação do serviço no site; apenas sobrepõe a interface dentro do app.
- A loja observada publicamente está acessível e informa zero aplicativos publicados no momento desta verificação.
- O sistema abre o site no WebView; não é possível prometer que todos os tipos de download ou todos os redirecionamentos externos funcionem sem teste em um aparelho Android real.
