package com.desktopui.api;

import com.desktopui.controller.ItemDTO;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class ApiClient {

    private static final String BASE_URL = "http://localhost:9090/api/items";
    private static final ObjectMapper mapper = new ObjectMapper();

    private ApiClient() {}

    public static void updateItem(ItemDTO item) throws Exception {

        URL url = new URL(BASE_URL + "/" + item.getId());
        HttpURLConnection conn =
                (HttpURLConnection) url.openConnection();

        conn.setRequestMethod("PUT");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);

        try (OutputStream os = conn.getOutputStream()) {
            mapper.writeValue(os, item);
        }

        if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
            throw new RuntimeException(
                    "Failed to update item. HTTP code: "
                            + conn.getResponseCode()
            );
        }

        conn.disconnect();
    }

    /* ================= DELETE ITEM ================= */

    public static void deleteItem(long itemId) throws Exception {

        URL url = new URL(BASE_URL + "/" + itemId);
        HttpURLConnection conn =
                (HttpURLConnection) url.openConnection();

        conn.setRequestMethod("DELETE");
        conn.setRequestProperty("Accept", "application/json");

        int responseCode = conn.getResponseCode();

        if (responseCode != HttpURLConnection.HTTP_OK
                && responseCode != HttpURLConnection.HTTP_NO_CONTENT) {

            throw new RuntimeException(
                    "Failed to delete item. HTTP code: " + responseCode
            );
        }

        conn.disconnect();
    }
}
