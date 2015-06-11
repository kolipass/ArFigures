package mobi.tarantino.arfigure;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.stream.JsonReader;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by kolipass on 02.06.15.
 */
public class FileListActivity extends Activity implements AdapterView.OnItemClickListener {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(android.R.layout.list_content);
        ListView listView = (ListView) findViewById(android.R.id.list);

        try {
            listView.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_expandable_list_item_1, getFileList()));
        } catch (IOException e) {
            e.printStackTrace();
            ((TextView) findViewById(android.R.id.empty)).setText(e.getLocalizedMessage());
        }
        listView.setOnItemClickListener(this);

    }

    private List<String> getFileList() throws IOException {
        List<String> result = new ArrayList<>();
        JsonReader jsonReader = new JsonReader(new InputStreamReader(
                FileListActivity.class.getResourceAsStream("/assets/mesh.json")));

        jsonReader.beginArray();

        while (jsonReader.hasNext()) {
            result.add(jsonReader.nextString());
        }

        jsonReader.endArray();
        jsonReader.close();

        return result;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent postIntent = new Intent(this, TestActivity.class);
        postIntent.putExtra(TestActivity.FILE_NAME, (String) parent.getAdapter().getItem(position));
        startActivity(postIntent);
    }
}
