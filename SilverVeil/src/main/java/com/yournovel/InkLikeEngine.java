package com.yournovel;

import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;

public class InkLikeEngine {
    private final Story story;
    private final Map<Long, String> userStates = new HashMap<>();

    public InkLikeEngine() {
        try (InputStreamReader reader = new InputStreamReader(
                getClass().getResourceAsStream("/story/novel.json"), "UTF-8")) {
            this.story = new Gson().fromJson(reader, Story.class);
        } catch (Exception e) {
            throw new RuntimeException("Cannot load novel.json", e);
        }
    }

    public void resetUser(long chatId) {
        userStates.put(chatId, "part1.1.Msg1");
    }

    public OutputBlock getOutput(long chatId) {
        String nodeKey = userStates.getOrDefault(chatId, "part1.1.Msg1");
        Node node = story.nodes.get(nodeKey);
        if (node == null) return new OutputBlock("Сцена не знайдена.", List.of());
        StringBuilder sb = new StringBuilder();
        for (ContentBlock block : node.content) {
            sb.append(block.text).append("\n\n");
        }
        return new OutputBlock(sb.toString().trim(), node.choices);
    }

    public OutputBlock choose(long chatId, String choiceText) {
        String nodeKey = userStates.getOrDefault(chatId, "part1.1.Msg1");
        Node node = story.nodes.get(nodeKey);
        if (node == null) return new OutputBlock("Сцена не знайдена.", List.of());
        for (Choice c : node.choices) {
            if (c.text.equalsIgnoreCase(choiceText)) {
                userStates.put(chatId, c.next);
                return getOutput(chatId);
            }
        }
        return new OutputBlock("Вибір не знайдено. Спробуйте ще раз.", node.choices);
    }

    // Внутрішні класи для Gson
    public static class Story {
        Map<String, Node> nodes;
    }
    public static class Node {
        List<ContentBlock> content;
        List<Choice> choices;
    }
    public static class ContentBlock {
        String type;
        String text;
        String name;
    }
    public static class Choice {
        String text;
        String next;
    }
    public static class OutputBlock {
        String text;
        List<Choice> choices;
        public OutputBlock(String text, List<Choice> choices) {
            this.text = text;
            this.choices = choices;
        }
    }
}