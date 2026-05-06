# Baroness Player - Java Edition 🎵

Este projeto é um player de música desenvolvido como atividade prática para a disciplina de **Programação Avançada** do curso de **Ciência da Computação** da **Universidade Tuiuti do Paraná (UTP)**.

O objetivo principal é aplicar conceitos avançados de programação orientada a objetos, design de interfaces e arquitetura de software utilizando a linguagem Java.

## 🚀 Tecnologias Utilizadas

- **Java 25**: Utilizando as versões mais recentes da linguagem.
- **JavaFX 26**: Framework para construção da interface gráfica (GUI).
- **Maven**: Gerenciador de dependências e automação de build.
- **Ikonli (Ionicons)**: Biblioteca para integração de ícones vetoriais.

## 🏗️ Arquitetura MVC

O projeto segue o padrão de arquitetura **MVC (Model-View-Controller)** para garantir a separação de responsabilidades e facilitar a manutenção:

- **Model**: Gerencia os dados e a lógica de negócio (ex: manipulação de arquivos de áudio, listas de reprodução).
- **View**: Define a interface visual utilizando arquivos `.fxml` (localizados em `src/main/resources`).
- **Controller**: Atua como intermediário, processando as interações do usuário na View e atualizando o Model (localizados em `com.otaviogustavo.controllers`).

## ✨ Ícones e Estilização

Para uma interface moderna e intuitiva, o projeto utiliza o pacote **Ionicons** através da biblioteca **Ikonli**.

**Como usar no FXML:**
```xml
<?import org.kordamp.ikonli.javafx.FontIcon?>

<FontIcon iconLiteral="ion-md-play" iconSize="24" />
```

Os prefixos disponíveis para este pacote são `ion-md-` (Material Design) e `ion-ios-` (iOS style).

## 🛠️ Como Compilar e Executar

Certifique-se de ter o **Maven** e o **JDK 25** instalados em sua máquina.

1. **Clonar o repositório:**
   ```bash
   git clone [url-do-repositorio]
   cd baroness-player
   ```

2. **Compilar e executar o projeto:**
   Utilize o plugin do JavaFX para Maven para rodar a aplicação diretamente:
   ```bash
   mvn clean javafx:run
   ```

3. **Gerar o pacote (Build):**
   ```bash
   mvn clean package
   ```

---
*Desenvolvido por Otávio Oliveira e Gustavo Coraleski - Acadêmicos de Ciência da Computação @ UTP*
