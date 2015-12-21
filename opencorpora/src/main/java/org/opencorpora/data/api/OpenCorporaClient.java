package org.opencorpora.data.api;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.RequestFuture;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.opencorpora.BuildConfig;
import org.opencorpora.data.SolvedTask;
import org.opencorpora.data.Task;
import org.opencorpora.data.TaskType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class OpenCorporaClient {
    private static final String LOG_TAG = "OpenCorporaClient";
    private static final String TYPES_FORMAT_URL = BuildConfig.server_address
            + "api/pool_types.php?uid=%s&token=%s";
    private static final String ACTUALIZE_FORMAT_URL = BuildConfig.server_address
            + "api/tasks.php?uid=%s&token=%s";
    private static final String TASKS_BY_TYPE_FORMAT_URL = BuildConfig.server_address
            + "api/tasks.php?uid=%s&token=%s&type=%d&count=%d\"";
    private static final String PUT_READY_TASKS_FORMAT_URL = BuildConfig.server_address
            + "api/tasks.php?uid=%s&token=%s";

    private OpenCorporaRequestQueue mQueue;

    public OpenCorporaClient(Context context) {
        mQueue = OpenCorporaRequestQueue.getInstance(context);
    }

    public ArrayList<TaskType> getTypes(final String uid, final String token) {
        RequestFuture<JSONObject> future = RequestFuture.newFuture();
        String url = String.format(TYPES_FORMAT_URL, uid, token);
        JsonObjectRequest request =
                new JsonObjectRequest(Request.Method.GET , url, future, future);
        mQueue.getRequestQueue().add(request);
        ArrayList<TaskType> result = new ArrayList<>();
        try {
            JSONObject response = future.get(10, TimeUnit.SECONDS);
            JSONArray types = response.getJSONArray("items");
            for(int i = 0; i < types.length(); ++i) {
                int id = types.getJSONObject(i).getInt("type_id");
                String name = types.getJSONObject(i).getString("name");
                int complexity = types.getJSONObject(i).getInt("complexity");
                TaskType type = new TaskType(id, name, complexity);
                result.add(type);
            }
            Log.i(LOG_TAG, "Receive " + types.length() + " items");
        } catch (InterruptedException | ExecutionException | TimeoutException | JSONException e) {
            e.printStackTrace();
        }

        return result;
    }

    // Returns only actual tasks
    public ArrayList<Integer> actualizeTasks(ArrayList<Integer> taskIds,
                                             final String uid,
                                             final String token) {
        String url = String.format(ACTUALIZE_FORMAT_URL, uid, token);
        ArrayList<Integer> result = new ArrayList<>();
        JSONObject taskIdsBody = new JSONObject();
        JSONArray array = new JSONArray(taskIds);
        try {
            taskIdsBody.put("items", array);
            RequestFuture<JSONObject> future = RequestFuture.newFuture();
            JsonObjectRequest request =
                    new JsonObjectRequest(Request.Method.POST,
                            url, taskIdsBody, future, future);
            mQueue.getRequestQueue().add(request);

            JSONObject response = future.get(10, TimeUnit.SECONDS);
            Log.i(LOG_TAG, "Actualize tasks response received.");
            JSONArray actual = response.getJSONArray("items");
            for(int i = 0; i < actual.length(); ++i) {
                int id = actual.getInt(i);
                result.add(id);
            }
        } catch (JSONException | InterruptedException | TimeoutException | ExecutionException e) {
            e.printStackTrace();
        }

        Log.i(LOG_TAG, "Task actual: " + result.size() + " / " + taskIds.size());

        return result;
    }

    public ArrayList<Task> getTasksByType(String uid, String token, TaskType type) {
        return getTasksByType(uid, token, type, 20); // ToDo: move magic number
    }

    public ArrayList<Task> getTasksByType(final String uid,
                                          final String token,
                                          final TaskType type,
                                          final int count) {
        String url = String.format(Locale.US, TASKS_BY_TYPE_FORMAT_URL, uid, token, type.getId(), count);
        ArrayList<Task> result = new ArrayList<>();
        RequestFuture<JSONObject> future = RequestFuture.newFuture();
        JsonObjectRequest request =
                new JsonObjectRequest(Request.Method.GET, url, future, future);
        mQueue.getRequestQueue().add(request);

        try {
            JSONObject response = future.get(10, TimeUnit.SECONDS);
            JSONArray items = response.getJSONArray("items");
            for(int i = 0; i < items.length(); ++i) {
                JSONObject task = items.getJSONObject(i);
                int id = task.getInt("id");
                String target = task.getString("target");
                String leftContext = task.getString("left_context");
                String rightContext = task.getString("right_context");
                boolean hasInstruction = task.getBoolean("has_instruction");
                HashMap<Integer, String> choices = parseChoices(task.getJSONArray("choices"));
                Task newTask =
                        new Task(id, type, target, leftContext, rightContext, hasInstruction);
                newTask.setChoices(choices);
            }
        } catch (InterruptedException | ExecutionException | TimeoutException | JSONException e) {
            e.printStackTrace();
        }

        return result;
    }

    public boolean putReadyTasks(final String uid,
                                 final String token,
                                 ArrayList<SolvedTask> readyTasks) {
        if(readyTasks.size() == 0) return true;
        String url = String.format(Locale.US, PUT_READY_TASKS_FORMAT_URL, uid, token);
        RequestFuture<JSONObject> future = RequestFuture.newFuture();
        JSONObject tasksJSON = new JSONObject();
        JSONArray tasksArray = new JSONArray();

        try {
            for (SolvedTask task:
                    readyTasks) {
                JSONObject json = new JSONObject();
                json.put("id", task.getId());
                json.put("answer", task.getAnswer());
                json.put("seconds_before_answer", task.getSecondsBeforeAnswer());
                json.put("is_left_context_showed", task.isLeftContextShowed());
                json.put("is_right_context_showed", task.isRightContextShowed());
                json.put("is_commented", task.isCommented());
                json.put("comment_text", task.getComment());
                tasksArray.put(json);
            }
            tasksJSON.put("items", tasksArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest request =
                new JsonObjectRequest(Request.Method.PUT, url, tasksJSON, future, future);

        mQueue.getRequestQueue().add(request);

        boolean success = false;

        try {
            JSONObject response = future.get(10, TimeUnit.SECONDS);
            if(response.has("error")) {
                Log.w(LOG_TAG, "Task sending failed");
            }
            else {
                Log.i(LOG_TAG,
                        "Ready tasks successfully posted. Count: " + readyTasks.size() + ".");
                success = true;
            }
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            e.printStackTrace();
        }

        return success;
    }

    private HashMap<Integer, String> parseChoices(JSONArray choices) throws JSONException {
        HashMap<Integer, String> result = new HashMap<>();

        for(int i = 0; i < choices.length(); ++i) {
            JSONObject choice = choices.getJSONObject(i);
            Iterator<String> keys = choice.keys();
            String key = keys.next();
            result.put(Integer.valueOf(key), choice.getString(key));
        }

        return result;
    }
}
