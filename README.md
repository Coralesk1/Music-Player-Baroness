# Baroness Player - Java Edition 🎵

<p align="center">
  <img src="src/main/resources/com/otaviogustavo/images/icons/baroness.png" alt="Baroness Player Logo" width="120">
</p>

O **Baroness Player** é um reprodutor de música moderno e intuitivo desenvolvido em Java. Criado como um projeto prático para a disciplina de **Programação Avançada** do curso de **Ciência da Computação** na **Universidade Tuiuti do Paraná (UTP)**, ele demonstra a aplicação de padrões de projeto robustos, interfaces gráficas fluidas e gestão eficiente de estruturas de dados.

---

## ✨ Funcionalidades Principais

- **🎧 Reprodução Completa**: Controles de play/pause, avançar, retroceder e controle de volume.
- **📚 Gestão de Biblioteca**: Adicione e remova suas músicas, com suporte a metadados (ID3 tags) e capas de álbum automáticas.
- **📝 Playlists Personalizadas**: Crie, edite e organize suas próprias listas de reprodução.
- **🔍 Busca Inteligente**: Filtre sua biblioteca por título, artista, álbum ou gênero.
- **🕒 Histórico de Reprodução**: Acesso rápido às últimas músicas ouvidas.
- **💾 Persistência Automática**: Seus dados e preferências são salvos automaticamente em formato JSON (`BibliotecaEPlaylists.json`).
- **🔄 Sincronização em Tempo Real**: O player detecta se arquivos foram movidos ou deletados do disco e mantém sua biblioteca limpa.
- **🎨 UI Dinâmica**: Interface moderna com sliders que mudam de cor conforme o progresso e volume.

---

## 🛠️ Tecnologias Utilizadas

O projeto utiliza o que há de mais moderno no ecossistema Java:

- **Java 25**: Aproveitando os recursos mais recentes da linguagem.
- **JavaFX 26**: Interface gráfica rica e responsiva.
- **Maven**: Gestão de dependências e automação de build.
- **Ikonli (Ionicons 4)**: Pacote de ícones vetoriais elegantes.
- **mp3agic**: Biblioteca para leitura de metadados MP3 e extração de capas.
- **Gson**: Serialização e desserialização de dados para persistência local.

---

## 🏗️ Arquitetura e Padrões de Projeto

O software foi projetado seguindo princípios de código limpo e modularidade:

### 1. MVC (Model-View-Controller)
- **Model**: `Musica`, `PlayList` e `GerenciadorEstruturas` gerenciam o estado e a lógica de negócio.
- **View**: Telas definidas em FXML (`main.fxml`, `playlist_view.fxml`, etc) com estilização em CSS.
- **Controller**: Controladores especializados gerenciam a interação do usuário e atualizam a interface.

### 2. Design Patterns
- **Singleton Pattern**: Garante a existência de uma instância única e global para o gerenciamento de estados centrais da aplicação e controle do player.
- **Command Pattern**: Utilizado para encapsular as ações de reprodução (`ComandoPlay`, `ComandoProximo`, `ComandoAnterior`), facilitando a manutenção e extensibilidade.
- **Observer Pattern**: Uso extensivo de `Properties` do JavaFX para atualizar a UI automaticamente quando o estado da música ou do player muda.

---

## 🚀 Como Executar

### Pré-requisitos
- **JDK 25** ou superior instalado.
- **Maven** configurado no PATH.

### Passos
1. **Clonar o repositório:**
   ```bash
   git clone [url-do-repositorio]
   cd baroness-player-java-edition
   ```

2. **Executar via Maven:**
   ```bash
   mvn clean javafx:run
   ```

3. **Gerar Executável Nativo (.exe):**
   ```bash
   mvn clean package
   jpackage --type app-image --name BaronessPlayer --input target --main-jar BaronessPlayer.jar --main-class com.otaviogustavo.Main --icon baroness.ico --dest executavel
   ```

> 💡 **Nota para a Avaliação (Professor):** O aplicativo já encontra-se compilado e empacotado de forma autossuficiente para o Windows. Para testar o programa **não é necessário ter o Java instalado**. Basta acessar o diretório `executavel/BaronessPlayer/` e dar um duplo-clique no arquivo `BaronessPlayer.exe`!

> 🎵 **Músicas para Teste:** Para facilitar a correção e experimentação do player, incluímos uma pasta separada no projeto contendo algumas músicas de teste. Sinta-se à vontade para importá-las no player para testar a reprodução, leitura de metadados e capas de álbum!

---

## 📁 Estrutura do Projeto

```text
baroness-player-java-edition/
├── executavel/                # Versão final compilada pronta para uso
│   └── BaronessPlayer/        # Pasta do programa (contém o .exe e a JVM embutida)
├── src/main/java/com/otaviogustavo/
│   ├── App.java               # Entry point do JavaFX
│   ├── Main.java              # Entry point auxiliar para o empacotamento nativo
│   ├── GerenciadorEstruturas.java # Core da lógica de dados
│   ├── commands/              # Implementação do Command Pattern
│   └── controllers/           # Controladores da interface
└── src/main/resources/com/otaviogustavo/
    ├── views/                 # Telas da interface (FXML)
    ├── css/                   # Estilização visual (CSS)
    └── images/                # Ícones e imagens (ex: baroness.png/ico)
```

---

## 👥 Autores

- **Otávio Oliveira**
- **Gustavo Coraleski**

*Acadêmicos de Ciência da Computação @ UTP*

---
<p align="center">
  Desenvolvido com ❤️ para a disciplina de Programação Avançada.
</p>
