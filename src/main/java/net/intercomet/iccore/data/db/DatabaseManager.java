package net.intercomet.iccore.data.db;

import net.intercomet.iccore.Intercomet;

import java.sql.*;
import java.io.File;
import java.util.logging.Level;

public class DatabaseManager {
    private Connection connection;
    private Intercomet plugin;

    public DatabaseManager(Intercomet plugin) {
        this.plugin = plugin;
    }

    public void connect(File pluginFolder) {
        try {
            String fileName = "data.db";

            File dbFile = new File(pluginFolder, fileName);

            connection = DriverManager.getConnection("jbdc:h2" + dbFile.getAbsolutePath());

            createTables();
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to connect to database " + "data.db", e);
            plugin.getLogger().warning("Data will be lost on restart");
        }
    }

    public Connection getConnection() {
        return connection;
    }

    private void createTables() throws SQLException {
        Statement statement = connection.createStatement();

        statement.execute("""
            CREATE TABLE IF NOT EXISTS player_settings (
                uuid VARCHAR(36) PRIMARY KEY,
                private_message BOOLEAN,
                show_other_players BOOLEAN,
                chat_pings BOOLEAN
            )
        """);

        statement.execute("""
            CREATE TABLE IF NOT EXISTS player_stats (
                uuid VARCHAR(36),
                stat_key VARCHAR(50),
                stat_value VARCHAR(255),
                PRIMARY KEY (uuid, stat_key)
            )
        """);

        statement.execute("""
            CREATE TABLE IF NOT EXISTS punishments (
                player_uuid VARCHAR(36),
                punisher_uuid VARCHAR(36),
                reason VARCHAR(255),
                punishment_action VARCHAR(10),
                duration VARCHAR(12)
            )
        """);

        statement.close();
    }
}
