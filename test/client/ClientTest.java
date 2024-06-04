package client;

import org.junit.jupiter.api.Test;

import javax.swing.*;


class ClientTest {

    @Test
    void sendMove() {

    }

    @Test
    void main() {
        SwingUtilities.invokeLater(() -> {
            new Client("Player");
        });
    }
}