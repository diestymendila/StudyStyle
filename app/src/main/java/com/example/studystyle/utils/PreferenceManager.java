package com.example.studystyle.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.studystyle.models.BookItem;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class PreferenceManager {
    private final SharedPreferences prefs;
    private final SharedPreferences.Editor editor;

    public PreferenceManager(Context context) {
        prefs  = context.getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE);
        editor = prefs.edit();
    }

    public void    setLoggedIn(boolean v) { editor.putBoolean(Constants.KEY_IS_LOGGED_IN, v).apply(); }
    public boolean isLoggedIn()           { return prefs.getBoolean(Constants.KEY_IS_LOGGED_IN, false); }

    public void saveUser(String name, String email, String jurusan, int userId) {
        editor.putString(Constants.KEY_USER_NAME, name)
                .putString(Constants.KEY_USER_EMAIL, email)
                .putString(Constants.KEY_USER_JURUSAN, jurusan)
                .putInt(Constants.KEY_USER_ID, userId).apply();
    }

    public String getUserName()    { return prefs.getString(Constants.KEY_USER_NAME, ""); }
    public String getUserEmail()   { return prefs.getString(Constants.KEY_USER_EMAIL, ""); }
    public String getUserJurusan() { return prefs.getString(Constants.KEY_USER_JURUSAN, ""); }
    public int    getUserId()      { return prefs.getInt(Constants.KEY_USER_ID, -1); }

    public void    setDarkMode(boolean v) { editor.putBoolean(Constants.KEY_DARK_MODE, v).apply(); }
    public boolean isDarkMode()           { return prefs.getBoolean(Constants.KEY_DARK_MODE, false); }

    public void saveLastResult(String type, int v, int a, int k) {
        int userId = getUserId();
        editor.putString(Constants.KEY_LAST_RESULT     + userId, type)
                .putInt(Constants.KEY_LAST_VISUAL        + userId, v)
                .putInt(Constants.KEY_LAST_AUDITORY      + userId, a)
                .putInt(Constants.KEY_LAST_KINESTETIK    + userId, k).apply();
    }

    public String getLastResult()     { return prefs.getString(Constants.KEY_LAST_RESULT + getUserId(), ""); }
    public int    getLastVisual()     { return prefs.getInt(Constants.KEY_LAST_VISUAL + getUserId(), 0); }
    public int    getLastAuditory()   { return prefs.getInt(Constants.KEY_LAST_AUDITORY + getUserId(), 0); }
    public int    getLastKinestetik() { return prefs.getInt(Constants.KEY_LAST_KINESTETIK + getUserId(), 0); }

    public void restoreLastResult(String type, int v, int a, int k, int userId) {
        editor.putString(Constants.KEY_LAST_RESULT     + userId, type)
                .putInt(Constants.KEY_LAST_VISUAL        + userId, v)
                .putInt(Constants.KEY_LAST_AUDITORY      + userId, a)
                .putInt(Constants.KEY_LAST_KINESTETIK    + userId, k).apply();
    }

    public void   cacheQuote(String content, String author) {
        editor.putString(Constants.KEY_CACHED_QUOTE, content)
                .putString(Constants.KEY_CACHED_QUOTE_AUTHOR, author).apply();
    }
    public String getCachedQuote()       { return prefs.getString(Constants.KEY_CACHED_QUOTE, ""); }
    public String getCachedQuoteAuthor() { return prefs.getString(Constants.KEY_CACHED_QUOTE_AUTHOR, ""); }

    public void updateName(String n)    { editor.putString(Constants.KEY_USER_NAME, n).apply(); }
    public void updateJurusan(String j) { editor.putString(Constants.KEY_USER_JURUSAN, j).apply(); }

    public String getProfilePhotoPath() {
        return prefs.getString("profile_photo_" + getUserId(), "");
    }
    public void setProfilePhotoPath(String path) {
        editor.putString("profile_photo_" + getUserId(), path).apply();
    }

    // ── Rating buku ──

    public void saveBookRating(String bookKey, int rating) {
        editor.putInt("book_rating_" + bookKey, rating).apply();
    }

    public int getBookRating(String bookKey) {
        return prefs.getInt("book_rating_" + bookKey, 0);
    }

    // ── Favorit Quotes ──
    // Disimpan sebagai JSON array dari array [content, author]

    private static final String KEY_FAV_QUOTES = "fav_quotes_";

    private String favQuotesKey() {
        return KEY_FAV_QUOTES + getUserId();
    }

    public List<String[]> getFavoriteQuotes() {
        String json = prefs.getString(favQuotesKey(), "[]");
        Type type = new TypeToken<List<String[]>>() {}.getType();
        List<String[]> result = new Gson().fromJson(json, type);
        return result != null ? result : new ArrayList<>();
    }

    public boolean isQuoteFavorited(String content) {
        for (String[] item : getFavoriteQuotes()) {
            if (item[0].equals(content)) return true;
        }
        return false;
    }

    public void addFavoriteQuote(String content, String author) {
        List<String[]> list = getFavoriteQuotes();
        // Hindari duplikat
        for (String[] item : list) {
            if (item[0].equals(content)) return;
        }
        list.add(new String[]{content, author});
        editor.putString(favQuotesKey(), new Gson().toJson(list)).apply();
    }

    public void removeFavoriteQuote(String content, String author) {
        List<String[]> list = getFavoriteQuotes();
        list.removeIf(item -> item[0].equals(content));
        editor.putString(favQuotesKey(), new Gson().toJson(list)).apply();
    }

    // ── Favorit Buku ──
    // Disimpan sebagai JSON array dari BookItem (serialized manual)

    private static final String KEY_FAV_BOOKS = "fav_books_";

    private String favBooksKey() {
        return KEY_FAV_BOOKS + getUserId();
    }

    /** Model ringkas untuk persistensi buku favorit */
    public static class FavBook {
        public String key, title, author, year, genre, cover;
    }

    public List<BookItem> getFavoriteBooks() {
        String json = prefs.getString(favBooksKey(), "[]");
        Type type = new TypeToken<List<FavBook>>() {}.getType();
        List<FavBook> stored = new Gson().fromJson(json, type);
        List<BookItem> result = new ArrayList<>();
        if (stored == null) return result;
        for (FavBook fb : stored) {
            BookItem b = new BookItem();
            b.setFavoriteFields(fb.key, fb.title, fb.author, fb.year, fb.genre, fb.cover);
            result.add(b);
        }
        return result;
    }

    public boolean isBookFavorited(String bookKey) {
        String json = prefs.getString(favBooksKey(), "[]");
        Type type = new TypeToken<List<FavBook>>() {}.getType();
        List<FavBook> stored = new Gson().fromJson(json, type);
        if (stored == null) return false;
        for (FavBook fb : stored) {
            if (bookKey.equals(fb.key)) return true;
        }
        return false;
    }

    public void addFavoriteBook(BookItem book) {
        String json = prefs.getString(favBooksKey(), "[]");
        Type type = new TypeToken<List<FavBook>>() {}.getType();
        List<FavBook> stored = new Gson().fromJson(json, type);
        if (stored == null) stored = new ArrayList<>();
        // Cek duplikat
        for (FavBook fb : stored) {
            if (book.getKey().equals(fb.key)) return;
        }
        FavBook fb = new FavBook();
        fb.key    = book.getKey();
        fb.title  = book.getTitle();
        fb.author = book.getAuthor();
        fb.year   = book.getYear();
        fb.genre  = book.getGenre();
        fb.cover  = book.getCover();
        stored.add(fb);
        editor.putString(favBooksKey(), new Gson().toJson(stored)).apply();
    }

    public void removeFavoriteBook(String bookKey) {
        String json = prefs.getString(favBooksKey(), "[]");
        Type type = new TypeToken<List<FavBook>>() {}.getType();
        List<FavBook> stored = new Gson().fromJson(json, type);
        if (stored == null) return;
        stored.removeIf(fb -> bookKey.equals(fb.key));
        editor.putString(favBooksKey(), new Gson().toJson(stored)).apply();
    }

    public void clearSession() {
        editor.remove(Constants.KEY_IS_LOGGED_IN)
                .remove(Constants.KEY_USER_NAME)
                .remove(Constants.KEY_USER_EMAIL)
                .remove(Constants.KEY_USER_JURUSAN)
                .remove(Constants.KEY_USER_ID).apply();
    }

    public void clearAll() { editor.clear().apply(); }
}