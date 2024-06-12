package org.example;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Main {
    private static final int PORT = 12345;
    private static List<Livro> livros = new ArrayList();
    private static final String FILE_PATH = "livros.json";
    private static final ObjectMapper mapper = new ObjectMapper();

    public Main () {
    }

    public static void main(String[] args) {
        loadLivros();

        try {
            ServerSocket serverSocket = new ServerSocket(12345);

            try {
                System.out.println("Servidor iniciado na porta 12345");

                while(true) {
                    Socket clientSocket = serverSocket.accept();
                    (new ClientHandler(clientSocket)).start();
                }
            } catch (Throwable var5) {
                try {
                    serverSocket.close();
                } catch (Throwable var4) {
                    var5.addSuppressed(var4);
                }

                throw var5;
            }
        } catch (IOException var6) {
            IOException e = var6;
            e.printStackTrace();
        }
    }

    private static void loadLivros() {
        File file = new File("livros.json");
        if (file.exists()) {
            try {
                livros = (List)mapper.readValue(file, new TypeReference<List<Livro>>() {
                });
            } catch (IOException var2) {
                IOException e = var2;
                e.printStackTrace();
            }
        } else {
            System.out.println("Arquivo livros.json não encontrado, iniciando com lista vazia.");
        }

    }

    private static void saveLivros() {
        try {
            mapper.writeValue(new File("livros.json"), livros);
        } catch (IOException var1) {
            IOException e = var1;
            e.printStackTrace();
        }

    }

    private static class ClientHandler extends Thread {
        private final Socket socket;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        public void run() {
            try {
                BufferedReader in = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));

                try {
                    PrintWriter out = new PrintWriter(this.socket.getOutputStream(), true);

                    String request;
                    try {
                        while((request = in.readLine()) != null) {
                            String[] parts = request.split(";");
                            switch (parts[0]) {
                                case "LISTA":
                                    out.println(Main.livros);
                                    break;
                                case "ALUGUEL":
                                    String tituloAlugar = parts[1];
                                    this.rentBook(tituloAlugar, out);
                                    break;
                                case "DEVOLUCAO":
                                    String tituloDevolver = parts[1];
                                    this.returnBook(tituloDevolver, out);
                                    break;
                                case "ADICIONA":
                                    String titulo = parts[1];
                                    String autor = parts[2];
                                    String genero = parts[3];
                                    int exemplares = Integer.parseInt(parts[4]);
                                    this.addBook(new Livro(autor, titulo, genero, exemplares), out);
                                    break;
                                default:
                                    out.println("Comando não reconhecido.");
                            }
                        }
                    } catch (Throwable var16) {
                        try {
                            out.close();
                        } catch (Throwable var15) {
                            var16.addSuppressed(var15);
                        }

                        throw var16;
                    }

                    out.close();
                } catch (Throwable var17) {
                    try {
                        in.close();
                    } catch (Throwable var14) {
                        var17.addSuppressed(var14);
                    }

                    throw var17;
                }

                in.close();
            } catch (IOException var18) {
                IOException e = var18;
                e.printStackTrace();
            }

        }

        private void rentBook(String titulo, PrintWriter out) {
            Iterator var3 = Main.livros.iterator();

            Livro livro;
            do {
                if (!var3.hasNext()) {
                    out.println("Livro não disponível .");
                    return;
                }

                livro = (Livro)var3.next();
            } while(!livro.getTitulo().equalsIgnoreCase(titulo) || livro.getExemplares() <= 0);

            livro.setExemplares(livro.getExemplares() - 1);
            Main.saveLivros();
            out.println("Livro alugado: " + livro);
        }

        private void returnBook(String titulo, PrintWriter out) {
            Iterator var3 = Main.livros.iterator();

            Livro livro;
            do {
                if (!var3.hasNext()) {
                    out.println("Livro não encontrado.");
                    return;
                }

                livro = (Livro)var3.next();
            } while(!livro.getTitulo().equalsIgnoreCase(titulo));

            livro.setExemplares(livro.getExemplares() + 1);
            Main.saveLivros();
            out.println("Livro devolvido: " + livro);
        }

        private void addBook(Livro livro, PrintWriter out) {
            Main.livros.add(livro);
            Main.saveLivros();
            out.println("Livro adicionado: " + livro);
        }
    }
}
