/**
 * Copyright (C) 2016 Programming Java Android Development Project
 * Programming Java is
 * <p/>
 * http://java-lang-programming.com/
 * <p/>
 * ContentProvider Generator version : 0.1.0
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package java_lang_programming.com.android_service_demo.provider;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;

import java.util.ArrayList;
import java.util.List;

public class VideoHelper {

    public static final String TAG = "VideoHelper";

    /**
     * return Video List
     *
     * @param context you should use ApplicationContext. ApplicationContext can get getApplicationContext().
     * @return the list objects of rows, null otherwise.
     */
    public static List<Video> getVideoList(final Context context) {
        List<Video> list = new ArrayList<Video>();
        Video video = null;
        Cursor c = null;

        ContentResolver cr = context.getContentResolver();

        // 外部メディアの動画
        c = cr.query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, null, null, null, null);

        boolean isResult = c.moveToFirst();

        if (isResult) {
            int idColumn = c.getColumnIndex(MediaStore.Video.Media._ID);
            int artistColumn = c.getColumnIndex(MediaStore.Video.Media.ARTIST);
            int titleColumn = c.getColumnIndex(MediaStore.Video.Media.TITLE);
            int albumColumn = c.getColumnIndex(MediaStore.Video.Media.ALBUM);
            int durationColumn = c.getColumnIndex(MediaStore.Video.Media.DURATION);
            int latitudeColumn = c.getColumnIndex(MediaStore.Video.Media.LATITUDE);
            int longitudeColumn = c.getColumnIndex(MediaStore.Video.Media.LONGITUDE);
            int dataColmun = c.getColumnIndex(MediaStore.Video.VideoColumns.DATA);

            while (isResult) {
                video = new Video();
                video.id = c.getLong(idColumn);
                video.artist = c.getString(artistColumn);
                video.title = c.getString(titleColumn);
                video.album = c.getString(albumColumn);
                video.duration = c.getLong(durationColumn);
                video.latitude = c.getString(latitudeColumn);
                video.longitude = c.getString(longitudeColumn);
                video.path = c.getString(dataColmun);
                list.add(video);
                isResult = c.moveToNext();
            }
        }

        if (c != null) {
            c.close();
        }

        return list;
    }

}