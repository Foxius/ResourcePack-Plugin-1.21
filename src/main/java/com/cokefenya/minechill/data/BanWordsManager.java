package com.cokefenya.minechill.data;

import com.google.gson.*;
import org.bukkit.plugin.Plugin;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class BanWordsManager {
    private Set<String> bannedWords;
    private final Gson gson = new Gson();

    public BanWordsManager(Plugin plugin) {
        File banWordsFile = new File(plugin.getDataFolder(), "banwords.json");
        if (banWordsFile.exists()) {
            try {
                JsonParser parser = new JsonParser();
                JsonObject json = parser.parse(new FileReader(banWordsFile)).getAsJsonObject();
                JsonArray bannedWordsArray = json.getAsJsonArray("bannedWords");

                bannedWords = new HashSet<>();
                for (JsonElement element : bannedWordsArray) {
                    bannedWords.add(element.getAsString().toLowerCase());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            // Загрузка запрещенных слов из ресурсов плагина
            InputStream resource = plugin.getResource("banwords.json");
            if (resource != null) {
                try {
                    InputStreamReader reader = new InputStreamReader(resource, StandardCharsets.UTF_8);
                    JsonParser parser = new JsonParser();
                    JsonObject json = parser.parse(reader).getAsJsonObject();
                    JsonArray bannedWordsArray = json.getAsJsonArray("bannedWords");

                    bannedWords = new HashSet<>();
                    for (JsonElement element : bannedWordsArray) {
                        bannedWords.add(element.getAsString().toLowerCase());
                    }

                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private List<String> loadBanWords(Path configFile) {
        try {
            String content = Files.readString(configFile);
            BannedWordsList list = gson.fromJson(content, BannedWordsList.class);
            if (list != null && list.banwords != null) {
                return list.banwords;
            }
        } catch (IOException e) {
            // Handle exception if needed
        }
        return new ArrayList<>();
    }

    public boolean containsBannedWords(String input) {
        for (String bannedWord : bannedWords) {
            if (input.toLowerCase().contains(bannedWord.toLowerCase())) {
                return true;
            }
        }
        return false;
    }

    private static class BannedWordsList {
        List<String> banwords;
    }
}
