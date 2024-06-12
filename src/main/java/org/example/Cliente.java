package org.example;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class Cliente {
    private static final String SERVER_ADDRESS = "localhost";
    private static final int SERVER_PORT = 12345;

    public Cliente() {
    }

    public static void main(String[] args) {
        try {
            Socket socket = new Socket("localhost", 12345);

            try {
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                try {
                    PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

                    try {
                        Scanner scanner = new Scanner(System.in);

                        try {
                            System.out.println("Cliente conectado.");

                            while(true) {
                                System.out.println("Escolha uma opção: LISTA, ALUGUEL, DEVOLUCAO, ADICIONA ou SAIDA:");
                                String input = scanner.nextLine();
                                if (input.equalsIgnoreCase("SAIDA")) {
                                    break;
                                }

                                out.println(input);
                                String response = in.readLine();
                                System.out.println("Resposta do servidor: " + response);
                            }
                        } catch (Throwable var11) {
                            try {
                                scanner.close();
                            } catch (Throwable var10) {
                                var11.addSuppressed(var10);
                            }

                            throw var11;
                        }

                        scanner.close();
                    } catch (Throwable var12) {
                        try {
                            out.close();
                        } catch (Throwable var9) {
                            var12.addSuppressed(var9);
                        }

                        throw var12;
                    }

                    out.close();
                } catch (Throwable var13) {
                    try {
                        in.close();
                    } catch (Throwable var8) {
                        var13.addSuppressed(var8);
                    }

                    throw var13;
                }

                in.close();
            } catch (Throwable var14) {
                try {
                    socket.close();
                } catch (Throwable var7) {
                    var14.addSuppressed(var7);
                }

                throw var14;
            }

            socket.close();
        } catch (IOException var15) {
            IOException e = var15;
            e.printStackTrace();
        }

    }
}
