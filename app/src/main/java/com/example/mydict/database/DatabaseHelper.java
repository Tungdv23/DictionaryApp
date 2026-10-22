
package com.example.mydict.database;
import android.util.Log;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.InputStream;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.mydict.model.Topic;
import com.example.mydict.model.Vocabulary;
import com.example.mydict.model.QuizResult;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "mydict.db";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_TOPIC = "Topic";
    private static final String TABLE_VOCABULARY = "Vocabulary";
    private static final String TABLE_QUIZ_RESULT = "QuizResult";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }



    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(
                "CREATE TABLE Topic (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "name TEXT NOT NULL UNIQUE)"
        );

        db.execSQL(
                "CREATE TABLE Vocabulary (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "topic_id INTEGER NOT NULL, " +
                        "word TEXT NOT NULL, " +
                        "meaning TEXT NOT NULL, " +
                        "part_of_speech TEXT, " +
                        "example TEXT, " +
                        "FOREIGN KEY(topic_id) REFERENCES Topic(id))"
        );

        db.execSQL(
                "CREATE TABLE QuizResult (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "topic_id INTEGER NOT NULL, " +
                        "score INTEGER NOT NULL, " +
                        "total INTEGER NOT NULL, " +
                        "date TEXT NOT NULL, " +
                        "FOREIGN KEY(topic_id) REFERENCES Topic(id))"
        );


        insertDefaultTopic(db, "Food");
        insertDefaultTopic(db, "Technology");
        insertDefaultTopic(db, "Education");
        insertDefaultTopic(db, "Travel");
        insertDefaultTopic(db, "Daily Life");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db,
                          int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS QuizResult");
        db.execSQL("DROP TABLE IF EXISTS Vocabulary");
        db.execSQL("DROP TABLE IF EXISTS Topic");

        onCreate(db);
    }


    private void insertDefaultTopic(SQLiteDatabase db, String name) {
        ContentValues values = new ContentValues();
        values.put("name", name);
        db.insert(TABLE_TOPIC, null, values);
    }

    public long addTopic(Topic topic) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("name", topic.getName());

        return db.insert(TABLE_TOPIC, null, values);
    }

    public List<Topic> getAllTopics() {
        List<Topic> list = new ArrayList<>();

        SQLiteDatabase db = getReadableDatabase();

        try (Cursor cursor = db.rawQuery(
                "SELECT id, name FROM Topic ORDER BY id ASC",
                null)) {

            while (cursor.moveToNext()) {
                int id = cursor.getInt(0);
                String name = cursor.getString(1);

                list.add(new Topic(id, name));
            }
        }

        return list;
    }


    public Topic getTopicById(int id) {
        SQLiteDatabase db = getReadableDatabase();

        try (Cursor cursor = db.query(
                TABLE_TOPIC,
                new String[]{"id", "name"},
                "id = ?",
                new String[]{String.valueOf(id)},
                null, null, null)) {

            if (cursor.moveToFirst()) {
                return new Topic(
                        cursor.getInt(0),
                        cursor.getString(1)
                );
            }
        }

        return null;
    }


    public long addVocabulary(Vocabulary vocabulary) {

        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put("topic_id", vocabulary.getTopicId());
        values.put("word", vocabulary.getWord());
        values.put("meaning", vocabulary.getMeaning());
        values.put("part_of_speech",
                vocabulary.getPartOfSpeech());
        values.put("example", vocabulary.getExample());

        return db.insert(TABLE_VOCABULARY, null, values);
    }


    private Vocabulary cursorToVocabulary(Cursor cursor) {

        return new Vocabulary(
                cursor.getInt(
                        cursor.getColumnIndexOrThrow("id")),
                cursor.getInt(
                        cursor.getColumnIndexOrThrow("topic_id")),
                cursor.getString(
                        cursor.getColumnIndexOrThrow("word")),
                cursor.getString(
                        cursor.getColumnIndexOrThrow("meaning")),
                cursor.getString(
                        cursor.getColumnIndexOrThrow("part_of_speech")),
                cursor.getString(
                        cursor.getColumnIndexOrThrow("example"))
        );
    }

    // Lấy toàn bộ từ vựng
    public List<Vocabulary> getAllVocabulary() {

        List<Vocabulary> list = new ArrayList<>();

        SQLiteDatabase db = getReadableDatabase();

        try (Cursor cursor = db.rawQuery(
                "SELECT * FROM Vocabulary ORDER BY word ASC",
                null)) {

            while (cursor.moveToNext()) {
                list.add(cursorToVocabulary(cursor));
            }
        }

        return list;
    }


    public List<Vocabulary> getVocabularyByTopic(int topicId) {

        List<Vocabulary> list = new ArrayList<>();

        SQLiteDatabase db = getReadableDatabase();

        try (Cursor cursor = db.query(
                TABLE_VOCABULARY,
                null,
                "topic_id = ?",
                new String[]{String.valueOf(topicId)},
                null,
                null,
                "word ASC")) {

            while (cursor.moveToNext()) {
                list.add(cursorToVocabulary(cursor));
            }
        }

        return list;
    }

    // Lấy từ vựng theo ID
    public Vocabulary getVocabularyById(int id) {

        SQLiteDatabase db = getReadableDatabase();

        try (Cursor cursor = db.query(
                TABLE_VOCABULARY,
                null,
                "id = ?",
                new String[]{String.valueOf(id)},
                null,
                null,
                null)) {

            if (cursor.moveToFirst()) {
                return cursorToVocabulary(cursor);
            }
        }

        return null;
    }

    // Tìm kiếm từ vựng
    public List<Vocabulary> searchVocabulary(String keyword) {

        List<Vocabulary> list = new ArrayList<>();

        SQLiteDatabase db = getReadableDatabase();

        String search = "%" + keyword + "%";

        try (Cursor cursor = db.query(
                TABLE_VOCABULARY,
                null,
                "word LIKE ? OR meaning LIKE ?",
                new String[]{search, search},
                null,
                null,
                "word ASC")) {

            while (cursor.moveToNext()) {
                list.add(cursorToVocabulary(cursor));
            }
        }

        return list;
    }

    // Sửa từ vựng
    public int updateVocabulary(Vocabulary vocabulary) {

        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put("topic_id", vocabulary.getTopicId());
        values.put("word", vocabulary.getWord());
        values.put("meaning", vocabulary.getMeaning());
        values.put("part_of_speech",
                vocabulary.getPartOfSpeech());
        values.put("example", vocabulary.getExample());

        return db.update(
                TABLE_VOCABULARY,
                values,
                "id = ?",
                new String[]{String.valueOf(vocabulary.getId())}
        );
    }

    // Xóa từ vựng
    public int deleteVocabulary(int id) {

        SQLiteDatabase db = getWritableDatabase();

        return db.delete(
                TABLE_VOCABULARY,
                "id = ?",
                new String[]{String.valueOf(id)}
        );
    }

    // Đếm số từ vựng theo chủ đề
    public int countVocabularyByTopic(int topicId) {

        SQLiteDatabase db = getReadableDatabase();

        try (Cursor cursor = db.rawQuery(
                "SELECT COUNT(*) FROM Vocabulary WHERE topic_id = ?",
                new String[]{String.valueOf(topicId)})) {

            if (cursor.moveToFirst()) {
                return cursor.getInt(0);
            }
        }

        return 0;
    }


    public long addQuizResult(QuizResult result) {

        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put("topic_id", result.getTopicId());
        values.put("score", result.getScore());
        values.put("total", result.getTotal());
        values.put("date", result.getDate());

        return db.insert(TABLE_QUIZ_RESULT, null, values);
    }

    // Chuyển Cursor thành QuizResult
    private QuizResult cursorToQuizResult(Cursor cursor) {

        return new QuizResult(
                cursor.getInt(
                        cursor.getColumnIndexOrThrow("id")),
                cursor.getInt(
                        cursor.getColumnIndexOrThrow("topic_id")),
                cursor.getInt(
                        cursor.getColumnIndexOrThrow("score")),
                cursor.getInt(
                        cursor.getColumnIndexOrThrow("total")),
                cursor.getString(
                        cursor.getColumnIndexOrThrow("date"))
        );
    }

    // Lấy toàn bộ lịch sử Quiz
    public List<QuizResult> getAllQuizResults() {

        List<QuizResult> list = new ArrayList<>();

        SQLiteDatabase db = getReadableDatabase();

        try (Cursor cursor = db.rawQuery(
                "SELECT * FROM QuizResult ORDER BY id DESC",
                null)) {

            while (cursor.moveToNext()) {
                list.add(cursorToQuizResult(cursor));
            }
        }

        return list;
    }

    // Lấy lịch sử Quiz theo chủ đề
    public List<QuizResult> getQuizResultsByTopic(int topicId) {

        List<QuizResult> list = new ArrayList<>();

        SQLiteDatabase db = getReadableDatabase();

        try (Cursor cursor = db.query(
                TABLE_QUIZ_RESULT,
                null,
                "topic_id = ?",
                new String[]{String.valueOf(topicId)},
                null,
                null,
                "id DESC")) {

            while (cursor.moveToNext()) {
                list.add(cursorToQuizResult(cursor));
            }
        }

        return list;
    }

    // Đếm tổng số lần làm Quiz
    public int getTotalQuizAttempts() {

        SQLiteDatabase db = getReadableDatabase();

        try (Cursor cursor = db.rawQuery(
                "SELECT COUNT(*) FROM QuizResult",
                null)) {

            if (cursor.moveToFirst()) {
                return cursor.getInt(0);
            }
        }

        return 0;
    }

    // Lấy điểm cao nhất
    public int getHighestScore() {

        SQLiteDatabase db = getReadableDatabase();

        try (Cursor cursor = db.rawQuery(
                "SELECT MAX(score) FROM QuizResult",
                null)) {

            if (cursor.moveToFirst() && !cursor.isNull(0)) {
                return cursor.getInt(0);
            }
        }

        return 0;
    }

    // Lấy điểm trung bình
    public double getAverageScore() {

        SQLiteDatabase db = getReadableDatabase();

        try (Cursor cursor = db.rawQuery(
                "SELECT AVG(score) FROM QuizResult",
                null)) {

            if (cursor.moveToFirst() && !cursor.isNull(0)) {
                return cursor.getDouble(0);
            }
        }

        return 0;
    }

    public void importVocabularyFromJson(Context context) {
        SQLiteDatabase db = getWritableDatabase();

        try (Cursor cursor = db.rawQuery(
                "SELECT COUNT(*) FROM Vocabulary", null)) {

            if (cursor.moveToFirst() && cursor.getInt(0) > 0) {
                Log.d("MYDICT_DB", "Vocabulary đã có dữ liệu");
                return;
            }
        }

        try (InputStream input =
                     context.getAssets().open("vocabulary.json");
             ByteArrayOutputStream output =
                     new ByteArrayOutputStream()) {

            byte[] buffer = new byte[4096];
            int length;

            while ((length = input.read(buffer)) != -1) {
                output.write(buffer, 0, length);
            }

            String json = new String(
                    output.toByteArray(),
                    StandardCharsets.UTF_8
            );

            JSONArray array = new JSONArray(json);

            db.beginTransaction();
            try {
                for (int i = 0; i < array.length(); i++) {
                    JSONObject item = array.getJSONObject(i);

                    ContentValues values = new ContentValues();
                    values.put("topic_id", item.getInt("topic_id"));
                    values.put("word", item.getString("word"));
                    values.put("meaning", item.getString("meaning"));
                    values.put("part_of_speech",
                            item.optString("part_of_speech"));
                    values.put("example",
                            item.optString("example"));

                    db.insertOrThrow("Vocabulary", null, values);
                }

                db.setTransactionSuccessful();
                Log.d("MYDICT_DB",
                        "Import thành công " + array.length() + " từ");

            } finally {
                db.endTransaction();
            }

        } catch (Exception e) {
            Log.e("MYDICT_DB", "Lỗi import JSON", e);
        }
    }

}
